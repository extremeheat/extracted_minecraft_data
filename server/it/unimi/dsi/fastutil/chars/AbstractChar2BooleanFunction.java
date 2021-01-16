package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;

public abstract class AbstractChar2BooleanFunction implements Char2BooleanFunction, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected boolean defRetValue;

   protected AbstractChar2BooleanFunction() {
      super();
   }

   public void defaultReturnValue(boolean var1) {
      this.defRetValue = var1;
   }

   public boolean defaultReturnValue() {
      return this.defRetValue;
   }
}
