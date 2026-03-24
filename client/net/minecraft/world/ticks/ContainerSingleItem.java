package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
   ItemStack getTheItem();

   default ItemStack splitTheItem(final int count) {
      return this.getTheItem().split(count);
   }

   void setTheItem(final ItemStack itemStack);

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

   default ItemStack removeItemNoUpdate(final int slot) {
      return this.removeItem(slot, this.getMaxStackSize());
   }

   default ItemStack getItem(final int slot) {
      return slot == 0 ? this.getTheItem() : ItemStack.EMPTY;
   }

   default ItemStack removeItem(final int slot, final int count) {
      return slot != 0 ? ItemStack.EMPTY : this.splitTheItem(count);
   }

   default void setItem(final int slot, final ItemStack itemStack) {
      if (slot == 0) {
         this.setTheItem(itemStack);
      }

   }

   public interface BlockContainerSingleItem extends ContainerSingleItem {
      BlockEntity getContainerBlockEntity();

      default boolean stillValid(final Player player) {
         return Container.stillValidBlockEntity(this.getContainerBlockEntity(), player);
      }
   }
}
