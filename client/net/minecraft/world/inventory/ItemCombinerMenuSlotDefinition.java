package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
   private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> slots;
   private final ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot;

   ItemCombinerMenuSlotDefinition(List<ItemCombinerMenuSlotDefinition.SlotDefinition> var1, ItemCombinerMenuSlotDefinition.SlotDefinition var2) {
      super();
      if (!var1.isEmpty() && !var2.equals(ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
         this.slots = var1;
         this.resultSlot = var2;
      } else {
         throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
      }
   }

   public static ItemCombinerMenuSlotDefinition.Builder create() {
      return new ItemCombinerMenuSlotDefinition.Builder();
   }

   public boolean hasSlot(int var1) {
      return this.slots.size() >= var1;
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getSlot(int var1) {
      return this.slots.get(var1);
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getResultSlot() {
      return this.resultSlot;
   }

   public List<ItemCombinerMenuSlotDefinition.SlotDefinition> getSlots() {
      return this.slots;
   }

   public int getNumOfInputSlots() {
      return this.slots.size();
   }

   public int getResultSlotIndex() {
      return this.getNumOfInputSlots();
   }

   public List<Integer> getInputSlotIndexes() {
      return this.slots.stream().map(ItemCombinerMenuSlotDefinition.SlotDefinition::slotIndex).collect(Collectors.toList());
   }

   public static class Builder {
      private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> slots = new ArrayList<>();
      private ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot = ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY;

      public Builder() {
         super();
      }

      public ItemCombinerMenuSlotDefinition.Builder withSlot(int var1, int var2, int var3, Predicate<ItemStack> var4) {
         this.slots.add(new ItemCombinerMenuSlotDefinition.SlotDefinition(var1, var2, var3, var4));
         return this;
      }

      public ItemCombinerMenuSlotDefinition.Builder withResultSlot(int var1, int var2, int var3) {
         this.resultSlot = new ItemCombinerMenuSlotDefinition.SlotDefinition(var1, var2, var3, var0 -> false);
         return this;
      }

      public ItemCombinerMenuSlotDefinition build() {
         return new ItemCombinerMenuSlotDefinition(this.slots, this.resultSlot);
      }
   }

   public static record SlotDefinition(int a, int b, int c, Predicate<ItemStack> d) {
      private final int slotIndex;
      private final int x;
      private final int y;
      private final Predicate<ItemStack> mayPlace;
      static final ItemCombinerMenuSlotDefinition.SlotDefinition EMPTY = new ItemCombinerMenuSlotDefinition.SlotDefinition(0, 0, 0, var0 -> true);

      public SlotDefinition(int var1, int var2, int var3, Predicate<ItemStack> var4) {
         super();
         this.slotIndex = var1;
         this.x = var2;
         this.y = var3;
         this.mayPlace = var4;
      }
   }
}
