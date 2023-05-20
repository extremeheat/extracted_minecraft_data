package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
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
   public static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.CODEC),
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
      new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC)
   );
   public static final List<RegistryDataLoader.RegistryData<?>> DIMENSION_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.LEVEL_STEM, LevelStem.CODEC)
   );

   public RegistryDataLoader() {
      super();
   }

   public static RegistryAccess.Frozen load(ResourceManager var0, RegistryAccess var1, List<RegistryDataLoader.RegistryData<?>> var2) {
      HashMap var3 = new HashMap();
      List var4 = var2.stream().map(var1x -> var1x.create(Lifecycle.stable(), var3)).toList();
      RegistryOps.RegistryInfoLookup var5 = createContext(var1, var4);
      var4.forEach(var2x -> ((RegistryDataLoader.Loader)var2x.getSecond()).load(var0, var5));
      var4.forEach(var1x -> {
         Registry var2x = (Registry)var1x.getFirst();

         try {
            var2x.freeze();
         } catch (Exception var4x) {
            var3.put(var2x.key(), var4x);
         }
      });
      if (!var3.isEmpty()) {
         logErrors(var3);
         throw new IllegalStateException("Failed to load registries due to above errors");
      } else {
         return new RegistryAccess.ImmutableRegistryAccess(var4.stream().map(Pair::getFirst).toList()).freeze();
      }
   }

   private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess var0, List<Pair<WritableRegistry<?>, RegistryDataLoader.Loader>> var1) {
      final HashMap var2 = new HashMap();
      var0.registries().forEach(var1x -> var2.put(var1x.key(), createInfoForContextRegistry(var1x.value())));
      var1.forEach(var1x -> var2.put(((WritableRegistry)var1x.getFirst()).key(), createInfoForNewRegistry((WritableRegistry)var1x.getFirst())));
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

   private static String registryDirPath(ResourceLocation var0) {
      return var0.getPath();
   }

   static <E> void loadRegistryContents(
      RegistryOps.RegistryInfoLookup var0,
      ResourceManager var1,
      ResourceKey<? extends Registry<E>> var2,
      WritableRegistry<E> var3,
      Decoder<E> var4,
      Map<ResourceKey<?>, Exception> var5
   ) {
      String var6 = registryDirPath(var2.location());
      FileToIdConverter var7 = FileToIdConverter.json(var6);
      RegistryOps var8 = RegistryOps.create(JsonOps.INSTANCE, var0);

      for(Entry var10 : var7.listMatchingResources(var1).entrySet()) {
         ResourceLocation var11 = (ResourceLocation)var10.getKey();
         ResourceKey var12 = ResourceKey.create(var2, var7.fileToId(var11));
         Resource var13 = (Resource)var10.getValue();

         try (BufferedReader var14 = var13.openAsReader()) {
            JsonElement var15 = JsonParser.parseReader(var14);
            DataResult var16 = var4.parse(var8, var15);
            Object var17 = var16.getOrThrow(false, var0x -> {
            });
            var3.register(var12, var17, var13.isBuiltin() ? Lifecycle.stable() : var16.lifecycle());
         } catch (Exception var20) {
            var5.put(var12, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", var11, var13.sourcePackId()), var20));
         }
      }
   }

   interface Loader {
      void load(ResourceManager var1, RegistryOps.RegistryInfoLookup var2);
   }

   public static record RegistryData<T>(ResourceKey<? extends Registry<T>> a, Codec<T> b) {
      private final ResourceKey<? extends Registry<T>> key;
      private final Codec<T> elementCodec;

      public RegistryData(ResourceKey<? extends Registry<T>> var1, Codec<T> var2) {
         super();
         this.key = var1;
         this.elementCodec = var2;
      }

      Pair<WritableRegistry<?>, RegistryDataLoader.Loader> create(Lifecycle var1, Map<ResourceKey<?>, Exception> var2) {
         MappedRegistry var3 = new MappedRegistry<>(this.key, var1);
         RegistryDataLoader.Loader var4 = (var3x, var4x) -> RegistryDataLoader.loadRegistryContents(var4x, var3x, this.key, var3, this.elementCodec, var2);
         return Pair.of(var3, var4);
      }
   }
}
