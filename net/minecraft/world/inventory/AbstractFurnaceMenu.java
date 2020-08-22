package net.minecraft.world.inventory;

import net.minecraft.recipebook.ServerPlaceSmeltingRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public abstract class AbstractFurnaceMenu extends RecipeBookMenu {
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType recipeType;

   protected AbstractFurnaceMenu(MenuType var1, RecipeType var2, int var3, Inventory var4) {
      this(var1, var2, var3, var4, new SimpleContainer(3), new SimpleContainerData(4));
   }

   protected AbstractFurnaceMenu(MenuType var1, RecipeType var2, int var3, Inventory var4, Container var5, ContainerData var6) {
      super(var1, var3);
      this.recipeType = var2;
      checkContainerSize(var5, 3);
      checkContainerDataCount(var6, 4);
      this.container = var5;
      this.data = var6;
      this.level = var4.player.level;
      this.addSlot(new Slot(var5, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, var5, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(var4.player, var5, 2, 116, 35));

      int var7;
      for(var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(var4, var8 + var7 * 9 + 9, 8 + var8 * 18, 84 + var7 * 18));
         }
      }

      for(var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(var4, var7, 8 + var7 * 18, 142));
      }

      this.addDataSlots(var6);
   }

   public void fillCraftSlotsStackedContents(StackedContents var1) {
      if (this.container instanceof StackedContentsCompatible) {
         ((StackedContentsCompatible)this.container).fillStackedContents(var1);
      }

   }

   public void clearCraftingContent() {
      this.container.clearContent();
   }

   public void handlePlacement(boolean var1, Recipe var2, ServerPlayer var3) {
      (new ServerPlaceSmeltingRecipe(this)).recipeClicked(var3, var2, var1);
   }

   public boolean recipeMatches(Recipe var1) {
      return var1.matches(this.container, this.level);
   }

   public int getResultSlotIndex() {
      return 2;
   }

   public int getGridWidth() {
      return 1;
   }

   public int getGridHeight() {
      return 1;
   }

   public int getSize() {
      return 3;
   }

   public boolean stillValid(Player var1) {
      return this.container.stillValid(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 2) {
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != 1 && var2 != 0) {
            if (this.canSmelt(var5)) {
               if (!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isFuel(var5)) {
               if (!this.moveItemStackTo(var5, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 3 && var2 < 30) {
               if (!this.moveItemStackTo(var5, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
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
      }

      return var3;
   }

   protected boolean canSmelt(ItemStack var1) {
      return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SimpleContainer(new ItemStack[]{var1}), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack var1) {
      return AbstractFurnaceBlockEntity.isFuel(var1);
   }

   public int getBurnProgress() {
      int var1 = this.data.get(2);
      int var2 = this.data.get(3);
      return var2 != 0 && var1 != 0 ? var1 * 24 / var2 : 0;
   }

   public int getLitProgress() {
      int var1 = this.data.get(1);
      if (var1 == 0) {
         var1 = 200;
      }

      return this.data.get(0) * 13 / var1;
   }

   public boolean isLit() {
      return this.data.get(0) > 0;
   }
}
