package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMerger implements IndexMerger {
   private final DoubleList coords;

   public IdenticalMerger(DoubleList var1) {
      super();
      this.coords = var1;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.coords.size() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3, var3, var3)) {
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
