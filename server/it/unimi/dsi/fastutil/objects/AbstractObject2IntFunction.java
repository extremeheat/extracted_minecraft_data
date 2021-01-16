package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2IntFunction<K> implements Object2IntFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected int defRetValue;

   protected AbstractObject2IntFunction() {
      super();
   }

   public void defaultReturnValue(int var1) {
      this.defRetValue = var1;
   }

   public int defaultReturnValue() {
      return this.defRetValue;
   }
}
