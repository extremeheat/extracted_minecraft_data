package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;

public abstract class AbstractFloat2DoubleFunction implements Float2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractFloat2DoubleFunction() {
      super();
   }

   public void defaultReturnValue(double var1) {
      this.defRetValue = var1;
   }

   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
