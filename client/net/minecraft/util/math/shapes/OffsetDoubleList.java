package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList extends AbstractDoubleList {
   private final DoubleList field_197888_a;
   private final double field_197889_b;

   public OffsetDoubleList(DoubleList var1, double var2) {
      super();
      this.field_197888_a = var1;
      this.field_197889_b = var2;
   }

   public double getDouble(int var1) {
      return this.field_197888_a.getDouble(var1) + this.field_197889_b;
   }

   public int size() {
      return this.field_197888_a.size();
   }
}
