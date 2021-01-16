package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2CharFunction implements Double2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractDouble2CharFunction() {
      super();
   }

   public void defaultReturnValue(char var1) {
      this.defRetValue = var1;
   }

   public char defaultReturnValue() {
      return this.defRetValue;
   }
}
