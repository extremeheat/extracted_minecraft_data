package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
   public TagNetworkSerialization() {
      super();
   }

   public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> var0) {
      return (Map)RegistrySynchronization.networkSafeRegistries(var0).map((var0x) -> Pair.of(var0x.key(), serializeToNetwork(var0x.value()))).filter((var0x) -> !((NetworkPayload)var0x.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static <T> NetworkPayload serializeToNetwork(Registry<T> var0) {
      HashMap var1 = new HashMap();
      var0.getTags().forEach((var2) -> {
         IntArrayList var3 = new IntArrayList(var2.size());

         for(Holder var5 : var2) {
            if (var5.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + String.valueOf(var5));
            }

            var3.add(var0.getId(var5.value()));
         }

         var1.put(var2.key().location(), var3);
      });
      return new NetworkPayload(var1);
   }

   static <T> TagLoader.LoadResult<T> deserializeTagsFromNetwork(Registry<T> var0, NetworkPayload var1) {
      ResourceKey var2 = var0.key();
      HashMap var3 = new HashMap();
      var1.tags.forEach((var3x, var4) -> {
         TagKey var5 = TagKey.create(var2, var3x);
         IntStream var10000 = var4.intStream();
         Objects.requireNonNull(var0);
         List var6 = (List)var10000.mapToObj(var0::get).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
         var3.put(var5, var6);
      });
      return new TagLoader.LoadResult<T>(var2, var3);
   }

   public static final class NetworkPayload {
      public static final NetworkPayload EMPTY = new NetworkPayload(Map.of());
      final Map<ResourceLocation, IntList> tags;

      NetworkPayload(Map<ResourceLocation, IntList> var1) {
         super();
         this.tags = var1;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
      }

      public static NetworkPayload read(FriendlyByteBuf var0) {
         return new NetworkPayload(var0.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
      }

      public boolean isEmpty() {
         return this.tags.isEmpty();
      }

      public int size() {
         return this.tags.size();
      }

      public <T> TagLoader.LoadResult<T> resolve(Registry<T> var1) {
         return TagNetworkSerialization.<T>deserializeTagsFromNetwork(var1, this);
      }
   }
}
