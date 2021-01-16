package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2LongFunction implements Long2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractLong2LongFunction() {
      super();
   }

   public void defaultReturnValue(long var1) {
      this.defRetValue = var1;
   }

   public long defaultReturnValue() {
      return this.defRetValue;
   }
}
