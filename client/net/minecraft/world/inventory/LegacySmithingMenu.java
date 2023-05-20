package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(
   forRemoval = true
)
public class LegacySmithingMenu extends ItemCombinerMenu {
   private final Level level;
   public static final int INPUT_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final int INPUT_SLOT_X_PLACEMENT = 27;
   private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
   private static final int RESULT_SLOT_X_PLACEMENT = 134;
   private static final int SLOT_Y_PLACEMENT = 47;
   @Nullable
   private LegacyUpgradeRecipe selectedRecipe;
   private final List<LegacyUpgradeRecipe> recipes;

   public LegacySmithingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public LegacySmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.LEGACY_SMITHING, var1, var2, var3);
      this.level = var2.player.level;
      this.recipes = this.level
         .getRecipeManager()
         .<Container, SmithingRecipe>getAllRecipesFor(RecipeType.SMITHING)
         .stream()
         .filter(var0 -> var0 instanceof LegacyUpgradeRecipe)
         .map(var0 -> (LegacyUpgradeRecipe)var0)
         .toList();
   }

   @Override
   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, var0 -> true).withSlot(1, 76, 47, var0 -> true).withResultSlot(2, 134, 47).build();
   }

   @Override
   protected boolean isValidBlock(BlockState var1) {
      return var1.is(Blocks.SMITHING_TABLE);
   }

   @Override
   protected boolean mayPickup(Player var1, boolean var2) {
      return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
   }

   @Override
   protected void onTake(Player var1, ItemStack var2) {
      var2.onCraftedBy(var1.level, var1, var2.getCount());
      this.resultSlots.awardUsedRecipes(var1);
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.access.execute((var0, var1x) -> var0.levelEvent(1044, var1x, 0));
   }

   private void shrinkStackInSlot(int var1) {
      ItemStack var2 = this.inputSlots.getItem(var1);
      var2.shrink(1);
      this.inputSlots.setItem(var1, var2);
   }

   @Override
   public void createResult() {
      List var1 = this.level
         .getRecipeManager()
         .getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level)
         .stream()
         .filter(var0 -> var0 instanceof LegacyUpgradeRecipe)
         .map(var0 -> (LegacyUpgradeRecipe)var0)
         .toList();
      if (var1.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         LegacyUpgradeRecipe var2 = (LegacyUpgradeRecipe)var1.get(0);
         ItemStack var3 = var2.assemble(this.inputSlots, this.level.registryAccess());
         if (var3.isItemEnabled(this.level.enabledFeatures())) {
            this.selectedRecipe = var2;
            this.resultSlots.setRecipeUsed(var2);
            this.resultSlots.setItem(0, var3);
         }
      }
   }

   @Override
   public int getSlotToQuickMoveTo(ItemStack var1) {
      return this.shouldQuickMoveToAdditionalSlot(var1) ? 1 : 0;
   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack var1) {
      return this.recipes.stream().anyMatch(var1x -> var1x.isAdditionIngredient(var1));
   }

   @Override
   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }
}
