package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
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
   private final DataSlot selectedRecipeIndex = DataSlot.standalone();
   private final Level level;
   private List<StonecutterRecipe> recipes = Lists.newArrayList();
   private ItemStack input = ItemStack.EMPTY;
   long lastSoundTime;
   final Slot inputSlot;
   final Slot resultSlot;
   Runnable slotUpdateListener = () -> {
   };
   public final Container container = new SimpleContainer(1) {
      @Override
      public void setChanged() {
         super.setChanged();
         StonecutterMenu.this.slotsChanged(this);
         StonecutterMenu.this.slotUpdateListener.run();
      }
   };
   final ResultContainer resultContainer = new ResultContainer();

   public StonecutterMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public StonecutterMenu(int var1, Inventory var2, final ContainerLevelAccess var3) {
      super(MenuType.STONECUTTER, var1);
      this.access = var3;
      this.level = var2.player.level;
      this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
      this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
         @Override
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         @Override
         public void onTake(Player var1, ItemStack var2) {
            var2.onCraftedBy(var1.level, var1, var2.getCount());
            StonecutterMenu.this.resultContainer.awardUsedRecipes(var1);
            ItemStack var3x = StonecutterMenu.this.inputSlot.remove(1);
            if (!var3x.isEmpty()) {
               StonecutterMenu.this.setupResultSlot();
            }

            var3.execute((var1x, var2x) -> {
               long var3xxx = var1x.getGameTime();
               if (StonecutterMenu.this.lastSoundTime != var3xxx) {
                  var1x.playSound(null, var2x, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  StonecutterMenu.this.lastSoundTime = var3xxx;
               }
            });
            super.onTake(var1, var2);
         }
      });

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlot(new Slot(var2, var6, 8 + var6 * 18, 142));
      }

      this.addDataSlot(this.selectedRecipeIndex);
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public List<StonecutterRecipe> getRecipes() {
      return this.recipes;
   }

   public int getNumRecipes() {
      return this.recipes.size();
   }

   public boolean hasInputItem() {
      return this.inputSlot.hasItem() && !this.recipes.isEmpty();
   }

   @Override
   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.STONECUTTER);
   }

   @Override
   public boolean clickMenuButton(Player var1, int var2) {
      if (this.isValidRecipeIndex(var2)) {
         this.selectedRecipeIndex.set(var2);
         this.setupResultSlot();
      }

      return true;
   }

   private boolean isValidRecipeIndex(int var1) {
      return var1 >= 0 && var1 < this.recipes.size();
   }

   @Override
   public void slotsChanged(Container var1) {
      ItemStack var2 = this.inputSlot.getItem();
      if (!var2.is(this.input.getItem())) {
         this.input = var2.copy();
         this.setupRecipeList(var1, var2);
      }
   }

   private void setupRecipeList(Container var1, ItemStack var2) {
      this.recipes.clear();
      this.selectedRecipeIndex.set(-1);
      this.resultSlot.set(ItemStack.EMPTY);
      if (!var2.isEmpty()) {
         this.recipes = this.level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, var1, this.level);
      }
   }

   void setupResultSlot() {
      if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
         StonecutterRecipe var1 = this.recipes.get(this.selectedRecipeIndex.get());
         ItemStack var2 = var1.assemble(this.container, this.level.registryAccess());
         if (var2.isItemEnabled(this.level.enabledFeatures())) {
            this.resultContainer.setRecipeUsed(var1);
            this.resultSlot.set(var2);
         } else {
            this.resultSlot.set(ItemStack.EMPTY);
         }
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
      }

      this.broadcastChanges();
   }

   @Override
   public MenuType<?> getType() {
      return MenuType.STONECUTTER;
   }

   public void registerUpdateListener(Runnable var1) {
      this.slotUpdateListener = var1;
   }

   @Override
   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultContainer && super.canTakeItemForPickAll(var1, var2);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         Item var6 = var5.getItem();
         var3 = var5.copy();
         if (var2 == 1) {
            var6.onCraftedBy(var5, var1.level, var1);
            if (!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.level.getRecipeManager().getRecipeFor(RecipeType.STONECUTTING, new SimpleContainer(var5), this.level).isPresent()) {
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
         this.broadcastChanges();
      }

      return var3;
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.resultContainer.removeItemNoUpdate(1);
      this.access.execute((var2, var3) -> this.clearContainer(var1, this.container));
   }
}
