package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IndexMerger {
   DoubleList getList();

   boolean forMergedIndexes(IndexConsumer consumer);

   int size();

   public interface IndexConsumer {
      boolean merge(int firstIndex, int secondIndex, int resultIndex);
   }
}
