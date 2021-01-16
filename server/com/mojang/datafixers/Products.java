package com.mojang.datafixers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
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

public interface Products {
   static <T1, T2> Products.P2<IdF.Mu, T1, T2> of(T1 var0, T2 var1) {
      return new Products.P2(IdF.create(var0), IdF.create(var1));
   }

   public static final class P16<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;
      private final App<F, T12> t12;
      private final App<F, T13> t13;
      private final App<F, T14> t14;
      private final App<F, T15> t15;
      private final App<F, T16> t16;

      public P16(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14, App<F, T15> var15, App<F, T16> var16) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
         this.t12 = var12;
         this.t13 = var13;
         this.t14 = var14;
         this.t15 = var15;
         this.t16 = var16;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>> var2) {
         return var1.ap16(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11, this.t12, this.t13, this.t14, this.t15, this.t16);
      }
   }

   public static final class P15<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;
      private final App<F, T12> t12;
      private final App<F, T13> t13;
      private final App<F, T14> t14;
      private final App<F, T15> t15;

      public P15(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14, App<F, T15> var15) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
         this.t12 = var12;
         this.t13 = var13;
         this.t14 = var14;
         this.t15 = var15;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>> var2) {
         return var1.ap15(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11, this.t12, this.t13, this.t14, this.t15);
      }
   }

   public static final class P14<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;
      private final App<F, T12> t12;
      private final App<F, T13> t13;
      private final App<F, T14> t14;

      public P14(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13, App<F, T14> var14) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
         this.t12 = var12;
         this.t13 = var13;
         this.t14 = var14;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>> var2) {
         return var1.ap14(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11, this.t12, this.t13, this.t14);
      }
   }

   public static final class P13<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;
      private final App<F, T12> t12;
      private final App<F, T13> t13;

      public P13(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12, App<F, T13> var13) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
         this.t12 = var12;
         this.t13 = var13;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>> var2) {
         return var1.ap13(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11, this.t12, this.t13);
      }
   }

   public static final class P12<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;
      private final App<F, T12> t12;

      public P12(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11, App<F, T12> var12) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
         this.t12 = var12;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>> var2) {
         return var1.ap12(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11, this.t12);
      }
   }

   public static final class P11<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;
      private final App<F, T11> t11;

      public P11(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10, App<F, T11> var11) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
         this.t11 = var11;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>> var2) {
         return var1.ap11(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10, this.t11);
      }
   }

   public static final class P10<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;
      private final App<F, T10> t10;

      public P10(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9, App<F, T10> var10) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
         this.t10 = var10;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> var2) {
         return var1.ap10(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9, this.t10);
      }
   }

   public static final class P9<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;
      private final App<F, T9> t9;

      public P9(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8, App<F, T9> var9) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
         this.t9 = var9;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> var2) {
         return var1.ap9(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8, this.t9);
      }
   }

   public static final class P8<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;
      private final App<F, T8> t8;

      public P8(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7, App<F, T8> var8) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
         this.t8 = var8;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public App<F, T4> t4() {
         return this.t4;
      }

      public App<F, T5> t5() {
         return this.t5;
      }

      public App<F, T6> t6() {
         return this.t6;
      }

      public App<F, T7> t7() {
         return this.t7;
      }

      public App<F, T8> t8() {
         return this.t8;
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function8<T1, T2, T3, T4, T5, T6, T7, T8, R>> var2) {
         return var1.ap8(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, this.t8);
      }
   }

   public static final class P7<F extends K1, T1, T2, T3, T4, T5, T6, T7> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;
      private final App<F, T7> t7;

      public P7(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6, App<F, T7> var7) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
         this.t7 = var7;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public App<F, T4> t4() {
         return this.t4;
      }

      public App<F, T5> t5() {
         return this.t5;
      }

      public App<F, T6> t6() {
         return this.t6;
      }

      public App<F, T7> t7() {
         return this.t7;
      }

      public <T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(App<F, T8> var1) {
         return new Products.P8(this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7, var1);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function7<T1, T2, T3, T4, T5, T6, T7, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function7<T1, T2, T3, T4, T5, T6, T7, R>> var2) {
         return var1.ap7(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, this.t7);
      }
   }

   public static final class P6<F extends K1, T1, T2, T3, T4, T5, T6> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;
      private final App<F, T6> t6;

      public P6(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5, App<F, T6> var6) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
         this.t6 = var6;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public App<F, T4> t4() {
         return this.t4;
      }

      public App<F, T5> t5() {
         return this.t5;
      }

      public App<F, T6> t6() {
         return this.t6;
      }

      public <T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(App<F, T7> var1) {
         return new Products.P7(this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, var1);
      }

      public <T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P2<F, T7, T8> var1) {
         return new Products.P8(this.t1, this.t2, this.t3, this.t4, this.t5, this.t6, var1.t1, var1.t2);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function6<T1, T2, T3, T4, T5, T6, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function6<T1, T2, T3, T4, T5, T6, R>> var2) {
         return var1.ap6(var2, this.t1, this.t2, this.t3, this.t4, this.t5, this.t6);
      }
   }

   public static final class P5<F extends K1, T1, T2, T3, T4, T5> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;
      private final App<F, T5> t5;

      public P5(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4, App<F, T5> var5) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
         this.t5 = var5;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public App<F, T4> t4() {
         return this.t4;
      }

      public App<F, T5> t5() {
         return this.t5;
      }

      public <T6> Products.P6<F, T1, T2, T3, T4, T5, T6> and(App<F, T6> var1) {
         return new Products.P6(this.t1, this.t2, this.t3, this.t4, this.t5, var1);
      }

      public <T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(Products.P2<F, T6, T7> var1) {
         return new Products.P7(this.t1, this.t2, this.t3, this.t4, this.t5, var1.t1, var1.t2);
      }

      public <T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P3<F, T6, T7, T8> var1) {
         return new Products.P8(this.t1, this.t2, this.t3, this.t4, this.t5, var1.t1, var1.t2, var1.t3);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function5<T1, T2, T3, T4, T5, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function5<T1, T2, T3, T4, T5, R>> var2) {
         return var1.ap5(var2, this.t1, this.t2, this.t3, this.t4, this.t5);
      }
   }

   public static final class P4<F extends K1, T1, T2, T3, T4> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;
      private final App<F, T4> t4;

      public P4(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3, App<F, T4> var4) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
         this.t4 = var4;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public App<F, T4> t4() {
         return this.t4;
      }

      public <T5> Products.P5<F, T1, T2, T3, T4, T5> and(App<F, T5> var1) {
         return new Products.P5(this.t1, this.t2, this.t3, this.t4, var1);
      }

      public <T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> and(Products.P2<F, T5, T6> var1) {
         return new Products.P6(this.t1, this.t2, this.t3, this.t4, var1.t1, var1.t2);
      }

      public <T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(Products.P3<F, T5, T6, T7> var1) {
         return new Products.P7(this.t1, this.t2, this.t3, this.t4, var1.t1, var1.t2, var1.t3);
      }

      public <T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P4<F, T5, T6, T7, T8> var1) {
         return new Products.P8(this.t1, this.t2, this.t3, this.t4, var1.t1, var1.t2, var1.t3, var1.t4);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function4<T1, T2, T3, T4, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function4<T1, T2, T3, T4, R>> var2) {
         return var1.ap4(var2, this.t1, this.t2, this.t3, this.t4);
      }
   }

   public static final class P3<F extends K1, T1, T2, T3> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;
      private final App<F, T3> t3;

      public P3(App<F, T1> var1, App<F, T2> var2, App<F, T3> var3) {
         super();
         this.t1 = var1;
         this.t2 = var2;
         this.t3 = var3;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public App<F, T3> t3() {
         return this.t3;
      }

      public <T4> Products.P4<F, T1, T2, T3, T4> and(App<F, T4> var1) {
         return new Products.P4(this.t1, this.t2, this.t3, var1);
      }

      public <T4, T5> Products.P5<F, T1, T2, T3, T4, T5> and(Products.P2<F, T4, T5> var1) {
         return new Products.P5(this.t1, this.t2, this.t3, var1.t1, var1.t2);
      }

      public <T4, T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> and(Products.P3<F, T4, T5, T6> var1) {
         return new Products.P6(this.t1, this.t2, this.t3, var1.t1, var1.t2, var1.t3);
      }

      public <T4, T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(Products.P4<F, T4, T5, T6, T7> var1) {
         return new Products.P7(this.t1, this.t2, this.t3, var1.t1, var1.t2, var1.t3, var1.t4);
      }

      public <T4, T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P5<F, T4, T5, T6, T7, T8> var1) {
         return new Products.P8(this.t1, this.t2, this.t3, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function3<T1, T2, T3, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function3<T1, T2, T3, R>> var2) {
         return var1.ap3(var2, this.t1, this.t2, this.t3);
      }
   }

   public static final class P2<F extends K1, T1, T2> {
      private final App<F, T1> t1;
      private final App<F, T2> t2;

      public P2(App<F, T1> var1, App<F, T2> var2) {
         super();
         this.t1 = var1;
         this.t2 = var2;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public App<F, T2> t2() {
         return this.t2;
      }

      public <T3> Products.P3<F, T1, T2, T3> and(App<F, T3> var1) {
         return new Products.P3(this.t1, this.t2, var1);
      }

      public <T3, T4> Products.P4<F, T1, T2, T3, T4> and(Products.P2<F, T3, T4> var1) {
         return new Products.P4(this.t1, this.t2, var1.t1, var1.t2);
      }

      public <T3, T4, T5> Products.P5<F, T1, T2, T3, T4, T5> and(Products.P3<F, T3, T4, T5> var1) {
         return new Products.P5(this.t1, this.t2, var1.t1, var1.t2, var1.t3);
      }

      public <T3, T4, T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> and(Products.P4<F, T3, T4, T5, T6> var1) {
         return new Products.P6(this.t1, this.t2, var1.t1, var1.t2, var1.t3, var1.t4);
      }

      public <T3, T4, T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(Products.P5<F, T3, T4, T5, T6, T7> var1) {
         return new Products.P7(this.t1, this.t2, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5);
      }

      public <T3, T4, T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P6<F, T3, T4, T5, T6, T7, T8> var1) {
         return new Products.P8(this.t1, this.t2, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5, var1.t6);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, BiFunction<T1, T2, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, BiFunction<T1, T2, R>> var2) {
         return var1.ap2(var2, this.t1, this.t2);
      }
   }

   public static final class P1<F extends K1, T1> {
      private final App<F, T1> t1;

      public P1(App<F, T1> var1) {
         super();
         this.t1 = var1;
      }

      public App<F, T1> t1() {
         return this.t1;
      }

      public <T2> Products.P2<F, T1, T2> and(App<F, T2> var1) {
         return new Products.P2(this.t1, var1);
      }

      public <T2, T3> Products.P3<F, T1, T2, T3> and(Products.P2<F, T2, T3> var1) {
         return new Products.P3(this.t1, var1.t1, var1.t2);
      }

      public <T2, T3, T4> Products.P4<F, T1, T2, T3, T4> and(Products.P3<F, T2, T3, T4> var1) {
         return new Products.P4(this.t1, var1.t1, var1.t2, var1.t3);
      }

      public <T2, T3, T4, T5> Products.P5<F, T1, T2, T3, T4, T5> and(Products.P4<F, T2, T3, T4, T5> var1) {
         return new Products.P5(this.t1, var1.t1, var1.t2, var1.t3, var1.t4);
      }

      public <T2, T3, T4, T5, T6> Products.P6<F, T1, T2, T3, T4, T5, T6> and(Products.P5<F, T2, T3, T4, T5, T6> var1) {
         return new Products.P6(this.t1, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5);
      }

      public <T2, T3, T4, T5, T6, T7> Products.P7<F, T1, T2, T3, T4, T5, T6, T7> and(Products.P6<F, T2, T3, T4, T5, T6, T7> var1) {
         return new Products.P7(this.t1, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5, var1.t6);
      }

      public <T2, T3, T4, T5, T6, T7, T8> Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> and(Products.P7<F, T2, T3, T4, T5, T6, T7, T8> var1) {
         return new Products.P8(this.t1, var1.t1, var1.t2, var1.t3, var1.t4, var1.t5, var1.t6, var1.t7);
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, Function<T1, R> var2) {
         return this.apply(var1, var1.point(var2));
      }

      public <R> App<F, R> apply(Applicative<F, ?> var1, App<F, Function<T1, R>> var2) {
         return var1.ap(var2, this.t1);
      }
   }
}
