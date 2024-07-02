package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.entity.BannerPattern;
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
   private static final RegistrationInfo NETWORK_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
   private static final Function<Optional<KnownPack>, RegistrationInfo> REGISTRATION_INFO_CACHE = Util.memoize(var0 -> {
      Lifecycle var1 = var0.map(KnownPack::isVanilla).map(var0x -> Lifecycle.stable()).orElse(Lifecycle.experimental());
      return new RegistrationInfo(var0, var1);
   });
   public static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE, Structure.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.ENCHANTMENT_PROVIDER, EnchantmentProvider.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC)
   );
   public static final List<RegistryDataLoader.RegistryData<?>> DIMENSION_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.LEVEL_STEM, LevelStem.CODEC)
   );
   public static final List<RegistryDataLoader.RegistryData<?>> SYNCHRONIZED_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.NETWORK_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC)
   );

   public RegistryDataLoader() {
      super();
   }

   public static RegistryAccess.Frozen load(ResourceManager var0, RegistryAccess var1, List<RegistryDataLoader.RegistryData<?>> var2) {
      return load((var1x, var2x) -> var1x.loadFromResources(var0, var2x), var1, var2);
   }

   public static RegistryAccess.Frozen load(
      Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> var0,
      ResourceProvider var1,
      RegistryAccess var2,
      List<RegistryDataLoader.RegistryData<?>> var3
   ) {
      return load((var2x, var3x) -> var2x.loadFromNetwork(var0, var1, var3x), var2, var3);
   }

   private static RegistryAccess.Frozen load(RegistryDataLoader.LoadingFunction var0, RegistryAccess var1, List<RegistryDataLoader.RegistryData<?>> var2) {
      HashMap var3 = new HashMap();
      List var4 = var2.stream().map(var1x -> var1x.create(Lifecycle.stable(), var3)).collect(Collectors.toUnmodifiableList());
      RegistryOps.RegistryInfoLookup var5 = createContext(var1, var4);
      var4.forEach(var2x -> var0.apply(var2x, var5));
      var4.forEach(var1x -> {
         WritableRegistry var2x = var1x.registry();

         try {
            var2x.freeze();
         } catch (Exception var4x) {
            var3.put(var2x.key(), var4x);
         }

         if (var1x.data.requiredNonEmpty && var2x.size() == 0) {
            var3.put(var2x.key(), new IllegalStateException("Registry must be non-empty"));
         }
      });
      if (!var3.isEmpty()) {
         logErrors(var3);
         throw new IllegalStateException("Failed to load registries due to above errors");
      } else {
         return new RegistryAccess.ImmutableRegistryAccess(var4.stream().map(RegistryDataLoader.Loader::registry).toList()).freeze();
      }
   }

   private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess var0, List<RegistryDataLoader.Loader<?>> var1) {
      final HashMap var2 = new HashMap();
      var0.registries().forEach(var1x -> var2.put(var1x.key(), createInfoForContextRegistry(var1x.value())));
      var1.forEach(var1x -> var2.put(var1x.registry.key(), createInfoForNewRegistry(var1x.registry)));
      return new RegistryOps.RegistryInfoLookup() {
         @Override
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            return Optional.ofNullable((RegistryOps.RegistryInfo<T>)var2.get(var1));
         }
      };
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> var0) {
      return new RegistryOps.RegistryInfo<>(var0.asLookup(), var0.createRegistrationLookup(), var0.registryLifecycle());
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(Registry<T> var0) {
      return new RegistryOps.RegistryInfo<>(var0.asLookup(), var0.asTagAddingLookup(), var0.registryLifecycle());
   }

   private static void logErrors(Map<ResourceKey<?>, Exception> var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1);
      Map var3 = var0.entrySet()
         .stream()
         .collect(
            Collectors.groupingBy(
               var0x -> ((ResourceKey)var0x.getKey()).registry(), Collectors.toMap(var0x -> ((ResourceKey)var0x.getKey()).location(), Entry::getValue)
            )
         );
      var3.entrySet().stream().sorted(Entry.comparingByKey()).forEach(var1x -> {
         var2.printf("> Errors in registry %s:%n", var1x.getKey());
         ((Map)var1x.getValue()).entrySet().stream().sorted(Entry.comparingByKey()).forEach(var1xx -> {
            var2.printf(">> Errors in element %s:%n", var1xx.getKey());
            ((Exception)var1xx.getValue()).printStackTrace(var2);
         });
      });
      var2.flush();
      LOGGER.error("Registry loading errors:\n{}", var1);
   }

   private static <E> void loadElementFromResource(
      WritableRegistry<E> var0, Decoder<E> var1, RegistryOps<JsonElement> var2, ResourceKey<E> var3, Resource var4, RegistrationInfo var5
   ) throws IOException {
      try (BufferedReader var6 = var4.openAsReader()) {
         JsonElement var7 = JsonParser.parseReader(var6);
         DataResult var8 = var1.parse(var2, var7);
         Object var9 = var8.getOrThrow();
         var0.register(var3, var9, var5);
      }
   }

   static <E> void loadContentsFromManager(
      ResourceManager var0, RegistryOps.RegistryInfoLookup var1, WritableRegistry<E> var2, Decoder<E> var3, Map<ResourceKey<?>, Exception> var4
   ) {
      String var5 = Registries.elementsDirPath(var2.key());
      FileToIdConverter var6 = FileToIdConverter.json(var5);
      RegistryOps var7 = RegistryOps.create(JsonOps.INSTANCE, var1);

      for (Entry var9 : var6.listMatchingResources(var0).entrySet()) {
         ResourceLocation var10 = (ResourceLocation)var9.getKey();
         ResourceKey var11 = ResourceKey.create(var2.key(), var6.fileToId(var10));
         Resource var12 = (Resource)var9.getValue();
         RegistrationInfo var13 = REGISTRATION_INFO_CACHE.apply(var12.knownPackInfo());

         try {
            loadElementFromResource(var2, var3, var7, var11, var12, var13);
         } catch (Exception var15) {
            var4.put(var11, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", var10, var12.sourcePackId()), var15));
         }
      }
   }

   static <E> void loadContentsFromNetwork(
      Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> var0,
      ResourceProvider var1,
      RegistryOps.RegistryInfoLookup var2,
      WritableRegistry<E> var3,
      Decoder<E> var4,
      Map<ResourceKey<?>, Exception> var5
   ) {
      List var6 = (List)var0.get(var3.key());
      if (var6 != null) {
         RegistryOps var7 = RegistryOps.create(NbtOps.INSTANCE, var2);
         RegistryOps var8 = RegistryOps.create(JsonOps.INSTANCE, var2);
         String var9 = Registries.elementsDirPath(var3.key());
         FileToIdConverter var10 = FileToIdConverter.json(var9);

         for (RegistrySynchronization.PackedRegistryEntry var12 : var6) {
            ResourceKey var13 = ResourceKey.create(var3.key(), var12.id());
            Optional var14 = var12.data();
            if (var14.isPresent()) {
               try {
                  DataResult var15 = var4.parse(var7, (Tag)var14.get());
                  Object var16 = var15.getOrThrow();
                  var3.register(var13, var16, NETWORK_REGISTRATION_INFO);
               } catch (Exception var17) {
                  var5.put(var13, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", var14.get()), var17));
               }
            } else {
               ResourceLocation var19 = var10.idToFile(var12.id());

               try {
                  Resource var20 = var1.getResourceOrThrow(var19);
                  loadElementFromResource(var3, var4, var8, var13, var20, NETWORK_REGISTRATION_INFO);
               } catch (Exception var18) {
                  var5.put(var13, new IllegalStateException("Failed to parse local data", var18));
               }
            }
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   @FunctionalInterface
   interface LoadingFunction {
      void apply(RegistryDataLoader.Loader<?> var1, RegistryOps.RegistryInfoLookup var2);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
