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

   @Override
   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount += Math.min(var1, this.getItem().getCount());
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void checkTakeAchievements(ItemStack var1) {
      if (this.removeCount > 0) {
         var1.onCraftedBy(this.player.level(), this.player, this.removeCount);
      }

      Container var3 = this.container;
      if (var3 instanceof RecipeCraftingHolder var2) {
         var2.awardUsedRecipes(this.player, this.craftSlots.getItems());
      }

      this.removeCount = 0;
   }

   @Override
   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      NonNullList var3 = var1.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, var1.level());

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
            } else if (ItemStack.isSameItemSameTags(var5, var6)) {
               var6.grow(var5.getCount());
               this.craftSlots.setItem(var4, var6);
            } else if (!this.player.getInventory().add(var6)) {
               this.player.drop(var6, false);
            }
         }
      }
   }

   @Override
   public boolean isFake() {
      return true;
   }
}
