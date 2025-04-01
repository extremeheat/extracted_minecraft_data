package net.minecraft.world.inventory;

import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class InventoryMenu extends AbstractCraftingMenu {
   public static final int CONTAINER_ID = 0;
   public static final int RESULT_SLOT = 0;
   private static final int CRAFTING_GRID_WIDTH = 3;
   private static final int CRAFTING_GRID_HEIGHT = 3;
   public static final int CRAFT_SLOT_START = 1;
   public static final int CRAFT_SLOT_COUNT = 9;
   public static final int CRAFT_SLOT_END = 10;
   public static final int ARMOR_SLOT_START = 10;
   public static final int ARMOR_SLOT_COUNT = 4;
   public static final int ARMOR_SLOT_END = 14;
   public static final int INV_SLOT_START = 14;
   public static final int INV_SLOT_END = 41;
   public static final int USE_ROW_SLOT_START = 41;
   public static final int USE_ROW_SLOT_END = 50;
   public static final int SHIELD_SLOT = 50;
   public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = ResourceLocation.withDefaultNamespace("container/slot/helmet");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("container/slot/chestplate");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("container/slot/leggings");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("container/slot/boots");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = ResourceLocation.withDefaultNamespace("container/slot/shield");
   private static final Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS;
   private static final EquipmentSlot[] SLOT_IDS;
   public final boolean active;
   private final Player owner;

   public InventoryMenu(Inventory var1, boolean var2, final Player var3) {
      super((MenuType)null, 0, 3, 3);
      this.active = var2;
      this.owner = var3;
      this.addResultSlot(var3, 154, 28);
      this.addCraftingGridSlots(var3, 98, 18);

      for(int var4 = 0; var4 < 4; ++var4) {
         final EquipmentSlot var5 = SLOT_IDS[var4];
         ResourceLocation var6 = (ResourceLocation)TEXTURE_EMPTY_SLOTS.get(var5);
         this.addSlot(new ArmorSlot(var1, var3, var5, 39 - var4, 8, 8 + var4 * 18, var6) {
            public boolean mayPlace(ItemStack var1) {
               return var3.isActive(PlayerUnlocks.ARMAMENTS) && var3.isEquippableInSlot(var1, var5);
            }

            public boolean isActive() {
               return var3.isActive(PlayerUnlocks.ARMAMENTS) && var3.canUseSlot(var5);
            }
         });
      }

      this.addStandardInventorySlots(var1, 8, 84);
      this.addSlot(new Slot(var1, 40, 77, 62) {
         public void setByPlayer(ItemStack var1, ItemStack var2) {
            var3.onEquipItem(EquipmentSlot.OFFHAND, var2, var1);
            super.setByPlayer(var1, var2);
         }

         public ResourceLocation getNoItemIcon() {
            return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
         }
      });
   }

   public void menuTick(Player var1) {
      if (var1.isActive(PlayerUnlocks.INVENTORY_CRAFTING_3X3)) {
         this.adjustCraftingGridSlotsPosition(1, 85, 11);

         for(Slot var3 : this.getInputGridSlots()) {
            var3.setActive(true);
         }

         this.getResultSlot().setActive(true);
      } else if (var1.isActive(PlayerUnlocks.INVENTORY_CRAFTING)) {
         for(int var4 = 0; var4 < 3; ++var4) {
            for(int var6 = 0; var6 < 3; ++var6) {
               if (var6 < 2 && var4 < 2) {
                  this.getSlot(1 + var6 + var4 * 3).setActive(true);
               } else {
                  this.getSlot(1 + var6 + var4 * 3).setActive(false);
               }
            }
         }

         this.getResultSlot().setActive(true);
      } else {
         for(Slot var7 : this.getInputGridSlots()) {
            var7.setActive(false);
         }

         this.getResultSlot().setActive(false);
      }

   }

   public int getGridHeight(Player var1) {
      return var1.isActive(PlayerUnlocks.INVENTORY_CRAFTING_3X3) ? 3 : 2;
   }

   public int getGridWidth(Player var1) {
      return var1.isActive(PlayerUnlocks.INVENTORY_CRAFTING_3X3) ? 3 : 2;
   }

   public static boolean isHotbarSlot(int var0) {
      return var0 >= 41 && var0 < 50 || var0 == 50;
   }

   public void slotsChanged(Container var1) {
      Level var3 = this.owner.level();
      if (var3 instanceof ServerLevel var2) {
         CraftingMenu.slotChangedCraftingGrid(this, var2, this.owner, this.craftSlots, this.resultSlots, (RecipeHolder)null);
      }

   }

   public void removed(Player var1) {
      super.removed(var1);
      this.resultSlots.clearContent();
      if (!var1.level().isClientSide) {
         this.clearContainer(var1, this.craftSlots);
      }
   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         EquipmentSlot var6 = var1.getEquipmentSlotForItem(var3);
         if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 14, 50, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 >= 1 && var2 < 10) {
            if (!this.moveItemStackTo(var5, 14, 50, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 10 && var2 < 14) {
            if (!this.moveItemStackTo(var5, 14, 50, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !((Slot)this.slots.get(13 - var6.getIndex())).hasItem()) {
            int var7 = 13 - var6.getIndex();
            if (!this.owner.isActive(PlayerUnlocks.ARMAMENTS) || !this.moveItemStackTo(var5, var7, var7 + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6 == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(50)).hasItem()) {
            if (!this.owner.isActive(PlayerUnlocks.ARMAMENTS) || !this.moveItemStackTo(var5, 50, 51, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 14 && var2 < 41) {
            if (!this.moveItemStackTo(var5, 41, 50, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 41 && var2 < 50) {
            if (!this.moveItemStackTo(var5, 14, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 14, 50, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY, var3);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
         if (var2 == 0) {
            var1.drop(var5, false);
         }
      }

      return var3;
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   public Slot getResultSlot() {
      return this.slots.get(0);
   }

   public List<Slot> getInputGridSlots() {
      return this.slots.subList(1, 10);
   }

   public CraftingContainer getCraftSlots() {
      return this.craftSlots;
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   protected Player owner() {
      return this.owner;
   }

   static {
      TEXTURE_EMPTY_SLOTS = Map.of(EquipmentSlot.FEET, EMPTY_ARMOR_SLOT_BOOTS, EquipmentSlot.LEGS, EMPTY_ARMOR_SLOT_LEGGINGS, EquipmentSlot.CHEST, EMPTY_ARMOR_SLOT_CHESTPLATE, EquipmentSlot.HEAD, EMPTY_ARMOR_SLOT_HELMET);
      SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   }
}
