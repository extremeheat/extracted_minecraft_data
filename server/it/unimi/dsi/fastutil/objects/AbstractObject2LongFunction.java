package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2LongFunction<K> implements Object2LongFunction<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected long defRetValue;

   protected AbstractObject2LongFunction() {
      super();
   }

   public void defaultReturnValue(long var1) {
      this.defRetValue = var1;
   }

   public long defaultReturnValue() {
      return this.defRetValue;
   }
}
