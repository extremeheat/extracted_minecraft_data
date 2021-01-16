package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TypeRewriteRule {
   <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1);

   static TypeRewriteRule nop() {
      return TypeRewriteRule.Nop.INSTANCE;
   }

   static TypeRewriteRule seq(List<TypeRewriteRule> var0) {
      return new TypeRewriteRule.Seq(var0);
   }

   static TypeRewriteRule seq(TypeRewriteRule var0, TypeRewriteRule var1) {
      if (Objects.equals(var0, nop())) {
         return var1;
      } else {
         return Objects.equals(var1, nop()) ? var0 : seq(ImmutableList.of(var0, var1));
      }
   }

   static TypeRewriteRule seq(TypeRewriteRule var0, TypeRewriteRule... var1) {
      if (var1.length == 0) {
         return var0;
      } else {
         int var2 = var1.length - 1;

         TypeRewriteRule var3;
         for(var3 = var1[var2]; var2 > 0; var3 = seq(var1[var2], var3)) {
            --var2;
         }

         return seq(var0, var3);
      }
   }

   static TypeRewriteRule orElse(TypeRewriteRule var0, TypeRewriteRule var1) {
      return orElse(var0, () -> {
         return var1;
      });
   }

   static TypeRewriteRule orElse(TypeRewriteRule var0, Supplier<TypeRewriteRule> var1) {
      return new TypeRewriteRule.OrElse(var0, var1);
   }

   static TypeRewriteRule all(TypeRewriteRule var0, boolean var1, boolean var2) {
      return new TypeRewriteRule.All(var0, var1, var2);
   }

   static TypeRewriteRule one(TypeRewriteRule var0) {
      return new TypeRewriteRule.One(var0);
   }

   static TypeRewriteRule once(TypeRewriteRule var0) {
      return orElse(var0, () -> {
         return one(once(var0));
      });
   }

   static TypeRewriteRule checkOnce(TypeRewriteRule var0, Consumer<Type<?>> var1) {
      return var0;
   }

   static TypeRewriteRule everywhere(TypeRewriteRule var0, PointFreeRule var1, boolean var2, boolean var3) {
      return new TypeRewriteRule.Everywhere(var0, var1, var2, var3);
   }

   static <B> TypeRewriteRule ifSame(Type<B> var0, RewriteResult<B, ?> var1) {
      return new TypeRewriteRule.IfSame(var0, var1);
   }

   public static class IfSame<B> implements TypeRewriteRule {
      private final Type<B> targetType;
      private final RewriteResult<B, ?> value;
      private final int hashCode;

      public IfSame(Type<B> var1, RewriteResult<B, ?> var2) {
         super();
         this.targetType = var1;
         this.value = var2;
         this.hashCode = Objects.hash(new Object[]{var1, var2});
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         return var1.ifSame(this.targetType, this.value);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.IfSame)) {
            return false;
         } else {
            TypeRewriteRule.IfSame var2 = (TypeRewriteRule.IfSame)var1;
            return Objects.equals(this.targetType, var2.targetType) && Objects.equals(this.value, var2.value);
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }

   public static class Everywhere implements TypeRewriteRule {
      protected final TypeRewriteRule rule;
      protected final PointFreeRule optimizationRule;
      protected final boolean recurse;
      private final boolean checkIndex;
      private final int hashCode;

      public Everywhere(TypeRewriteRule var1, PointFreeRule var2, boolean var3, boolean var4) {
         super();
         this.rule = var1;
         this.optimizationRule = var2;
         this.recurse = var3;
         this.checkIndex = var4;
         this.hashCode = Objects.hash(new Object[]{var1, var2, var3, var4});
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         return var1.everywhere(this.rule, this.optimizationRule, this.recurse, this.checkIndex);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.Everywhere)) {
            return false;
         } else {
            TypeRewriteRule.Everywhere var2 = (TypeRewriteRule.Everywhere)var1;
            return Objects.equals(this.rule, var2.rule) && Objects.equals(this.optimizationRule, var2.optimizationRule) && this.recurse == var2.recurse && this.checkIndex == var2.checkIndex;
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }

   public static class CheckOnce implements TypeRewriteRule {
      private final TypeRewriteRule rule;
      private final Consumer<Type<?>> onFail;

      public CheckOnce(TypeRewriteRule var1, Consumer<Type<?>> var2) {
         super();
         this.rule = var1;
         this.onFail = var2;
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         Optional var2 = this.rule.rewrite(var1);
         if (!var2.isPresent() || Objects.equals(((RewriteResult)var2.get()).view.function(), Functions.id())) {
            this.onFail.accept(var1);
         }

         return var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof TypeRewriteRule.CheckOnce && Objects.equals(this.rule, ((TypeRewriteRule.CheckOnce)var1).rule);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.rule});
      }
   }

   public static class One implements TypeRewriteRule {
      private final TypeRewriteRule rule;

      public One(TypeRewriteRule var1) {
         super();
         this.rule = var1;
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         return var1.one(this.rule);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.One)) {
            return false;
         } else {
            TypeRewriteRule.One var2 = (TypeRewriteRule.One)var1;
            return Objects.equals(this.rule, var2.rule);
         }
      }

      public int hashCode() {
         return this.rule.hashCode();
      }
   }

   public static class All implements TypeRewriteRule {
      private final TypeRewriteRule rule;
      private final boolean recurse;
      private final boolean checkIndex;
      private final int hashCode;

      public All(TypeRewriteRule var1, boolean var2, boolean var3) {
         super();
         this.rule = var1;
         this.recurse = var2;
         this.checkIndex = var3;
         this.hashCode = Objects.hash(new Object[]{var1, var2, var3});
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         return Optional.of(var1.all(this.rule, this.recurse, this.checkIndex));
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.All)) {
            return false;
         } else {
            TypeRewriteRule.All var2 = (TypeRewriteRule.All)var1;
            return Objects.equals(this.rule, var2.rule) && this.recurse == var2.recurse && this.checkIndex == var2.checkIndex;
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }

   public static final class OrElse implements TypeRewriteRule {
      protected final TypeRewriteRule first;
      protected final Supplier<TypeRewriteRule> second;
      private final int hashCode;

      public OrElse(TypeRewriteRule var1, Supplier<TypeRewriteRule> var2) {
         super();
         this.first = var1;
         this.second = var2;
         this.hashCode = Objects.hash(new Object[]{var1, var2});
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         Optional var2 = this.first.rewrite(var1);
         return var2.isPresent() ? var2 : ((TypeRewriteRule)this.second.get()).rewrite(var1);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.OrElse)) {
            return false;
         } else {
            TypeRewriteRule.OrElse var2 = (TypeRewriteRule.OrElse)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }

   public static final class Seq implements TypeRewriteRule {
      protected final List<TypeRewriteRule> rules;
      private final int hashCode;

      public Seq(List<TypeRewriteRule> var1) {
         super();
         this.rules = ImmutableList.copyOf((Collection)var1);
         this.hashCode = this.rules.hashCode();
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         RewriteResult var2 = RewriteResult.nop(var1);

         Optional var5;
         for(Iterator var3 = this.rules.iterator(); var3.hasNext(); var2 = (RewriteResult)var5.get()) {
            TypeRewriteRule var4 = (TypeRewriteRule)var3.next();
            var5 = this.cap1(var4, var2);
            if (!var5.isPresent()) {
               return Optional.empty();
            }
         }

         return Optional.of(var2);
      }

      protected <A, B> Optional<RewriteResult<A, ?>> cap1(TypeRewriteRule var1, RewriteResult<A, B> var2) {
         return var1.rewrite(var2.view.newType).map((var1x) -> {
            return var1x.compose(var2);
         });
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TypeRewriteRule.Seq)) {
            return false;
         } else {
            TypeRewriteRule.Seq var2 = (TypeRewriteRule.Seq)var1;
            return Objects.equals(this.rules, var2.rules);
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }

   public static enum Nop implements TypeRewriteRule, Supplier<TypeRewriteRule> {
      INSTANCE;

      private Nop() {
      }

      public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1) {
         return Optional.of(RewriteResult.nop(var1));
      }

      public TypeRewriteRule get() {
         return this;
      }
   }
}
