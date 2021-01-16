package com.mojang.datafixers.functions;

import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

final class Bang<A> extends PointFree<Function<A, Void>> {
   Bang() {
      super();
   }

   public String toString(int var1) {
      return "!";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Bang;
   }

   public int hashCode() {
      return Bang.class.hashCode();
   }

   public Function<DynamicOps<?>, Function<A, Void>> eval() {
      return (var0) -> {
         return (var0x) -> {
            return null;
         };
      };
   }
}
