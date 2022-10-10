package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IDoubleListMerger {
   private final DoubleList field_199638_a;
   private final DoubleList field_199639_b;
   private final boolean field_199640_c;

   public NonOverlappingMerger(DoubleList var1, DoubleList var2, boolean var3) {
      super();
      this.field_199638_a = var1;
      this.field_199639_b = var2;
      this.field_199640_c = var3;
   }

   public int size() {
      return this.field_199638_a.size() + this.field_199639_b.size();
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer var1) {
      return this.field_199640_c ? this.func_199637_b((var1x, var2, var3) -> {
         return var1.merge(var2, var1x, var3);
      }) : this.func_199637_b(var1);
   }

   private boolean func_199637_b(IDoubleListMerger.Consumer var1) {
      int var2 = this.field_199638_a.size() - 1;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3, -1, var3)) {
            return false;
         }
      }

      if (!var1.merge(var2, -1, var2)) {
         return false;
      } else {
         for(var3 = 0; var3 < this.field_199639_b.size(); ++var3) {
            if (!var1.merge(var2, var3, var2 + 1 + var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public double getDouble(int var1) {
      return var1 < this.field_199638_a.size() ? this.field_199638_a.getDouble(var1) : this.field_199639_b.getDouble(var1 - this.field_199638_a.size());
   }

   public DoubleList func_212435_a() {
      return this;
   }
}
