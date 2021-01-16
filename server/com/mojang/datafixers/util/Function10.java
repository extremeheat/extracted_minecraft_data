package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> {
   R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8, T9 var9, T10 var10);

   default Function<T1, Function9<T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> curry() {
      return (var1) -> {
         return (var2, var3, var4, var5, var6, var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default BiFunction<T1, T2, Function8<T3, T4, T5, T6, T7, T8, T9, T10, R>> curry2() {
      return (var1, var2) -> {
         return (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function3<T1, T2, T3, Function7<T4, T5, T6, T7, T8, T9, T10, R>> curry3() {
      return (var1, var2, var3) -> {
         return (var4, var5, var6, var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function4<T1, T2, T3, T4, Function6<T5, T6, T7, T8, T9, T10, R>> curry4() {
      return (var1, var2, var3, var4) -> {
         return (var5, var6, var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function5<T1, T2, T3, T4, T5, Function5<T6, T7, T8, T9, T10, R>> curry5() {
      return (var1, var2, var3, var4, var5) -> {
         return (var6, var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function6<T1, T2, T3, T4, T5, T6, Function4<T7, T8, T9, T10, R>> curry6() {
      return (var1, var2, var3, var4, var5, var6) -> {
         return (var7, var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function7<T1, T2, T3, T4, T5, T6, T7, Function3<T8, T9, T10, R>> curry7() {
      return (var1, var2, var3, var4, var5, var6, var7) -> {
         return (var8, var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function8<T1, T2, T3, T4, T5, T6, T7, T8, BiFunction<T9, T10, R>> curry8() {
      return (var1, var2, var3, var4, var5, var6, var7, var8) -> {
         return (var9, var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }

   default Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Function<T10, R>> curry9() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         return (var10) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         };
      };
   }
}
