package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

final class IndirectMerger implements IDoubleListMerger {
   private final DoubleArrayList field_197856_a;
   private final IntArrayList field_197857_b;
   private final IntArrayList field_197858_c;

   IndirectMerger(DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      super();
      int var5 = 0;
      int var6 = 0;
      double var7 = 0.0D / 0.0;
      int var9 = var1.size();
      int var10 = var2.size();
      int var11 = var9 + var10;
      this.field_197856_a = new DoubleArrayList(var11);
      this.field_197857_b = new IntArrayList(var11);
      this.field_197858_c = new IntArrayList(var11);

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
                  if (this.field_197856_a.isEmpty()) {
                     this.field_197856_a.add(Math.min(var1.getDouble(var9 - 1), var2.getDouble(var10 - 1)));
                  }

                  return;
               }

               var14 = var12 && (!var13 || var1.getDouble(var5) < var2.getDouble(var6) + 1.0E-7D);
               var15 = var14 ? var1.getDouble(var5++) : var2.getDouble(var6++);
            } while((var5 == 0 || !var12) && !var14 && !var4);
         } while((var6 == 0 || !var13) && var14 && !var3);

         if (var7 <= var15 - 1.0E-7D) {
            this.field_197857_b.add(var5 - 1);
            this.field_197858_c.add(var6 - 1);
            this.field_197856_a.add(var15);
            var7 = var15;
         } else if (!this.field_197856_a.isEmpty()) {
            this.field_197857_b.set(this.field_197857_b.size() - 1, var5 - 1);
            this.field_197858_c.set(this.field_197858_c.size() - 1, var6 - 1);
         }
      }
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer var1) {
      for(int var2 = 0; var2 < this.field_197856_a.size() - 1; ++var2) {
         if (!var1.merge(this.field_197857_b.getInt(var2), this.field_197858_c.getInt(var2), var2)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_197856_a;
   }
}
