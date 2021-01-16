package com.mojang.datafixers.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> {
   R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8, T9 var9, T10 var10, T11 var11, T12 var12, T13 var13, T14 var14, T15 var15, T16 var16);

   default Function<T1, Function15<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry() {
      return (var1) -> {
         return (var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default BiFunction<T1, T2, Function14<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry2() {
      return (var1, var2) -> {
         return (var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function3<T1, T2, T3, Function13<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry3() {
      return (var1, var2, var3) -> {
         return (var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function4<T1, T2, T3, T4, Function12<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry4() {
      return (var1, var2, var3, var4) -> {
         return (var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function5<T1, T2, T3, T4, T5, Function11<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry5() {
      return (var1, var2, var3, var4, var5) -> {
         return (var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function6<T1, T2, T3, T4, T5, T6, Function10<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry6() {
      return (var1, var2, var3, var4, var5, var6) -> {
         return (var7, var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function7<T1, T2, T3, T4, T5, T6, T7, Function9<T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> curry7() {
      return (var1, var2, var3, var4, var5, var6, var7) -> {
         return (var8, var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function8<T1, T2, T3, T4, T5, T6, T7, T8, Function8<T9, T10, T11, T12, T13, T14, T15, T16, R>> curry8() {
      return (var1, var2, var3, var4, var5, var6, var7, var8) -> {
         return (var9, var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Function7<T10, T11, T12, T13, T14, T15, T16, R>> curry9() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         return (var10, var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Function6<T11, T12, T13, T14, T15, T16, R>> curry10() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10) -> {
         return (var11, var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Function5<T12, T13, T14, T15, T16, R>> curry11() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11) -> {
         return (var12, var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Function4<T13, T14, T15, T16, R>> curry12() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12) -> {
         return (var13, var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Function3<T14, T15, T16, R>> curry13() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13) -> {
         return (var14, var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, BiFunction<T15, T16, R>> curry14() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14) -> {
         return (var15, var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }

   default Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Function<T16, R>> curry15() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15) -> {
         return (var16) -> {
            return this.apply(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
         };
      };
   }
}
