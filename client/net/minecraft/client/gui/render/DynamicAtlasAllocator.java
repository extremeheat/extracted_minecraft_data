package net.minecraft.client.gui.render;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public class DynamicAtlasAllocator<K> {
   private final int width;
   private final List<Slot> slots;
   private final Map<K, Slot> usedSlotByKey = new HashMap();
   private final BitSet freeSlots;

   public DynamicAtlasAllocator(final int width, final int height) {
      super();
      this.width = width;
      int size = width * height;
      this.slots = new ArrayList(size);

      for(int y = 0; y < height; ++y) {
         for(int x = 0; x < width; ++x) {
            this.slots.add(new Slot(x, y));
         }
      }

      this.freeSlots = new BitSet(size);
      this.freeSlots.set(0, size);
   }

   public boolean reclaimSpaceFor(final Set<K> keys) {
      int preexistingKeyCount = Sets.intersection(this.usedSlotByKey.keySet(), keys).size();
      if (preexistingKeyCount == keys.size()) {
         return true;
      } else {
         MutableInt needSpaceFor = new MutableInt(keys.size() - preexistingKeyCount);
         this.freeSlotIf((key, var3) -> {
            if (needSpaceFor.intValue() != 0 && !keys.contains(key)) {
               needSpaceFor.decrement();
               return true;
            } else {
               return false;
            }
         });
         return needSpaceFor.intValue() == 0;
      }
   }

   public void endFrame() {
      this.freeSlotIf((var0, slot) -> slot.discardAfterFrame);
   }

   private void freeSlotIf(final BiPredicate<K, Slot> predicate) {
      this.usedSlotByKey.entrySet().removeIf((entry) -> {
         if (!predicate.test(entry.getKey(), (Slot)entry.getValue())) {
            return false;
         } else {
            Slot slot = (Slot)entry.getValue();
            this.freeSlots.set(slot.x + slot.y * this.width);
            slot.discardAfterFrame = false;
            return true;
         }
      });
   }

   public boolean hasSpaceForAll(final Set<K> keys) {
      Set<K> predictedUsedSlots = Sets.union(this.usedSlotByKey.keySet(), keys);
      return predictedUsedSlots.size() <= this.slots.size();
   }

   public @Nullable Slot getOrAllocate(final K key, final boolean discardAfterFrame) {
      Slot usedSlot = (Slot)this.usedSlotByKey.get(key);
      if (usedSlot != null) {
         usedSlot.discardAfterFrame |= discardAfterFrame;
         usedSlot.externalState = DynamicAtlasAllocator.SlotState.READY;
         return usedSlot;
      } else {
         int freeSlotIndex = this.freeSlots.nextSetBit(0);
         if (freeSlotIndex == -1) {
            return null;
         } else {
            Slot freeSlot = (Slot)this.slots.get(freeSlotIndex);
            freeSlot.externalState = freeSlot.fresh ? DynamicAtlasAllocator.SlotState.EMPTY : DynamicAtlasAllocator.SlotState.STALE;
            freeSlot.fresh = false;
            freeSlot.discardAfterFrame = discardAfterFrame;
            this.usedSlotByKey.put(key, freeSlot);
            this.freeSlots.clear(freeSlotIndex);
            return freeSlot;
         }
      }
   }

   @VisibleForTesting
   public int freeSlotCount() {
      return this.slots.size() - this.usedSlotByKey.size();
   }

   @VisibleForTesting
   public Set<K> usedSlotKeys() {
      return Collections.unmodifiableSet(this.usedSlotByKey.keySet());
   }

   public static class Slot {
      private final int x;
      private final int y;
      private boolean fresh = true;
      private boolean discardAfterFrame;
      private SlotState externalState;

      private Slot(final int x, final int y) {
         super();
         this.externalState = DynamicAtlasAllocator.SlotState.EMPTY;
         this.x = x;
         this.y = y;
      }

      public int x() {
         return this.x;
      }

      public int y() {
         return this.y;
      }

      public SlotState state() {
         return this.externalState;
      }
   }

   public static enum SlotState {
      EMPTY,
      STALE,
      READY;

      private SlotState() {
      }

      // $FF: synthetic method
      private static SlotState[] $values() {
         return new SlotState[]{EMPTY, STALE, READY};
      }
   }
}
