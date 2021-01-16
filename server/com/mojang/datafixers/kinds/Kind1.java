package com.mojang.datafixers.kinds;

import com.mojang.datafixers.Products;

public interface Kind1<F extends K1, Mu extends Kind1.Mu> extends App<Mu, F> {
   static <F extends K1, Proof extends Kind1.Mu> Kind1<F, Proof> unbox(App<Proof, F> var0) {
      return (Kind1)var0;
   }

   default <T1> Products.P1<F, T1> group(App<F, T1> var1) {
      return new Products.P1(var1);
   }

   default <T1, T2> Products.P2<F, T1, T2> group(App<F, T1> var1, App<F, T2> var2) {
      return new Products.P2(var1, var2);
   }

   default <T1, T2, T3> Products.P3<F, T1, T2, T3> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3) {
      return new Products.P3(var1, var2, var3);
   }

   default <T1, T2, T3, T4> Products.P4<F, T1, T2, T3, T4> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4) {
      return new Products.P4(var1, var2, var3, var4);
   }

   default <T1, T2, T3, T4, T5> Products.P5<F, T1, T2, T3, T4, T5> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5) {
      return new Products.P5(var1, var2, var3, var4, var5);
   }

   default <T1, T2, T3, T4, T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6) {
      return new Products.P6(var1, var2, var3, var4, var5, var6);
   }

   default <T1, T2, T3, T4, T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7) {
      return new Products.P7(var1, var2, var3, var4, var5, var6, var7);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8) {
      return new Products.P8(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9) {
      return new Products.P9(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10) {
      return new Products.P10(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11) {
      return new Products.P11(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12) {
      return new Products.P12(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Products.P13<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13) {
      return new Products.P13(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Products.P14<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14) {
      return new Products.P14(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Products.P15<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14, App<F, T15> var15) {
      return new Products.P15(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Products.P16<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> group(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14, App<F, T15> var15, App<F, T16> var16) {
      return new Products.P16(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   public interface Mu extends K1 {
   }
}
