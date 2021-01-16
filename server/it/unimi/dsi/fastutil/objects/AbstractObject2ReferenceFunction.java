package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2ReferenceFunction<K, V> implements Object2ReferenceFunction<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractObject2ReferenceFunction() {
      super();
   }

   public void defaultReturnValue(V var1) {
      this.defRetValue = var1;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
