package net.minecraft.world;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SimpleContainer implements Container, StackedContentsCompatible {
   private final int size;
   private final NonNullList<ItemStack> items;

   public SimpleContainer(final int size) {
      super();
      this.size = size;
      this.items = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
   }

   public SimpleContainer(final ItemStack... itemstacks) {
      super();
      this.size = itemstacks.length;
      this.items = NonNullList.<ItemStack>of(ItemStack.EMPTY, itemstacks);
   }

   public ItemStack getItem(final int slot) {
      return slot >= 0 && slot < this.items.size() ? (ItemStack)this.items.get(slot) : ItemStack.EMPTY;
   }

   public List<ItemStack> removeAllItems() {
      List<ItemStack> itemsRemoved = (List)this.items.stream().filter((item) -> !item.isEmpty()).collect(Collectors.toList());
      this.clearContent();
      return itemsRemoved;
   }

   public ItemStack removeItem(final int slot, final int count) {
      ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
      if (!result.isEmpty()) {
         this.setChanged();
      }

      return result;
   }

   public ItemStack removeItemType(final Item itemType, final int count) {
      ItemStack removed = new ItemStack(itemType, 0);

      for(int slot = this.size - 1; slot >= 0; --slot) {
         ItemStack current = this.getItem(slot);
         if (current.getItem().equals(itemType)) {
            int stillNeeded = count - removed.getCount();
            ItemStack removedFromThisSlot = current.split(stillNeeded);
            removed.grow(removedFromThisSlot.getCount());
            if (removed.getCount() == count) {
               break;
            }
         }
      }

      if (!removed.isEmpty()) {
         this.setChanged();
      }

      return removed;
   }

   public ItemStack addItem(final ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack remainingItems = itemStack.copy();
         this.moveItemToOccupiedSlotsWithSameType(remainingItems);
         if (remainingItems.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.moveItemToEmptySlots(remainingItems);
            return remainingItems.isEmpty() ? ItemStack.EMPTY : remainingItems;
         }
      }
   }

   public boolean canAddItem(final ItemStack itemStack) {
      boolean hasSpace = false;

      for(ItemStack targetStack : this.items) {
         if (targetStack.isEmpty() || ItemStack.isSameItemSameComponents(targetStack, itemStack) && targetStack.getCount() < targetStack.getMaxStackSize()) {
            hasSpace = true;
            break;
         }
      }

      return hasSpace;
   }

   public ItemStack removeItemNoUpdate(final int slot) {
      ItemStack itemStack = this.items.get(slot);
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.items.set(slot, ItemStack.EMPTY);
         return itemStack;
      }
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      this.items.set(slot, itemStack);
      itemStack.limitSize(this.getMaxStackSize(itemStack));
      this.setChanged();
   }

   public void setChanged() {
   }

   public int getContainerSize() {
      return this.size;
   }

   public boolean isEmpty() {
      for(ItemStack itemStack : this.items) {
         if (!itemStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean stillValid(final Player player) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public void fillStackedContents(final StackedItemContents contents) {
      for(ItemStack itemStack : this.items) {
         contents.accountStack(itemStack);
      }

   }

   public String toString() {
      return this.items.stream().filter((item) -> !item.isEmpty()).toList().toString();
   }

   private void moveItemToEmptySlots(final ItemStack sourceStack) {
      for(int slot = 0; slot < this.size; ++slot) {
         ItemStack targetStack = this.getItem(slot);
         if (targetStack.isEmpty()) {
            this.setItem(slot, sourceStack.copyAndClear());
            return;
         }
      }

   }

   private void moveItemToOccupiedSlotsWithSameType(final ItemStack sourceStack) {
      for(int slot = 0; slot < this.size; ++slot) {
         ItemStack targetStack = this.getItem(slot);
         if (ItemStack.isSameItemSameComponents(targetStack, sourceStack)) {
            this.moveItemsBetweenStacks(sourceStack, targetStack);
            if (sourceStack.isEmpty()) {
               return;
            }
         }
      }

   }

   private void moveItemsBetweenStacks(final ItemStack sourceStack, final ItemStack targetStack) {
      int maxCount = this.getMaxStackSize(targetStack);
      int diff = Math.min(sourceStack.getCount(), maxCount - targetStack.getCount());
      if (diff > 0) {
         targetStack.grow(diff);
         sourceStack.shrink(diff);
         this.setChanged();
      }

   }

   public void fromItemList(final ValueInput.TypedInputList<ItemStack> items) {
      this.clearContent();

      for(ItemStack stack : items) {
         this.addItem(stack);
      }

   }

   public void storeAsItemList(final ValueOutput.TypedOutputList<ItemStack> output) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemStack = this.getItem(i);
         if (!itemStack.isEmpty()) {
            output.add(itemStack);
         }
      }

   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }
}
