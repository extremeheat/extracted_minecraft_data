package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
   ItemStack getTheItem();

   default ItemStack splitTheItem(int var1) {
      return this.getTheItem().split(var1);
   }

   void setTheItem(ItemStack var1);

   default ItemStack removeTheItem() {
      return this.splitTheItem(this.getMaxStackSize());
   }

   default int getContainerSize() {
      return 1;
   }

   default boolean isEmpty() {
      return this.getTheItem().isEmpty();
   }

   default void clearContent() {
      this.removeTheItem();
   }

   default ItemStack removeItemNoUpdate(int var1) {
      return this.removeItem(var1, this.getMaxStackSize());
   }

   default ItemStack getItem(int var1) {
      return var1 == 0 ? this.getTheItem() : ItemStack.EMPTY;
   }

   default ItemStack removeItem(int var1, int var2) {
      return var1 != 0 ? ItemStack.EMPTY : this.splitTheItem(var2);
   }

   default void setItem(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.setTheItem(var2);
      }

   }

   public interface BlockContainerSingleItem extends ContainerSingleItem {
      BlockEntity getContainerBlockEntity();

      default boolean stillValid(Player var1) {
         return Container.stillValidBlockEntity(this.getContainerBlockEntity(), var1);
      }
   }
}
