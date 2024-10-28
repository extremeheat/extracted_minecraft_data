package net.minecraft.world;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Container extends Clearable {
   float DEFAULT_DISTANCE_BUFFER = 4.0F;

   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int var1);

   ItemStack removeItem(int var1, int var2);

   ItemStack removeItemNoUpdate(int var1);

   void setItem(int var1, ItemStack var2);

   default int getMaxStackSize() {
      return 99;
   }

   default int getMaxStackSize(ItemStack var1) {
      return Math.min(this.getMaxStackSize(), var1.getMaxStackSize());
   }

   void setChanged();

   boolean stillValid(Player var1);

   default void startOpen(Player var1) {
   }

   default void stopOpen(Player var1) {
   }

   default boolean canPlaceItem(int var1, ItemStack var2) {
      return true;
   }

   default boolean canTakeItem(Container var1, int var2, ItemStack var3) {
      return true;
   }

   default int countItem(Item var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
         ItemStack var4 = this.getItem(var3);
         if (var4.getItem().equals(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   default boolean hasAnyOf(Set<Item> var1) {
      return this.hasAnyMatching((var1x) -> {
         return !var1x.isEmpty() && var1.contains(var1x.getItem());
      });
   }

   default boolean hasAnyMatching(Predicate<ItemStack> var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (var1.test(var3)) {
            return true;
         }
      }

      return false;
   }

   static boolean stillValidBlockEntity(BlockEntity var0, Player var1) {
      return stillValidBlockEntity(var0, var1, 4.0F);
   }

   static boolean stillValidBlockEntity(BlockEntity var0, Player var1, float var2) {
      Level var3 = var0.getLevel();
      BlockPos var4 = var0.getBlockPos();
      if (var3 == null) {
         return false;
      } else {
         return var3.getBlockEntity(var4) != var0 ? false : var1.canInteractWithBlock(var4, (double)var2);
      }
   }
}
