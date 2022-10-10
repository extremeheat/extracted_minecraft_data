package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class IntRangeList extends AbstractDoubleList {
   private final int field_197864_a;
   private final int field_197865_b;

   IntRangeList(int var1, int var2) {
      super();
      this.field_197864_a = var1;
      this.field_197865_b = var2;
   }

   public double getDouble(int var1) {
      return (double)(this.field_197865_b + var1);
   }

   public int size() {
      return this.field_197864_a + 1;
   }
}
