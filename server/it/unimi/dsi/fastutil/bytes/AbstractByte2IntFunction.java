package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

public abstract class AbstractByte2IntFunction implements Byte2IntFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractByte2IntFunction() {
      super();
   }

   public void defaultReturnValue(int var1) {
      this.defRetValue = var1;
   }

   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
