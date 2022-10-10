package net.minecraft.util.math.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DoubleCubeMergingList implements IDoubleListMerger {
   private final DoubleRangeList field_212436_a;
   private final int field_197859_a;
   private final int field_197860_b;
   private final int field_197861_c;

   DoubleCubeMergingList(int var1, int var2) {
      super();
      this.field_212436_a = new DoubleRangeList((int)VoxelShapes.func_197877_a(var1, var2));
      this.field_197859_a = var1;
      this.field_197860_b = var2;
      this.field_197861_c = IntMath.gcd(var1, var2);
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer var1) {
      int var2 = this.field_197859_a / this.field_197861_c;
      int var3 = this.field_197860_b / this.field_197861_c;

      for(int var4 = 0; var4 <= this.field_212436_a.size(); ++var4) {
         if (!var1.merge(var4 / var3, var4 / var2, var4)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_212436_a;
   }
}
