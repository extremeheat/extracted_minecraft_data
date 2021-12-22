package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;

public class IndirectMerger implements IndexMerger {
   private static final DoubleList EMPTY = DoubleLists.unmodifiable(DoubleArrayList.wrap(new double[]{0.0D}));
   private final double[] result;
   private final int[] firstIndices;
   private final int[] secondIndices;
   private final int resultLength;

   public IndirectMerger(DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      super();
      double var5 = 0.0D / 0.0;
      int var7 = var1.size();
      int var8 = var2.size();
      int var9 = var7 + var8;
      this.result = new double[var9];
      this.firstIndices = new int[var9];
      this.secondIndices = new int[var9];
      boolean var10 = !var3;
      boolean var11 = !var4;
      int var12 = 0;
      int var13 = 0;
      int var14 = 0;

      while(true) {
         boolean var17;
         while(true) {
            boolean var15 = var13 >= var7;
            boolean var16 = var14 >= var8;
            if (var15 && var16) {
               this.resultLength = Math.max(1, var12);
               return;
            }

            var17 = !var15 && (var16 || var1.getDouble(var13) < var2.getDouble(var14) + 1.0E-7D);
            if (var17) {
               ++var13;
               if (!var10 || var14 != 0 && !var16) {
                  break;
               }
            } else {
               ++var14;
               if (!var11 || var13 != 0 && !var15) {
                  break;
               }
            }
         }

         int var18 = var13 - 1;
         int var19 = var14 - 1;
         double var20 = var17 ? var1.getDouble(var18) : var2.getDouble(var19);
         if (!(var5 >= var20 - 1.0E-7D)) {
            this.firstIndices[var12] = var18;
            this.secondIndices[var12] = var19;
            this.result[var12] = var20;
            ++var12;
            var5 = var20;
         } else {
            this.firstIndices[var12 - 1] = var18;
            this.secondIndices[var12 - 1] = var19;
         }
      }
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.resultLength - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(this.firstIndices[var3], this.secondIndices[var3], var3)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.resultLength;
   }

   public DoubleList getList() {
      return (DoubleList)(this.resultLength <= 1 ? EMPTY : DoubleArrayList.wrap(this.result, this.resultLength));
   }
}
