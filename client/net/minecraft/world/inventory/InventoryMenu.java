package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class InventoryMenu extends RecipeBookMenu<CraftingContainer> {
   public static final int CONTAINER_ID = 0;
   public static final int RESULT_SLOT = 0;
   public static final int CRAFT_SLOT_START = 1;
   public static final int CRAFT_SLOT_END = 5;
   public static final int ARMOR_SLOT_START = 5;
   public static final int ARMOR_SLOT_END = 9;
   public static final int INV_SLOT_START = 9;
   public static final int INV_SLOT_END = 36;
   public static final int USE_ROW_SLOT_START = 36;
   public static final int USE_ROW_SLOT_END = 45;
   public static final int SHIELD_SLOT = 45;
   public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
   static final ResourceLocation[] TEXTURE_EMPTY_SLOTS;
   private static final EquipmentSlot[] SLOT_IDS;
   private final CraftingContainer craftSlots = new CraftingContainer(this, 2, 2);
   private final ResultContainer resultSlots = new ResultContainer();
   public final boolean active;
   private final Player owner;

   public InventoryMenu(Inventory var1, boolean var2, final Player var3) {
      super((MenuType)null, 0);
      this.active = var2;
      this.owner = var3;
      this.addSlot(new ResultSlot(var1.player, this.craftSlots, this.resultSlots, 0, 154, 28));

      int var4;
      int var5;
      for(var4 = 0; var4 < 2; ++var4) {
         for(var5 = 0; var5 < 2; ++var5) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 2, 98 + var5 * 18, 18 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 4; ++var4) {
         final EquipmentSlot var6 = SLOT_IDS[var4];
         this.addSlot(new Slot(var1, 39 - var4, 8, 8 + var4 * 18) {
            public void set(ItemStack var1) {
               ItemStack var2 = this.getItem();
               super.set(var1);
               var3.onEquipItem(var6, var2, var1);
            }

            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack var1) {
               return var6 == Mob.getEquipmentSlotForItem(var1);
            }

            public boolean mayPickup(Player var1) {
               ItemStack var2 = this.getItem();
               return !var2.isEmpty() && !var1.isCreative() && EnchantmentHelper.hasBindingCurse(var2) ? false : super.mayPickup(var1);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.TEXTURE_EMPTY_SLOTS[var6.getIndex()]);
            }
         });
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var1, var5 + (var4 + 1) * 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var1, var4, 8 + var4 * 18, 142));
      }

      this.addSlot(new Slot(var1, 40, 77, 62) {
         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
         }
      });
   }

   public static boolean isHotbarSlot(int var0) {
      return var0 >= 36 && var0 < 45 || var0 == 45;
   }

   public void fillCraftSlotsStackedContents(StackedContents var1) {
      this.craftSlots.fillStackedContents(var1);
   }

   public void clearCraftingContent() {
      this.resultSlots.clearContent();
      this.craftSlots.clearContent();
   }

   public boolean recipeMatches(Recipe<? super CraftingContainer> var1) {
      return var1.matches(this.craftSlots, this.owner.level);
   }

   public void slotsChanged(Container var1) {
      CraftingMenu.slotChangedCraftingGrid(this, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.resultSlots.clearContent();
      if (!var1.level.isClientSide) {
         this.clearContainer(var1, this.craftSlots);
      }
   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         EquipmentSlot var6 = Mob.getEquipmentSlotForItem(var3);
         if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 >= 1 && var2 < 5) {
            if (!this.moveItemStackTo(var5, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 5 && var2 < 9) {
            if (!this.moveItemStackTo(var5, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - var6.getIndex())).hasItem()) {
            int var7 = 8 - var6.getIndex();
            if (!this.moveItemStackTo(var5, var7, var7 + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6 == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasItem()) {
            if (!this.moveItemStackTo(var5, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 9 && var2 < 36) {
            if (!this.moveItemStackTo(var5, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 36 && var2 < 45) {
            if (!this.moveItemStackTo(var5, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
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

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   public int getSize() {
      return 5;
   }

   public CraftingContainer getCraftSlots() {
      return this.craftSlots;
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   public boolean shouldMoveToInventory(int var1) {
      return var1 != this.getResultSlotIndex();
   }

   static {
      TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
      SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   }
}
