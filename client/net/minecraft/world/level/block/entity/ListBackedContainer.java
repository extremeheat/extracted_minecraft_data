package net.minecraft.world.level.block.entity;

import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public interface ListBackedContainer extends Container {
   NonNullList<ItemStack> getItems();

   default int count() {
      return (int)this.getItems().stream().filter(Predicate.not(ItemStack::isEmpty)).count();
   }

   default int getContainerSize() {
      return this.getItems().size();
   }

   default void clearContent() {
      this.getItems().clear();
   }

   default boolean isEmpty() {
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   default ItemStack getItem(int var1) {
      return (ItemStack)this.getItems().get(var1);
   }

   default ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.getItems(), var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   default ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.removeItem(this.getItems(), var1, this.getMaxStackSize());
   }

   default boolean canPlaceItem(int var1, ItemStack var2) {
      return this.acceptsItemType(var2) && (this.getItem(var1).isEmpty() || this.getItem(var1).getCount() < this.getMaxStackSize(var2));
   }

   default boolean acceptsItemType(ItemStack var1) {
      return true;
   }

   default void setItem(int var1, ItemStack var2) {
      this.setItemNoUpdate(var1, var2);
      this.setChanged();
   }

   default void setItemNoUpdate(int var1, ItemStack var2) {
      this.getItems().set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
   }
}
