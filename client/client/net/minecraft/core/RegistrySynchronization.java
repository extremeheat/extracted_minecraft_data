package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
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
   public static final Set<ResourceKey<? extends Registry<?>>> NETWORKABLE_REGISTRIES = RegistryDataLoader.SYNCHRONIZED_REGISTRIES
      .stream()
      .map(RegistryDataLoader.RegistryData::key)
      .collect(Collectors.toUnmodifiableSet());

   public RegistrySynchronization() {
      super();
   }

   public static void packRegistries(
      DynamicOps<Tag> var0,
      RegistryAccess var1,
      Set<KnownPack> var2,
      BiConsumer<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> var3
   ) {
      RegistryDataLoader.SYNCHRONIZED_REGISTRIES.forEach(var4 -> packRegistry(var0, (RegistryDataLoader.RegistryData<?>)var4, var1, var2, var3));
   }

   private static <T> void packRegistry(
      DynamicOps<Tag> var0,
      RegistryDataLoader.RegistryData<T> var1,
      RegistryAccess var2,
      Set<KnownPack> var3,
      BiConsumer<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> var4
   ) {
      var2.registry(var1.key())
         .ifPresent(
            var4x -> {
               ArrayList var5 = new ArrayList(var4x.size());
               var4x.holders()
                  .forEach(
                     var5x -> {
                        boolean var6 = var4x.registrationInfo(var5x.key()).flatMap(RegistrationInfo::knownPackInfo).filter(var3::contains).isPresent();
                        Optional var7;
                        if (var6) {
                           var7 = Optional.empty();
                        } else {
                           Tag var8 = (Tag)var1.elementCodec()
                              .encodeStart(var0, var5x.value())
                              .getOrThrow(var1xxx -> new IllegalArgumentException("Failed to serialize " + var5x.key() + ": " + var1xxx));
                           var7 = Optional.of(var8);
                        }
            
                        var5.add(new RegistrySynchronization.PackedRegistryEntry(var5x.key().location(), var7));
                     }
                  );
               var4.accept(var4x.key(), var5);
            }
         );
   }

   private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess var0) {
      return var0.registries().filter(var0x -> NETWORKABLE_REGISTRIES.contains(var0x.key()));
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      return ownedNetworkableRegistries(var0.getAccessFrom(RegistryLayer.WORLDGEN));
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> var0) {
      Stream var1 = var0.getLayer(RegistryLayer.STATIC).registries();
      Stream var2 = networkedRegistries(var0);
      return Stream.concat(var2, var1);
   }

   public static record PackedRegistryEntry(ResourceLocation id, Optional<Tag> data) {
      public static final StreamCodec<ByteBuf, RegistrySynchronization.PackedRegistryEntry> STREAM_CODEC = StreamCodec.composite(
         ResourceLocation.STREAM_CODEC,
         RegistrySynchronization.PackedRegistryEntry::id,
         ByteBufCodecs.TAG.apply(ByteBufCodecs::optional),
         RegistrySynchronization.PackedRegistryEntry::data,
         RegistrySynchronization.PackedRegistryEntry::new
      );

      public PackedRegistryEntry(ResourceLocation id, Optional<Tag> data) {
         super();
         this.id = id;
         this.data = data;
      }
   }
}
