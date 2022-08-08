package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu extends AbstractContainerMenu {
   private final Container horseContainer;
   private final AbstractHorse horse;

   public HorseInventoryMenu(int var1, Inventory var2, Container var3, final AbstractHorse var4) {
      super((MenuType)null, var1);
      this.horseContainer = var3;
      this.horse = var4;
      boolean var5 = true;
      var3.startOpen(var2.player);
      boolean var6 = true;
      this.addSlot(new Slot(var3, 0, 8, 18) {
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.SADDLE) && !this.hasItem() && var4.isSaddleable();
         }

         public boolean isActive() {
            return var4.isSaddleable();
         }
      });
      this.addSlot(new Slot(var3, 1, 8, 36) {
         public boolean mayPlace(ItemStack var1) {
            return var4.isArmor(var1);
         }

         public boolean isActive() {
            return var4.canWearArmor();
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      int var7;
      int var8;
      if (this.hasChest(var4)) {
         for(var7 = 0; var7 < 3; ++var7) {
            for(var8 = 0; var8 < ((AbstractChestedHorse)var4).getInventoryColumns(); ++var8) {
               this.addSlot(new Slot(var3, 2 + var8 + var7 * ((AbstractChestedHorse)var4).getInventoryColumns(), 80 + var8 * 18, 18 + var7 * 18));
            }
         }
      }

      for(var7 = 0; var7 < 3; ++var7) {
         for(var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(var2, var8 + var7 * 9 + 9, 8 + var8 * 18, 102 + var7 * 18 + -18));
         }
      }

      for(var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(var2, var7, 8 + var7 * 18, 142));
      }

   }

   public boolean stillValid(Player var1) {
      return !this.horse.hasInventoryChanged(this.horseContainer) && this.horseContainer.stillValid(var1) && this.horse.isAlive() && this.horse.distanceTo(var1) < 8.0F;
   }

   private boolean hasChest(AbstractHorse var1) {
      return var1 instanceof AbstractChestedHorse && ((AbstractChestedHorse)var1).hasChest();
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         int var6 = this.horseContainer.getContainerSize();
         if (var2 < var6) {
            if (!this.moveItemStackTo(var5, var6, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).mayPlace(var5) && !this.getSlot(1).hasItem()) {
            if (!this.moveItemStackTo(var5, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).mayPlace(var5)) {
            if (!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6 <= 2 || !this.moveItemStackTo(var5, 2, var6, false)) {
            int var8 = var6 + 27;
            int var10 = var8 + 9;
            if (var2 >= var8 && var2 < var10) {
               if (!this.moveItemStackTo(var5, var6, var8, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= var6 && var2 < var8) {
               if (!this.moveItemStackTo(var5, var8, var10, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, var8, var8, false)) {
               return ItemStack.EMPTY;
            }

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

   public void removed(Player var1) {
      super.removed(var1);
      this.horseContainer.stopOpen(var1);
   }
}
