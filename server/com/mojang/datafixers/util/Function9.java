package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
   R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8, T9 var9);

   default Function<T1, Function8<T2, T3, T4, T5, T6, T7, T8, T9, R>> curry() {
      return (var1) -> {
         return (var2, var3, var4, var5, var6, var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default BiFunction<T1, T2, Function7<T3, T4, T5, T6, T7, T8, T9, R>> curry2() {
      return (var1, var2) -> {
         return (var3, var4, var5, var6, var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function3<T1, T2, T3, Function6<T4, T5, T6, T7, T8, T9, R>> curry3() {
      return (var1, var2, var3) -> {
         return (var4, var5, var6, var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function4<T1, T2, T3, T4, Function5<T5, T6, T7, T8, T9, R>> curry4() {
      return (var1, var2, var3, var4) -> {
         return (var5, var6, var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function5<T1, T2, T3, T4, T5, Function4<T6, T7, T8, T9, R>> curry5() {
      return (var1, var2, var3, var4, var5) -> {
         return (var6, var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function6<T1, T2, T3, T4, T5, T6, Function3<T7, T8, T9, R>> curry6() {
      return (var1, var2, var3, var4, var5, var6) -> {
         return (var7, var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function7<T1, T2, T3, T4, T5, T6, T7, BiFunction<T8, T9, R>> curry7() {
      return (var1, var2, var3, var4, var5, var6, var7) -> {
         return (var8, var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }

   default Function8<T1, T2, T3, T4, T5, T6, T7, T8, Function<T9, R>> curry8() {
      return (var1, var2, var3, var4, var5, var6, var7, var8) -> {
         return (var9) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         };
      };
   }
}
