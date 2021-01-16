package com.mojang.datafixers.functions;

import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class Out<A> extends PointFree<Function<A, A>> {
   private final RecursivePoint.RecursivePointType<A> type;

   public Out(RecursivePoint.RecursivePointType<A> var1) {
      super();
      this.type = var1;
   }

   public String toString(int var1) {
      return "Out[" + this.type + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof Out && Objects.equals(this.type, ((Out)var1).type);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type});
   }

   public Function<DynamicOps<?>, Function<A, A>> eval() {
      return (var0) -> {
         return Function.identity();
      };
   }
}
