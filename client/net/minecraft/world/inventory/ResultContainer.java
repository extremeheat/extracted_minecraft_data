package net.minecraft.world.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ResultContainer implements Container, RecipeCraftingHolder {
   private final NonNullList<ItemStack> itemStacks;
   @Nullable
   private RecipeHolder<?> recipeUsed;

   public ResultContainer() {
      super();
      this.itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
   }

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      Iterator var1 = this.itemStacks.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public ItemStack getItem(int var1) {
      return (ItemStack)this.itemStacks.get(0);
   }

   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public void setItem(int var1, ItemStack var2) {
      this.itemStacks.set(0, var2);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public void setRecipeUsed(@Nullable RecipeHolder<?> var1) {
      this.recipeUsed = var1;
   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
