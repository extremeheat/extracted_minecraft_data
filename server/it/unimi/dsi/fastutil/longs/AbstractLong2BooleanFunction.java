package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2BooleanFunction implements Long2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractLong2BooleanFunction() {
      super();
   }

   public void defaultReturnValue(boolean var1) {
      this.defRetValue = var1;
   }

   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
