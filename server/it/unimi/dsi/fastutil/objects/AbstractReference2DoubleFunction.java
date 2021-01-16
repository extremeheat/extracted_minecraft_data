package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractReference2DoubleFunction<K> implements Reference2DoubleFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected double defRetValue;

   protected AbstractReference2DoubleFunction() {
      super();
   }

   public void defaultReturnValue(double var1) {
      this.defRetValue = var1;
   }

   public double defaultReturnValue() {
      return this.defRetValue;
   }
}
