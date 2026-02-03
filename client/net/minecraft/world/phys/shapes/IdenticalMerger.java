package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMerger implements IndexMerger {
   private final DoubleList coords;

   public IdenticalMerger(final DoubleList coords) {
      super();
      this.coords = coords;
   }

   public boolean forMergedIndexes(final IndexMerger.IndexConsumer consumer) {
      int size = this.coords.size() - 1;

      for(int i = 0; i < size; ++i) {
         if (!consumer.merge(i, i, i)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.coords.size();
   }

   public DoubleList getList() {
      return this.coords;
   }
}
