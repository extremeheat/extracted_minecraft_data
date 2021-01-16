package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2LongFunction implements Char2LongFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractChar2LongFunction() {
      super();
   }

   public void defaultReturnValue(long var1) {
      this.defRetValue = var1;
   }

   public long defaultReturnValue() {
      return this.defRetValue;
   }
}
