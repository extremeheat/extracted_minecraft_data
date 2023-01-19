package net.minecraft.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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
   Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRIES = Util.make(() -> {
      Builder var0 = ImmutableMap.builder();
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
   Supplier<RegistryAccess.Frozen> BUILTIN = Suppliers.memoize(() -> builtinCopy().freeze());

   <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1);

   default <E> Registry<E> ownedRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.<E>ownedRegistry(var1).orElseThrow(() -> new IllegalStateException("Missing registry: " + var1));
   }

   default <E> Optional<? extends Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1) {
      Optional var2 = this.ownedRegistry(var1);
      return var2.isPresent() ? var2 : Registry.REGISTRY.getOptional(var1.location());
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.<E>registry(var1).orElseThrow(() -> new IllegalStateException("Missing registry: " + var1));
   }

   private static <E> void put(
      Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2
   ) {
      var0.put(var1, new RegistryAccess.RegistryData(var1, var2, null));
   }

   private static <E> void put(
      Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> var0, ResourceKey<? extends Registry<E>> var1, Codec<E> var2, Codec<E> var3
   ) {
      var0.put(var1, new RegistryAccess.RegistryData(var1, var2, var3));
   }

   static Iterable<RegistryAccess.RegistryData<?>> knownRegistries() {
      return REGISTRIES.values();
   }

   Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries();

   private static Stream<RegistryAccess.RegistryEntry<Object>> globalRegistries() {
      return Registry.REGISTRY.holders().map(RegistryAccess.RegistryEntry::fromHolder);
   }

   default Stream<RegistryAccess.RegistryEntry<?>> registries() {
      return Stream.concat(this.ownedRegistries(), globalRegistries());
   }

   default Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries() {
      return Stream.concat(this.ownedNetworkableRegistries(), globalRegistries());
   }

   private static <E> Codec<RegistryAccess> makeNetworkCodec() {
      Codec var0 = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
      Codec var1 = var0.partialDispatch(
         "type",
         var0x -> DataResult.success(var0x.key()),
         var0x -> getNetworkCodec(var0x).map(var1x -> RegistryCodecs.networkCodec(var0x, Lifecycle.experimental(), var1x))
      );
      UnboundedMapCodec var2 = Codec.unboundedMap(var0, var1);
      return captureMap(var2);
   }

   private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> var0) {
      return var0.xmap(
         RegistryAccess.ImmutableRegistryAccess::new,
         var0x -> var0x.ownedNetworkableRegistries().collect(ImmutableMap.toImmutableMap(var0xx -> var0xx.key(), var0xx -> var0xx.value()))
      );
   }

   private Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries() {
      return this.ownedRegistries().filter(var0 -> REGISTRIES.get(var0.key).sendToClient());
   }

   private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> var0) {
      return (DataResult<? extends Codec<E>>)Optional.ofNullable(REGISTRIES.get(var0))
         .map(var0x -> var0x.networkCodec())
         .map(DataResult::success)
         .orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + var0));
   }

   private static Map<ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> createFreshRegistries() {
      return REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), RegistryAccess::createRegistry));
   }

   private static RegistryAccess.Writable blankWriteable() {
      return new RegistryAccess.WritableRegistryAccess(createFreshRegistries());
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> var0) {
      return new RegistryAccess.Frozen() {
         @Override
         public <T> Optional<Registry<T>> ownedRegistry(ResourceKey<? extends Registry<? extends T>> var1) {
            Registry var2 = var0;
            return var2.getOptional(var1);
         }

         @Override
         public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
            return var0.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }
      };
   }

   static RegistryAccess.Writable builtinCopy() {
      RegistryAccess.Writable var0 = blankWriteable();
      RegistryResourceAccess.InMemoryStorage var1 = new RegistryResourceAccess.InMemoryStorage();

      for(Entry var3 : REGISTRIES.entrySet()) {
         addBuiltinElements(var1, (RegistryAccess.RegistryData)var3.getValue());
      }

      RegistryOps.createAndLoad(JsonOps.INSTANCE, var0, var1);
      return var0;
   }

   private static <E> void addBuiltinElements(RegistryResourceAccess.InMemoryStorage var0, RegistryAccess.RegistryData<E> var1) {
      ResourceKey var2 = var1.key();
      Registry var3 = BuiltinRegistries.ACCESS.registryOrThrow(var2);

      for(Entry var5 : var3.entrySet()) {
         ResourceKey var6 = (ResourceKey)var5.getKey();
         Object var7 = var5.getValue();
         var0.add(BuiltinRegistries.ACCESS, var6, var1.codec(), var3.getId(var7), (E)var7, var3.lifecycle(var7));
      }
   }

   static void load(RegistryAccess.Writable var0, DynamicOps<JsonElement> var1, RegistryLoader var2) {
      RegistryLoader.Bound var3 = var2.bind(var0);

      for(RegistryAccess.RegistryData var5 : REGISTRIES.values()) {
         readRegistry(var1, var3, var5);
      }
   }

   private static <E> void readRegistry(DynamicOps<JsonElement> var0, RegistryLoader.Bound var1, RegistryAccess.RegistryData<E> var2) {
      DataResult var3 = var1.overrideRegistryFromResources(var2.key(), var2.codec(), var0);
      var3.error().ifPresent(var0x -> {
         throw new JsonParseException("Error loading registry data: " + var0x.message());
      });
   }

   static RegistryAccess readFromDisk(Dynamic<?> var0) {
      return new RegistryAccess.ImmutableRegistryAccess(
         REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), var1 -> retrieveRegistry(var1, var0)))
      );
   }

   static <E> Registry<E> retrieveRegistry(ResourceKey<? extends Registry<? extends E>> var0, Dynamic<?> var1) {
      return (Registry<E>)RegistryOps.retrieveRegistry(var0)
         .codec()
         .parse(var1)
         .resultOrPartial(Util.prefix(var0 + " registry: ", LOGGER::error))
         .orElseThrow(() -> new IllegalStateException("Failed to get " + var0 + " registry"));
   }

   static <E> WritableRegistry<?> createRegistry(ResourceKey<? extends Registry<?>> var0) {
      return new MappedRegistry<>(var0, Lifecycle.stable(), null);
   }

   default RegistryAccess.Frozen freeze() {
      return new RegistryAccess.ImmutableRegistryAccess(this.ownedRegistries().map(RegistryAccess.RegistryEntry::freeze));
   }

   default Lifecycle allElementsLifecycle() {
      return (Lifecycle)this.ownedRegistries().map(var0 -> var0.value.elementsLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
   }

   public interface Frozen extends RegistryAccess {
      @Override
      default RegistryAccess.Frozen freeze() {
         return this;
      }
   }

   public static final class ImmutableRegistryAccess implements RegistryAccess.Frozen {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> var1) {
         super();
         this.registries = Map.copyOf(var1);
      }

      ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> var1) {
         super();
         this.registries = var1.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      @Override
      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map((Function<? super Registry<?>, ? extends Registry<E>>)(var0 -> var0));
      }

      @Override
      public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
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
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> a, Registry<T> b) {
      final ResourceKey<? extends Registry<T>> key;
      final Registry<T> value;

      public RegistryEntry(ResourceKey<? extends Registry<T>> var1, Registry<T> var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      private static <T, R extends Registry<? extends T>> RegistryAccess.RegistryEntry<T> fromMapEntry(
         Entry<? extends ResourceKey<? extends Registry<?>>, R> var0
      ) {
         return fromUntyped((ResourceKey<? extends Registry<?>>)var0.getKey(), (Registry<?>)var0.getValue());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromHolder(Holder.Reference<? extends Registry<? extends T>> var0) {
         return fromUntyped(var0.key(), (Registry<?>)var0.value());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> var0, Registry<?> var1) {
         return new RegistryAccess.RegistryEntry<>(var0, var1);
      }

      private RegistryAccess.RegistryEntry<T> freeze() {
         return new RegistryAccess.RegistryEntry<>(this.key, this.value.freeze());
      }
   }

   public interface Writable extends RegistryAccess {
      <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> var1);

      default <E> WritableRegistry<E> ownedWritableRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
         return this.<E>ownedWritableRegistry(var1).orElseThrow(() -> new IllegalStateException("Missing registry: " + var1));
      }
   }

   public static final class WritableRegistryAccess implements RegistryAccess.Writable {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> registries;

      WritableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> var1) {
         super();
         this.registries = var1;
      }

      @Override
      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map(var0 -> var0);
      }

      @Override
      public <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map((Function<? super WritableRegistry<?>, ? extends WritableRegistry<E>>)(var0 -> var0));
      }

      @Override
      public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }
}
