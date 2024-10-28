package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HopperMenu extends AbstractContainerMenu {
   public static final int CONTAINER_SIZE = 5;
   private final Container hopper;

   public HopperMenu(int var1, Inventory var2) {
      this(var1, var2, new SimpleContainer(5));
   }

   public HopperMenu(int var1, Inventory var2, Container var3) {
      super(MenuType.HOPPER, var1);
      this.hopper = var3;
      checkContainerSize(var3, 5);
      var3.startOpen(var2.player);
      boolean var4 = true;

      int var5;
      for(var5 = 0; var5 < 5; ++var5) {
         this.addSlot(new Slot(var3, var5, 44 + var5 * 18, 20));
      }

      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot(var2, var6 + var5 * 9 + 9, 8 + var6 * 18, var5 * 18 + 51));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.addSlot(new Slot(var2, var5, 8 + var5 * 18, 109));
      }

   }

   public boolean stillValid(Player var1) {
      return this.hopper.stillValid(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 < this.hopper.getContainerSize()) {
            if (!this.moveItemStackTo(var5, this.hopper.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 0, this.hopper.getContainerSize(), false)) {
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

   public void removed(Player var1) {
      super.removed(var1);
      this.hopper.stopOpen(var1);
   }
}
