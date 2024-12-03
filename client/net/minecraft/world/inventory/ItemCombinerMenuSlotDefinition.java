package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
   private final List<SlotDefinition> slots;
   private final SlotDefinition resultSlot;

   ItemCombinerMenuSlotDefinition(List<SlotDefinition> var1, SlotDefinition var2) {
      super();
      if (!var1.isEmpty() && !var2.equals(ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
         this.slots = var1;
         this.resultSlot = var2;
      } else {
         throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
      }
   }

   public static Builder create() {
      return new Builder();
   }

   public SlotDefinition getSlot(int var1) {
      return (SlotDefinition)this.slots.get(var1);
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

      public Builder withSlot(int var1, int var2, int var3, Predicate<ItemStack> var4) {
         this.inputSlots.add(new SlotDefinition(var1, var2, var3, var4));
         return this;
      }

      public Builder withResultSlot(int var1, int var2, int var3) {
         this.resultSlot = new SlotDefinition(var1, var2, var3, (var0) -> false);
         return this;
      }

      public ItemCombinerMenuSlotDefinition build() {
         int var1 = this.inputSlots.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            SlotDefinition var3 = (SlotDefinition)this.inputSlots.get(var2);
            if (var3.slotIndex != var2) {
               throw new IllegalArgumentException("Expected input slots to have continous indexes");
            }
         }

         if (this.resultSlot.slotIndex != var1) {
            throw new IllegalArgumentException("Expected result slot index to follow last input slot");
         } else {
            return new ItemCombinerMenuSlotDefinition(this.inputSlots, this.resultSlot);
         }
      }
   }

   public static record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
      final int slotIndex;
      static final SlotDefinition EMPTY = new SlotDefinition(0, 0, 0, (var0) -> true);

      public SlotDefinition(int var1, int var2, int var3, Predicate<ItemStack> var4) {
         super();
         this.slotIndex = var1;
         this.x = var2;
         this.y = var3;
         this.mayPlace = var4;
      }
   }
}
