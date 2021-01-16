package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

public abstract class AbstractByte2FloatFunction implements Byte2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractByte2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
