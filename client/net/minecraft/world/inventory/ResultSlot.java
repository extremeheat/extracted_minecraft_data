package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.UnlockCondition;

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
         var1.onCraftedBy(this.player, this.removeCount);
      }

      Container var3 = this.container;
      if (var3 instanceof RecipeCraftingHolder var2) {
         var2.awardUsedRecipes(this.player, this.craftSlots.getItems());
      }

      this.removeCount = 0;
   }

   private static NonNullList<ItemStack> copyAllInputItems(CraftingInput var0) {
      NonNullList var1 = NonNullList.withSize(var0.size(), ItemStack.EMPTY);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         var1.set(var2, var0.getItem(var2));
      }

      return var1;
   }

   private NonNullList<ItemStack> getRemainingItems(CraftingInput var1, Level var2) {
      if (var2 instanceof ServerLevel var3) {
         return (NonNullList)var3.recipeAccess().getRecipeFor(RecipeType.CRAFTING, var1, var3).map((var1x) -> ((CraftingRecipe)var1x.value()).getRemainingItems(var1)).orElseGet(() -> copyAllInputItems(var1));
      } else {
         return CraftingRecipe.defaultCraftingReminder(var1);
      }
   }

   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      if (var1 instanceof ServerPlayer var3) {
         UnlockCondition.onCraftedItem(var1.level(), var3, var2);
      }

      CraftingInput.Positioned var14 = this.craftSlots.asPositionedCraftInput();
      CraftingInput var4 = var14.input();
      int var5 = var14.left();
      int var6 = var14.top();
      NonNullList var7 = this.getRemainingItems(var4, var1.level());
      boolean var8 = var1.isActive(PlayerUnlocks.CRAFTING_EFFICIENCY);

      for(int var9 = 0; var9 < var4.height(); ++var9) {
         for(int var10 = 0; var10 < var4.width(); ++var10) {
            int var11 = var10 + var5 + (var9 + var6) * this.craftSlots.getWidth();
            ItemStack var12 = this.craftSlots.getItem(var11);
            ItemStack var13 = (ItemStack)var7.get(var10 + var9 * var4.width());
            if (!var12.isEmpty()) {
               if (!var8 || var1.getRandom().nextBoolean()) {
                  this.craftSlots.removeItem(var11, 1);
               }

               var12 = this.craftSlots.getItem(var11);
            }

            if (!var13.isEmpty()) {
               if (var12.isEmpty()) {
                  this.craftSlots.setItem(var11, var13);
               } else if (ItemStack.isSameItemSameComponents(var12, var13)) {
                  var13.grow(var12.getCount());
                  this.craftSlots.setItem(var11, var13);
               } else if (!this.player.getInventory().add(var13)) {
                  this.player.drop(var13, false);
               }
            }
         }
      }

   }

   public boolean isFake() {
      return true;
   }
}
