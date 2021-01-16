package com.mojang.datafixers.functions;

import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

final class Id<A> extends PointFree<Function<A, A>> {
   public Id() {
      super();
   }

   public boolean equals(Object var1) {
      return var1 instanceof Id;
   }

   public int hashCode() {
      return Id.class.hashCode();
   }

   public String toString(int var1) {
      return "id";
   }

   public Function<DynamicOps<?>, Function<A, A>> eval() {
      return (var0) -> {
         return Function.identity();
      };
   }
}
