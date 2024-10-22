package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ResultSlot extends Slot {
   private final CraftingContainer craftSlots;
   private final Player player;
   private int removeCount;

   public ResultSlot(Player var1, CraftingContainer var2, Container var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.player = var1;
      this.craftSlots = var2;
   }

   @Override
   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount = this.removeCount + Math.min(var1, this.getItem().getCount());
      }

      return super.remove(var1);
   }

   @Override
   protected void onQuickCraft(ItemStack var1, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(var1);
   }

   @Override
   protected void onSwapCraft(int var1) {
      this.removeCount += var1;
   }

   @Override
   protected void checkTakeAchievements(ItemStack var1) {
      if (this.removeCount > 0) {
         var1.onCraftedBy(this.player.level(), this.player, this.removeCount);
      }

      if (this.container instanceof RecipeCraftingHolder var2) {
         var2.awardUsedRecipes(this.player, this.craftSlots.getItems());
      }

      this.removeCount = 0;
   }

   private static NonNullList<ItemStack> copyAllInputItems(CraftingInput var0) {
      NonNullList var1 = NonNullList.withSize(var0.size(), ItemStack.EMPTY);

      for (int var2 = 0; var2 < var1.size(); var2++) {
         var1.set(var2, var0.getItem(var2));
      }

      return var1;
   }

   private NonNullList<ItemStack> getRemainingItems(CraftingInput var1, Level var2) {
      return var2 instanceof ServerLevel var3
         ? var3.recipeAccess()
            .getRecipeFor(RecipeType.CRAFTING, var1, var3)
            .map(var1x -> var1x.value().getRemainingItems(var1))
            .orElseGet(() -> copyAllInputItems(var1))
         : CraftingRecipe.defaultCraftingReminder(var1);
   }

   @Override
   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      CraftingInput.Positioned var3 = this.craftSlots.asPositionedCraftInput();
      CraftingInput var4 = var3.input();
      int var5 = var3.left();
      int var6 = var3.top();
      NonNullList var7 = this.getRemainingItems(var4, var1.level());

      for (int var8 = 0; var8 < var4.height(); var8++) {
         for (int var9 = 0; var9 < var4.width(); var9++) {
            int var10 = var9 + var5 + (var8 + var6) * this.craftSlots.getWidth();
            ItemStack var11 = this.craftSlots.getItem(var10);
            ItemStack var12 = (ItemStack)var7.get(var9 + var8 * var4.width());
            if (!var11.isEmpty()) {
               this.craftSlots.removeItem(var10, 1);
               var11 = this.craftSlots.getItem(var10);
            }

            if (!var12.isEmpty()) {
               if (var11.isEmpty()) {
                  this.craftSlots.setItem(var10, var12);
               } else if (ItemStack.isSameItemSameComponents(var11, var12)) {
                  var12.grow(var11.getCount());
                  this.craftSlots.setItem(var10, var12);
               } else if (!this.player.getInventory().add(var12)) {
                  this.player.drop(var12, false);
               }
            }
         }
      }
   }

   @Override
   public boolean isFake() {
      return true;
   }
}
