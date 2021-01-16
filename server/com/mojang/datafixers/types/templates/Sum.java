package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Traversal;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Sum implements TypeTemplate {
   private final TypeTemplate f;
   private final TypeTemplate g;

   public Sum(TypeTemplate var1, TypeTemplate var2) {
      super();
      this.f = var1;
      this.g = var2;
   }

   public int size() {
      return Math.max(this.f.size(), this.g.size());
   }

   public TypeFamily apply(final TypeFamily var1) {
      return new TypeFamily() {
         public Type<?> apply(int var1x) {
            return DSL.or(Sum.this.f.apply(var1).apply(var1x), Sum.this.g.apply(var1).apply(var1x));
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.cap(this.f.applyO(var1, var2, var3), this.g.applyO(var1, var2, var3), var4);
      });
   }

   private <A, B, LS, RS, LT, RT> OpticParts<A, B> cap(final FamilyOptic<A, B> var1, final FamilyOptic<A, B> var2, final int var3) {
      final TypeToken var4 = TraversalP.Mu.TYPE_TOKEN;
      return new OpticParts(ImmutableSet.of(var4), new Traversal<Either<LS, RS>, Either<LT, RT>, A, B>() {
         public <F extends K1> FunctionType<Either<LS, RS>, App<F, Either<LT, RT>>> wander(Applicative<F, ?> var1x, FunctionType<A, App<F, B>> var2x) {
            return (var6) -> {
               return (App)var6.map((var5) -> {
                  OpticParts var6 = var1.apply(var3);
                  Traversal var7 = Optics.toTraversal((Optic)var6.optic().upCast(var6.bounds(), var4).orElseThrow(IllegalArgumentException::new));
                  return var1x.ap(Either::left, (App)var7.wander(var1x, var2x).apply(var5));
               }, (var5) -> {
                  OpticParts var6 = var2.apply(var3);
                  Traversal var7 = Optics.toTraversal((Optic)var6.optic().upCast(var6.bounds(), var4).orElseThrow(IllegalArgumentException::new));
                  return var1x.ap(Either::right, (App)var7.wander(var1x, var2x).apply(var5));
               });
            };
         }
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      Either var5 = this.f.findFieldOrType(var1, var2, var3, var4);
      return (Either)var5.map((var1x) -> {
         return Either.left(new Sum(var1x, this.g));
      }, (var5x) -> {
         return this.g.findFieldOrType(var1, var2, var3, var4).mapLeft((var1x) -> {
            return new Sum(this.f, var1x);
         });
      });
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.f.hmap(var1, var2).apply(var3);
         RewriteResult var5 = (RewriteResult)this.g.hmap(var1, var2).apply(var3);
         return this.cap(this.apply(var1).apply(var3), var4, var5);
      };
   }

   private <L, R> RewriteResult<?, ?> cap(Type<?> var1, RewriteResult<L, ?> var2, RewriteResult<R, ?> var3) {
      return ((Sum.SumType)var1).mergeViews(var2, var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Sum)) {
         return false;
      } else {
         Sum var2 = (Sum)var1;
         return Objects.equals(this.f, var2.f) && Objects.equals(this.g, var2.g);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.f, this.g});
   }

   public String toString() {
      return "(" + this.f + " | " + this.g + ")";
   }

   public static final class SumType<F, G> extends Type<Either<F, G>> {
      protected final Type<F> first;
      protected final Type<G> second;
      private int hashCode;

      public SumType(Type<F> var1, Type<G> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public RewriteResult<Either<F, G>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return this.mergeViews(this.first.rewriteOrNop(var1), this.second.rewriteOrNop(var1));
      }

      public <F2, G2> RewriteResult<Either<F, G>, ?> mergeViews(RewriteResult<F, F2> var1, RewriteResult<G, G2> var2) {
         RewriteResult var3 = fixLeft(this, this.first, this.second, var1);
         RewriteResult var4 = fixRight(var3.view().newType(), var1.view().newType(), this.second, var2);
         return var4.compose(var3);
      }

      public Optional<RewriteResult<Either<F, G>, ?>> one(TypeRewriteRule var1) {
         return DataFixUtils.or(var1.rewrite(this.first).map((var1x) -> {
            return fixLeft(this, this.first, this.second, var1x);
         }), () -> {
            return var1.rewrite(this.second).map((var1x) -> {
               return fixRight(this, this.first, this.second, var1x);
            });
         });
      }

      private static <F, G, F2> RewriteResult<Either<F, G>, Either<F2, G>> fixLeft(Type<Either<F, G>> var0, Type<F> var1, Type<G> var2, RewriteResult<F, F2> var3) {
         return opticView(var0, var3, TypedOptic.inj1(var1, var2, var3.view().newType()));
      }

      private static <F, G, G2> RewriteResult<Either<F, G>, Either<F, G2>> fixRight(Type<Either<F, G>> var0, Type<F> var1, Type<G> var2, RewriteResult<G, G2> var3) {
         return opticView(var0, var3, TypedOptic.inj2(var1, var2, var3.view().newType()));
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.or(this.first.updateMu(var1), this.second.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.or(this.first.template(), this.second.template());
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return DataFixUtils.or(this.first.findChoiceType(var1, var2), () -> {
            return this.second.findChoiceType(var1, var2);
         });
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return DataFixUtils.or(this.first.findCheckedType(var1), () -> {
            return this.second.findCheckedType(var1);
         });
      }

      protected Codec<Either<F, G>> buildCodec() {
         return Codec.either(this.first.codec(), this.second.codec());
      }

      public String toString() {
         return "(" + this.first + " | " + this.second + ")";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof Sum.SumType)) {
            return false;
         } else {
            Sum.SumType var4 = (Sum.SumType)var1;
            return this.first.equals(var4.first, var2, var3) && this.second.equals(var4.second, var2, var3);
         }
      }

      public int hashCode() {
         if (this.hashCode == 0) {
            this.hashCode = Objects.hash(new Object[]{this.first, this.second});
         }

         return this.hashCode;
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return DataFixUtils.or(this.first.findFieldTypeOpt(var1), () -> {
            return this.second.findFieldTypeOpt(var1);
         });
      }

      public Optional<Either<F, G>> point(DynamicOps<?> var1) {
         return DataFixUtils.or(this.second.point(var1).map(Either::right), () -> {
            return this.first.point(var1).map(Either::left);
         });
      }

      private static <A, B, LS, RS, LT, RT> TypedOptic<Either<LS, RS>, Either<LT, RT>, A, B> mergeOptics(final TypedOptic<LS, LT, A, B> var0, final TypedOptic<RS, RT, A, B> var1) {
         final TypeToken var2 = TraversalP.Mu.TYPE_TOKEN;
         return new TypedOptic(var2, DSL.or(var0.sType(), var1.sType()), DSL.or(var0.tType(), var1.tType()), var0.aType(), var0.bType(), new Traversal<Either<LS, RS>, Either<LT, RT>, A, B>() {
            public <F extends K1> FunctionType<Either<LS, RS>, App<F, Either<LT, RT>>> wander(Applicative<F, ?> var1x, FunctionType<A, App<F, B>> var2x) {
               return (var5) -> {
                  return (App)var5.map((var4) -> {
                     Traversal var5 = Optics.toTraversal((Optic)var0.optic().upCast(var0.bounds(), var2).orElseThrow(IllegalArgumentException::new));
                     return var1x.ap(Either::left, (App)var5.wander(var1x, var2x).apply(var4));
                  }, (var4) -> {
                     Traversal var5 = Optics.toTraversal((Optic)var1.optic().upCast(var1.bounds(), var2).orElseThrow(IllegalArgumentException::new));
                     return var1x.ap(Either::right, (App)var5.wander(var1x, var2x).apply(var4));
                  });
               };
            }
         });
      }

      public <FT, FR> Either<TypedOptic<Either<F, G>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         Either var5 = this.first.findType(var1, var2, var3, var4);
         Either var6 = this.second.findType(var1, var2, var3, var4);
         if (var5.left().isPresent() && var6.left().isPresent()) {
            return Either.left(mergeOptics((TypedOptic)var5.left().get(), (TypedOptic)var6.left().get()));
         } else {
            return var5.left().isPresent() ? var5.mapLeft(this::capLeft) : var6.mapLeft(this::capRight);
         }
      }

      private <FT, FR, F2> TypedOptic<Either<F, G>, ?, FT, FR> capLeft(TypedOptic<F, F2, FT, FR> var1) {
         return TypedOptic.inj1(var1.sType(), this.second, var1.tType()).compose(var1);
      }

      private <FT, FR, G2> TypedOptic<Either<F, G>, ?, FT, FR> capRight(TypedOptic<G, G2, FT, FR> var1) {
         return TypedOptic.inj2(this.first, var1.sType(), var1.tType()).compose(var1);
      }
   }
}
