package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList extends AbstractDoubleList {
   private final DoubleList delegate;
   private final double offset;

   public OffsetDoubleList(DoubleList var1, double var2) {
      super();
      this.delegate = var1;
      this.offset = var2;
   }

   public double getDouble(int var1) {
      return this.delegate.getDouble(var1) + this.offset;
   }

   public int size() {
      return this.delegate.size();
   }
}
