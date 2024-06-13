package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public class ItemEnchantments implements TooltipProvider {
   public static final ItemEnchantments EMPTY = new ItemEnchantments(new Object2IntLinkedOpenHashMap(), true);
   public static final int MAX_LEVEL = 255;
   private static final Codec<Integer> LEVEL_CODEC = Codec.intRange(0, 255);
   private static final Codec<Object2IntLinkedOpenHashMap<Holder<Enchantment>>> LEVELS_CODEC = Codec.unboundedMap(
         BuiltInRegistries.ENCHANTMENT.holderByNameCodec(), LEVEL_CODEC
      )
      .xmap(Object2IntLinkedOpenHashMap::new, Function.identity());
   private static final Codec<ItemEnchantments> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               LEVELS_CODEC.fieldOf("levels").forGetter(var0x -> var0x.enchantments),
               Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(var0x -> var0x.showInTooltip)
            )
            .apply(var0, ItemEnchantments::new)
   );
   public static final Codec<ItemEnchantments> CODEC = Codec.withAlternative(FULL_CODEC, LEVELS_CODEC, var0 -> new ItemEnchantments(var0, true));
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantments> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.map(Object2IntLinkedOpenHashMap::new, ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT), ByteBufCodecs.VAR_INT),
      var0 -> var0.enchantments,
      ByteBufCodecs.BOOL,
      var0 -> var0.showInTooltip,
      ItemEnchantments::new
   );
   final Object2IntLinkedOpenHashMap<Holder<Enchantment>> enchantments;
   final boolean showInTooltip;

   ItemEnchantments(Object2IntLinkedOpenHashMap<Holder<Enchantment>> var1, boolean var2) {
      super();
      this.enchantments = var1;
      this.showInTooltip = var2;
   }

   public int getLevel(Enchantment var1) {
      return this.enchantments.getInt(var1.builtInRegistryHolder());
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      if (this.showInTooltip) {
         ObjectBidirectionalIterator var3 = this.enchantments.object2IntEntrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var1.accept(((Enchantment)((Holder)var4.getKey()).value()).getFullname(var4.getIntValue()));
         }
      }
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
      private final Object2IntLinkedOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntLinkedOpenHashMap();
      private final boolean showInTooltip;

      public Mutable(ItemEnchantments var1) {
         super();
         this.enchantments.putAll(var1.enchantments);
         this.showInTooltip = var1.showInTooltip;
      }

      public void set(Enchantment var1, int var2) {
         if (var2 <= 0) {
            this.enchantments.removeInt(var1.builtInRegistryHolder());
         } else {
            this.enchantments.put(var1.builtInRegistryHolder(), var2);
         }
      }

      public void upgrade(Enchantment var1, int var2) {
         if (var2 > 0) {
            this.enchantments.merge(var1.builtInRegistryHolder(), var2, Integer::max);
         }
      }

      public void removeIf(Predicate<Holder<Enchantment>> var1) {
         this.enchantments.keySet().removeIf(var1);
      }

      public int getLevel(Enchantment var1) {
         return this.enchantments.getOrDefault(var1.builtInRegistryHolder(), 0);
      }

      public Set<Holder<Enchantment>> keySet() {
         return this.enchantments.keySet();
      }

      public ItemEnchantments toImmutable() {
         return new ItemEnchantments(this.enchantments, this.showInTooltip);
      }
   }
}
