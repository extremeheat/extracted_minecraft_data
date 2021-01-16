package com.mojang.datafixers.functions;

import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class FunctionWrapper<A, B> extends PointFree<Function<A, B>> {
   private final String name;
   protected final Function<DynamicOps<?>, Function<A, B>> fun;

   FunctionWrapper(String var1, Function<DynamicOps<?>, Function<A, B>> var2) {
      super();
      this.name = var1;
      this.fun = var2;
   }

   public String toString(int var1) {
      return "fun[" + this.name + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FunctionWrapper var2 = (FunctionWrapper)var1;
         return Objects.equals(this.fun, var2.fun);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.fun});
   }

   public Function<DynamicOps<?>, Function<A, B>> eval() {
      return this.fun;
   }
}
