package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2FloatFunction implements Double2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractDouble2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
