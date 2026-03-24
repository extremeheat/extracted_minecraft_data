package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
   private final List<SlotDefinition> slots;
   private final SlotDefinition resultSlot;

   private ItemCombinerMenuSlotDefinition(final List<SlotDefinition> inputSlots, final SlotDefinition resultSlot) {
      super();
      if (!inputSlots.isEmpty() && !resultSlot.equals(ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
         this.slots = inputSlots;
         this.resultSlot = resultSlot;
      } else {
         throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
      }
   }

   public static Builder create() {
      return new Builder();
   }

   public SlotDefinition getSlot(final int index) {
      return (SlotDefinition)this.slots.get(index);
   }

   public SlotDefinition getResultSlot() {
      return this.resultSlot;
   }

   public List<SlotDefinition> getSlots() {
      return this.slots;
   }

   public int getNumOfInputSlots() {
      return this.slots.size();
   }

   public int getResultSlotIndex() {
      return this.getNumOfInputSlots();
   }

   public static class Builder {
      private final List<SlotDefinition> inputSlots = new ArrayList();
      private SlotDefinition resultSlot;

      public Builder() {
         super();
         this.resultSlot = ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY;
      }

      public Builder withSlot(final int slotIndex, final int xPlacement, final int yPlacement, final Predicate<ItemStack> mayPlace) {
         this.inputSlots.add(new SlotDefinition(slotIndex, xPlacement, yPlacement, mayPlace));
         return this;
      }

      public Builder withResultSlot(final int slotIndex, final int xPlacement, final int yPlacement) {
         this.resultSlot = new SlotDefinition(slotIndex, xPlacement, yPlacement, (itemStack) -> false);
         return this;
      }

      public ItemCombinerMenuSlotDefinition build() {
         int inputCount = this.inputSlots.size();

         for(int i = 0; i < inputCount; ++i) {
            SlotDefinition inputDefinition = (SlotDefinition)this.inputSlots.get(i);
            if (inputDefinition.slotIndex != i) {
               throw new IllegalArgumentException("Expected input slots to have continous indexes");
            }
         }

         if (this.resultSlot.slotIndex != inputCount) {
            throw new IllegalArgumentException("Expected result slot index to follow last input slot");
         } else {
            return new ItemCombinerMenuSlotDefinition(this.inputSlots, this.resultSlot);
         }
      }
   }

   public static record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
      private static final SlotDefinition EMPTY = new SlotDefinition(0, 0, 0, (itemStack) -> true);

      public SlotDefinition {
         super();
      }
   }
}
