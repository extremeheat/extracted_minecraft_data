package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ObjectFunction<V> implements Long2ObjectFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractLong2ObjectFunction() {
      super();
   }

   public void defaultReturnValue(V var1) {
      this.defRetValue = var1;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
