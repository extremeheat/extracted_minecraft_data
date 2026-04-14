package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SlotProvider;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemInstance;

public record SlotsPredicate(Map<SlotRange, ItemPredicate> slots) {
   public static final Codec<SlotsPredicate> CODEC;

   public SlotsPredicate {
      super();
   }

   public boolean matches(final SlotProvider slotProvider) {
      for(Map.Entry<SlotRange, ItemPredicate> entry : this.slots.entrySet()) {
         if (!matchSlots(slotProvider, (ItemPredicate)entry.getValue(), ((SlotRange)entry.getKey()).slots())) {
            return false;
         }
      }

      return true;
   }

   private static boolean matchSlots(final SlotProvider slotProvider, final ItemPredicate test, final IntList slots) {
      for(int i = 0; i < slots.size(); ++i) {
         int slotId = slots.getInt(i);
         SlotAccess slot = slotProvider.getSlot(slotId);
         if (slot != null && test.test((ItemInstance)slot.get())) {
            return true;
         }
      }

      return false;
   }

   static {
      CODEC = Codec.unboundedMap(SlotRanges.CODEC, ItemPredicate.CODEC).xmap(SlotsPredicate::new, SlotsPredicate::slots);
   }
}
