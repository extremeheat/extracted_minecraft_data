package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function7<T1, T2, T3, T4, T5, T6, T7, R> {
   R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7);

   default Function<T1, Function6<T2, T3, T4, T5, T6, T7, R>> curry() {
      return (var1) -> {
         return (var2, var3, var4, var5, var6, var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }

   default BiFunction<T1, T2, Function5<T3, T4, T5, T6, T7, R>> curry2() {
      return (var1, var2) -> {
         return (var3, var4, var5, var6, var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }

   default Function3<T1, T2, T3, Function4<T4, T5, T6, T7, R>> curry3() {
      return (var1, var2, var3) -> {
         return (var4, var5, var6, var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }

   default Function4<T1, T2, T3, T4, Function3<T5, T6, T7, R>> curry4() {
      return (var1, var2, var3, var4) -> {
         return (var5, var6, var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }

   default Function5<T1, T2, T3, T4, T5, BiFunction<T6, T7, R>> curry5() {
      return (var1, var2, var3, var4, var5) -> {
         return (var6, var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }

   default Function6<T1, T2, T3, T4, T5, T6, Function<T7, R>> curry6() {
      return (var1, var2, var3, var4, var5, var6) -> {
         return (var7) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7);
         };
      };
   }
}
