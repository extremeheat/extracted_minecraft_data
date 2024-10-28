package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SimpleContainer implements Container, StackedContentsCompatible {
   private final int size;
   private final NonNullList<ItemStack> items;
   @Nullable
   private List<ContainerListener> listeners;

   public SimpleContainer(int var1) {
      super();
      this.size = var1;
      this.items = NonNullList.withSize(var1, ItemStack.EMPTY);
   }

   public SimpleContainer(ItemStack... var1) {
      super();
      this.size = var1.length;
      this.items = NonNullList.of(ItemStack.EMPTY, var1);
   }

   public void addListener(ContainerListener var1) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(var1);
   }

   public void removeListener(ContainerListener var1) {
      if (this.listeners != null) {
         this.listeners.remove(var1);
      }

   }

   public ItemStack getItem(int var1) {
      return var1 >= 0 && var1 < this.items.size() ? (ItemStack)this.items.get(var1) : ItemStack.EMPTY;
   }

   public List<ItemStack> removeAllItems() {
      List var1 = (List)this.items.stream().filter((var0) -> {
         return !var0.isEmpty();
      }).collect(Collectors.toList());
      this.clearContent();
      return var1;
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.items, var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   public ItemStack removeItemType(Item var1, int var2) {
      ItemStack var3 = new ItemStack(var1, 0);

      for(int var4 = this.size - 1; var4 >= 0; --var4) {
         ItemStack var5 = this.getItem(var4);
         if (var5.getItem().equals(var1)) {
            int var6 = var2 - var3.getCount();
            ItemStack var7 = var5.split(var6);
            var3.grow(var7.getCount());
            if (var3.getCount() == var2) {
               break;
            }
         }
      }

      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   public ItemStack addItem(ItemStack var1) {
      if (var1.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var2 = var1.copy();
         this.moveItemToOccupiedSlotsWithSameType(var2);
         if (var2.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.moveItemToEmptySlots(var2);
            return var2.isEmpty() ? ItemStack.EMPTY : var2;
         }
      }
   }

   public boolean canAddItem(ItemStack var1) {
      boolean var2 = false;
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         ItemStack var4 = (ItemStack)var3.next();
         if (var4.isEmpty() || ItemStack.isSameItemSameComponents(var4, var1) && var4.getCount() < var4.getMaxStackSize()) {
            var2 = true;
            break;
         }
      }

      return var2;
   }

   public ItemStack removeItemNoUpdate(int var1) {
      ItemStack var2 = (ItemStack)this.items.get(var1);
      if (var2.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.items.set(var1, ItemStack.EMPTY);
         return var2;
      }
   }

   public void setItem(int var1, ItemStack var2) {
      this.items.set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      this.setChanged();
   }

   public int getContainerSize() {
      return this.size;
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

   public void setChanged() {
      if (this.listeners != null) {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            ContainerListener var2 = (ContainerListener)var1.next();
            var2.containerChanged(this);
         }
      }

   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public void fillStackedContents(StackedContents var1) {
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.accountStack(var3);
      }

   }

   public String toString() {
      return ((List)this.items.stream().filter((var0) -> {
         return !var0.isEmpty();
      }).collect(Collectors.toList())).toString();
   }

   private void moveItemToEmptySlots(ItemStack var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (var3.isEmpty()) {
            this.setItem(var2, var1.copyAndClear());
            return;
         }
      }

   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (ItemStack.isSameItemSameComponents(var3, var1)) {
            this.moveItemsBetweenStacks(var1, var3);
            if (var1.isEmpty()) {
               return;
            }
         }
      }

   }

   private void moveItemsBetweenStacks(ItemStack var1, ItemStack var2) {
      int var3 = this.getMaxStackSize(var2);
      int var4 = Math.min(var1.getCount(), var3 - var2.getCount());
      if (var4 > 0) {
         var2.grow(var4);
         var1.shrink(var4);
         this.setChanged();
      }

   }

   public void fromTag(ListTag var1, HolderLookup.Provider var2) {
      this.clearContent();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         ItemStack.parse(var2, var1.getCompound(var3)).ifPresent(this::addItem);
      }

   }

   public ListTag createTag(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
         ItemStack var4 = this.getItem(var3);
         if (!var4.isEmpty()) {
            var2.add(var4.save(var1));
         }
      }

      return var2;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }
}
