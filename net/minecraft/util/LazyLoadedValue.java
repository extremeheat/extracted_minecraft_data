package net.minecraft.util;

import java.util.function.Supplier;

public class LazyLoadedValue {
   private Supplier factory;
   private Object value;

   public LazyLoadedValue(Supplier var1) {
      this.factory = var1;
   }

   public Object get() {
      Supplier var1 = this.factory;
      if (var1 != null) {
         this.value = var1.get();
         this.factory = null;
      }

      return this.value;
   }
}
