package net.minecraft.tags;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface TagCollection<T> {
   Map<ResourceLocation, Tag<T>> getAllTags();

   @Nullable
   default Tag<T> getTag(ResourceLocation var1) {
      return (Tag)this.getAllTags().get(var1);
   }

   Tag<T> getTagOrEmpty(ResourceLocation var1);

   @Nullable
   default ResourceLocation getId(Tag.Named<T> var1) {
      return var1.getName();
   }

   @Nullable
   ResourceLocation getId(Tag<T> var1);

   default boolean hasTag(ResourceLocation var1) {
      return this.getAllTags().containsKey(var1);
   }

   default Collection<ResourceLocation> getAvailableTags() {
      return this.getAllTags().keySet();
   }

   default Collection<ResourceLocation> getMatchingTags(T var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.getAllTags().entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((Tag)var4.getValue()).contains(var1)) {
            var2.add((ResourceLocation)var4.getKey());
         }
      }

      return var2;
   }

   default TagCollection.NetworkPayload serializeToNetwork(Registry<T> var1) {
      Map var2 = this.getAllTags();
      HashMap var3 = Maps.newHashMapWithExpectedSize(var2.size());
      var2.forEach((var2x, var3x) -> {
         List var4 = var3x.getValues();
         IntArrayList var5 = new IntArrayList(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            var5.add(var1.getId(var7));
         }

         var3.put(var2x, var5);
      });
      return new TagCollection.NetworkPayload(var3);
   }

   static <T> TagCollection<T> createFromNetwork(TagCollection.NetworkPayload var0, Registry<? extends T> var1) {
      HashMap var2 = Maps.newHashMapWithExpectedSize(var0.tags.size());
      var0.tags.forEach((var2x, var3) -> {
         Builder var4 = ImmutableSet.builder();
         IntListIterator var5 = var3.iterator();

         while(var5.hasNext()) {
            int var6 = (Integer)var5.next();
            var4.add(var1.byId(var6));
         }

         var2.put(var2x, Tag.fromSet(var4.build()));
      });
      return method_8(var2);
   }

   static <T> TagCollection<T> empty() {
      return method_8(ImmutableBiMap.of());
   }

   // $FF: renamed from: of (java.util.Map) net.minecraft.tags.TagCollection
   static <T> TagCollection<T> method_8(Map<ResourceLocation, Tag<T>> var0) {
      final ImmutableBiMap var1 = ImmutableBiMap.copyOf(var0);
      return new TagCollection<T>() {
         private final Tag<T> empty = SetTag.empty();

         public Tag<T> getTagOrEmpty(ResourceLocation var1x) {
            return (Tag)var1.getOrDefault(var1x, this.empty);
         }

         @Nullable
         public ResourceLocation getId(Tag<T> var1x) {
            return var1x instanceof Tag.Named ? ((Tag.Named)var1x).getName() : (ResourceLocation)var1.inverse().get(var1x);
         }

         public Map<ResourceLocation, Tag<T>> getAllTags() {
            return var1;
         }
      };
   }

   public static class NetworkPayload {
      final Map<ResourceLocation, IntList> tags;

      NetworkPayload(Map<ResourceLocation, IntList> var1) {
         super();
         this.tags = var1;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
      }

      public static TagCollection.NetworkPayload read(FriendlyByteBuf var0) {
         return new TagCollection.NetworkPayload(var0.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
      }
   }
}
