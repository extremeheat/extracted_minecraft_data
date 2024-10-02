package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
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

   public SmithingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      this(var1, var2, var3, var2.player.level());
   }

   private SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3, Level var4) {
      super(MenuType.SMITHING, var1, var2, var3, createInputSlotDefinitions(var4.recipeAccess()));
      this.level = var4;
      this.baseItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_BASE);
      this.templateItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      this.additionItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_ADDITION);
   }

   private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions(RecipeAccess var0) {
      RecipePropertySet var1 = var0.propertySet(RecipePropertySet.SMITHING_BASE);
      RecipePropertySet var2 = var0.propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      RecipePropertySet var3 = var0.propertySet(RecipePropertySet.SMITHING_ADDITION);
      return ItemCombinerMenuSlotDefinition.create()
         .withSlot(0, 8, 48, var2::test)
         .withSlot(1, 26, 48, var1::test)
         .withSlot(2, 44, 48, var3::test)
         .withResultSlot(3, 98, 48)
         .build();
   }

   @Override
   protected boolean isValidBlock(BlockState var1) {
      return var1.is(Blocks.SMITHING_TABLE);
   }

   @Override
   protected void onTake(Player var1, ItemStack var2) {
      var2.onCraftedBy(var1.level(), var1, var2.getCount());
      this.resultSlots.awardUsedRecipes(var1, this.getRelevantItems());
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.shrinkStackInSlot(2);
      this.access.execute((var0, var1x) -> var0.levelEvent(1044, var1x, 0));
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

   @Override
   public void createResult() {
      SmithingRecipeInput var1 = this.createRecipeInput();
      Optional var2;
      if (this.level instanceof ServerLevel var3) {
         var2 = var3.recipeAccess().getRecipeFor(RecipeType.SMITHING, var1, var3);
      } else {
         var2 = Optional.empty();
      }

      var2.ifPresentOrElse(var2x -> {
         ItemStack var3x = ((SmithingRecipe)var2x.value()).assemble(var1, this.level.registryAccess());
         this.resultSlots.setRecipeUsed((RecipeHolder<?>)var2x);
         this.resultSlots.setItem(0, var3x);
      }, () -> {
         this.resultSlots.setRecipeUsed(null);
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      });
   }

   @Override
   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   @Override
   public boolean canMoveIntoInputSlots(ItemStack var1) {
      if (this.templateItemTest.test(var1) && !this.getSlot(0).hasItem()) {
         return true;
      } else {
         return this.baseItemTest.test(var1) && !this.getSlot(1).hasItem() ? true : this.additionItemTest.test(var1) && !this.getSlot(2).hasItem();
      }
   }
}
