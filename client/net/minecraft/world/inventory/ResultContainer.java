package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

public class ResultContainer implements Container, RecipeCraftingHolder {
   private final NonNullList<ItemStack> itemStacks;
   private @Nullable RecipeHolder<?> recipeUsed;

   public ResultContainer() {
      super();
      this.itemStacks = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
   }

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack itemStack : this.itemStacks) {
         if (!itemStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(final int slot) {
      return this.itemStacks.get(0);
   }

   public ItemStack removeItem(final int slot, final int count) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public ItemStack removeItemNoUpdate(final int slot) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      this.itemStacks.set(0, itemStack);
   }

   public void setChanged() {
   }

   public boolean stillValid(final Player player) {
      return true;
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public void setRecipeUsed(final @Nullable RecipeHolder<?> recipeUsed) {
      this.recipeUsed = recipeUsed;
   }

   public @Nullable RecipeHolder<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
