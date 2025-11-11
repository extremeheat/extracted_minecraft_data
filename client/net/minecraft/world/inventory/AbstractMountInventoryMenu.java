package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractMountInventoryMenu extends AbstractContainerMenu {
   protected final Container mountContainer;
   protected final LivingEntity mount;
   protected final int SLOT_SADDLE = 0;
   protected final int SLOT_BODY_ARMOR = 1;
   protected final int SLOT_INVENTORY_START = 2;
   protected static final int INVENTORY_ROWS = 3;

   protected AbstractMountInventoryMenu(int var1, Inventory var2, Container var3, LivingEntity var4) {
      super((MenuType)null, var1);
      this.mountContainer = var3;
      this.mount = var4;
      var3.startOpen(var2.player);
   }

   protected abstract boolean hasInventoryChanged(Container var1);

   public boolean stillValid(Player var1) {
      return !this.hasInventoryChanged(this.mountContainer) && this.mountContainer.stillValid(var1) && this.mount.isAlive() && var1.canInteractWithEntity((Entity)this.mount, 4.0);
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.mountContainer.stopOpen(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         int var6 = 2 + this.mountContainer.getContainerSize();
         if (var2 < var6) {
            if (!this.moveItemStackTo(var5, var6, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).mayPlace(var5) && !this.getSlot(1).hasItem()) {
            if (!this.moveItemStackTo(var5, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).mayPlace(var5) && !this.getSlot(0).hasItem()) {
            if (!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.mountContainer.getContainerSize() == 0 || !this.moveItemStackTo(var5, 2, var6, false)) {
            int var7 = var6 + 27;
            int var9 = var7 + 9;
            if (var2 >= var7 && var2 < var9) {
               if (!this.moveItemStackTo(var5, var6, var7, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= var6 && var2 < var7) {
               if (!this.moveItemStackTo(var5, var7, var9, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, var7, var7, false)) {
               return ItemStack.EMPTY;
            }

            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }
      }

      return var3;
   }

   public static int getInventorySize(int var0) {
      return var0 * 3;
   }
}
