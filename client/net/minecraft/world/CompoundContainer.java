package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompoundContainer implements Container {
   private final Container container1;
   private final Container container2;

   public CompoundContainer(Container var1, Container var2) {
      super();
      this.container1 = var1;
      this.container2 = var2;
   }

   public int getContainerSize() {
      return this.container1.getContainerSize() + this.container2.getContainerSize();
   }

   public boolean isEmpty() {
      return this.container1.isEmpty() && this.container2.isEmpty();
   }

   public boolean contains(Container var1) {
      return this.container1 == var1 || this.container2 == var1;
   }

   public ItemStack getItem(int var1) {
      return var1 >= this.container1.getContainerSize() ? this.container2.getItem(var1 - this.container1.getContainerSize()) : this.container1.getItem(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      return var1 >= this.container1.getContainerSize() ? this.container2.removeItem(var1 - this.container1.getContainerSize(), var2) : this.container1.removeItem(var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return var1 >= this.container1.getContainerSize() ? this.container2.removeItemNoUpdate(var1 - this.container1.getContainerSize()) : this.container1.removeItemNoUpdate(var1);
   }

   public void setItem(int var1, ItemStack var2) {
      if (var1 >= this.container1.getContainerSize()) {
         this.container2.setItem(var1 - this.container1.getContainerSize(), var2);
      } else {
         this.container1.setItem(var1, var2);
      }

   }

   public int getMaxStackSize() {
      return this.container1.getMaxStackSize();
   }

   public void setChanged() {
      this.container1.setChanged();
      this.container2.setChanged();
   }

   public boolean stillValid(Player var1) {
      return this.container1.stillValid(var1) && this.container2.stillValid(var1);
   }

   public void startOpen(Player var1) {
      this.container1.startOpen(var1);
      this.container2.startOpen(var1);
   }

   public void stopOpen(Player var1) {
      this.container1.stopOpen(var1);
      this.container2.stopOpen(var1);
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      return var1 >= this.container1.getContainerSize() ? this.container2.canPlaceItem(var1 - this.container1.getContainerSize(), var2) : this.container1.canPlaceItem(var1, var2);
   }

   public void clearContent() {
      this.container1.clearContent();
      this.container2.clearContent();
   }
}
