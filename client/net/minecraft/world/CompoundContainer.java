package net.minecraft.world;

import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompoundContainer implements Container {
   private final Container container1;
   private final Container container2;

   public CompoundContainer(final Container container1, final Container container2) {
      super();
      this.container1 = container1;
      this.container2 = container2;
   }

   public int getContainerSize() {
      return this.container1.getContainerSize() + this.container2.getContainerSize();
   }

   public boolean isEmpty() {
      return this.container1.isEmpty() && this.container2.isEmpty();
   }

   public boolean contains(final Container container) {
      return this.container1 == container || this.container2 == container;
   }

   public ItemStack getItem(final int slot) {
      return slot >= this.container1.getContainerSize() ? this.container2.getItem(slot - this.container1.getContainerSize()) : this.container1.getItem(slot);
   }

   public ItemStack removeItem(final int slot, final int count) {
      return slot >= this.container1.getContainerSize() ? this.container2.removeItem(slot - this.container1.getContainerSize(), count) : this.container1.removeItem(slot, count);
   }

   public ItemStack removeItemNoUpdate(final int slot) {
      return slot >= this.container1.getContainerSize() ? this.container2.removeItemNoUpdate(slot - this.container1.getContainerSize()) : this.container1.removeItemNoUpdate(slot);
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      if (slot >= this.container1.getContainerSize()) {
         this.container2.setItem(slot - this.container1.getContainerSize(), itemStack);
      } else {
         this.container1.setItem(slot, itemStack);
      }

   }

   public int getMaxStackSize() {
      return this.container1.getMaxStackSize();
   }

   public void setChanged() {
      this.container1.setChanged();
      this.container2.setChanged();
   }

   public boolean stillValid(final Player player) {
      return this.container1.stillValid(player) && this.container2.stillValid(player);
   }

   public void startOpen(final ContainerUser containerUser) {
      this.container1.startOpen(containerUser);
      this.container2.startOpen(containerUser);
   }

   public void stopOpen(final ContainerUser containerUser) {
      this.container1.stopOpen(containerUser);
      this.container2.stopOpen(containerUser);
   }

   public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
      return slot >= this.container1.getContainerSize() ? this.container2.canPlaceItem(slot - this.container1.getContainerSize(), itemStack) : this.container1.canPlaceItem(slot, itemStack);
   }

   public void clearContent() {
      this.container1.clearContent();
      this.container2.clearContent();
   }
}
