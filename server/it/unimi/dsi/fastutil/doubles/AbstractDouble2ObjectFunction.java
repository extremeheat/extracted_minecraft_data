package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2ObjectFunction<V> implements Double2ObjectFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractDouble2ObjectFunction() {
      super();
   }

   public void defaultReturnValue(V var1) {
      this.defRetValue = var1;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
