package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2DoubleFunction implements Char2DoubleFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractChar2DoubleFunction() {
      super();
   }

   public void defaultReturnValue(double var1) {
      this.defRetValue = var1;
   }

   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
