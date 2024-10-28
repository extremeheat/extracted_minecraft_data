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

   public SmithingMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      this(var1, var2, var3, var2.player.level());
   }

   private SmithingMenu(int var1, Inventory var2, ContainerLevelAccess var3, Level var4) {
      super(MenuType.SMITHING, var1, var2, var3, createInputSlotDefinitions(var4.recipeAccess()));
      this.hasRecipeError = DataSlot.standalone();
      this.level = var4;
      this.baseItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_BASE);
      this.templateItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      this.additionItemTest = var4.recipeAccess().propertySet(RecipePropertySet.SMITHING_ADDITION);
      this.addDataSlot(this.hasRecipeError).set(0);
   }

   private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions(RecipeAccess var0) {
      RecipePropertySet var1 = var0.propertySet(RecipePropertySet.SMITHING_BASE);
      RecipePropertySet var2 = var0.propertySet(RecipePropertySet.SMITHING_TEMPLATE);
      RecipePropertySet var3 = var0.propertySet(RecipePropertySet.SMITHING_ADDITION);
      ItemCombinerMenuSlotDefinition.Builder var10000 = ItemCombinerMenuSlotDefinition.create();
      Objects.requireNonNull(var2);
      var10000 = var10000.withSlot(0, 8, 48, var2::test);
      Objects.requireNonNull(var1);
      var10000 = var10000.withSlot(1, 26, 48, var1::test);
      Objects.requireNonNull(var3);
      return var10000.withSlot(2, 44, 48, var3::test).withResultSlot(3, 98, 48).build();
   }

   protected boolean isValidBlock(BlockState var1) {
      return var1.is(Blocks.SMITHING_TABLE);
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

   public void slotsChanged(Container var1) {
      super.slotsChanged(var1);
      if (this.level instanceof ServerLevel) {
         boolean var2 = this.getSlot(0).hasItem() && this.getSlot(1).hasItem() && this.getSlot(2).hasItem() && !this.getSlot(this.getResultSlot()).hasItem();
         this.hasRecipeError.set(var2 ? 1 : 0);
      }

   }

   public void createResult() {
      SmithingRecipeInput var1 = this.createRecipeInput();
      Level var4 = this.level;
      Optional var2;
      if (var4 instanceof ServerLevel var3) {
         var2 = var3.recipeAccess().getRecipeFor(RecipeType.SMITHING, var1, var3);
      } else {
         var2 = Optional.empty();
      }

      var2.ifPresentOrElse((var2x) -> {
         ItemStack var3 = ((SmithingRecipe)var2x.value()).assemble(var1, this.level.registryAccess());
         this.resultSlots.setRecipeUsed(var2x);
         this.resultSlots.setItem(0, var3);
      }, () -> {
         this.resultSlots.setRecipeUsed((RecipeHolder)null);
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      });
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultSlots && super.canTakeItemForPickAll(var1, var2);
   }

   public boolean canMoveIntoInputSlots(ItemStack var1) {
      if (this.templateItemTest.test(var1) && !this.getSlot(0).hasItem()) {
         return true;
      } else if (this.baseItemTest.test(var1) && !this.getSlot(1).hasItem()) {
         return true;
      } else {
         return this.additionItemTest.test(var1) && !this.getSlot(2).hasItem();
      }
   }

   public boolean hasRecipeError() {
      return this.hasRecipeError.get() > 0;
   }
}
