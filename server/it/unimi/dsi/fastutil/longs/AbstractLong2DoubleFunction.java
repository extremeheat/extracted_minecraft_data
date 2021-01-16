package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2DoubleFunction implements Long2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractLong2DoubleFunction() {
      super();
   }

   public void defaultReturnValue(double var1) {
      this.defRetValue = var1;
   }

   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
