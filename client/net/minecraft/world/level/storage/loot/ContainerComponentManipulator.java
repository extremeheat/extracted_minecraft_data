package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotCollection;

public interface ContainerComponentManipulator<T> {
   DataComponentType<T> type();

   T empty();

   T setContents(T component, Stream<ItemStack> newContents);

   Stream<ItemStack> getContents(T component);

   default void setContents(final ItemStack itemStack, final T defaultValue, final Stream<ItemStack> newContents) {
      T currentValue = (T)itemStack.getOrDefault(this.type(), defaultValue);
      T newValue = (T)this.setContents(currentValue, newContents);
      itemStack.set(this.type(), newValue);
   }

   default void setContents(final ItemStack itemStack, final Stream<ItemStack> newContents) {
      this.setContents(itemStack, this.empty(), newContents);
   }

   default void modifyItems(final ItemStack itemStack, final UnaryOperator<ItemStack> modifier) {
      T contents = (T)itemStack.get(this.type());
      if (contents != null) {
         UnaryOperator<ItemStack> nonEmptyModifier = (currentItemStack) -> {
            if (currentItemStack.isEmpty()) {
               return currentItemStack;
            } else {
               ItemStack newItemStack = (ItemStack)modifier.apply(currentItemStack);
               newItemStack.limitSize(newItemStack.getMaxStackSize());
               return newItemStack;
            }
         };
         this.setContents(itemStack, this.getContents(contents).map(nonEmptyModifier));
      }

   }

   default SlotCollection getSlots(final ItemStack itemStack) {
      return () -> {
         T contents = (T)itemStack.get(this.type());
         return contents != null ? this.getContents(contents).filter((stack) -> !stack.isEmpty()) : Stream.empty();
      };
   }
}
