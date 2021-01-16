package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2CharFunction implements Int2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractInt2CharFunction() {
      super();
   }

   public void defaultReturnValue(char var1) {
      this.defRetValue = var1;
   }

   public char defaultReturnValue() {
      return this.defRetValue;
   }
}
