package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ResultContainer implements Container, RecipeCraftingHolder {
   private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
   @Nullable
   private RecipeHolder<?> recipeUsed;

   public ResultContainer() {
      super();
   }

   @Override
   public int getContainerSize() {
      return 1;
   }

   @Override
   public boolean isEmpty() {
      for (ItemStack var2 : this.itemStacks) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int var1) {
      return this.itemStacks.get(0);
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.itemStacks.set(0, var2);
   }

   @Override
   public void setChanged() {
   }

   @Override
   public boolean stillValid(Player var1) {
      return true;
   }

   @Override
   public void clearContent() {
      this.itemStacks.clear();
   }

   @Override
   public void setRecipeUsed(@Nullable RecipeHolder<?> var1) {
      this.recipeUsed = var1;
   }

   @Nullable
   @Override
   public RecipeHolder<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
