package net.minecraft.world.item.component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotSelector;

public abstract class SimpleMutableContainer<T> implements ContainerComponent.Mutable<T> {
   protected final List<ItemStack> items;

   public SimpleMutableContainer(final List<ItemStack> items) {
      super();
      this.items = items;
   }

   public Stream<ItemStack> itemCopies() {
      return this.items.stream().map(ItemStack::copy);
   }

   public int size() {
      return this.items.size();
   }

   public int replaceSlotItems(final ItemProvider newItems, final SlotSelector slotSelector) {
      int successCount = 0;

      for(int index = 0; index < this.items.size() && newItems.hasNext(); ++index) {
         ItemStack currentItem = (ItemStack)this.items.get(index);
         if (slotSelector.trySelectSlot(currentItem)) {
            boolean success = this.setItem(index, newItems.next());
            if (success) {
               ++successCount;
            }
         }
      }

      return successCount;
   }

   public void modifySlots(final Consumer<? super SlotAccess> consumer, final SlotSelector slotSelector) {
      for(int i = 0; i < this.items.size(); ++i) {
         ItemStack currentItem = (ItemStack)this.items.get(i);
         if (slotSelector.trySelectSlot(currentItem)) {
            consumer.accept(SlotAccess.of(() -> (ItemStack)this.items.get(i), (stack) -> this.setItem(i, stack)));
         }
      }

   }

   protected boolean setItem(final int slot, final ItemStack itemStack) {
      this.items.set(slot, itemStack);
      return true;
   }
}
