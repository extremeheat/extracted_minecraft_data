package com.mojang.datafixers.kinds;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function15;
import com.mojang.datafixers.util.Function16;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Applicative<F extends K1, Mu extends Applicative.Mu> extends Functor<F, Mu> {
   static <F extends K1, Mu extends Applicative.Mu> Applicative<F, Mu> unbox(App<Mu, F> var0) {
      return (Applicative)var0;
   }

   <A> App<F, A> point(A var1);

   <A, R> Function<App<F, A>, App<F, R>> lift1(App<F, Function<A, R>> var1);

   default <A, B, R> BiFunction<App<F, A>, App<F, B>, App<F, R>> lift2(App<F, BiFunction<A, B, R>> var1) {
      return (var2, var3) -> {
         return this.ap2(var1, var2, var3);
      };
   }

   default <T1, T2, T3, R> Function3<App<F, T1>, App<F, T2>, App<F, T3>, App<F, R>> lift3(App<F, Function3<T1, T2, T3, R>> var1) {
      return (var2, var3, var4) -> {
         return this.ap3(var1, var2, var3, var4);
      };
   }

   default <T1, T2, T3, T4, R> Function4<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, R>> lift4(App<F, Function4<T1, T2, T3, T4, R>> var1) {
      return (var2, var3, var4, var5) -> {
         return this.ap4(var1, var2, var3, var4, var5);
      };
   }

   default <T1, T2, T3, T4, T5, R> Function5<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, T5>, App<F, R>> lift5(App<F, Function5<T1, T2, T3, T4, T5, R>> var1) {
      return (var2, var3, var4, var5, var6) -> {
         return this.ap5(var1, var2, var3, var4, var5, var6);
      };
   }

   default <T1, T2, T3, T4, T5, T6, R> Function6<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, T5>, App<F, T6>, App<F, R>> lift6(App<F, Function6<T1, T2, T3, T4, T5, T6, R>> var1) {
      return (var2, var3, var4, var5, var6, var7) -> {
         return this.ap6(var1, var2, var3, var4, var5, var6, var7);
      };
   }

   default <T1, T2, T3, T4, T5, T6, T7, R> Function7<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, T5>, App<F, T6>, App<F, T7>, App<F, R>> lift7(App<F, Function7<T1, T2, T3, T4, T5, T6, T7, R>> var1) {
      return (var2, var3, var4, var5, var6, var7, var8) -> {
         return this.ap7(var1, var2, var3, var4, var5, var6, var7, var8);
      };
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, R> Function8<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, T5>, App<F, T6>, App<F, T7>, App<F, T8>, App<F, R>> lift8(App<F, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R>> var1) {
      return (var2, var3, var4, var5, var6, var7, var8, var9) -> {
         return this.ap8(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      };
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Function9<App<F, T1>, App<F, T2>, App<F, T3>, App<F, T4>, App<F, T5>, App<F, T6>, App<F, T7>, App<F, T8>, App<F, T9>, App<F, R>> lift9(App<F, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> var1) {
      return (var2, var3, var4, var5, var6, var7, var8, var9, var10) -> {
         return this.ap9(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      };
   }

   default <A, R> App<F, R> ap(App<F, Function<A, R>> var1, App<F, A> var2) {
      return (App)this.lift1(var1).apply(var2);
   }

   default <A, R> App<F, R> ap(Function<A, R> var1, App<F, A> var2) {
      return this.map(var1, var2);
   }

   default <A, B, R> App<F, R> ap2(App<F, BiFunction<A, B, R>> var1, App<F, A> var2, App<F, B> var3) {
      Function var4 = (var0) -> {
         return (var1) -> {
            return (var2) -> {
               return var0.apply(var1, var2);
            };
         };
      };
      return this.ap(this.ap(this.map(var4, var1), var2), var3);
   }

   default <T1, T2, T3, R> App<F, R> ap3(App<F, Function3<T1, T2, T3, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4) {
      return this.ap2(this.ap(this.map(Function3::curry, var1), var2), var3, var4);
   }

   default <T1, T2, T3, T4, R> App<F, R> ap4(App<F, Function4<T1, T2, T3, T4, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5) {
      return this.ap2(this.ap2(this.map(Function4::curry2, var1), var2, var3), var4, var5);
   }

   default <T1, T2, T3, T4, T5, R> App<F, R> ap5(App<F, Function5<T1, T2, T3, T4, T5, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6) {
      return this.ap3(this.ap2(this.map(Function5::curry2, var1), var2, var3), var4, var5, var6);
   }

   default <T1, T2, T3, T4, T5, T6, R> App<F, R> ap6(App<F, Function6<T1, T2, T3, T4, T5, T6, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7) {
      return this.ap3(this.ap3(this.map(Function6::curry3, var1), var2, var3, var4), var5, var6, var7);
   }

   default <T1, T2, T3, T4, T5, T6, T7, R> App<F, R> ap7(App<F, Function7<T1, T2, T3, T4, T5, T6, T7, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8) {
      return this.ap4(this.ap3(this.map(Function7::curry3, var1), var2, var3, var4), var5, var6, var7, var8);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, R> App<F, R> ap8(App<F, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9) {
      return this.ap4(this.ap4(this.map(Function8::curry4, var1), var2, var3, var4, var5), var6, var7, var8, var9);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> App<F, R> ap9(App<F, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10) {
      return this.ap5(this.ap4(this.map(Function9::curry4, var1), var2, var3, var4, var5), var6, var7, var8, var9, var10);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> App<F, R> ap10(App<F, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11) {
      return this.ap5(this.ap5(this.map(Function10::curry5, var1), var2, var3, var4, var5, var6), var7, var8, var9, var10, var11);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> App<F, R> ap11(App<F, Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12) {
      return this.ap6(this.ap5(this.map(Function11::curry5, var1), var2, var3, var4, var5, var6), var7, var8, var9, var10, var11, var12);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> App<F, R> ap12(App<F, Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12, App<F, T12> var13) {
      return this.ap6(this.ap6(this.map(Function12::curry6, var1), var2, var3, var4, var5, var6, var7), var8, var9, var10, var11, var12, var13);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> App<F, R> ap13(App<F, Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12, App<F, T12> var13, App<F, T13> var14) {
      return this.ap7(this.ap6(this.map(Function13::curry6, var1), var2, var3, var4, var5, var6, var7), var8, var9, var10, var11, var12, var13, var14);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> App<F, R> ap14(App<F, Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12, App<F, T12> var13, App<F, T13> var14, App<F, T14> var15) {
      return this.ap7(this.ap7(this.map(Function14::curry7, var1), var2, var3, var4, var5, var6, var7, var8), var9, var10, var11, var12, var13, var14, var15);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> App<F, R> ap15(App<F, Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12, App<F, T12> var13, App<F, T13> var14, App<F, T14> var15, App<F, T15> var16) {
      return this.ap8(this.ap7(this.map(Function15::curry7, var1), var2, var3, var4, var5, var6, var7, var8), var9, var10, var11, var12, var13, var14, var15, var16);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> App<F, R> ap16(App<F, Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10, App<F, T10> var11, App<F, T11> var12, App<F, T12> var13, App<F, T13> var14, App<F, T14> var15, App<F, T15> var16, App<F, T16> var17) {
      return this.ap8(this.ap8(this.map(Function16::curry8, var1), var2, var3, var4, var5, var6, var7, var8, var9), var10, var11, var12, var13, var14, var15, var16, var17);
   }

   default <A, B, R> App<F, R> apply2(BiFunction<A, B, R> var1, App<F, A> var2, App<F, B> var3) {
      return this.ap2(this.point(var1), var2, var3);
   }

   default <T1, T2, T3, R> App<F, R> apply3(Function3<T1, T2, T3, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4) {
      return this.ap3(this.point(var1), var2, var3, var4);
   }

   default <T1, T2, T3, T4, R> App<F, R> apply4(Function4<T1, T2, T3, T4, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5) {
      return this.ap4(this.point(var1), var2, var3, var4, var5);
   }

   default <T1, T2, T3, T4, T5, R> App<F, R> apply5(Function5<T1, T2, T3, T4, T5, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6) {
      return this.ap5(this.point(var1), var2, var3, var4, var5, var6);
   }

   default <T1, T2, T3, T4, T5, T6, R> App<F, R> apply6(Function6<T1, T2, T3, T4, T5, T6, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7) {
      return this.ap6(this.point(var1), var2, var3, var4, var5, var6, var7);
   }

   default <T1, T2, T3, T4, T5, T6, T7, R> App<F, R> apply7(Function7<T1, T2, T3, T4, T5, T6, T7, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8) {
      return this.ap7(this.point(var1), var2, var3, var4, var5, var6, var7, var8);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, R> App<F, R> apply8(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9) {
      return this.ap8(this.point(var1), var2, var3, var4, var5, var6, var7, var8, var9);
   }

   default <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> App<F, R> apply9(Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> var1, App<F, T1> var2, App<F, T2> var3, App<F, T3> var4, App<F, T4> var5, App<F, T5> var6, App<F, T6> var7, App<F, T7> var8, App<F, T8> var9, App<F, T9> var10) {
      return this.ap9(this.point(var1), var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public interface Mu extends Functor.Mu {
   }
}
