package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IndexMerger {
   private final DoubleList lower;
   private final DoubleList upper;
   private final boolean swap;

   protected NonOverlappingMerger(DoubleList var1, DoubleList var2, boolean var3) {
      super();
      this.lower = var1;
      this.upper = var2;
      this.swap = var3;
   }

   public int size() {
      return this.lower.size() + this.upper.size();
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      return this.swap ? this.forNonSwappedIndexes((var1x, var2, var3) -> {
         return var1.merge(var2, var1x, var3);
      }) : this.forNonSwappedIndexes(var1);
   }

   private boolean forNonSwappedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.lower.size();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3, -1, var3)) {
            return false;
         }
      }

      var3 = this.upper.size() - 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (!var1.merge(var2 - 1, var4, var2 + var4)) {
            return false;
         }
      }

      return true;
   }

   public double getDouble(int var1) {
      return var1 < this.lower.size() ? this.lower.getDouble(var1) : this.upper.getDouble(var1 - this.lower.size());
   }

   public DoubleList getList() {
      return this;
   }
}
