package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChestMenu extends AbstractContainerMenu {
   private static final int SLOTS_PER_ROW = 9;
   private final Container container;
   private final int containerRows;

   private ChestMenu(MenuType<?> var1, int var2, Inventory var3, int var4) {
      this(var1, var2, var3, new SimpleContainer(9 * var4), var4);
   }

   public static ChestMenu oneRow(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x1, var0, var1, 1);
   }

   public static ChestMenu twoRows(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x2, var0, var1, 2);
   }

   public static ChestMenu threeRows(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x3, var0, var1, 3);
   }

   public static ChestMenu fourRows(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x4, var0, var1, 4);
   }

   public static ChestMenu fiveRows(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x5, var0, var1, 5);
   }

   public static ChestMenu sixRows(int var0, Inventory var1) {
      return new ChestMenu(MenuType.GENERIC_9x6, var0, var1, 6);
   }

   public static ChestMenu threeRows(int var0, Inventory var1, Container var2) {
      return new ChestMenu(MenuType.GENERIC_9x3, var0, var1, var2, 3);
   }

   public static ChestMenu sixRows(int var0, Inventory var1, Container var2) {
      return new ChestMenu(MenuType.GENERIC_9x6, var0, var1, var2, 6);
   }

   public ChestMenu(MenuType<?> var1, int var2, Inventory var3, Container var4, int var5) {
      super(var1, var2);
      checkContainerSize(var4, var5 * 9);
      this.container = var4;
      this.containerRows = var5;
      var4.startOpen(var3.player);
      int var6 = (this.containerRows - 4) * 18;

      for(int var7 = 0; var7 < this.containerRows; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(var4, var8 + var7 * 9, 8 + var8 * 18, 18 + var7 * 18));
         }
      }

      for(int var9 = 0; var9 < 3; ++var9) {
         for(int var11 = 0; var11 < 9; ++var11) {
            this.addSlot(new Slot(var3, var11 + var9 * 9 + 9, 8 + var11 * 18, 103 + var9 * 18 + var6));
         }
      }

      for(int var10 = 0; var10 < 9; ++var10) {
         this.addSlot(new Slot(var3, var10, 8 + var10 * 18, 161 + var6));
      }
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.container.stillValid(var1);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 < this.containerRows * 9) {
            if (!this.moveItemStackTo(var5, this.containerRows * 9, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 0, this.containerRows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }
      }

      return var3;
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.container.stopOpen(var1);
   }

   public Container getContainer() {
      return this.container;
   }

   public int getRowCount() {
      return this.containerRows;
   }
}
