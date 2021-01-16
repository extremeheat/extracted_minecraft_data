package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;

public abstract class AbstractFloat2IntFunction implements Float2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractFloat2IntFunction() {
      super();
   }

   public void defaultReturnValue(int var1) {
      this.defRetValue = var1;
   }

   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
