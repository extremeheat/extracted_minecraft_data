package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class RegistrySynchronization {
   private static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = Util.make(() -> {
      Builder var0 = ImmutableMap.builder();
      put(var0, Registries.BIOME, Biome.NETWORK_CODEC);
      put(var0, Registries.CHAT_TYPE, ChatType.CODEC);
      put(var0, Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC);
      return var0.build();
   });
   public static final Codec<RegistryAccess> NETWORK_CODEC = makeNetworkCodec();

   public RegistrySynchronization() {
      super();
   }

   private static <E> void put(
      Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> var0,
      ResourceKey<? extends Registry<E>> var1,
      Codec<E> var2
   ) {
      var0.put(var1, new RegistrySynchronization.NetworkedRegistryData(var1, var2));
   }

   private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess var0) {
      return var0.registries().filter(var0x -> NETWORKABLE_REGISTRIES.containsKey(var0x.key()));
   }

   private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> var0) {
      return (DataResult<? extends Codec<E>>)Optional.ofNullable(NETWORKABLE_REGISTRIES.get(var0))
         .map(var0x -> var0x.networkCodec())
         .map(DataResult::success)
         .orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + var0));
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
         var0x -> ownedNetworkableRegistries(var0x).collect(ImmutableMap.toImmutableMap(var0xx -> var0xx.key(), var0xx -> var0xx.value()))
      );
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      return ownedNetworkableRegistries(var0.getAccessFrom(RegistryLayer.WORLDGEN));
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      Stream var1 = var0.getLayer(RegistryLayer.STATIC).registries();
      Stream var2 = networkedRegistries(var0);
      return Stream.concat(var2, var1);
   }

   static record NetworkedRegistryData<E>(ResourceKey<? extends Registry<E>> a, Codec<E> b) {
      private final ResourceKey<? extends Registry<E>> key;
      private final Codec<E> networkCodec;

      NetworkedRegistryData(ResourceKey<? extends Registry<E>> var1, Codec<E> var2) {
         super();
         this.key = var1;
         this.networkCodec = var2;
      }
   }
}
