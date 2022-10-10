package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

class DoubleRangeList extends AbstractDoubleList {
   private final int field_197854_a;

   DoubleRangeList(int var1) {
      super();
      this.field_197854_a = var1;
   }

   public double getDouble(int var1) {
      return (double)var1 / (double)this.field_197854_a;
   }

   public int size() {
      return this.field_197854_a + 1;
   }
}
