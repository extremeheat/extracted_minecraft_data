package net.minecraft.util;

import java.util.function.Supplier;

public class LazyLoadBase<T> {
   private Supplier<T> field_201152_a;
   private T field_179283_a;

   public LazyLoadBase(Supplier<T> var1) {
      super();
      this.field_201152_a = var1;
   }

   public T func_179281_c() {
      Supplier var1 = this.field_201152_a;
      if (var1 != null) {
         this.field_179283_a = var1.get();
         this.field_201152_a = null;
      }

      return this.field_179283_a;
   }
}
