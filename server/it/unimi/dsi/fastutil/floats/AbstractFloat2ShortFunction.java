package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;

public abstract class AbstractFloat2ShortFunction implements Float2ShortFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected short defRetValue;

   protected AbstractFloat2ShortFunction() {
      super();
   }

   public void defaultReturnValue(short var1) {
      this.defRetValue = var1;
   }

   public short defaultReturnValue() {
      return this.defRetValue;
   }
}
