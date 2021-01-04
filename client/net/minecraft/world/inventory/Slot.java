package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Slot {
   private final int slot;
   public final Container container;
   public int index;
   public int x;
   public int y;

   public Slot(Container var1, int var2, int var3, int var4) {
      super();
      this.container = var1;
      this.slot = var2;
      this.x = var3;
      this.y = var4;
   }

   public void onQuickCraft(ItemStack var1, ItemStack var2) {
      int var3 = var2.getCount() - var1.getCount();
      if (var3 > 0) {
         this.onQuickCraft(var2, var3);
      }

   }

   protected void onQuickCraft(ItemStack var1, int var2) {
   }

   protected void onSwapCraft(int var1) {
   }

   protected void checkTakeAchievements(ItemStack var1) {
   }

   public ItemStack onTake(Player var1, ItemStack var2) {
      this.setChanged();
      return var2;
   }

   public boolean mayPlace(ItemStack var1) {
      return true;
   }

   public ItemStack getItem() {
      return this.container.getItem(this.slot);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack var1) {
      this.container.setItem(this.slot, var1);
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack var1) {
      return this.getMaxStackSize();
   }

   @Nullable
   public String getNoItemIcon() {
      return null;
   }

   public ItemStack remove(int var1) {
      return this.container.removeItem(this.slot, var1);
   }

   public boolean mayPickup(Player var1) {
      return true;
   }

   public boolean isActive() {
      return true;
   }
}
