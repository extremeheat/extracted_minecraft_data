package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
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

public class ItemEnchantments implements TooltipProvider {
   public static final ItemEnchantments EMPTY = new ItemEnchantments(new Object2IntOpenHashMap(), true);
   private static final Codec<Integer> LEVEL_CODEC = Codec.intRange(0, 255);
   private static final Codec<Object2IntOpenHashMap<Holder<Enchantment>>> LEVELS_CODEC = Codec.unboundedMap(Enchantment.CODEC, LEVEL_CODEC)
      .xmap(Object2IntOpenHashMap::new, Function.identity());
   private static final Codec<ItemEnchantments> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               LEVELS_CODEC.fieldOf("levels").forGetter(var0x -> var0x.enchantments),
               Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(var0x -> var0x.showInTooltip)
            )
            .apply(var0, ItemEnchantments::new)
   );
   public static final Codec<ItemEnchantments> CODEC = Codec.withAlternative(FULL_CODEC, LEVELS_CODEC, var0 -> new ItemEnchantments(var0, true));
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantments> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.map(Object2IntOpenHashMap::new, Enchantment.STREAM_CODEC, ByteBufCodecs.VAR_INT),
      var0 -> var0.enchantments,
      ByteBufCodecs.BOOL,
      var0 -> var0.showInTooltip,
      ItemEnchantments::new
   );
   final Object2IntOpenHashMap<Holder<Enchantment>> enchantments;
   final boolean showInTooltip;

   ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>> var1, boolean var2) {
      super();
      this.enchantments = var1;
      this.showInTooltip = var2;
      ObjectIterator var3 = var1.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         int var5 = var4.getIntValue();
         if (var5 < 0 || var5 > 255) {
            throw new IllegalArgumentException("Enchantment " + var4.getKey() + " has invalid level " + var5);
         }
      }
   }

   public int getLevel(Holder<Enchantment> var1) {
      return this.enchantments.getInt(var1);
   }

   @Override
   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.showInTooltip) {
         HolderLookup.Provider var4 = var1.registries();
         HolderSet var5 = getTagOrEmpty(var4, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);

         for (Holder var7 : var5) {
            int var8 = this.enchantments.getInt(var7);
            if (var8 > 0) {
               var2.accept(Enchantment.getFullname(var7, var8));
            }
         }

         ObjectIterator var9 = this.enchantments.object2IntEntrySet().iterator();

         while (var9.hasNext()) {
            Entry var10 = (Entry)var9.next();
            Holder var11 = (Holder)var10.getKey();
            if (!var5.contains(var11)) {
               var2.accept(Enchantment.getFullname((Holder<Enchantment>)var10.getKey(), var10.getIntValue()));
            }
         }
      }
   }

   private static <T> HolderSet<T> getTagOrEmpty(@Nullable HolderLookup.Provider var0, ResourceKey<Registry<T>> var1, TagKey<T> var2) {
      if (var0 != null) {
         Optional var3 = var0.lookupOrThrow(var1).get(var2);
         if (var3.isPresent()) {
            return (HolderSet<T>)var3.get();
         }
      }

      return HolderSet.direct();
   }

   public ItemEnchantments withTooltip(boolean var1) {
      return new ItemEnchantments(this.enchantments, var1);
   }

   public Set<Holder<Enchantment>> keySet() {
      return Collections.unmodifiableSet(this.enchantments.keySet());
   }

   public Set<Entry<Holder<Enchantment>>> entrySet() {
      return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
   }

   public int size() {
      return this.enchantments.size();
   }

   public boolean isEmpty() {
      return this.enchantments.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof ItemEnchantments var2) ? false : this.showInTooltip == var2.showInTooltip && this.enchantments.equals(var2.enchantments);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.enchantments.hashCode();
      return 31 * var1 + (this.showInTooltip ? 1 : 0);
   }

   @Override
   public String toString() {
      return "ItemEnchantments{enchantments=" + this.enchantments + ", showInTooltip=" + this.showInTooltip + "}";
   }

   public static class Mutable {
      private final Object2IntOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntOpenHashMap();
      private final boolean showInTooltip;

      public Mutable(ItemEnchantments var1) {
         super();
         this.enchantments.putAll(var1.enchantments);
         this.showInTooltip = var1.showInTooltip;
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
         return new ItemEnchantments(this.enchantments, this.showInTooltip);
      }
   }
}
