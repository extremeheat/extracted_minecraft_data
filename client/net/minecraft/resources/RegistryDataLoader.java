package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class RegistryDataLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Comparator<ResourceKey<?>> ERROR_KEY_COMPARATOR = Comparator.comparing(ResourceKey::registry).thenComparing(ResourceKey::location);
   private static final RegistrationInfo NETWORK_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
   private static final Function<Optional<KnownPack>, RegistrationInfo> REGISTRATION_INFO_CACHE = Util.memoize((Function)((var0) -> {
      Lifecycle var1 = (Lifecycle)var0.map(KnownPack::isVanilla).map((var0x) -> Lifecycle.stable()).orElse(Lifecycle.experimental());
      return new RegistrationInfo(var0, var1);
   }));
   public static final List<RegistryData<?>> WORLDGEN_REGISTRIES;
   public static final List<RegistryData<?>> DIMENSION_REGISTRIES;
   public static final List<RegistryData<?>> SYNCHRONIZED_REGISTRIES;

   public RegistryDataLoader() {
      super();
   }

   public static RegistryAccess.Frozen load(ResourceManager var0, List<HolderLookup.RegistryLookup<?>> var1, List<RegistryData<?>> var2) {
      return load((LoadingFunction)((var1x, var2x) -> var1x.loadFromResources(var0, var2x)), var1, var2);
   }

   public static RegistryAccess.Frozen load(Map<ResourceKey<? extends Registry<?>>, NetworkedRegistryData> var0, ResourceProvider var1, List<HolderLookup.RegistryLookup<?>> var2, List<RegistryData<?>> var3) {
      return load((LoadingFunction)((var2x, var3x) -> var2x.loadFromNetwork(var0, var1, var3x)), var2, var3);
   }

   private static RegistryAccess.Frozen load(LoadingFunction var0, List<HolderLookup.RegistryLookup<?>> var1, List<RegistryData<?>> var2) {
      HashMap var3 = new HashMap();
      List var4 = (List)var2.stream().map((var1x) -> var1x.create(Lifecycle.stable(), var3)).collect(Collectors.toUnmodifiableList());
      RegistryOps.RegistryInfoLookup var5 = createContext(var1, var4);
      var4.forEach((var2x) -> var0.apply(var2x, var5));
      var4.forEach((var1x) -> {
         WritableRegistry var2 = var1x.registry();

         try {
            var2.freeze();
         } catch (Exception var4) {
            var3.put(var2.key(), var4);
         }

         if (var1x.data.requiredNonEmpty && var2.size() == 0) {
            var3.put(var2.key(), new IllegalStateException("Registry must be non-empty"));
         }

      });
      if (!var3.isEmpty()) {
         throw logErrors(var3);
      } else {
         return (new RegistryAccess.ImmutableRegistryAccess(var4.stream().map(Loader::registry).toList())).freeze();
      }
   }

   private static RegistryOps.RegistryInfoLookup createContext(List<HolderLookup.RegistryLookup<?>> var0, List<Loader<?>> var1) {
      final HashMap var2 = new HashMap();
      var0.forEach((var1x) -> var2.put(var1x.key(), createInfoForContextRegistry(var1x)));
      var1.forEach((var1x) -> var2.put(var1x.registry.key(), createInfoForNewRegistry(var1x.registry)));
      return new RegistryOps.RegistryInfoLookup() {
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            return Optional.ofNullable((RegistryOps.RegistryInfo)var2.get(var1));
         }
      };
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> var0) {
      return new RegistryOps.RegistryInfo<T>(var0, var0.createRegistrationLookup(), var0.registryLifecycle());
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(HolderLookup.RegistryLookup<T> var0) {
      return new RegistryOps.RegistryInfo<T>(var0, var0, var0.registryLifecycle());
   }

   private static ReportedException logErrors(Map<ResourceKey<?>, Exception> var0) {
      printFullDetailsToLog(var0);
      return createReportWithBriefInfo(var0);
   }

   private static void printFullDetailsToLog(Map<ResourceKey<?>, Exception> var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1);
      Map var3 = (Map)var0.entrySet().stream().collect(Collectors.groupingBy((var0x) -> ((ResourceKey)var0x.getKey()).registry(), Collectors.toMap((var0x) -> ((ResourceKey)var0x.getKey()).location(), Map.Entry::getValue)));
      var3.entrySet().stream().sorted(Entry.comparingByKey()).forEach((var1x) -> {
         var2.printf("> Errors in registry %s:%n", var1x.getKey());
         ((Map)var1x.getValue()).entrySet().stream().sorted(Entry.comparingByKey()).forEach((var1) -> {
            var2.printf(">> Errors in element %s:%n", var1.getKey());
            ((Exception)var1.getValue()).printStackTrace(var2);
         });
      });
      var2.flush();
      LOGGER.error("Registry loading errors:\n{}", var1);
   }

   private static ReportedException createReportWithBriefInfo(Map<ResourceKey<?>, Exception> var0) {
      CrashReport var1 = CrashReport.forThrowable(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
      CrashReportCategory var2 = var1.addCategory("Loading info");
      var2.setDetail("Errors", (CrashReportDetail)(() -> {
         StringBuilder var1 = new StringBuilder();
         var0.entrySet().stream().sorted(Entry.comparingByKey(ERROR_KEY_COMPARATOR)).forEach((var1x) -> var1.append("\n\t\t").append(((ResourceKey)var1x.getKey()).registry()).append("/").append(((ResourceKey)var1x.getKey()).location()).append(": ").append(((Exception)var1x.getValue()).getMessage()));
         return var1.toString();
      }));
      return new ReportedException(var1);
   }

   private static <E> void loadElementFromResource(WritableRegistry<E> var0, Decoder<E> var1, RegistryOps<JsonElement> var2, ResourceKey<E> var3, Resource var4, RegistrationInfo var5) throws IOException {
      BufferedReader var6 = var4.openAsReader();

      try {
         JsonElement var7 = JsonParser.parseReader(var6);
         DataResult var8 = var1.parse(var2, var7);
         Object var9 = var8.getOrThrow();
         var0.register(var3, var9, var5);
      } catch (Throwable var11) {
         if (var6 != null) {
            try {
               ((Reader)var6).close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (var6 != null) {
         ((Reader)var6).close();
      }

   }

   static <E> void loadContentsFromManager(ResourceManager var0, RegistryOps.RegistryInfoLookup var1, WritableRegistry<E> var2, Decoder<E> var3, Map<ResourceKey<?>, Exception> var4) {
      FileToIdConverter var5 = FileToIdConverter.registry(var2.key());
      RegistryOps var6 = RegistryOps.create(JsonOps.INSTANCE, (RegistryOps.RegistryInfoLookup)var1);

      for(Map.Entry var8 : var5.listMatchingResources(var0).entrySet()) {
         ResourceLocation var9 = (ResourceLocation)var8.getKey();
         ResourceKey var10 = ResourceKey.create(var2.key(), var5.fileToId(var9));
         Resource var11 = (Resource)var8.getValue();
         RegistrationInfo var12 = (RegistrationInfo)REGISTRATION_INFO_CACHE.apply(var11.knownPackInfo());

         try {
            loadElementFromResource(var2, var3, var6, var10, var11, var12);
         } catch (Exception var14) {
            var4.put(var10, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", var9, var11.sourcePackId()), var14));
         }
      }

      TagLoader.loadTagsForRegistry(var0, var2);
   }

   static <E> void loadContentsFromNetwork(Map<ResourceKey<? extends Registry<?>>, NetworkedRegistryData> var0, ResourceProvider var1, RegistryOps.RegistryInfoLookup var2, WritableRegistry<E> var3, Decoder<E> var4, Map<ResourceKey<?>, Exception> var5) {
      NetworkedRegistryData var6 = (NetworkedRegistryData)var0.get(var3.key());
      if (var6 != null) {
         RegistryOps var7 = RegistryOps.create(NbtOps.INSTANCE, (RegistryOps.RegistryInfoLookup)var2);
         RegistryOps var8 = RegistryOps.create(JsonOps.INSTANCE, (RegistryOps.RegistryInfoLookup)var2);
         FileToIdConverter var9 = FileToIdConverter.registry(var3.key());

         for(RegistrySynchronization.PackedRegistryEntry var11 : var6.elements) {
            ResourceKey var12 = ResourceKey.create(var3.key(), var11.id());
            Optional var13 = var11.data();
            if (var13.isPresent()) {
               try {
                  DataResult var14 = var4.parse(var7, (Tag)var13.get());
                  Object var15 = var14.getOrThrow();
                  var3.register(var12, var15, NETWORK_REGISTRATION_INFO);
               } catch (Exception var16) {
                  var5.put(var12, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", var13.get()), var16));
               }
            } else {
               ResourceLocation var18 = var9.idToFile(var11.id());

               try {
                  Resource var19 = var1.getResourceOrThrow(var18);
                  loadElementFromResource(var3, var4, var8, var12, var19, NETWORK_REGISTRATION_INFO);
               } catch (Exception var17) {
                  var5.put(var12, new IllegalStateException("Failed to parse local data", var17));
               }
            }
         }

         TagLoader.loadTagsFromNetwork(var6.tags, var3);
      }
   }

   static {
      WORLDGEN_REGISTRIES = List.of(new RegistryData(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC), new RegistryData(Registries.BIOME, Biome.DIRECT_CODEC), new RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC), new RegistryData(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC), new RegistryData(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC), new RegistryData(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC), new RegistryData(Registries.STRUCTURE, Structure.DIRECT_CODEC), new RegistryData(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC), new RegistryData(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC), new RegistryData(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC), new RegistryData(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC), new RegistryData(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC), new RegistryData(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC), new RegistryData(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC), new RegistryData(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC), new RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryData(Registries.TRIAL_SPAWNER_CONFIG, TrialSpawnerConfig.DIRECT_CODEC), new RegistryData(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, true), new RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true), new RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC), new RegistryData(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC), new RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT_PROVIDER, EnchantmentProvider.DIRECT_CODEC), new RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC), new RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC));
      DIMENSION_REGISTRIES = List.of(new RegistryData(Registries.LEVEL_STEM, LevelStem.CODEC));
      SYNCHRONIZED_REGISTRIES = List.of(new RegistryData(Registries.BIOME, Biome.NETWORK_CODEC), new RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC), new RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryData(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, true), new RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true), new RegistryData(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC), new RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC), new RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC), new RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC), new RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC));
   }

   public static record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec, boolean requiredNonEmpty) {
      final Codec<T> elementCodec;
      final boolean requiredNonEmpty;

      RegistryData(ResourceKey<? extends Registry<T>> var1, Codec<T> var2) {
         this(var1, var2, false);
      }

      public RegistryData(ResourceKey<? extends Registry<T>> var1, Codec<T> var2, boolean var3) {
         super();
         this.key = var1;
         this.elementCodec = var2;
         this.requiredNonEmpty = var3;
      }

      Loader<T> create(Lifecycle var1, Map<ResourceKey<?>, Exception> var2) {
         MappedRegistry var3 = new MappedRegistry(this.key, var1);
         return new Loader<T>(this, var3, var2);
      }

      public void runWithArguments(BiConsumer<ResourceKey<? extends Registry<T>>, Codec<T>> var1) {
         var1.accept(this.key, this.elementCodec);
      }
   }

   static record Loader<T>(RegistryData<T> data, WritableRegistry<T> registry, Map<ResourceKey<?>, Exception> loadingErrors) {
      final RegistryData<T> data;
      final WritableRegistry<T> registry;

      Loader(RegistryData<T> var1, WritableRegistry<T> var2, Map<ResourceKey<?>, Exception> var3) {
         super();
         this.data = var1;
         this.registry = var2;
         this.loadingErrors = var3;
      }

      public void loadFromResources(ResourceManager var1, RegistryOps.RegistryInfoLookup var2) {
         RegistryDataLoader.loadContentsFromManager(var1, var2, this.registry, this.data.elementCodec, this.loadingErrors);
      }

      public void loadFromNetwork(Map<ResourceKey<? extends Registry<?>>, NetworkedRegistryData> var1, ResourceProvider var2, RegistryOps.RegistryInfoLookup var3) {
         RegistryDataLoader.loadContentsFromNetwork(var1, var2, var3, this.registry, this.data.elementCodec, this.loadingErrors);
      }
   }

   public static record NetworkedRegistryData(List<RegistrySynchronization.PackedRegistryEntry> elements, TagNetworkSerialization.NetworkPayload tags) {
      final List<RegistrySynchronization.PackedRegistryEntry> elements;
      final TagNetworkSerialization.NetworkPayload tags;

      public NetworkedRegistryData(List<RegistrySynchronization.PackedRegistryEntry> var1, TagNetworkSerialization.NetworkPayload var2) {
         super();
         this.elements = var1;
         this.tags = var2;
      }
   }

   @FunctionalInterface
   interface LoadingFunction {
      void apply(Loader<?> var1, RegistryOps.RegistryInfoLookup var2);
   }
}
