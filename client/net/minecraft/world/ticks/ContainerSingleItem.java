package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
   ItemStack getTheItem();

   ItemStack splitTheItem(int var1);

   void setTheItem(ItemStack var1);

   BlockEntity getContainerBlockEntity();

   default ItemStack removeTheItem() {
      return this.splitTheItem(this.getMaxStackSize());
   }

   @Override
   default int getContainerSize() {
      return 1;
   }

   @Override
   default boolean isEmpty() {
      return this.getTheItem().isEmpty();
   }

   @Override
   default void clearContent() {
      this.removeTheItem();
   }

   @Override
   default ItemStack removeItemNoUpdate(int var1) {
      return this.removeItem(var1, this.getMaxStackSize());
   }

   @Override
   default ItemStack getItem(int var1) {
      return var1 == 0 ? this.getTheItem() : ItemStack.EMPTY;
   }

   @Override
   default ItemStack removeItem(int var1, int var2) {
      return var1 != 0 ? ItemStack.EMPTY : this.splitTheItem(var2);
   }

   @Override
   default void setItem(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.setTheItem(var2);
      }
   }

   @Override
   default boolean stillValid(Player var1) {
      return Container.stillValidBlockEntity(this.getContainerBlockEntity(), var1);
   }
}
