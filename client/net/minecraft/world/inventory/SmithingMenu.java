package net.minecraft.world.inventory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
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
   private final RecipePropertySet baseItemTest;
   private final RecipePropertySet templateItemTest;
   private final RecipePropertySet additionItemTest;
   private final DataSlot hasRecipeError;

   public SmithingMenu(final int containerId, final Inventory inventory) {
      this(containerId, inventory, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access) {
      this(containerId, inventory, access, inventory.player.level());
   }

   private SmithingMenu(final int containerId, final Inventory inventory, final ContainerLevelAccess access, final Level level) {
      super(MenuType.SMITHING, containerId, inventory, access, createInputSlotDefinitions(level.recipeAccess()));
      this.hasRecipeError = DataSlot.standalone();
      this.level = level;
      this.baseItemTest = level.recipeAccess().propertySet(RecipePropertySet.SMITHING_BASE);
      this.templateItemTest = level.recipeAccess().propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      this.additionItemTest = level.recipeAccess().propertySet(RecipePropertySet.SMITHING_ADDITION);
      this.addDataSlot(this.hasRecipeError).set(0);
   }

   private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions(final RecipeAccess recipes) {
      RecipePropertySet baseItemTest = recipes.propertySet(RecipePropertySet.SMITHING_BASE);
      RecipePropertySet templateItemTest = recipes.propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      RecipePropertySet additionItemTest = recipes.propertySet(RecipePropertySet.SMITHING_ADDITION);
      ItemCombinerMenuSlotDefinition.Builder var10000 = ItemCombinerMenuSlotDefinition.create();
      Objects.requireNonNull(templateItemTest);
      var10000 = var10000.withSlot(0, 8, 48, templateItemTest::test);
      Objects.requireNonNull(baseItemTest);
      var10000 = var10000.withSlot(1, 26, 48, baseItemTest::test);
      Objects.requireNonNull(additionItemTest);
      return var10000.withSlot(2, 44, 48, additionItemTest::test).withResultSlot(3, 98, 48).build();
   }

   protected boolean isValidBlock(final BlockState state) {
      return state.is(Blocks.SMITHING_TABLE);
   }

   protected void onTake(final Player player, final ItemStack carried) {
      carried.onCraftedBy(player, carried.getCount());
      this.resultSlots.awardUsedRecipes(player, this.getRelevantItems());
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.shrinkStackInSlot(2);
      this.access.execute((level, pos) -> level.levelEvent(1044, pos, 0));
   }

   private List<ItemStack> getRelevantItems() {
      return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
   }

   private SmithingRecipeInput createRecipeInput() {
      return new SmithingRecipeInput(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
   }

   private void shrinkStackInSlot(final int slot) {
      ItemStack stack = this.inputSlots.getItem(slot);
      if (!stack.isEmpty()) {
         stack.shrink(1);
         this.inputSlots.setItem(slot, stack);
      }

   }

   public void slotsChanged(final Container container) {
      super.slotsChanged(container);
      if (this.level instanceof ServerLevel) {
         boolean hasRecipeError = this.getSlot(0).hasItem() && this.getSlot(1).hasItem() && this.getSlot(2).hasItem() && !this.getSlot(this.getResultSlot()).hasItem();
         this.hasRecipeError.set(hasRecipeError ? 1 : 0);
      }

   }

   public void createResult() {
      SmithingRecipeInput input = this.createRecipeInput();
      Level var4 = this.level;
      Optional<RecipeHolder<SmithingRecipe>> foundRecipe;
      if (var4 instanceof ServerLevel serverLevel) {
         foundRecipe = serverLevel.recipeAccess().getRecipeFor(RecipeType.SMITHING, input, serverLevel);
      } else {
         foundRecipe = Optional.empty();
      }

      foundRecipe.ifPresentOrElse((recipe) -> {
         ItemStack result = ((SmithingRecipe)recipe.value()).assemble(input);
         this.resultSlots.setRecipeUsed(recipe);
         this.resultSlots.setItem(0, result);
      }, () -> {
         this.resultSlots.setRecipeUsed((RecipeHolder)null);
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      });
   }

   public boolean canTakeItemForPickAll(final ItemStack carried, final Slot target) {
      return target.container != this.resultSlots && super.canTakeItemForPickAll(carried, target);
   }

   public boolean canMoveIntoInputSlots(final ItemStack stack) {
      if (this.templateItemTest.test(stack) && !this.getSlot(0).hasItem()) {
         return true;
      } else if (this.baseItemTest.test(stack) && !this.getSlot(1).hasItem()) {
         return true;
      } else {
         return this.additionItemTest.test(stack) && !this.getSlot(2).hasItem();
      }
   }

   public boolean hasRecipeError() {
      return this.hasRecipeError.get() > 0;
   }
}
