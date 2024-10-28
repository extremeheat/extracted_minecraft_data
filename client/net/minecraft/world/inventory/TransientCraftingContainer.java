package net.minecraft.world.inventory;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;

public class TransientCraftingContainer implements CraftingContainer {
   private final NonNullList<ItemStack> items;
   private final int width;
   private final int height;
   private final AbstractContainerMenu menu;

   public TransientCraftingContainer(AbstractContainerMenu var1, int var2, int var3) {
      this(var1, var2, var3, NonNullList.withSize(var2 * var3, ItemStack.EMPTY));
   }

   public TransientCraftingContainer(AbstractContainerMenu var1, int var2, int var3, NonNullList<ItemStack> var4) {
      super();
      this.items = var4;
      this.menu = var1;
      this.width = var2;
      this.height = var3;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.items.iterator();

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
      return var1 >= this.getContainerSize() ? ItemStack.EMPTY : (ItemStack)this.items.get(var1);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.items, var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.items, var1, var2);
      if (!var3.isEmpty()) {
         this.menu.slotsChanged(this);
      }

      return var3;
   }

   public void setItem(int var1, ItemStack var2) {
      this.items.set(var1, var2);
      this.menu.slotsChanged(this);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public List<ItemStack> getItems() {
      return List.copyOf(this.items);
   }

   public void fillStackedContents(StackedContents var1) {
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.accountSimpleStack(var3);
      }

   }
}
