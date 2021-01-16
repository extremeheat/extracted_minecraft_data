package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2ReferenceFunction<V> implements Double2ReferenceFunction<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractDouble2ReferenceFunction() {
      super();
   }

   public void defaultReturnValue(V var1) {
      this.defRetValue = var1;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
