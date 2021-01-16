package com.mojang.datafixers.functions;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.Algebra;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

public abstract class Functions {
   private static final Id<?> ID = new Id();

   public Functions() {
      super();
   }

   public static <A, B, C> PointFree<Function<A, C>> comp(Type<B> var0, PointFree<Function<B, C>> var1, PointFree<Function<A, B>> var2) {
      if (Objects.equals(var1, id())) {
         return var2;
      } else {
         return (PointFree)(Objects.equals(var2, id()) ? var1 : new Comp(var0, var1, var2));
      }
   }

   public static <A, B> PointFree<Function<A, B>> fun(String var0, Function<DynamicOps<?>, Function<A, B>> var1) {
      return new FunctionWrapper(var0, var1);
   }

   public static <A, B> PointFree<B> app(PointFree<Function<A, B>> var0, PointFree<A> var1, Type<A> var2) {
      return new Apply(var0, var1, var2);
   }

   public static <S, T, A, B> PointFree<Function<Function<A, B>, Function<S, T>>> profunctorTransformer(Optic<? super FunctionType.Instance.Mu, S, T, A, B> var0) {
      return new ProfunctorTransformer(var0);
   }

   public static <A> Bang<A> bang() {
      return new Bang();
   }

   public static <A> PointFree<Function<A, A>> in(RecursivePoint.RecursivePointType<A> var0) {
      return new In(var0);
   }

   public static <A> PointFree<Function<A, A>> out(RecursivePoint.RecursivePointType<A> var0) {
      return new Out(var0);
   }

   public static <A, B> PointFree<Function<A, B>> fold(RecursivePoint.RecursivePointType<A> var0, RewriteResult<?, B> var1, Algebra var2, int var3) {
      return new Fold(var0, var1, var2, var3);
   }

   public static <A> PointFree<Function<A, A>> id() {
      return ID;
   }
}
