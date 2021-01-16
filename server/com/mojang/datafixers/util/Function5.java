package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function5<T1, T2, T3, T4, T5, R> {
   R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);

   default Function<T1, Function4<T2, T3, T4, T5, R>> curry() {
      return (var1) -> {
         return (var2, var3, var4, var5) -> {
            return this.apply(var1, var2, var3, var4, var5);
         };
      };
   }

   default BiFunction<T1, T2, Function3<T3, T4, T5, R>> curry2() {
      return (var1, var2) -> {
         return (var3, var4, var5) -> {
            return this.apply(var1, var2, var3, var4, var5);
         };
      };
   }

   default Function3<T1, T2, T3, BiFunction<T4, T5, R>> curry3() {
      return (var1, var2, var3) -> {
         return (var4, var5) -> {
            return this.apply(var1, var2, var3, var4, var5);
         };
      };
   }

   default Function4<T1, T2, T3, T4, Function<T5, R>> curry4() {
      return (var1, var2, var3, var4) -> {
         return (var5) -> {
            return this.apply(var1, var2, var3, var4, var5);
         };
      };
   }
}
