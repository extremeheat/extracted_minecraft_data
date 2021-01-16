package com.mojang.datafixers;

import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class View<A, B> implements App2<View.Mu, A, B> {
   private final Type<A> type;
   protected final Type<B> newType;
   private final PointFree<Function<A, B>> function;

   static <A, B> View<A, B> unbox(App2<View.Mu, A, B> var0) {
      return (View)var0;
   }

   public static <A> View<A, A> nopView(Type<A> var0) {
      return create(var0, var0, Functions.id());
   }

   public View(Type<A> var1, Type<B> var2, PointFree<Function<A, B>> var3) {
      super();
      this.type = var1;
      this.newType = var2;
      this.function = var3;
   }

   public Type<A> type() {
      return this.type;
   }

   public Type<B> newType() {
      return this.newType;
   }

   public PointFree<Function<A, B>> function() {
      return this.function;
   }

   public Type<Function<A, B>> getFuncType() {
      return DSL.func(this.type, this.newType);
   }

   public String toString() {
      return "View[" + this.function + "," + this.newType + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         View var2 = (View)var1;
         return Objects.equals(this.type, var2.type) && Objects.equals(this.newType, var2.newType) && Objects.equals(this.function, var2.function);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, this.newType, this.function});
   }

   public Optional<? extends View<A, B>> rewrite(PointFreeRule var1) {
      return var1.rewrite(DSL.func(this.type, this.newType), this.function()).map((var1x) -> {
         return create(this.type, this.newType, var1x);
      });
   }

   public View<A, B> rewriteOrNop(PointFreeRule var1) {
      return (View)DataFixUtils.orElse(this.rewrite(var1), this);
   }

   public <C> View<A, C> flatMap(Function<Type<B>, View<B, C>> var1) {
      View var2 = (View)var1.apply(this.newType);
      return new View(this.type, var2.newType, Functions.comp(this.newType, var2.function(), this.function()));
   }

   public static <A, B> View<A, B> create(Type<A> var0, Type<B> var1, PointFree<Function<A, B>> var2) {
      return new View(var0, var1, var2);
   }

   public static <A, B> View<A, B> create(String var0, Type<A> var1, Type<B> var2, Function<DynamicOps<?>, Function<A, B>> var3) {
      return new View(var1, var2, Functions.fun(var0, var3));
   }

   public <C> View<C, B> compose(View<C, A> var1) {
      if (Objects.equals(this.function(), Functions.id())) {
         return new View(var1.type(), this.newType(), var1.function());
      } else {
         return Objects.equals(var1.function(), Functions.id()) ? new View(var1.type(), this.newType(), this.function()) : create(var1.type, this.newType, Functions.comp(var1.newType, this.function(), var1.function()));
      }
   }

   static final class Mu implements K2 {
      Mu() {
         super();
      }
   }
}
