package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class InventoryMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
   public static final int CONTAINER_ID = 0;
   public static final int RESULT_SLOT = 0;
   public static final int CRAFT_SLOT_START = 1;
   public static final int CRAFT_SLOT_COUNT = 4;
   public static final int CRAFT_SLOT_END = 5;
   public static final int ARMOR_SLOT_START = 5;
   public static final int ARMOR_SLOT_COUNT = 4;
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
   private static final Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS = Map.of(
      EquipmentSlot.FEET,
      EMPTY_ARMOR_SLOT_BOOTS,
      EquipmentSlot.LEGS,
      EMPTY_ARMOR_SLOT_LEGGINGS,
      EquipmentSlot.CHEST,
      EMPTY_ARMOR_SLOT_CHESTPLATE,
      EquipmentSlot.HEAD,
      EMPTY_ARMOR_SLOT_HELMET
   );
   private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
   private final ResultContainer resultSlots = new ResultContainer();
   public final boolean active;
   private final Player owner;

   public InventoryMenu(Inventory var1, boolean var2, final Player var3) {
      super(null, 0);
      this.active = var2;
      this.owner = var3;
      this.addSlot(new ResultSlot(var1.player, this.craftSlots, this.resultSlots, 0, 154, 28));

      for (int var4 = 0; var4 < 2; var4++) {
         for (int var5 = 0; var5 < 2; var5++) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 2, 98 + var5 * 18, 18 + var4 * 18));
         }
      }

      for (int var7 = 0; var7 < 4; var7++) {
         EquipmentSlot var10 = SLOT_IDS[var7];
         ResourceLocation var6 = TEXTURE_EMPTY_SLOTS.get(var10);
         this.addSlot(new ArmorSlot(var1, var3, var10, 39 - var7, 8, 8 + var7 * 18, var6));
      }

      for (int var8 = 0; var8 < 3; var8++) {
         for (int var11 = 0; var11 < 9; var11++) {
            this.addSlot(new Slot(var1, var11 + (var8 + 1) * 9, 8 + var11 * 18, 84 + var8 * 18));
         }
      }

      for (int var9 = 0; var9 < 9; var9++) {
         this.addSlot(new Slot(var1, var9, 8 + var9 * 18, 142));
      }

      this.addSlot(new Slot(var1, 40, 77, 62) {
         @Override
         public void setByPlayer(ItemStack var1, ItemStack var2) {
            var3.onEquipItem(EquipmentSlot.OFFHAND, var2, var1);
            super.setByPlayer(var1, var2);
         }

         @Override
         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
         }
      });
   }

   public static boolean isHotbarSlot(int var0) {
      return var0 >= 36 && var0 < 45 || var0 == 45;
   }

   @Override
   public void fillCraftSlotsStackedContents(StackedContents var1) {
      this.craftSlots.fillStackedContents(var1);
   }

   @Override
   public void clearCraftingContent() {
      this.resultSlots.clearContent();
      this.craftSlots.clearContent();
   }

   @Override
   public boolean recipeMatches(RecipeHolder<CraftingRecipe> var1) {
      return ((CraftingRecipe)var1.value()).matches(this.craftSlots.asCraftInput(), this.owner.level());
   }

   @Override
   public void slotsChanged(Container var1) {
      CraftingMenu.slotChangedCraftingGrid(this, this.owner.level(), this.owner, this.craftSlots, this.resultSlots, null);
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.resultSlots.clearContent();
      if (!var1.level().isClientSide) {
         this.clearContainer(var1, this.craftSlots);
      }
   }

   @Override
   public boolean stillValid(Player var1) {
      return true;
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4.hasItem()) {
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
         } else if (var6.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - var6.getIndex()).hasItem()) {
            int var7 = 8 - var6.getIndex();
            if (!this.moveItemStackTo(var5, var7, var7 + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var6 == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
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

   @Override
   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   @Override
   public int getResultSlotIndex() {
      return 0;
   }

   @Override
   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   @Override
   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   @Override
   public int getSize() {
      return 5;
   }

   public CraftingContainer getCraftSlots() {
      return this.craftSlots;
   }

   @Override
   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   @Override
   public boolean shouldMoveToInventory(int var1) {
      return var1 != this.getResultSlotIndex();
   }
}
