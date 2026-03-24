package net.minecraft.world.entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {
   ItemStack get();

   boolean set(ItemStack itemStack);

   static SlotAccess of(final Supplier<ItemStack> getter, final Consumer<ItemStack> setter) {
      return new SlotAccess() {
         public ItemStack get() {
            return (ItemStack)getter.get();
         }

         public boolean set(final ItemStack itemStack) {
            setter.accept(itemStack);
            return true;
         }
      };
   }

   static SlotAccess forEquipmentSlot(final LivingEntity entity, final EquipmentSlot slot, final Predicate<ItemStack> validator) {
      return new SlotAccess() {
         public ItemStack get() {
            return entity.getItemBySlot(slot);
         }

         public boolean set(final ItemStack itemStack) {
            if (!validator.test(itemStack)) {
               return false;
            } else {
               entity.setItemSlot(slot, itemStack);
               return true;
            }
         }
      };
   }

   static SlotAccess forEquipmentSlot(final LivingEntity entity, final EquipmentSlot slot) {
      return forEquipmentSlot(entity, slot, (stack) -> true);
   }

   static SlotAccess forListElement(final List<ItemStack> stacks, final int index) {
      return new SlotAccess() {
         public ItemStack get() {
            return (ItemStack)stacks.get(index);
         }

         public boolean set(final ItemStack itemStack) {
            stacks.set(index, itemStack);
            return true;
         }
      };
   }
}
