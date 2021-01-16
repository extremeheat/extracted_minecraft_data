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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Product implements TypeTemplate {
   private final TypeTemplate f;
   private final TypeTemplate g;

   public Product(TypeTemplate var1, TypeTemplate var2) {
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
            return DSL.and(Product.this.f.apply(var1).apply(var1x), Product.this.g.apply(var1).apply(var1x));
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.cap(this.f.applyO(var1, var2, var3), this.g.applyO(var1, var2, var3), var4);
      });
   }

   private <A, B, LS, RS, LT, RT> OpticParts<A, B> cap(FamilyOptic<A, B> var1, FamilyOptic<A, B> var2, int var3) {
      TypeToken var4 = TraversalP.Mu.TYPE_TOKEN;
      OpticParts var5 = var1.apply(var3);
      OpticParts var6 = var2.apply(var3);
      Optic var7 = (Optic)var5.optic().upCast(var5.bounds(), var4).orElseThrow(IllegalArgumentException::new);
      Optic var8 = (Optic)var6.optic().upCast(var6.bounds(), var4).orElseThrow(IllegalArgumentException::new);
      final Traversal var9 = Optics.toTraversal(var7);
      final Traversal var10 = Optics.toTraversal(var8);
      return new OpticParts(ImmutableSet.of(var4), new Traversal<Pair<LS, RS>, Pair<LT, RT>, A, B>() {
         public <F extends K1> FunctionType<Pair<LS, RS>, App<F, Pair<LT, RT>>> wander(Applicative<F, ?> var1, FunctionType<A, App<F, B>> var2) {
            return (var4) -> {
               return var1.ap2(var1.point(Pair::of), (App)var9.wander(var1, var2).apply(var4.getFirst()), (App)var10.wander(var1, var2).apply(var4.getSecond()));
            };
         }
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      Either var5 = this.f.findFieldOrType(var1, var2, var3, var4);
      return (Either)var5.map((var1x) -> {
         return Either.left(new Product(var1x, this.g));
      }, (var5x) -> {
         return this.g.findFieldOrType(var1, var2, var3, var4).mapLeft((var1x) -> {
            return new Product(this.f, var1x);
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
      return ((Product.ProductType)var1).mergeViews(var2, var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Product)) {
         return false;
      } else {
         Product var2 = (Product)var1;
         return Objects.equals(this.f, var2.f) && Objects.equals(this.g, var2.g);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.f, this.g});
   }

   public String toString() {
      return "(" + this.f + ", " + this.g + ")";
   }

   public static final class ProductType<F, G> extends Type<Pair<F, G>> {
      protected final Type<F> first;
      protected final Type<G> second;
      private int hashCode;

      public ProductType(Type<F> var1, Type<G> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public RewriteResult<Pair<F, G>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return this.mergeViews(this.first.rewriteOrNop(var1), this.second.rewriteOrNop(var1));
      }

      public <F2, G2> RewriteResult<Pair<F, G>, ?> mergeViews(RewriteResult<F, F2> var1, RewriteResult<G, G2> var2) {
         RewriteResult var3 = fixLeft(this, this.first, this.second, var1);
         RewriteResult var4 = fixRight(var3.view().newType(), var1.view().newType(), this.second, var2);
         return var4.compose(var3);
      }

      public Optional<RewriteResult<Pair<F, G>, ?>> one(TypeRewriteRule var1) {
         return DataFixUtils.or(var1.rewrite(this.first).map((var1x) -> {
            return fixLeft(this, this.first, this.second, var1x);
         }), () -> {
            return var1.rewrite(this.second).map((var1x) -> {
               return fixRight(this, this.first, this.second, var1x);
            });
         });
      }

      private static <F, G, F2> RewriteResult<Pair<F, G>, Pair<F2, G>> fixLeft(Type<Pair<F, G>> var0, Type<F> var1, Type<G> var2, RewriteResult<F, F2> var3) {
         return opticView(var0, var3, TypedOptic.proj1(var1, var2, var3.view().newType()));
      }

      private static <F, G, G2> RewriteResult<Pair<F, G>, Pair<F, G2>> fixRight(Type<Pair<F, G>> var0, Type<F> var1, Type<G> var2, RewriteResult<G, G2> var3) {
         return opticView(var0, var3, TypedOptic.proj2(var1, var2, var3.view().newType()));
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.and(this.first.updateMu(var1), this.second.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.and(this.first.template(), this.second.template());
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

      public Codec<Pair<F, G>> buildCodec() {
         return Codec.pair(this.first.codec(), this.second.codec());
      }

      public String toString() {
         return "(" + this.first + ", " + this.second + ")";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof Product.ProductType)) {
            return false;
         } else {
            Product.ProductType var4 = (Product.ProductType)var1;
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

      public Optional<Pair<F, G>> point(DynamicOps<?> var1) {
         return this.first.point(var1).flatMap((var2) -> {
            return this.second.point(var1).map((var1x) -> {
               return Pair.of(var2, var1x);
            });
         });
      }

      public <FT, FR> Either<TypedOptic<Pair<F, G>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         Either var5 = this.first.findType(var1, var2, var3, var4);
         return (Either)var5.map(this::capLeft, (var5x) -> {
            Either var6 = this.second.findType(var1, var2, var3, var4);
            return var6.mapLeft(this::capRight);
         });
      }

      private <FT, F2, FR> Either<TypedOptic<Pair<F, G>, ?, FT, FR>, Type.FieldNotFoundException> capLeft(TypedOptic<F, F2, FT, FR> var1) {
         return Either.left(TypedOptic.proj1(var1.sType(), this.second, var1.tType()).compose(var1));
      }

      private <FT, G2, FR> TypedOptic<Pair<F, G>, ?, FT, FR> capRight(TypedOptic<G, G2, FT, FR> var1) {
         return TypedOptic.proj2(this.first, var1.sType(), var1.tType()).compose(var1);
      }
   }
}
