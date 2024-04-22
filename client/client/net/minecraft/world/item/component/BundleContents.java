package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

public final class BundleContents implements TooltipComponent {
   public static final BundleContents EMPTY = new BundleContents(List.of());
   public static final Codec<BundleContents> CODEC = ItemStack.CODEC.listOf().xmap(BundleContents::new, var0 -> var0.items);
   public static final StreamCodec<RegistryFriendlyByteBuf, BundleContents> STREAM_CODEC = ItemStack.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(BundleContents::new, var0 -> var0.items);
   private static final Fraction BUNDLE_IN_BUNDLE_WEIGHT = Fraction.getFraction(1, 16);
   private static final int NO_STACK_INDEX = -1;
   final List<ItemStack> items;
   final Fraction weight;

   BundleContents(List<ItemStack> var1, Fraction var2) {
      super();
      this.items = var1;
      this.weight = var2;
   }

   public BundleContents(List<ItemStack> var1) {
      this((List<ItemStack>)var1, computeContentWeight((List<ItemStack>)var1));
   }

   private static Fraction computeContentWeight(List<ItemStack> var0) {
      Fraction var1 = Fraction.ZERO;

      for (ItemStack var3 : var0) {
         var1 = var1.add(getWeight(var3).multiplyBy(Fraction.getFraction(var3.getCount(), 1)));
      }

      return var1;
   }

   static Fraction getWeight(ItemStack var0) {
      BundleContents var1 = var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var1 != null) {
         return BUNDLE_IN_BUNDLE_WEIGHT.add(var1.weight());
      } else {
         List var2 = var0.getOrDefault(DataComponents.BEES, List.of());
         return !var2.isEmpty() ? Fraction.ONE : Fraction.getFraction(1, var0.getMaxStackSize());
      }
   }

   public ItemStack getItemUnsafe(int var1) {
      return this.items.get(var1);
   }

   public Stream<ItemStack> itemCopyStream() {
      return this.items.stream().map(ItemStack::copy);
   }

   public Iterable<ItemStack> items() {
      return this.items;
   }

   public Iterable<ItemStack> itemsCopy() {
      return Lists.transform(this.items, ItemStack::copy);
   }

   public int size() {
      return this.items.size();
   }

   public Fraction weight() {
      return this.weight;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof BundleContents var2) ? false : this.weight.equals(var2.weight) && ItemStack.listMatches(this.items, var2.items);
      }
   }

   @Override
   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   @Override
   public String toString() {
      return "BundleContents" + this.items;
   }

   public static class Mutable {
      private final List<ItemStack> items;
      private Fraction weight;

      public Mutable(BundleContents var1) {
         super();
         this.items = new ArrayList<>(var1.items);
         this.weight = var1.weight;
      }

      public BundleContents.Mutable clearItems() {
         this.items.clear();
         this.weight = Fraction.ZERO;
         return this;
      }

      private int findStackIndex(ItemStack var1) {
         if (!var1.isStackable()) {
            return -1;
         } else {
            for (int var2 = 0; var2 < this.items.size(); var2++) {
               if (ItemStack.isSameItemSameComponents(this.items.get(var2), var1)) {
                  return var2;
               }
            }

            return -1;
         }
      }

      private int getMaxAmountToAdd(ItemStack var1) {
         Fraction var2 = Fraction.ONE.subtract(this.weight);
         return Math.max(var2.divideBy(BundleContents.getWeight(var1)).intValue(), 0);
      }

      public int tryInsert(ItemStack var1) {
         if (!var1.isEmpty() && var1.getItem().canFitInsideContainerItems()) {
            int var2 = Math.min(var1.getCount(), this.getMaxAmountToAdd(var1));
            if (var2 == 0) {
               return 0;
            } else {
               this.weight = this.weight.add(BundleContents.getWeight(var1).multiplyBy(Fraction.getFraction(var2, 1)));
               int var3 = this.findStackIndex(var1);
               if (var3 != -1) {
                  ItemStack var4 = this.items.remove(var3);
                  ItemStack var5 = var4.copyWithCount(var4.getCount() + var2);
                  var1.shrink(var2);
                  this.items.add(0, var5);
               } else {
                  this.items.add(0, var1.split(var2));
               }

               return var2;
            }
         } else {
            return 0;
         }
      }

      public int tryTransfer(Slot var1, Player var2) {
         ItemStack var3 = var1.getItem();
         int var4 = this.getMaxAmountToAdd(var3);
         return this.tryInsert(var1.safeTake(var3.getCount(), var4, var2));
      }

      @Nullable
      public ItemStack removeOne() {
         if (this.items.isEmpty()) {
            return null;
         } else {
            ItemStack var1 = this.items.remove(0).copy();
            this.weight = this.weight.subtract(BundleContents.getWeight(var1).multiplyBy(Fraction.getFraction(var1.getCount(), 1)));
            return var1;
         }
      }

      public Fraction weight() {
         return this.weight;
      }

      public BundleContents toImmutable() {
         return new BundleContents(List.copyOf(this.items), this.weight);
      }
   }
}