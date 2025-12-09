package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.Nullable;

public class ItemEnchantments implements TooltipProvider {
   public static final ItemEnchantments EMPTY = new ItemEnchantments(new Object2IntOpenHashMap());
   private static final Codec<Integer> LEVEL_CODEC = Codec.intRange(1, 255);
   public static final Codec<ItemEnchantments> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantments> STREAM_CODEC;
   final Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

   ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>> var1) {
      super();
      this.enchantments = var1;
      ObjectIterator var2 = var1.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry var3 = (Object2IntMap.Entry)var2.next();
         int var4 = var3.getIntValue();
         if (var4 < 0 || var4 > 255) {
            String var10002 = String.valueOf(var3.getKey());
            throw new IllegalArgumentException("Enchantment " + var10002 + " has invalid level " + var4);
         }
      }

   }

   public int getLevel(Holder<Enchantment> var1) {
      return this.enchantments.getInt(var1);
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      HolderLookup.Provider var5 = var1.registries();
      HolderSet var6 = getTagOrEmpty(var5, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);

      for(Holder var8 : var6) {
         int var9 = this.enchantments.getInt(var8);
         if (var9 > 0) {
            var2.accept(Enchantment.getFullname(var8, var9));
         }
      }

      ObjectIterator var10 = this.enchantments.object2IntEntrySet().iterator();

      while(var10.hasNext()) {
         Object2IntMap.Entry var11 = (Object2IntMap.Entry)var10.next();
         Holder var12 = (Holder)var11.getKey();
         if (!var6.contains(var12)) {
            var2.accept(Enchantment.getFullname((Holder)var11.getKey(), var11.getIntValue()));
         }
      }

   }

   private static <T> HolderSet<T> getTagOrEmpty(HolderLookup.@Nullable Provider var0, ResourceKey<Registry<T>> var1, TagKey<T> var2) {
      if (var0 != null) {
         Optional var3 = var0.lookupOrThrow(var1).get(var2);
         if (var3.isPresent()) {
            return (HolderSet)var3.get();
         }
      }

      return HolderSet.direct();
   }

   public Set<Holder<Enchantment>> keySet() {
      return Collections.unmodifiableSet(this.enchantments.keySet());
   }

   public Set<Object2IntMap.Entry<Holder<Enchantment>>> entrySet() {
      return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
   }

   public int size() {
      return this.enchantments.size();
   }

   public boolean isEmpty() {
      return this.enchantments.isEmpty();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ItemEnchantments) {
         ItemEnchantments var2 = (ItemEnchantments)var1;
         return this.enchantments.equals(var2.enchantments);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.enchantments.hashCode();
   }

   public String toString() {
      return "ItemEnchantments{enchantments=" + String.valueOf(this.enchantments) + "}";
   }

   static {
      CODEC = Codec.unboundedMap(Enchantment.CODEC, LEVEL_CODEC).xmap((var0) -> new ItemEnchantments(new Object2IntOpenHashMap(var0)), (var0) -> var0.enchantments);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(Object2IntOpenHashMap::new, Enchantment.STREAM_CODEC, ByteBufCodecs.VAR_INT), (var0) -> var0.enchantments, ItemEnchantments::new);
   }

   public static class Mutable {
      private final Object2IntOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntOpenHashMap();

      public Mutable(ItemEnchantments var1) {
         super();
         this.enchantments.putAll(var1.enchantments);
      }

      public void set(Holder<Enchantment> var1, int var2) {
         if (var2 <= 0) {
            this.enchantments.removeInt(var1);
         } else {
            this.enchantments.put(var1, Math.min(var2, 255));
         }

      }

      public void upgrade(Holder<Enchantment> var1, int var2) {
         if (var2 > 0) {
            this.enchantments.merge(var1, Math.min(var2, 255), Integer::max);
         }

      }

      public void removeIf(Predicate<Holder<Enchantment>> var1) {
         this.enchantments.keySet().removeIf(var1);
      }

      public int getLevel(Holder<Enchantment> var1) {
         return this.enchantments.getOrDefault(var1, 0);
      }

      public Set<Holder<Enchantment>> keySet() {
         return this.enchantments.keySet();
      }

      public ItemEnchantments toImmutable() {
         return new ItemEnchantments(this.enchantments);
      }
   }
}
