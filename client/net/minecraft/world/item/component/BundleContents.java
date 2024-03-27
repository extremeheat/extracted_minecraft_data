package net.minecraft.world.item.component;

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

public final class BundleContents implements TooltipComponent {
   public static final int MAX_WEIGHT = 64;
   public static final BundleContents EMPTY = new BundleContents(List.of());
   public static final Codec<BundleContents> CODEC = ItemStack.CODEC.sizeLimitedListOf(64).xmap(BundleContents::new, var0 -> var0.items);
   public static final StreamCodec<RegistryFriendlyByteBuf, BundleContents> STREAM_CODEC = ItemStack.STREAM_CODEC
      .<List<ItemStack>>apply(ByteBufCodecs.list(64))
      .map(BundleContents::new, var0 -> var0.items);
   private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
   private static final int NO_STACK_INDEX = -1;
   final List<ItemStack> items;
   final int weight;

   BundleContents(List<ItemStack> var1, int var2) {
      super();
      this.items = var1;
      this.weight = var2;
   }

   public BundleContents(List<ItemStack> var1) {
      this(var1, computeContentWeight(var1));
   }

   private static int computeContentWeight(List<ItemStack> var0) {
      int var1 = 0;

      for(ItemStack var3 : var0) {
         var1 += getWeight(var3) * var3.getCount();
      }

      return var1;
   }

   static int getWeight(ItemStack var0) {
      BundleContents var1 = var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var1 != null) {
         return 4 + var1.weight();
      } else {
         List var2 = var0.getOrDefault(DataComponents.BEES, List.of());
         return !var2.isEmpty() ? 64 : 64 / var0.getMaxStackSize();
      }
   }

   public ItemStack getItemUnsafe(int var1) {
      return this.items.get(var1);
   }

   public Stream<ItemStack> items() {
      return this.items.stream().map(ItemStack::copy);
   }

   public int size() {
      return this.items.size();
   }

   public int weight() {
      return this.weight;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BundleContents)) {
         return false;
      } else {
         BundleContents var2 = (BundleContents)var1;
         return this.weight == var2.weight && ItemStack.listMatches(this.items, var2.items);
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
      private int weight;

      public Mutable(BundleContents var1) {
         super();
         this.items = new ArrayList<>(var1.items);
         this.weight = var1.weight;
      }

      private int findStackIndex(ItemStack var1) {
         if (!var1.isStackable()) {
            return -1;
         } else {
            for(int var2 = 0; var2 < this.items.size(); ++var2) {
               if (ItemStack.isSameItemSameComponents(this.items.get(var2), var1)) {
                  return var2;
               }
            }

            return -1;
         }
      }

      private int getMaxAmountToAdd(ItemStack var1) {
         return Math.max(64 - this.weight, 0) / BundleContents.getWeight(var1);
      }

      public int tryInsert(ItemStack var1) {
         if (!var1.isEmpty() && var1.getItem().canFitInsideContainerItems()) {
            int var2 = Math.min(var1.getCount(), this.getMaxAmountToAdd(var1));
            if (var2 == 0) {
               return 0;
            } else {
               this.weight += BundleContents.getWeight(var1) * var2;
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
            this.weight -= BundleContents.getWeight(var1) * var1.getCount();
            return var1;
         }
      }

      public int weight() {
         return this.weight;
      }

      public BundleContents toImmutable() {
         return new BundleContents(List.copyOf(this.items), this.weight);
      }
   }
}
