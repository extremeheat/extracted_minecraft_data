package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

public abstract class AbstractByte2LongFunction implements Byte2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractByte2LongFunction() {
      super();
   }

   public void defaultReturnValue(long var1) {
      this.defRetValue = var1;
   }

   public long defaultReturnValue() {
      return this.defRetValue;
   }
}
