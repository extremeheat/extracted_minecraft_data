package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ResultSlot extends Slot {
   private final CraftingContainer craftSlots;
   private final Player player;
   private int removeCount;

   public ResultSlot(Player var1, CraftingContainer var2, Container var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.player = var1;
      this.craftSlots = var2;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount += Math.min(var1, this.getItem().getCount());
      }

      return super.remove(var1);
   }

   protected void onQuickCraft(ItemStack var1, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(var1);
   }

   protected void onSwapCraft(int var1) {
      this.removeCount += var1;
   }

   protected void checkTakeAchievements(ItemStack var1) {
      if (this.removeCount > 0) {
         var1.onCraftedBy(this.player.level, this.player, this.removeCount);
      }

      if (this.container instanceof RecipeHolder) {
         ((RecipeHolder)this.container).awardUsedRecipes(this.player);
      }

      this.removeCount = 0;
   }

   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      NonNullList var3 = var1.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, var1.level);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         ItemStack var5 = this.craftSlots.getItem(var4);
         ItemStack var6 = (ItemStack)var3.get(var4);
         if (!var5.isEmpty()) {
            this.craftSlots.removeItem(var4, 1);
            var5 = this.craftSlots.getItem(var4);
         }

         if (!var6.isEmpty()) {
            if (var5.isEmpty()) {
               this.craftSlots.setItem(var4, var6);
            } else if (ItemStack.isSame(var5, var6) && ItemStack.tagMatches(var5, var6)) {
               var6.grow(var5.getCount());
               this.craftSlots.setItem(var4, var6);
            } else if (!this.player.getInventory().add(var6)) {
               this.player.drop(var6, false);
            }
         }
      }

   }
}
