package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;

public abstract class AbstractFloat2FloatFunction implements Float2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractFloat2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
