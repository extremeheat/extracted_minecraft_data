package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public abstract class AbstractFurnaceMenu extends RecipeBookMenu {
   public static final int INGREDIENT_SLOT = 0;
   public static final int FUEL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   public static final int SLOT_COUNT = 3;
   public static final int DATA_COUNT = 4;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType<? extends AbstractCookingRecipe> recipeType;
   private final RecipePropertySet acceptedInputs;
   private final RecipeBookType recipeBookType;

   protected AbstractFurnaceMenu(
      MenuType<?> var1, RecipeType<? extends AbstractCookingRecipe> var2, ResourceKey<RecipePropertySet> var3, RecipeBookType var4, int var5, Inventory var6
   ) {
      this(var1, var2, var3, var4, var5, var6, new SimpleContainer(3), new SimpleContainerData(4));
   }

   protected AbstractFurnaceMenu(
      MenuType<?> var1,
      RecipeType<? extends AbstractCookingRecipe> var2,
      ResourceKey<RecipePropertySet> var3,
      RecipeBookType var4,
      int var5,
      Inventory var6,
      Container var7,
      ContainerData var8
   ) {
      super(var1, var5);
      this.recipeType = var2;
      this.recipeBookType = var4;
      checkContainerSize(var7, 3);
      checkContainerDataCount(var8, 4);
      this.container = var7;
      this.data = var8;
      this.level = var6.player.level();
      this.acceptedInputs = this.level.recipeAccess().propertySet(var3);
      this.addSlot(new Slot(var7, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, var7, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(var6.player, var7, 2, 116, 35));
      this.addStandardInventorySlots(var6, 8, 84);
      this.addDataSlots(var8);
   }

   @Override
   public void fillCraftSlotsStackedContents(StackedItemContents var1) {
      if (this.container instanceof StackedContentsCompatible) {
         ((StackedContentsCompatible)this.container).fillStackedContents(var1);
      }
   }

   public Slot getResultSlot() {
      return this.slots.get(2);
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
      return this.acceptedInputs.test(var1);
   }

   protected boolean isFuel(ItemStack var1) {
      return this.level.fuelValues().isFuel(var1);
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

   @Override
   public RecipeBookType getRecipeBookType() {
      return this.recipeBookType;
   }

   @Override
   public RecipeBookMenu.PostPlaceAction handlePlacement(boolean var1, boolean var2, RecipeHolder<?> var3, final ServerLevel var4, Inventory var5) {
      final List var6 = List.of(this.getSlot(0), this.getSlot(2));
      return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<AbstractCookingRecipe>() {
         @Override
         public void fillCraftSlotsStackedContents(StackedItemContents var1) {
            AbstractFurnaceMenu.this.fillCraftSlotsStackedContents(var1);
         }

         @Override
         public void clearCraftingContent() {
            var6.forEach(var0 -> var0.set(ItemStack.EMPTY));
         }

         @Override
         public boolean recipeMatches(RecipeHolder<AbstractCookingRecipe> var1) {
            return ((AbstractCookingRecipe)var1.value()).matches(new SingleRecipeInput(AbstractFurnaceMenu.this.container.getItem(0)), var4);
         }
      }, 1, 1, List.of(this.getSlot(0)), var6, var5, var3, var1, var2);
   }
}
