package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2IntFunction implements Short2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractShort2IntFunction() {
      super();
   }

   public void defaultReturnValue(int var1) {
      this.defRetValue = var1;
   }

   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
