package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2ShortFunction<K> implements Object2ShortFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected short defRetValue;

   protected AbstractObject2ShortFunction() {
      super();
   }

   public void defaultReturnValue(short var1) {
      this.defRetValue = var1;
   }

   public short defaultReturnValue() {
      return this.defRetValue;
   }
}
