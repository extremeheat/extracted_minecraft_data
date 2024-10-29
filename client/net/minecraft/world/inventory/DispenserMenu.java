package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DispenserMenu extends AbstractContainerMenu {
   private static final int SLOT_COUNT = 9;
   private static final int INV_SLOT_START = 9;
   private static final int INV_SLOT_END = 36;
   private static final int USE_ROW_SLOT_START = 36;
   private static final int USE_ROW_SLOT_END = 45;
   private final Container dispenser;

   public DispenserMenu(int var1, Inventory var2) {
      this(var1, var2, new SimpleContainer(9));
   }

   public DispenserMenu(int var1, Inventory var2, Container var3) {
      super(MenuType.GENERIC_3x3, var1);
      checkContainerSize(var3, 9);
      this.dispenser = var3;
      var3.startOpen(var2.player);
      this.add3x3GridSlots(var3, 62, 17);
      this.addStandardInventorySlots(var2, 8, 84);
   }

   protected void add3x3GridSlots(Container var1, int var2, int var3) {
      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            int var6 = var5 + var4 * 3;
            this.addSlot(new Slot(var1, var6, var2 + var5 * 18, var3 + var4 * 18));
         }
      }

   }

   public boolean stillValid(Player var1) {
      return this.dispenser.stillValid(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 < 9) {
            if (!this.moveItemStackTo(var5, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.dispenser.stopOpen(var1);
   }
}
