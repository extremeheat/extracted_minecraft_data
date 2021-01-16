package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2CharFunction implements Short2CharFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractShort2CharFunction() {
      super();
   }

   public void defaultReturnValue(char var1) {
      this.defRetValue = var1;
   }

   public char defaultReturnValue() {
      return this.defRetValue;
   }
}
