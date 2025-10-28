package net.minecraft.world.entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {
   ItemStack get();

   boolean set(ItemStack var1);

   static SlotAccess of(final Supplier<ItemStack> var0, final Consumer<ItemStack> var1) {
      return new SlotAccess() {
         public ItemStack get() {
            return (ItemStack)var0.get();
         }

         public boolean set(ItemStack var1x) {
            var1.accept(var1x);
            return true;
         }
      };
   }

   static SlotAccess forEquipmentSlot(final LivingEntity var0, final EquipmentSlot var1, final Predicate<ItemStack> var2) {
      return new SlotAccess() {
         public ItemStack get() {
            return var0.getItemBySlot(var1);
         }

         public boolean set(ItemStack var1x) {
            if (!var2.test(var1x)) {
               return false;
            } else {
               var0.setItemSlot(var1, var1x);
               return true;
            }
         }
      };
   }

   static SlotAccess forEquipmentSlot(LivingEntity var0, EquipmentSlot var1) {
      return forEquipmentSlot(var0, var1, (var0x) -> true);
   }

   static SlotAccess forListElement(final List<ItemStack> var0, final int var1) {
      return new SlotAccess() {
         public ItemStack get() {
            return (ItemStack)var0.get(var1);
         }

         public boolean set(ItemStack var1x) {
            var0.set(var1, var1x);
            return true;
         }
      };
   }
}
