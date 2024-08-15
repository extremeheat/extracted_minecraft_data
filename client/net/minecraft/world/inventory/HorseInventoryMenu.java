package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HorseInventoryMenu extends AbstractContainerMenu {
   private final Container horseContainer;
   private final Container armorContainer;
   private final AbstractHorse horse;
   private static final int SLOT_BODY_ARMOR = 1;
   private static final int SLOT_HORSE_INVENTORY_START = 2;

   public HorseInventoryMenu(int var1, Inventory var2, Container var3, final AbstractHorse var4, int var5) {
      super(null, var1);
      this.horseContainer = var3;
      this.armorContainer = var4.getBodyArmorAccess();
      this.horse = var4;
      var3.startOpen(var2.player);
      this.addSlot(new Slot(var3, 0, 8, 18) {
         @Override
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.SADDLE) && !this.hasItem() && var4.isSaddleable();
         }

         @Override
         public boolean isActive() {
            return var4.isSaddleable();
         }
      });
      this.addSlot(new ArmorSlot(this.armorContainer, var4, EquipmentSlot.BODY, 0, 8, 36, null) {
         @Override
         public boolean mayPlace(ItemStack var1) {
            return var4.isBodyArmorItem(var1);
         }

         @Override
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.BODY);
         }
      });
      if (var5 > 0) {
         for (int var6 = 0; var6 < 3; var6++) {
            for (int var7 = 0; var7 < var5; var7++) {
               this.addSlot(new Slot(var3, 1 + var7 + var6 * var5, 80 + var7 * 18, 18 + var6 * 18));
            }
         }
      }

      this.addStandardInventorySlots(var2, 8, 84);
   }

   @Override
   public boolean stillValid(Player var1) {
      return !this.horse.hasInventoryChanged(this.horseContainer)
         && this.horseContainer.stillValid(var1)
         && this.armorContainer.stillValid(var1)
         && this.horse.isAlive()
         && var1.canInteractWithEntity(this.horse, 4.0);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         int var6 = this.horseContainer.getContainerSize() + 1;
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
         } else if (var6 <= 1 || !this.moveItemStackTo(var5, 2, var6, false)) {
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
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }
      }

      return var3;
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.horseContainer.stopOpen(var1);
   }
}
