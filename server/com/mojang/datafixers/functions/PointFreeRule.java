package com.mojang.datafixers.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.View;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.types.families.ListAlgebra;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public interface PointFreeRule {
   <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2);

   default <A, B> Optional<View<A, B>> rewrite(View<A, B> var1) {
      return this.rewrite(var1.getFuncType(), var1.function()).map((var1x) -> {
         return View.create(var1.type(), var1.newType(), var1x);
      });
   }

   default <A> PointFree<A> rewriteOrNop(Type<A> var1, PointFree<A> var2) {
      return (PointFree)DataFixUtils.orElse(this.rewrite(var1, var2), var2);
   }

   default <A, B> View<A, B> rewriteOrNop(View<A, B> var1) {
      return (View)DataFixUtils.orElse(this.rewrite(var1), var1);
   }

   static PointFreeRule nop() {
      return PointFreeRule.Nop.INSTANCE;
   }

   static PointFreeRule seq(PointFreeRule var0, Supplier<PointFreeRule> var1) {
      return seq(ImmutableList.of(() -> {
         return var0;
      }, var1));
   }

   static PointFreeRule seq(List<Supplier<PointFreeRule>> var0) {
      return new PointFreeRule.Seq(var0);
   }

   static PointFreeRule orElse(PointFreeRule var0, PointFreeRule var1) {
      return new PointFreeRule.OrElse(var0, () -> {
         return var1;
      });
   }

   static PointFreeRule orElseStrict(PointFreeRule var0, Supplier<PointFreeRule> var1) {
      return new PointFreeRule.OrElse(var0, var1);
   }

   static PointFreeRule all(PointFreeRule var0) {
      return new PointFreeRule.All(var0);
   }

   static PointFreeRule one(PointFreeRule var0) {
      return new PointFreeRule.One(var0);
   }

   static PointFreeRule once(PointFreeRule var0) {
      return orElseStrict(var0, () -> {
         return one(once(var0));
      });
   }

   static PointFreeRule many(PointFreeRule var0) {
      return new PointFreeRule.Many(var0);
   }

   static PointFreeRule everywhere(PointFreeRule var0) {
      return seq(orElse(var0, PointFreeRule.Nop.INSTANCE), () -> {
         return all(everywhere(var0));
      });
   }

   public static final class Many implements PointFreeRule {
      private final PointFreeRule rule;

      public Many(PointFreeRule var1) {
         super();
         this.rule = var1;
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         Optional var3 = Optional.of(var2);

         while(true) {
            Optional var4 = var3.flatMap((var2x) -> {
               return this.rule.rewrite(var1, var2x).map((var0) -> {
                  return var0;
               });
            });
            if (!var4.isPresent()) {
               return var3;
            }

            var3 = var4;
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            PointFreeRule.Many var2 = (PointFreeRule.Many)var1;
            return Objects.equals(this.rule, var2.rule);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.rule});
      }
   }

   public static final class One implements PointFreeRule {
      private final PointFreeRule rule;

      public One(PointFreeRule var1) {
         super();
         this.rule = var1;
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         return var2.one(this.rule, var1);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PointFreeRule.One)) {
            return false;
         } else {
            PointFreeRule.One var2 = (PointFreeRule.One)var1;
            return Objects.equals(this.rule, var2.rule);
         }
      }

      public int hashCode() {
         return this.rule.hashCode();
      }
   }

   public static final class All implements PointFreeRule {
      private final PointFreeRule rule;

      public All(PointFreeRule var1) {
         super();
         this.rule = var1;
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         return var2.all(this.rule, var1);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PointFreeRule.All)) {
            return false;
         } else {
            PointFreeRule.All var2 = (PointFreeRule.All)var1;
            return Objects.equals(this.rule, var2.rule);
         }
      }

      public int hashCode() {
         return this.rule.hashCode();
      }
   }

   public static final class OrElse implements PointFreeRule {
      protected final PointFreeRule first;
      protected final Supplier<PointFreeRule> second;

      public OrElse(PointFreeRule var1, Supplier<PointFreeRule> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         Optional var3 = this.first.rewrite(var1, var2);
         return var3.isPresent() ? var3 : ((PointFreeRule)this.second.get()).rewrite(var1, var2);
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PointFreeRule.OrElse)) {
            return false;
         } else {
            PointFreeRule.OrElse var2 = (PointFreeRule.OrElse)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }
   }

   public static final class Seq implements PointFreeRule {
      private final List<Supplier<PointFreeRule>> rules;

      public Seq(List<Supplier<PointFreeRule>> var1) {
         super();
         this.rules = ImmutableList.copyOf((Collection)var1);
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         Optional var3 = Optional.of(var2);

         Supplier var5;
         for(Iterator var4 = this.rules.iterator(); var4.hasNext(); var3 = var3.flatMap((var2x) -> {
            return ((PointFreeRule)var5.get()).rewrite(var1, var2x);
         })) {
            var5 = (Supplier)var4.next();
         }

         return var3;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PointFreeRule.Seq)) {
            return false;
         } else {
            PointFreeRule.Seq var2 = (PointFreeRule.Seq)var1;
            return Objects.equals(this.rules, var2.rules);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.rules});
      }
   }

   public static enum CataFuseDifferent implements PointFreeRule.CompRewrite {
      INSTANCE;

      private CataFuseDifferent() {
      }

      public <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4) {
         if (var3 instanceof Fold && var4 instanceof Fold) {
            Fold var5 = (Fold)var3;
            Fold var6 = (Fold)var4;
            RecursiveTypeFamily var7 = var5.aType.family();
            if (Objects.equals(var7, var6.aType.family()) && var5.index == var6.index) {
               ArrayList var8 = Lists.newArrayList();
               BitSet var9 = new BitSet(var7.size());
               BitSet var10 = new BitSet(var7.size());

               RewriteResult var13;
               for(int var11 = 0; var11 < var7.size(); ++var11) {
                  RewriteResult var12 = var5.algebra.apply(var11);
                  var13 = var6.algebra.apply(var11);
                  boolean var14 = Objects.equals(PointFreeRule.CompAssocRight.INSTANCE.rewriteOrNop(var12.view()).function(), Functions.id());
                  boolean var15 = Objects.equals(var13.view().function(), Functions.id());
                  var9.set(var11, !var14);
                  var10.set(var11, !var15);
               }

               BitSet var19 = (BitSet)ObjectUtils.clone(var9);
               var19.or(var10);

               for(int var20 = 0; var20 < var7.size(); ++var20) {
                  var13 = var5.algebra.apply(var20);
                  RewriteResult var22 = var6.algebra.apply(var20);
                  PointFree var23 = PointFreeRule.CompAssocRight.INSTANCE.rewriteOrNop(var13.view()).function();
                  PointFree var16 = PointFreeRule.CompAssocRight.INSTANCE.rewriteOrNop(var22.view()).function();
                  boolean var17 = Objects.equals(var23, Functions.id());
                  boolean var18 = Objects.equals(var16, Functions.id());
                  if (var13.recData().intersects(var10) || var22.recData().intersects(var9)) {
                     return Optional.empty();
                  }

                  if (var17) {
                     var8.add(var22);
                  } else {
                     if (!var18) {
                        return Optional.empty();
                     }

                     var8.add(var13);
                  }
               }

               ListAlgebra var21 = new ListAlgebra("FusedDifferent", var8);
               return Optional.of(((RewriteResult)var7.fold(var21).apply(var5.index)).view().function());
            }
         }

         return Optional.empty();
      }
   }

   public static enum CataFuseSame implements PointFreeRule.CompRewrite {
      INSTANCE;

      private CataFuseSame() {
      }

      public <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4) {
         if (var3 instanceof Fold && var4 instanceof Fold) {
            Fold var5 = (Fold)var3;
            Fold var6 = (Fold)var4;
            RecursiveTypeFamily var7 = var5.aType.family();
            if (Objects.equals(var7, var6.aType.family()) && var5.index == var6.index) {
               ArrayList var8 = Lists.newArrayList();
               boolean var9 = false;

               for(int var10 = 0; var10 < var7.size(); ++var10) {
                  RewriteResult var11 = var5.algebra.apply(var10);
                  RewriteResult var12 = var6.algebra.apply(var10);
                  boolean var13 = Objects.equals(PointFreeRule.CompAssocRight.INSTANCE.rewriteOrNop(var11.view()).function(), Functions.id());
                  boolean var14 = Objects.equals(var12.view().function(), Functions.id());
                  if (var13 && var14) {
                     var8.add(var5.algebra.apply(var10));
                  } else {
                     if (var9 || var13 || var14) {
                        return Optional.empty();
                     }

                     var8.add(this.getCompose(var11, var12));
                     var9 = true;
                  }
               }

               ListAlgebra var15 = new ListAlgebra("FusedSame", var8);
               return Optional.of(((RewriteResult)var7.fold(var15).apply(var5.index)).view().function());
            }
         }

         return Optional.empty();
      }

      private <B> RewriteResult<?, ?> getCompose(RewriteResult<B, ?> var1, RewriteResult<?, ?> var2) {
         return var1.compose(var2);
      }
   }

   public static enum LensComp implements PointFreeRule.CompRewrite {
      INSTANCE;

      private LensComp() {
      }

      public <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4) {
         if (var3 instanceof Apply && var4 instanceof Apply) {
            Apply var5 = (Apply)var3;
            Apply var6 = (Apply)var4;
            PointFree var7 = var5.func;
            PointFree var8 = var6.func;
            if (var7 instanceof ProfunctorTransformer && var8 instanceof ProfunctorTransformer) {
               ProfunctorTransformer var9 = (ProfunctorTransformer)var7;
               ProfunctorTransformer var10 = (ProfunctorTransformer)var8;
               if (Objects.equals(var9.optic, var10.optic)) {
                  Func var11 = (Func)var5.argType;
                  Func var12 = (Func)var6.argType;
                  return this.cap(var9, var10, var5.arg, var6.arg, var11, var12);
               }
            }
         }

         return Optional.empty();
      }

      private <R, A, B, C, S, T, U> Optional<? extends PointFree<R>> cap(ProfunctorTransformer<S, T, A, B> var1, ProfunctorTransformer<?, U, ?, C> var2, PointFree<?> var3, PointFree<?> var4, Func<?, ?> var5, Func<?, ?> var6) {
         return this.cap2(var1, var2, var3, var4, var5, var6);
      }

      private <R, P extends K2, Proof extends K1, A, B, C, S, T, U> Optional<? extends PointFree<R>> cap2(ProfunctorTransformer<S, T, A, B> var1, ProfunctorTransformer<T, U, B, C> var2, PointFree<Function<B, C>> var3, PointFree<Function<A, B>> var4, Func<B, C> var5, Func<A, B> var6) {
         PointFree var8 = Functions.comp(var5.first(), var3, var4);
         return Optional.of(Functions.app(var1, var8, DSL.func(var6.first(), var5.second())));
      }
   }

   public static enum LensCompFunc implements PointFreeRule {
      INSTANCE;

      private LensCompFunc() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Comp) {
            Comp var3 = (Comp)var2;
            PointFree var4 = var3.first;
            PointFree var5 = var3.second;
            if (var4 instanceof ProfunctorTransformer && var5 instanceof ProfunctorTransformer) {
               ProfunctorTransformer var6 = (ProfunctorTransformer)var4;
               ProfunctorTransformer var7 = (ProfunctorTransformer)var5;
               return Optional.of(this.cap(var6, var7));
            }
         }

         return Optional.empty();
      }

      private <R, X, Y, S, T, A, B> R cap(ProfunctorTransformer<X, Y, ?, ?> var1, ProfunctorTransformer<S, T, A, B> var2) {
         return Functions.profunctorTransformer(var1.optic.compose(var2.optic));
      }
   }

   public static enum SortInj implements PointFreeRule.CompRewrite {
      INSTANCE;

      private SortInj() {
      }

      public <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4) {
         if (var3 instanceof Apply && var4 instanceof Apply) {
            Apply var5 = (Apply)var3;
            Apply var6 = (Apply)var4;
            PointFree var7 = var5.func;
            PointFree var8 = var6.func;
            if (var7 instanceof ProfunctorTransformer && var8 instanceof ProfunctorTransformer) {
               ProfunctorTransformer var9 = (ProfunctorTransformer)var7;
               ProfunctorTransformer var10 = (ProfunctorTransformer)var8;

               Optic var11;
               for(var11 = var9.optic; var11 instanceof Optic.CompositionOptic; var11 = ((Optic.CompositionOptic)var11).outer()) {
               }

               Optic var12;
               for(var12 = var10.optic; var12 instanceof Optic.CompositionOptic; var12 = ((Optic.CompositionOptic)var12).outer()) {
               }

               if (Objects.equals(var11, Optics.inj2()) && Objects.equals(var12, Optics.inj1())) {
                  Func var13 = (Func)var5.argType;
                  Func var14 = (Func)var6.argType;
                  return Optional.of(this.cap(var13, var14, var5, var6));
               }
            }
         }

         return Optional.empty();
      }

      private <R, A, A2, B, B2> R cap(Func<B, B2> var1, Func<A, A2> var2, Apply<?, ?> var3, Apply<?, ?> var4) {
         return Functions.comp(DSL.or(var2.first(), var1.second()), var4, var3);
      }
   }

   public static enum SortProj implements PointFreeRule.CompRewrite {
      INSTANCE;

      private SortProj() {
      }

      public <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4) {
         if (var3 instanceof Apply && var4 instanceof Apply) {
            Apply var5 = (Apply)var3;
            Apply var6 = (Apply)var4;
            PointFree var7 = var5.func;
            PointFree var8 = var6.func;
            if (var7 instanceof ProfunctorTransformer && var8 instanceof ProfunctorTransformer) {
               ProfunctorTransformer var9 = (ProfunctorTransformer)var7;
               ProfunctorTransformer var10 = (ProfunctorTransformer)var8;

               Optic var11;
               for(var11 = var9.optic; var11 instanceof Optic.CompositionOptic; var11 = ((Optic.CompositionOptic)var11).outer()) {
               }

               Optic var12;
               for(var12 = var10.optic; var12 instanceof Optic.CompositionOptic; var12 = ((Optic.CompositionOptic)var12).outer()) {
               }

               if (Objects.equals(var11, Optics.proj2()) && Objects.equals(var12, Optics.proj1())) {
                  Func var13 = (Func)var5.argType;
                  Func var14 = (Func)var6.argType;
                  return Optional.of(this.cap(var13, var14, var5, var6));
               }
            }
         }

         return Optional.empty();
      }

      private <R, A, A2, B, B2> R cap(Func<B, B2> var1, Func<A, A2> var2, Apply<?, ?> var3, Apply<?, ?> var4) {
         return Functions.comp(DSL.and(var2.first(), var1.second()), var4, var3);
      }
   }

   public interface CompRewrite extends PointFreeRule {
      default <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Comp) {
            Comp var3 = (Comp)var2;
            PointFree var4 = var3.first;
            PointFree var5 = var3.second;
            Comp var6;
            if (var4 instanceof Comp) {
               var6 = (Comp)var4;
               return this.doRewrite(var1, var3.middleType, var6.second, var3.second).map((var1x) -> {
                  if (var1x instanceof Comp) {
                     Comp var2 = (Comp)var1x;
                     return buildLeftNested(var2, var6);
                  } else {
                     return buildRight(var6, var1x);
                  }
               });
            } else if (var5 instanceof Comp) {
               var6 = (Comp)var5;
               return this.doRewrite(var1, var3.middleType, var3.first, var6.first).map((var1x) -> {
                  if (var1x instanceof Comp) {
                     Comp var2 = (Comp)var1x;
                     return buildRightNested(var6, var2);
                  } else {
                     return buildLeft(var1x, var6);
                  }
               });
            } else {
               return this.doRewrite(var1, var3.middleType, var3.first, var3.second);
            }
         } else {
            return Optional.empty();
         }
      }

      static <A, B, C, D> PointFree<D> buildLeft(PointFree<?> var0, Comp<A, B, C> var1) {
         return new Comp(var1.middleType, var0, var1.second);
      }

      static <A, B, C, D> PointFree<D> buildRight(Comp<A, B, C> var0, PointFree<?> var1) {
         return new Comp(var0.middleType, var0.first, var1);
      }

      static <A, B, C, D, E> PointFree<E> buildLeftNested(Comp<A, B, C> var0, Comp<?, ?, D> var1) {
         return new Comp(var0.middleType, new Comp(var1.middleType, var1.first, var0.first), var0.second);
      }

      static <A, B, C, D, E> PointFree<E> buildRightNested(Comp<A, B, D> var0, Comp<?, C, ?> var1) {
         return new Comp(var1.middleType, var1.first, new Comp(var0.middleType, var1.second, var0.second));
      }

      <A> Optional<? extends PointFree<?>> doRewrite(Type<A> var1, Type<?> var2, PointFree<? extends Function<?, ?>> var3, PointFree<? extends Function<?, ?>> var4);
   }

   public static enum AppNest implements PointFreeRule {
      INSTANCE;

      private AppNest() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Apply) {
            Apply var3 = (Apply)var2;
            if (var3.arg instanceof Apply) {
               Apply var4 = (Apply)var3.arg;
               return this.cap(var3, var4);
            }
         }

         return Optional.empty();
      }

      private <A, B, C, D, E> Optional<? extends PointFree<A>> cap(Apply<D, E> var1, Apply<B, C> var2) {
         PointFree var3 = var2.func;
         return Optional.of(Functions.app(Functions.comp(var1.argType, var1.func, var3), var2.arg, var2.argType));
      }
   }

   public static enum LensAppId implements PointFreeRule {
      INSTANCE;

      private LensAppId() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Apply) {
            Apply var3 = (Apply)var2;
            PointFree var4 = var3.func;
            if (var4 instanceof ProfunctorTransformer && Objects.equals(var3.arg, Functions.id())) {
               return Optional.of(Functions.id());
            }
         }

         return Optional.empty();
      }
   }

   public static enum CompAssocRight implements PointFreeRule {
      INSTANCE;

      private CompAssocRight() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Comp) {
            Comp var3 = (Comp)var2;
            PointFree var4 = var3.first;
            if (var4 instanceof Comp) {
               Comp var5 = (Comp)var4;
               return swap(var3, var5);
            }
         }

         return Optional.empty();
      }

      private static <A, B, C, D, E> Optional<PointFree<E>> swap(Comp<A, B, D> var0, Comp<?, C, ?> var1) {
         return Optional.of(new Comp(var1.middleType, var1.first, new Comp(var0.middleType, var1.second, var0.second)));
      }
   }

   public static enum CompAssocLeft implements PointFreeRule {
      INSTANCE;

      private CompAssocLeft() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Comp) {
            Comp var3 = (Comp)var2;
            PointFree var4 = var3.second;
            if (var4 instanceof Comp) {
               Comp var5 = (Comp)var4;
               return swap(var5, var3);
            }
         }

         return Optional.empty();
      }

      private static <A, B, C, D, E> Optional<PointFree<E>> swap(Comp<A, B, C> var0, Comp<?, ?, D> var1) {
         return Optional.of(new Comp(var0.middleType, new Comp(var1.middleType, var1.first, var0.first), var0.second));
      }
   }

   public static enum BangEta implements PointFreeRule {
      INSTANCE;

      private BangEta() {
      }

      public <A> Optional<? extends PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         if (var2 instanceof Bang) {
            return Optional.empty();
         } else {
            if (var1 instanceof Func) {
               Func var3 = (Func)var1;
               if (var3.second() instanceof EmptyPart) {
                  return Optional.of(Functions.bang());
               }
            }

            return Optional.empty();
         }
      }
   }

   public static enum Nop implements PointFreeRule, Supplier<PointFreeRule> {
      INSTANCE;

      private Nop() {
      }

      public <A> Optional<PointFree<A>> rewrite(Type<A> var1, PointFree<A> var2) {
         return Optional.of(var2);
      }

      public PointFreeRule get() {
         return this;
      }
   }
}
