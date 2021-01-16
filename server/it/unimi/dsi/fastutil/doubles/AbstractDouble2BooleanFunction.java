package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;

public abstract class AbstractDouble2BooleanFunction implements Double2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractDouble2BooleanFunction() {
      super();
   }

   public void defaultReturnValue(boolean var1) {
      this.defRetValue = var1;
   }

   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
