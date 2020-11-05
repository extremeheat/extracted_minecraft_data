package net.minecraft.tags;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
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
   ResourceLocation getId(Tag<T> var1);

   default ResourceLocation getIdOrThrow(Tag<T> var1) {
      ResourceLocation var2 = this.getId(var1);
      if (var2 == null) {
         throw new IllegalStateException("Unrecognized tag");
      } else {
         return var2;
      }
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
            var2.add(var4.getKey());
         }
      }

      return var2;
   }

   default void serializeToNetwork(FriendlyByteBuf var1, DefaultedRegistry<T> var2) {
      Map var3 = this.getAllTags();
      var1.writeVarInt(var3.size());
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var1.writeResourceLocation((ResourceLocation)var5.getKey());
         var1.writeVarInt(((Tag)var5.getValue()).getValues().size());
         Iterator var6 = ((Tag)var5.getValue()).getValues().iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            var1.writeVarInt(var2.getId(var7));
         }
      }

   }

   static <T> TagCollection<T> loadFromNetwork(FriendlyByteBuf var0, Registry<T> var1) {
      HashMap var2 = Maps.newHashMap();
      int var3 = var0.readVarInt();

      for(int var4 = 0; var4 < var3; ++var4) {
         ResourceLocation var5 = var0.readResourceLocation();
         int var6 = var0.readVarInt();
         Builder var7 = ImmutableSet.builder();

         for(int var8 = 0; var8 < var6; ++var8) {
            var7.add(var1.byId(var0.readVarInt()));
         }

         var2.put(var5, Tag.fromSet(var7.build()));
      }

      return of(var2);
   }

   static <T> TagCollection<T> empty() {
      return of(ImmutableBiMap.of());
   }

   static <T> TagCollection<T> of(Map<ResourceLocation, Tag<T>> var0) {
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
}
