package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuiltinRegistries {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry(ResourceKey.createRegistryKey(new ResourceLocation("root")), Lifecycle.experimental());
   public static final Registry<? extends Registry<?>> REGISTRY;
   public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER;
   public static final Registry<ConfiguredWorldCarver<?>> CONFIGURED_CARVER;
   public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE;
   public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE;
   public static final Registry<StructureProcessorList> PROCESSOR_LIST;
   public static final Registry<StructureTemplatePool> TEMPLATE_POOL;
   public static final Registry<Biome> BIOME;
   public static final Registry<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS;

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Supplier<T> var1) {
      return registerSimple(var0, Lifecycle.stable(), var1);
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Supplier<T> var2) {
      return internalRegister(var0, new MappedRegistry(var0, var1), var2, var1);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> var0, R var1, Supplier<T> var2, Lifecycle var3) {
      ResourceLocation var4 = var0.location();
      LOADERS.put(var4, var2);
      WritableRegistry var5 = WRITABLE_REGISTRY;
      return (WritableRegistry)var5.register(var0, var1, var3);
   }

   public static <T> T register(Registry<? super T> var0, String var1, T var2) {
      return register(var0, new ResourceLocation(var1), var2);
   }

   public static <V, T extends V> T register(Registry<V> var0, ResourceLocation var1, T var2) {
      return ((WritableRegistry)var0).register(ResourceKey.create(var0.key(), var1), var2, Lifecycle.stable());
   }

   public static <V, T extends V> T registerMapping(Registry<V> var0, int var1, ResourceKey<V> var2, T var3) {
      return ((WritableRegistry)var0).registerMapping(var1, var2, var3, Lifecycle.stable());
   }

   public static void bootstrap() {
   }

   static {
      REGISTRY = WRITABLE_REGISTRY;
      CONFIGURED_SURFACE_BUILDER = registerSimple(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, () -> {
         return SurfaceBuilders.NOPE;
      });
      CONFIGURED_CARVER = registerSimple(Registry.CONFIGURED_CARVER_REGISTRY, () -> {
         return Carvers.CAVE;
      });
      CONFIGURED_FEATURE = registerSimple(Registry.CONFIGURED_FEATURE_REGISTRY, () -> {
         return Features.OAK;
      });
      CONFIGURED_STRUCTURE_FEATURE = registerSimple(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, () -> {
         return StructureFeatures.MINESHAFT;
      });
      PROCESSOR_LIST = registerSimple(Registry.PROCESSOR_LIST_REGISTRY, () -> {
         return ProcessorLists.ZOMBIE_PLAINS;
      });
      TEMPLATE_POOL = registerSimple(Registry.TEMPLATE_POOL_REGISTRY, Pools::bootstrap);
      BIOME = registerSimple(Registry.BIOME_REGISTRY, () -> {
         return Biomes.PLAINS;
      });
      NOISE_GENERATOR_SETTINGS = registerSimple(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings::bootstrap);
      LOADERS.forEach((var0, var1) -> {
         if (var1.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", var0);
         }

      });
      Registry.checkRegistry(WRITABLE_REGISTRY);
   }
}
