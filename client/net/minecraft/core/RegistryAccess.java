package net.minecraft.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
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

public interface RegistryAccess {
   Logger LOGGER = LogUtils.getLogger();
   Map<ResourceKey<? extends Registry<?>>, RegistryData<?>> REGISTRIES = (Map)Util.make(() -> {
      ImmutableMap.Builder var0 = ImmutableMap.builder();
      put(var0, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionType.DIRECT_CODEC);
      put(var0, Registry.BIOME_REGISTRY, Biome.DIRECT_CODEC, Biome.NETWORK_CODEC);
      put(var0, Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.DIRECT_CODEC);
      put(var0, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC);
      put(var0, Registry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
      put(var0, Registry.STRUCTURE_REGISTRY, Structure.DIRECT_CODEC);
      put(var0, Registry.STRUCTURE_SET_REGISTRY, StructureSet.DIRECT_CODEC);
      put(var0, Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorType.DIRECT_CODEC);
      put(var0, Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.DIRECT_CODEC);
      put(var0, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC);
      put(var0, Registry.NOISE_REGISTRY, NormalNoise.NoiseParameters.DIRECT_CODEC);
      put(var0, Registry.DENSITY_FUNCTION_REGISTRY, DensityFunction.DIRECT_CODEC);
      put(var0, Registry.CHAT_TYPE_REGISTRY, ChatType.CODEC, ChatType.CODEC);
      put(var0, Registry.WORLD_PRESET_REGISTRY, WorldPreset.DIRECT_CODEC);
      put(var0, Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY, FlatLevelGeneratorPreset.DIRECT_CODEC);
      return var0.build();
   });
   Codec<RegistryAccess> NETWORK_CODEC = makeNetworkCodec();
   Supplier<Frozen> BUILTIN = Suppliers.memoize(() -> {
      return builtinCopy().freeze();
   });

   <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1);

   default <E> Registry<E> ownedRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return (Registry)this.ownedRegistry(var1).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + var1);
      });
   }

   default <E> Optional<? extends Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1) {
      Optional var2 = this.ownedRegistry(var1);
      return var2.isPresent() ? var2 : Registry.REGISTRY.getOptional(var1.location());
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return (Registry)this.registry(var1).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + var1);
      });
   }

   private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2) {
      var0.put(var1, new RegistryData(var1, var2, (Codec)null));
   }

   private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2, Codec<E> var3) {
      var0.put(var1, new RegistryData(var1, var2, var3));
   }

   static Iterable<RegistryData<?>> knownRegistries() {
      return REGISTRIES.values();
   }

   Stream<RegistryEntry<?>> ownedRegistries();

   private static Stream<RegistryEntry<Object>> globalRegistries() {
      return Registry.REGISTRY.holders().map(RegistryEntry::fromHolder);
   }

   default Stream<RegistryEntry<?>> registries() {
      return Stream.concat(this.ownedRegistries(), globalRegistries());
   }

   default Stream<RegistryEntry<?>> networkSafeRegistries() {
      return Stream.concat(this.ownedNetworkableRegistries(), globalRegistries());
   }

   private static <E> Codec<RegistryAccess> makeNetworkCodec() {
      Codec var0 = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
      Codec var1 = var0.partialDispatch("type", (var0x) -> {
         return DataResult.success(var0x.key());
      }, (var0x) -> {
         return getNetworkCodec(var0x).map((var1) -> {
            return RegistryCodecs.networkCodec(var0x, Lifecycle.experimental(), var1);
         });
      });
      UnboundedMapCodec var2 = Codec.unboundedMap(var0, var1);
      return captureMap(var2);
   }

   private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> var0) {
      return var0.xmap(ImmutableRegistryAccess::new, (var0x) -> {
         return (Map)var0x.ownedNetworkableRegistries().collect(ImmutableMap.toImmutableMap((var0) -> {
            return var0.key();
         }, (var0) -> {
            return var0.value();
         }));
      });
   }

   private Stream<RegistryEntry<?>> ownedNetworkableRegistries() {
      return this.ownedRegistries().filter((var0) -> {
         return ((RegistryData)REGISTRIES.get(var0.key)).sendToClient();
      });
   }

   private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> var0) {
      return (DataResult)Optional.ofNullable((RegistryData)REGISTRIES.get(var0)).map((var0x) -> {
         return var0x.networkCodec();
      }).map(DataResult::success).orElseGet(() -> {
         return DataResult.error("Unknown or not serializable registry: " + var0);
      });
   }

   private static Map<ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> createFreshRegistries() {
      return (Map)REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), RegistryAccess::createRegistry));
   }

   private static Writable blankWriteable() {
      return new WritableRegistryAccess(createFreshRegistries());
   }

   static Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> var0) {
      return new Frozen() {
         public <T> Optional<Registry<T>> ownedRegistry(ResourceKey<? extends Registry<? extends T>> var1) {
            Registry var2 = var0;
            return var2.getOptional(var1);
         }

         public Stream<RegistryEntry<?>> ownedRegistries() {
            return var0.entrySet().stream().map(RegistryEntry::fromMapEntry);
         }
      };
   }

   static Writable builtinCopy() {
      Writable var0 = blankWriteable();
      RegistryResourceAccess.InMemoryStorage var1 = new RegistryResourceAccess.InMemoryStorage();
      Iterator var2 = REGISTRIES.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         addBuiltinElements(var1, (RegistryData)var3.getValue());
      }

      RegistryOps.createAndLoad(JsonOps.INSTANCE, var0, (RegistryResourceAccess)var1);
      return var0;
   }

   private static <E> void addBuiltinElements(RegistryResourceAccess.InMemoryStorage var0, RegistryData<E> var1) {
      ResourceKey var2 = var1.key();
      Registry var3 = BuiltinRegistries.ACCESS.registryOrThrow(var2);
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         ResourceKey var6 = (ResourceKey)var5.getKey();
         Object var7 = var5.getValue();
         var0.add(BuiltinRegistries.ACCESS, var6, var1.codec(), var3.getId(var7), var7, var3.lifecycle(var7));
      }

   }

   static void load(Writable var0, DynamicOps<JsonElement> var1, RegistryLoader var2) {
      RegistryLoader.Bound var3 = var2.bind(var0);
      Iterator var4 = REGISTRIES.values().iterator();

      while(var4.hasNext()) {
         RegistryData var5 = (RegistryData)var4.next();
         readRegistry(var1, var3, var5);
      }

   }

   private static <E> void readRegistry(DynamicOps<JsonElement> var0, RegistryLoader.Bound var1, RegistryData<E> var2) {
      DataResult var3 = var1.overrideRegistryFromResources(var2.key(), var2.codec(), var0);
      var3.error().ifPresent((var0x) -> {
         throw new JsonParseException("Error loading registry data: " + var0x.message());
      });
   }

   static RegistryAccess readFromDisk(Dynamic<?> var0) {
      return new ImmutableRegistryAccess((Map)REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), (var1) -> {
         return retrieveRegistry(var1, var0);
      })));
   }

   static <E> Registry<E> retrieveRegistry(ResourceKey<? extends Registry<? extends E>> var0, Dynamic<?> var1) {
      DataResult var10000 = RegistryOps.retrieveRegistry(var0).codec().parse(var1);
      String var10001 = "" + var0 + " registry: ";
      Logger var10002 = LOGGER;
      Objects.requireNonNull(var10002);
      return (Registry)var10000.resultOrPartial(Util.prefix(var10001, var10002::error)).orElseThrow(() -> {
         return new IllegalStateException("Failed to get " + var0 + " registry");
      });
   }

   static <E> WritableRegistry<?> createRegistry(ResourceKey<? extends Registry<?>> var0) {
      return new MappedRegistry(var0, Lifecycle.stable(), (Function)null);
   }

   default Frozen freeze() {
      return new ImmutableRegistryAccess(this.ownedRegistries().map(RegistryEntry::freeze));
   }

   default Lifecycle allElementsLifecycle() {
      return (Lifecycle)this.ownedRegistries().map((var0) -> {
         return var0.value.elementsLifecycle();
      }).reduce(Lifecycle.stable(), Lifecycle::add);
   }

   public static record RegistryData<E>(ResourceKey<? extends Registry<E>> a, Codec<E> b, @Nullable Codec<E> c) {
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

      public boolean sendToClient() {
         return this.networkCodec != null;
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
   }

   public static final class WritableRegistryAccess implements Writable {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> registries;

      WritableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> var1) {
         super();
         this.registries = var1;
      }

      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable((WritableRegistry)this.registries.get(var1)).map((var0) -> {
            return var0;
         });
      }

      public <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable((WritableRegistry)this.registries.get(var1)).map((var0) -> {
            return var0;
         });
      }

      public Stream<RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
      }
   }

   public interface Writable extends RegistryAccess {
      <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> var1);

      default <E> WritableRegistry<E> ownedWritableRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
         return (WritableRegistry)this.ownedWritableRegistry(var1).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + var1);
         });
      }
   }

   public static final class ImmutableRegistryAccess implements Frozen {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> var1) {
         super();
         this.registries = Map.copyOf(var1);
      }

      ImmutableRegistryAccess(Stream<RegistryEntry<?>> var1) {
         super();
         this.registries = (Map)var1.collect(ImmutableMap.toImmutableMap(RegistryEntry::key, RegistryEntry::value));
      }

      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable((Registry)this.registries.get(var1)).map((var0) -> {
            return var0;
         });
      }

      public Stream<RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> a, Registry<T> b) {
      final ResourceKey<? extends Registry<T>> key;
      final Registry<T> value;

      public RegistryEntry(ResourceKey<? extends Registry<T>> var1, Registry<T> var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      private static <T, R extends Registry<? extends T>> RegistryEntry<T> fromMapEntry(Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> var0) {
         return fromUntyped((ResourceKey)var0.getKey(), (Registry)var0.getValue());
      }

      private static <T> RegistryEntry<T> fromHolder(Holder.Reference<? extends Registry<? extends T>> var0) {
         return fromUntyped(var0.key(), (Registry)var0.value());
      }

      private static <T> RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> var0, Registry<?> var1) {
         return new RegistryEntry(var0, var1);
      }

      private RegistryEntry<T> freeze() {
         return new RegistryEntry(this.key, this.value.freeze());
      }

      public ResourceKey<? extends Registry<T>> key() {
         return this.key;
      }

      public Registry<T> value() {
         return this.value;
      }
   }

   public interface Frozen extends RegistryAccess {
      default Frozen freeze() {
         return this;
      }
   }
}
