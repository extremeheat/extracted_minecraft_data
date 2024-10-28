package net.minecraft.world.inventory;

import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu extends ItemCombinerMenu {
   public static final int TEMPLATE_SLOT = 0;
   public static final int BASE_SLOT = 1;
   public static final int ADDITIONAL_SLOT = 2;
   public static final int RESULT_SLOT = 3;
   public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
   public static final int BASE_SLOT_X_PLACEMENT = 26;
   public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
   private static final int RESULT_SLOT_X_PLACEMENT = 98;
   public static final int SLOT_Y_PLACEMENT = 48;
   private final Level level;
   @Nullable
   private RecipeHolder<SmithingRecipe> selectedRecipe;
   private final List<RecipeHolder<SmithingRecipe>> recipes;

   public SmithingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.SMITHING, var1, var2, var3);
      this.level = var2.player.level();
      this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
   }

   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create().withSlot(0, 8, 48, (var1) -> {
         return this.recipes.stream().anyMatch((var1x) -> {
            return ((SmithingRecipe)var1x.value()).isTemplateIngredient(var1);
         });
      }).withSlot(1, 26, 48, (var1) -> {
         return this.recipes.stream().anyMatch((var1x) -> {
            return ((SmithingRecipe)var1x.value()).isBaseIngredient(var1);
         });
      }).withSlot(2, 44, 48, (var1) -> {
         return this.recipes.stream().anyMatch((var1x) -> {
            return ((SmithingRecipe)var1x.value()).isAdditionIngredient(var1);
         });
      }).withResultSlot(3, 98, 48).build();
   }

   protected boolean isValidBlock(BlockState var1) {
      return var1.is(Blocks.SMITHING_TABLE);
   }

   protected boolean mayPickup(Player var1, boolean var2) {
      return this.selectedRecipe != null && ((SmithingRecipe)this.selectedRecipe.value()).matches(this.createRecipeInput(), this.level);
   }

   protected void onTake(Player var1, ItemStack var2) {
      var2.onCraftedBy(var1.level(), var1, var2.getCount());
      this.resultSlots.awardUsedRecipes(var1, this.getRelevantItems());
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.shrinkStackInSlot(2);
      this.access.execute((var0, var1x) -> {
         var0.levelEvent(1044, var1x, 0);
      });
   }

   private List<ItemStack> getRelevantItems() {
      return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
   }

   private SmithingRecipeInput createRecipeInput() {
      return new SmithingRecipeInput(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
   }

   private void shrinkStackInSlot(int var1) {
      ItemStack var2 = this.inputSlots.getItem(var1);
      if (!var2.isEmpty()) {
         var2.shrink(1);
         this.inputSlots.setItem(var1, var2);
      }

   }

   public void createResult() {
      SmithingRecipeInput var1 = this.createRecipeInput();
      List var2 = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, var1, this.level);
      if (var2.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         RecipeHolder var3 = (RecipeHolder)var2.get(0);
         ItemStack var4 = ((SmithingRecipe)var3.value()).assemble(var1, this.level.registryAccess());
         if (var4.isItemEnabled(this.level.enabledFeatures())) {
            this.selectedRecipe = var3;
            this.resultSlots.setRecipeUsed(var3);
            this.resultSlots.setItem(0, var4);
         }
      }

   }

   public int getSlotToQuickMoveTo(ItemStack var1) {
      return this.findSlotToQuickMoveTo(var1).orElse(0);
   }

   private static OptionalInt findSlotMatchingIngredient(SmithingRecipe var0, ItemStack var1) {
      if (var0.isTemplateIngredient(var1)) {
         return OptionalInt.of(0);
      } else if (var0.isBaseIngredient(var1)) {
         return OptionalInt.of(1);
      } else {
         return var0.isAdditionIngredient(var1) ? OptionalInt.of(2) : OptionalInt.empty();
      }
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   public boolean canMoveIntoInputSlots(ItemStack var1) {
      return this.findSlotToQuickMoveTo(var1).isPresent();
   }

   private OptionalInt findSlotToQuickMoveTo(ItemStack var1) {
      return this.recipes.stream().flatMapToInt((var1x) -> {
         return findSlotMatchingIngredient((SmithingRecipe)var1x.value(), var1).stream();
      }).filter((var1x) -> {
         return !this.getSlot(var1x).hasItem();
      }).findFirst();
   }
}
