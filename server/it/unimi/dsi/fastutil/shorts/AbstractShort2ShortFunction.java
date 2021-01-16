package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2ShortFunction implements Short2ShortFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected short defRetValue;

   protected AbstractShort2ShortFunction() {
      super();
   }

   public void defaultReturnValue(short var1) {
      this.defRetValue = var1;
   }

   public short defaultReturnValue() {
      return this.defRetValue;
   }
}
