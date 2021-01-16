package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function3<T1, T2, T3, R> {
   R apply(T1 var1, T2 var2, T3 var3);

   default Function<T1, BiFunction<T2, T3, R>> curry() {
      return (var1) -> {
         return (var2, var3) -> {
            return this.apply(var1, var2, var3);
         };
      };
   }

   default BiFunction<T1, T2, Function<T3, R>> curry2() {
      return (var1, var2) -> {
         return (var3) -> {
            return this.apply(var1, var2, var3);
         };
      };
   }
}
