package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterMenu extends AbstractContainerMenu {
   public static final int INPUT_SLOT = 0;
   public static final int RESULT_SLOT = 1;
   private static final int INV_SLOT_START = 2;
   private static final int INV_SLOT_END = 29;
   private static final int USE_ROW_SLOT_START = 29;
   private static final int USE_ROW_SLOT_END = 38;
   private final ContainerLevelAccess access;
   final DataSlot selectedRecipeIndex;
   private final Level level;
   private SelectableRecipe.SingleInputSet<StonecutterRecipe> recipesForInput;
   private ItemStack input;
   long lastSoundTime;
   final Slot inputSlot;
   final Slot resultSlot;
   Runnable slotUpdateListener;
   public final Container container;
   final ResultContainer resultContainer;

   public StonecutterMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public StonecutterMenu(int var1, Inventory var2, final ContainerLevelAccess var3) {
      super(MenuType.STONECUTTER, var1);
      this.selectedRecipeIndex = DataSlot.standalone();
      this.recipesForInput = SelectableRecipe.SingleInputSet.empty();
      this.input = ItemStack.EMPTY;
      this.slotUpdateListener = () -> {
      };
      this.container = new SimpleContainer(1) {
         public void setChanged() {
            super.setChanged();
            StonecutterMenu.this.slotsChanged(this);
            StonecutterMenu.this.slotUpdateListener.run();
         }
      };
      this.resultContainer = new ResultContainer();
      this.access = var3;
      this.level = var2.player.level();
      this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
      this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public void onTake(Player var1, ItemStack var2) {
            var2.onCraftedBy(var1.level(), var1, var2.getCount());
            StonecutterMenu.this.resultContainer.awardUsedRecipes(var1, this.getRelevantItems());
            ItemStack var3x = StonecutterMenu.this.inputSlot.remove(1);
            if (!var3x.isEmpty()) {
               StonecutterMenu.this.setupResultSlot(StonecutterMenu.this.selectedRecipeIndex.get());
            }

            var3.execute((var1x, var2x) -> {
               long var3x = var1x.getGameTime();
               if (StonecutterMenu.this.lastSoundTime != var3x) {
                  var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  StonecutterMenu.this.lastSoundTime = var3x;
               }

            });
            super.onTake(var1, var2);
         }

         private List<ItemStack> getRelevantItems() {
            return List.of(StonecutterMenu.this.inputSlot.getItem());
         }
      });
      this.addStandardInventorySlots(var2, 8, 84);
      this.addDataSlot(this.selectedRecipeIndex);
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> getVisibleRecipes() {
      return this.recipesForInput;
   }

   public int getNumberOfVisibleRecipes() {
      return this.recipesForInput.size();
   }

   public boolean hasInputItem() {
      return this.inputSlot.hasItem() && !this.recipesForInput.isEmpty();
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.STONECUTTER);
   }

   public boolean clickMenuButton(Player var1, int var2) {
      if (this.isValidRecipeIndex(var2)) {
         this.selectedRecipeIndex.set(var2);
         this.setupResultSlot(var2);
      }

      return true;
   }

   private boolean isValidRecipeIndex(int var1) {
      return var1 >= 0 && var1 < this.recipesForInput.size();
   }

   public void slotsChanged(Container var1) {
      ItemStack var2 = this.inputSlot.getItem();
      if (!var2.is(this.input.getItem())) {
         this.input = var2.copy();
         this.setupRecipeList(var2);
      }

   }

   private void setupRecipeList(ItemStack var1) {
      this.selectedRecipeIndex.set(-1);
      this.resultSlot.set(ItemStack.EMPTY);
      if (!var1.isEmpty()) {
         this.recipesForInput = this.level.recipeAccess().stonecutterRecipes().selectByInput(var1);
      } else {
         this.recipesForInput = SelectableRecipe.SingleInputSet.empty();
      }

   }

   void setupResultSlot(int var1) {
      Optional var2;
      if (!this.recipesForInput.isEmpty() && this.isValidRecipeIndex(var1)) {
         SelectableRecipe.SingleInputEntry var3 = (SelectableRecipe.SingleInputEntry)this.recipesForInput.entries().get(var1);
         var2 = var3.recipe().recipe();
      } else {
         var2 = Optional.empty();
      }

      var2.ifPresentOrElse((var1x) -> {
         this.resultContainer.setRecipeUsed(var1x);
         this.resultSlot.set(((StonecutterRecipe)var1x.value()).assemble(new SingleRecipeInput(this.container.getItem(0)), this.level.registryAccess()));
      }, () -> {
         this.resultSlot.set(ItemStack.EMPTY);
         this.resultContainer.setRecipeUsed((RecipeHolder)null);
      });
      this.broadcastChanges();
   }

   public MenuType<?> getType() {
      return MenuType.STONECUTTER;
   }

   public void registerUpdateListener(Runnable var1) {
      this.slotUpdateListener = var1;
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultContainer && super.canTakeItemForPickAll(var1, var2);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         Item var6 = var5.getItem();
         var3 = var5.copy();
         if (var2 == 1) {
            var6.onCraftedBy(var5, var1.level(), var1);
            if (!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.level.recipeAccess().stonecutterRecipes().acceptsInput(var5)) {
            if (!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 2 && var2 < 29) {
            if (!this.moveItemStackTo(var5, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 29 && var2 < 38 && !this.moveItemStackTo(var5, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         }

         var4.setChanged();
         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
         if (var2 == 1) {
            var1.drop(var5, false);
         }

         this.broadcastChanges();
      }

      return var3;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.resultContainer.removeItemNoUpdate(1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.container);
      });
   }
}
