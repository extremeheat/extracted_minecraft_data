package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMerger implements IndexMerger {
   private final DoubleList coords;

   public IdenticalMerger(DoubleList var1) {
      super();
      this.coords = var1;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      for(int var2 = 0; var2 <= this.coords.size(); ++var2) {
         if (!var1.merge(var2, var2, var2)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.coords;
   }
}
