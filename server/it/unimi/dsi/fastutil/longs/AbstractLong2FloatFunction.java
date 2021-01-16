package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2FloatFunction implements Long2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractLong2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
