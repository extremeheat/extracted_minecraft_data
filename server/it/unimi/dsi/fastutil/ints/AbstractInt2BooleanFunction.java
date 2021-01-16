package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2BooleanFunction implements Int2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractInt2BooleanFunction() {
      super();
   }

   public void defaultReturnValue(boolean var1) {
      this.defRetValue = var1;
   }

   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
