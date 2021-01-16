package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;

@GwtCompatible
public abstract class Escaper {
   private final Function<String, String> asFunction = new Function<String, String>() {
      public String apply(String var1) {
         return Escaper.this.escape(var1);
      }
   };

   protected Escaper() {
      super();
   }

   public abstract String escape(String var1);

   public final Function<String, String> asFunction() {
      return this.asFunction;
   }
}
