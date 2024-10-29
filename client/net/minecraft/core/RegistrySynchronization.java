package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.KnownPack;

public class RegistrySynchronization {
   private static final Set<ResourceKey<? extends Registry<?>>> NETWORKABLE_REGISTRIES;

   public RegistrySynchronization() {
      super();
   }

   public static void packRegistries(DynamicOps<Tag> var0, RegistryAccess var1, Set<KnownPack> var2, BiConsumer<ResourceKey<? extends Registry<?>>, List<PackedRegistryEntry>> var3) {
      RegistryDataLoader.SYNCHRONIZED_REGISTRIES.forEach((var4) -> {
         packRegistry(var0, var4, var1, var2, var3);
      });
   }

   private static <T> void packRegistry(DynamicOps<Tag> var0, RegistryDataLoader.RegistryData<T> var1, RegistryAccess var2, Set<KnownPack> var3, BiConsumer<ResourceKey<? extends Registry<?>>, List<PackedRegistryEntry>> var4) {
      var2.lookup(var1.key()).ifPresent((var4x) -> {
         ArrayList var5 = new ArrayList(var4x.size());
         var4x.listElements().forEach((var5x) -> {
            Optional var10000 = var4x.registrationInfo(var5x.key()).flatMap(RegistrationInfo::knownPackInfo);
            Objects.requireNonNull(var3);
            boolean var6 = var10000.filter(var3::contains).isPresent();
            Optional var7;
            if (var6) {
               var7 = Optional.empty();
            } else {
               Tag var8 = (Tag)var1.elementCodec().encodeStart(var0, var5x.value()).getOrThrow((var1x) -> {
                  String var10002 = String.valueOf(var5x.key());
                  return new IllegalArgumentException("Failed to serialize " + var10002 + ": " + var1x);
               });
               var7 = Optional.of(var8);
            }

            var5.add(new PackedRegistryEntry(var5x.key().location(), var7));
         });
         var4.accept(var4x.key(), var5);
      });
   }

   private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess var0) {
      return var0.registries().filter((var0x) -> {
         return isNetworkable(var0x.key());
      });
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      return ownedNetworkableRegistries(var0.getAccessFrom(RegistryLayer.WORLDGEN));
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      Stream var1 = var0.getLayer(RegistryLayer.STATIC).registries();
      Stream var2 = networkedRegistries(var0);
      return Stream.concat(var2, var1);
   }

   public static boolean isNetworkable(ResourceKey<? extends Registry<?>> var0) {
      return NETWORKABLE_REGISTRIES.contains(var0);
   }

   static {
      NETWORKABLE_REGISTRIES = (Set)RegistryDataLoader.SYNCHRONIZED_REGISTRIES.stream().map(RegistryDataLoader.RegistryData::key).collect(Collectors.toUnmodifiableSet());
   }

   public static record PackedRegistryEntry(ResourceLocation id, Optional<Tag> data) {
      public static final StreamCodec<ByteBuf, PackedRegistryEntry> STREAM_CODEC;

      public PackedRegistryEntry(ResourceLocation var1, Optional<Tag> var2) {
         super();
         this.id = var1;
         this.data = var2;
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Optional<Tag> data() {
         return this.data;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, PackedRegistryEntry::id, ByteBufCodecs.TAG.apply(ByteBufCodecs::optional), PackedRegistryEntry::data, PackedRegistryEntry::new);
      }
   }
}
