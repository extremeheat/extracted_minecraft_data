package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu extends ItemCombinerMenu {
   private final Level level;
   @Nullable
   private UpgradeRecipe selectedRecipe;
   private final List<UpgradeRecipe> recipes;

   public SmithingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.SMITHING, var1, var2, var3);
      this.level = var2.player.level;
      this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
   }

   protected boolean isValidBlock(BlockState var1) {
      return var1.is(Blocks.SMITHING_TABLE);
   }

   protected boolean mayPickup(Player var1, boolean var2) {
      return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
   }

   protected void onTake(Player var1, ItemStack var2) {
      var2.onCraftedBy(var1.level, var1, var2.getCount());
      this.resultSlots.awardUsedRecipes(var1);
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.access.execute((var0, var1x) -> {
         var0.levelEvent(1044, var1x, 0);
      });
   }

   private void shrinkStackInSlot(int var1) {
      ItemStack var2 = this.inputSlots.getItem(var1);
      var2.shrink(1);
      this.inputSlots.setItem(var1, var2);
   }

   public void createResult() {
      List var1 = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level);
      if (var1.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         this.selectedRecipe = (UpgradeRecipe)var1.get(0);
         ItemStack var2 = this.selectedRecipe.assemble(this.inputSlots);
         this.resultSlots.setRecipeUsed(this.selectedRecipe);
         this.resultSlots.setItem(0, var2);
      }

   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack var1) {
      return this.recipes.stream().anyMatch((var1x) -> {
         return var1x.isAdditionIngredient(var1);
      });
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }
}
