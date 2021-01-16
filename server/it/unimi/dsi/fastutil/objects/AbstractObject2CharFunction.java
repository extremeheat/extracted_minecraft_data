package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2CharFunction<K> implements Object2CharFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected char defRetValue;

   protected AbstractObject2CharFunction() {
      super();
   }

   public void defaultReturnValue(char var1) {
      this.defRetValue = var1;
   }

   public char defaultReturnValue() {
      return this.defRetValue;
   }
}
