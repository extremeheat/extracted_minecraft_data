package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;

public class CraftingContainer implements Container, StackedContentsCompatible {
   private final NonNullList<ItemStack> items;
   private final int width;
   private final int height;
   private final AbstractContainerMenu menu;

   public CraftingContainer(AbstractContainerMenu var1, int var2, int var3) {
      super();
      this.items = NonNullList.withSize(var2 * var3, ItemStack.EMPTY);
      this.menu = var1;
      this.width = var2;
      this.height = var3;
   }

   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int var1) {
      return var1 >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(var1);
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.items, var1);
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.items, var1, var2);
      if (!var3.isEmpty()) {
         this.menu.slotsChanged(this);
      }

      return var3;
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.items.set(var1, var2);
      this.menu.slotsChanged(this);
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
      this.items.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @Override
   public void fillStackedContents(StackedContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountSimpleStack(var3);
      }
   }
}
