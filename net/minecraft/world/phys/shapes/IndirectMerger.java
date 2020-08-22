package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class IndirectMerger implements IndexMerger {
   private final DoubleArrayList result;
   private final IntArrayList firstIndices;
   private final IntArrayList secondIndices;

   protected IndirectMerger(DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      int var5 = 0;
      int var6 = 0;
      double var7 = Double.NaN;
      int var9 = var1.size();
      int var10 = var2.size();
      int var11 = var9 + var10;
      this.result = new DoubleArrayList(var11);
      this.firstIndices = new IntArrayList(var11);
      this.secondIndices = new IntArrayList(var11);

      while(true) {
         boolean var12;
         boolean var13;
         boolean var14;
         double var15;
         do {
            do {
               var12 = var5 < var9;
               var13 = var6 < var10;
               if (!var12 && !var13) {
                  if (this.result.isEmpty()) {
                     this.result.add(Math.min(var1.getDouble(var9 - 1), var2.getDouble(var10 - 1)));
                  }

                  return;
               }

               var14 = var12 && (!var13 || var1.getDouble(var5) < var2.getDouble(var6) + 1.0E-7D);
               var15 = var14 ? var1.getDouble(var5++) : var2.getDouble(var6++);
            } while((var5 == 0 || !var12) && !var14 && !var4);
         } while((var6 == 0 || !var13) && var14 && !var3);

         if (var7 < var15 - 1.0E-7D) {
            this.firstIndices.add(var5 - 1);
            this.secondIndices.add(var6 - 1);
            this.result.add(var15);
            var7 = var15;
         } else if (!this.result.isEmpty()) {
            this.firstIndices.set(this.firstIndices.size() - 1, var5 - 1);
            this.secondIndices.set(this.secondIndices.size() - 1, var6 - 1);
         }
      }
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      for(int var2 = 0; var2 < this.result.size() - 1; ++var2) {
         if (!var1.merge(this.firstIndices.getInt(var2), this.secondIndices.getInt(var2), var2)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.result;
   }
}
