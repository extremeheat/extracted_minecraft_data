package net.minecraft.world.inventory;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public abstract class AbstractFurnaceMenu extends RecipeBookMenu<SingleRecipeInput, AbstractCookingRecipe> {
   public static final int INGREDIENT_SLOT = 0;
   public static final int FUEL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   public static final int SLOT_COUNT = 3;
   public static final int DATA_COUNT = 4;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType<? extends AbstractCookingRecipe> recipeType;
   private final RecipeBookType recipeBookType;

   protected AbstractFurnaceMenu(MenuType<?> var1, RecipeType<? extends AbstractCookingRecipe> var2, RecipeBookType var3, int var4, Inventory var5) {
      this(var1, var2, var3, var4, var5, new SimpleContainer(3), new SimpleContainerData(4));
   }

   protected AbstractFurnaceMenu(MenuType<?> var1, RecipeType<? extends AbstractCookingRecipe> var2, RecipeBookType var3, int var4, Inventory var5, Container var6, ContainerData var7) {
      super(var1, var4);
      this.recipeType = var2;
      this.recipeBookType = var3;
      checkContainerSize(var6, 3);
      checkContainerDataCount(var7, 4);
      this.container = var6;
      this.data = var7;
      this.level = var5.player.level();
      this.addSlot(new Slot(var6, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, var6, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(var5.player, var6, 2, 116, 35));

      int var8;
      for(var8 = 0; var8 < 3; ++var8) {
         for(int var9 = 0; var9 < 9; ++var9) {
            this.addSlot(new Slot(var5, var9 + var8 * 9 + 9, 8 + var9 * 18, 84 + var8 * 18));
         }
      }

      for(var8 = 0; var8 < 9; ++var8) {
         this.addSlot(new Slot(var5, var8, 8 + var8 * 18, 142));
      }

      this.addDataSlots(var7);
   }

   public void fillCraftSlotsStackedContents(StackedContents var1) {
      if (this.container instanceof StackedContentsCompatible) {
         ((StackedContentsCompatible)this.container).fillStackedContents(var1);
      }

   }

   public void clearCraftingContent() {
      this.getSlot(0).set(ItemStack.EMPTY);
      this.getSlot(2).set(ItemStack.EMPTY);
   }

   public boolean recipeMatches(RecipeHolder<AbstractCookingRecipe> var1) {
      return ((AbstractCookingRecipe)var1.value()).matches(new SingleRecipeInput(this.container.getItem(0)), this.level);
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

   protected boolean canSmelt(ItemStack var1) {
      return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SingleRecipeInput(var1), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack var1) {
      return AbstractFurnaceBlockEntity.isFuel(var1);
   }

   public float getBurnProgress() {
      int var1 = this.data.get(2);
      int var2 = this.data.get(3);
      return var2 != 0 && var1 != 0 ? Mth.clamp((float)var1 / (float)var2, 0.0F, 1.0F) : 0.0F;
   }

   public float getLitProgress() {
      int var1 = this.data.get(1);
      if (var1 == 0) {
         var1 = 200;
      }

      return Mth.clamp((float)this.data.get(0) / (float)var1, 0.0F, 1.0F);
   }

   public boolean isLit() {
      return this.data.get(0) > 0;
   }

   public RecipeBookType getRecipeBookType() {
      return this.recipeBookType;
   }

   public boolean shouldMoveToInventory(int var1) {
      return var1 != 1;
   }
}
