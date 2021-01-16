package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2DoubleFunction implements Int2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractInt2DoubleFunction() {
      super();
   }

   public void defaultReturnValue(double var1) {
      this.defRetValue = var1;
   }

   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
