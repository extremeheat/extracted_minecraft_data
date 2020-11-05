package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RegistryAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRIES = (Map)Util.make(() -> {
      Builder var0 = ImmutableMap.builder();
      put(var0, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionType.DIRECT_CODEC);
      put(var0, Registry.BIOME_REGISTRY, Biome.DIRECT_CODEC, Biome.NETWORK_CODEC);
      put(var0, Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, ConfiguredSurfaceBuilder.DIRECT_CODEC);
      put(var0, Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.DIRECT_CODEC);
      put(var0, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC);
      put(var0, Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, ConfiguredStructureFeature.DIRECT_CODEC);
      put(var0, Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorType.DIRECT_CODEC);
      put(var0, Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.DIRECT_CODEC);
      put(var0, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC);
      return var0.build();
   });
   private static final RegistryAccess.RegistryHolder BUILTIN = (RegistryAccess.RegistryHolder)Util.make(() -> {
      RegistryAccess.RegistryHolder var0 = new RegistryAccess.RegistryHolder();
      DimensionType.registerBuiltin(var0);
      REGISTRIES.keySet().stream().filter((var0x) -> {
         return !var0x.equals(Registry.DIMENSION_TYPE_REGISTRY);
      }).forEach((var1) -> {
         copyBuiltin(var0, var1);
      });
      return var0;
   });

   public RegistryAccess() {
      super();
   }

   public abstract <E> Optional<WritableRegistry<E>> registry(ResourceKey<? extends Registry<E>> var1);

   public <E> WritableRegistry<E> registryOrThrow(ResourceKey<? extends Registry<E>> var1) {
      return (WritableRegistry)this.registry(var1).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + var1);
      });
   }

   public Registry<DimensionType> dimensionTypes() {
      return this.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
   }

   private static <E> void put(Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2) {
      var0.put(var1, new RegistryAccess.RegistryData(var1, var2, (Codec)null));
   }

   private static <E> void put(Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2, Codec<E> var3) {
      var0.put(var1, new RegistryAccess.RegistryData(var1, var2, var3));
   }

   public static RegistryAccess.RegistryHolder builtin() {
      RegistryAccess.RegistryHolder var0 = new RegistryAccess.RegistryHolder();
      RegistryReadOps.ResourceAccess.MemoryMap var1 = new RegistryReadOps.ResourceAccess.MemoryMap();
      Iterator var2 = REGISTRIES.values().iterator();

      while(var2.hasNext()) {
         RegistryAccess.RegistryData var3 = (RegistryAccess.RegistryData)var2.next();
         addBuiltinElements(var0, var1, var3);
      }

      RegistryReadOps.create(JsonOps.INSTANCE, (RegistryReadOps.ResourceAccess)var1, var0);
      return var0;
   }

   private static <E> void addBuiltinElements(RegistryAccess.RegistryHolder var0, RegistryReadOps.ResourceAccess.MemoryMap var1, RegistryAccess.RegistryData<E> var2) {
      ResourceKey var3 = var2.key();
      boolean var4 = !var3.equals(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY) && !var3.equals(Registry.DIMENSION_TYPE_REGISTRY);
      WritableRegistry var5 = BUILTIN.registryOrThrow(var3);
      WritableRegistry var6 = var0.registryOrThrow(var3);
      Iterator var7 = var5.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         Object var9 = var8.getValue();
         if (var4) {
            var1.add(BUILTIN, (ResourceKey)var8.getKey(), var2.codec(), var5.getId(var9), var9, var5.lifecycle(var9));
         } else {
            var6.registerMapping(var5.getId(var9), (ResourceKey)var8.getKey(), var9, var5.lifecycle(var9));
         }
      }

   }

   private static <R extends Registry<?>> void copyBuiltin(RegistryAccess.RegistryHolder var0, ResourceKey<R> var1) {
      Registry var2 = BuiltinRegistries.REGISTRY;
      Registry var3 = (Registry)var2.get(var1);
      if (var3 == null) {
         throw new IllegalStateException("Missing builtin registry: " + var1);
      } else {
         copy(var0, var3);
      }
   }

   private static <E> void copy(RegistryAccess.RegistryHolder var0, Registry<E> var1) {
      WritableRegistry var2 = (WritableRegistry)var0.registry(var1.key()).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + var1.key());
      });
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Object var5 = var4.getValue();
         var2.registerMapping(var1.getId(var5), (ResourceKey)var4.getKey(), var5, var1.lifecycle(var5));
      }

   }

   public static void load(RegistryAccess.RegistryHolder var0, RegistryReadOps<?> var1) {
      Iterator var2 = REGISTRIES.values().iterator();

      while(var2.hasNext()) {
         RegistryAccess.RegistryData var3 = (RegistryAccess.RegistryData)var2.next();
         readRegistry(var1, var0, var3);
      }

   }

   private static <E> void readRegistry(RegistryReadOps<?> var0, RegistryAccess.RegistryHolder var1, RegistryAccess.RegistryData<E> var2) {
      ResourceKey var3 = var2.key();
      MappedRegistry var4 = (MappedRegistry)Optional.ofNullable(var1.registries.get(var3)).map((var0x) -> {
         return var0x;
      }).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + var3);
      });
      DataResult var5 = var0.decodeElements(var4, var2.key(), var2.codec());
      var5.error().ifPresent((var0x) -> {
         LOGGER.error("Error loading registry data: {}", var0x.message());
      });
   }

   public static final class RegistryHolder extends RegistryAccess {
      public static final Codec<RegistryAccess.RegistryHolder> NETWORK_CODEC = makeNetworkCodec();
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> registries;

      private static <E> Codec<RegistryAccess.RegistryHolder> makeNetworkCodec() {
         Codec var0 = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
         Codec var1 = var0.partialDispatch("type", (var0x) -> {
            return DataResult.success(var0x.key());
         }, (var0x) -> {
            return getNetworkCodec(var0x).map((var1) -> {
               return MappedRegistry.networkCodec(var0x, Lifecycle.experimental(), var1);
            });
         });
         UnboundedMapCodec var2 = Codec.unboundedMap(var0, var1);
         return captureMap(var2);
      }

      private static <K extends ResourceKey<? extends Registry<?>>, V extends MappedRegistry<?>> Codec<RegistryAccess.RegistryHolder> captureMap(UnboundedMapCodec<K, V> var0) {
         return var0.xmap(RegistryAccess.RegistryHolder::new, (var0x) -> {
            return (ImmutableMap)var0x.registries.entrySet().stream().filter((var0) -> {
               return ((RegistryAccess.RegistryData)RegistryAccess.REGISTRIES.get(var0.getKey())).sendToClient();
            }).collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
         });
      }

      private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> var0) {
         return (DataResult)Optional.ofNullable(RegistryAccess.REGISTRIES.get(var0)).map((var0x) -> {
            return var0x.networkCodec();
         }).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown or not serializable registry: " + var0);
         });
      }

      public RegistryHolder() {
         this((Map)RegistryAccess.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), RegistryAccess.RegistryHolder::createRegistry)));
      }

      private RegistryHolder(Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> var1) {
         super();
         this.registries = var1;
      }

      private static <E> MappedRegistry<?> createRegistry(ResourceKey<? extends Registry<?>> var0) {
         return new MappedRegistry(var0, Lifecycle.stable());
      }

      public <E> Optional<WritableRegistry<E>> registry(ResourceKey<? extends Registry<E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map((var0) -> {
            return var0;
         });
      }
   }

   static final class RegistryData<E> {
      private final ResourceKey<? extends Registry<E>> key;
      private final Codec<E> codec;
      @Nullable
      private final Codec<E> networkCodec;

      public RegistryData(ResourceKey<? extends Registry<E>> var1, Codec<E> var2, @Nullable Codec<E> var3) {
         super();
         this.key = var1;
         this.codec = var2;
         this.networkCodec = var3;
      }

      public ResourceKey<? extends Registry<E>> key() {
         return this.key;
      }

      public Codec<E> codec() {
         return this.codec;
      }

      @Nullable
      public Codec<E> networkCodec() {
         return this.networkCodec;
      }

      public boolean sendToClient() {
         return this.networkCodec != null;
      }
   }
}
