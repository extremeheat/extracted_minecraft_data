package net.minecraft.world.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HorseInventoryMenu extends AbstractContainerMenu {
   private static final ResourceLocation SADDLE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/saddle");
   private static final ResourceLocation LLAMA_ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/llama_armor");
   private static final ResourceLocation ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/horse_armor");
   private final Container horseContainer;
   private final AbstractHorse horse;
   private static final int SLOT_SADDLE = 0;
   private static final int SLOT_BODY_ARMOR = 1;
   private static final int SLOT_HORSE_INVENTORY_START = 2;

   public HorseInventoryMenu(int var1, Inventory var2, Container var3, final AbstractHorse var4, int var5) {
      super((MenuType)null, var1);
      this.horseContainer = var3;
      this.horse = var4;
      var3.startOpen(var2.getPlayer());
      Container var6 = var4.createEquipmentSlotContainer(EquipmentSlot.SADDLE);
      this.addSlot(new ArmorSlot(var6, var4, EquipmentSlot.SADDLE, 0, 8, 18, SADDLE_SLOT_SPRITE) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.SADDLE) && var4.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
         }
      });
      final boolean var7 = var4 instanceof Llama;
      ResourceLocation var8 = var7 ? LLAMA_ARMOR_SLOT_SPRITE : ARMOR_SLOT_SPRITE;
      Container var9 = var4.createEquipmentSlotContainer(EquipmentSlot.BODY);
      this.addSlot(new ArmorSlot(var9, var4, EquipmentSlot.BODY, 0, 8, 36, var8) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.BODY) && (var4.getType().is(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || var7);
         }
      });
      if (var5 > 0) {
         for(int var10 = 0; var10 < 3; ++var10) {
            for(int var11 = 0; var11 < var5; ++var11) {
               this.addSlot(new Slot(var3, var11 + var10 * var5, 80 + var11 * 18, 18 + var10 * 18));
            }
         }
      }

      this.addStandardInventorySlots(var2, 8, 84);
   }

   public boolean stillValid(Player var1) {
      return !this.horse.hasInventoryChanged(this.horseContainer) && this.horseContainer.stillValid(var1) && this.horse.isAlive() && var1.canInteractWithEntity((Entity)this.horse, 4.0);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         int var6 = 2 + this.horseContainer.getContainerSize();
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
         } else if (this.horseContainer.getContainerSize() == 0 || !this.moveItemStackTo(var5, 2, var6, false)) {
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

   public void removed(Player var1) {
      super.removed(var1);
      this.horseContainer.stopOpen(var1);
   }
}
