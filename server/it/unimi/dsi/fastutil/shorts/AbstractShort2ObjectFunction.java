package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;

public abstract class AbstractShort2ObjectFunction<V> implements Short2ObjectFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractShort2ObjectFunction() {
      super();
   }

   public void defaultReturnValue(V var1) {
      this.defRetValue = var1;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
