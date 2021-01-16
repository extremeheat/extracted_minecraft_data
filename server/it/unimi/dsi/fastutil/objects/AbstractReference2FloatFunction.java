package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractReference2FloatFunction<K> implements Reference2FloatFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected float defRetValue;

   protected AbstractReference2FloatFunction() {
      super();
   }

   public void defaultReturnValue(float var1) {
      this.defRetValue = var1;
   }

   public float defaultReturnValue() {
      return this.defRetValue;
   }
}
