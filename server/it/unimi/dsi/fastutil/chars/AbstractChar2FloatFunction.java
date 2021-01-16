package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2FloatFunction implements Char2FloatFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractChar2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
