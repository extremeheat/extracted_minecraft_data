package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TagNetworkSerialization {
   public TagNetworkSerialization() {
      super();
   }

   public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(RegistryAccess var0) {
      return (Map)var0.networkSafeRegistries().map((var0x) -> {
         return Pair.of(var0x.key(), serializeToNetwork(var0x.value()));
      }).filter((var0x) -> {
         return !((NetworkPayload)var0x.getSecond()).isEmpty();
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static <T> NetworkPayload serializeToNetwork(Registry<T> var0) {
      HashMap var1 = new HashMap();
      var0.getTags().forEach((var2) -> {
         HolderSet var3 = (HolderSet)var2.getSecond();
         IntArrayList var4 = new IntArrayList(var3.size());
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Holder var6 = (Holder)var5.next();
            if (var6.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + var6);
            }

            var4.add(var0.getId(var6.value()));
         }

         var1.put(((TagKey)var2.getFirst()).location(), var4);
      });
      return new NetworkPayload(var1);
   }

   public static <T> void deserializeTagsFromNetwork(ResourceKey<? extends Registry<T>> var0, Registry<T> var1, NetworkPayload var2, TagOutput<T> var3) {
      var2.tags.forEach((var3x, var4) -> {
         TagKey var5 = TagKey.create(var0, var3x);
         IntStream var10000 = var4.intStream();
         Objects.requireNonNull(var1);
         List var6 = var10000.mapToObj(var1::getHolder).flatMap(Optional::stream).toList();
         var3.accept(var5, var6);
      });
   }

   public static final class NetworkPayload {
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
   }

   @FunctionalInterface
   public interface TagOutput<T> {
      void accept(TagKey<T> var1, List<Holder<T>> var2);
   }
}
