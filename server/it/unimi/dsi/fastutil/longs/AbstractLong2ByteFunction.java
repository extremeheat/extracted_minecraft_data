package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ByteFunction implements Long2ByteFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected byte defRetValue;

   protected AbstractLong2ByteFunction() {
      super();
   }

   public void defaultReturnValue(byte var1) {
      this.defRetValue = var1;
   }

   public byte defaultReturnValue() {
      return this.defRetValue;
   }
}
