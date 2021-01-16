package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;

public final class RecursivePoint implements TypeTemplate {
   private final int index;

   public RecursivePoint(int var1) {
      super();
      this.index = var1;
   }

   public int size() {
      return this.index + 1;
   }

   public TypeFamily apply(TypeFamily var1) {
      final Type var2 = var1.apply(this.index);
      return new TypeFamily() {
         public Type<?> apply(int var1) {
            return var2;
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var2x) -> {
         return var1.apply(this.index);
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return Either.right(new Type.FieldNotFoundException("Recursion point"));
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)var2.apply(this.index);
         return this.cap(var1, var4);
      };
   }

   public <S, T> RewriteResult<S, T> cap(TypeFamily var1, RewriteResult<S, T> var2) {
      Type var3 = var1.apply(this.index);
      if (!(var3 instanceof RecursivePoint.RecursivePointType)) {
         throw new IllegalArgumentException("Type error: Recursive point template template got a non-recursice type as an input.");
      } else if (!Objects.equals(var2.view().type(), ((RecursivePoint.RecursivePointType)var3).unfold())) {
         throw new IllegalArgumentException("Type error: hmap function input type");
      } else {
         RecursivePoint.RecursivePointType var4 = (RecursivePoint.RecursivePointType)var3;
         RecursivePoint.RecursivePointType var5 = var4.family().buildMuType(var2.view().newType(), (RecursiveTypeFamily)null);
         BitSet var6 = (BitSet)ObjectUtils.clone(var2.recData());
         var6.set(this.index);
         return RewriteResult.create(View.create(var4, var5, var2.view().function()), var6);
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof RecursivePoint && this.index == ((RecursivePoint)var1).index;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.index});
   }

   public String toString() {
      return "Id[" + this.index + "]";
   }

   public int index() {
      return this.index;
   }

   public static final class RecursivePointType<A> extends Type<A> {
      private final RecursiveTypeFamily family;
      private final int index;
      private final Supplier<Type<A>> delegate;
      @Nullable
      private volatile Type<A> type;

      public RecursivePointType(RecursiveTypeFamily var1, int var2, Supplier<Type<A>> var3) {
         super();
         this.family = var1;
         this.index = var2;
         this.delegate = var3;
      }

      public RecursiveTypeFamily family() {
         return this.family;
      }

      public int index() {
         return this.index;
      }

      public Type<A> unfold() {
         if (this.type == null) {
            this.type = (Type)this.delegate.get();
         }

         return this.type;
      }

      protected Codec<A> buildCodec() {
         return new Codec<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
               return RecursivePointType.this.unfold().codec().decode(var1, var2).setLifecycle(Lifecycle.experimental());
            }

            public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
               return RecursivePointType.this.unfold().codec().encode(var1, var2, var3).setLifecycle(Lifecycle.experimental());
            }
         };
      }

      public RewriteResult<A, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return this.unfold().all(var1, var2, var3);
      }

      public Optional<RewriteResult<A, ?>> one(TypeRewriteRule var1) {
         return this.unfold().one(var1);
      }

      public Optional<RewriteResult<A, ?>> everywhere(TypeRewriteRule var1, PointFreeRule var2, boolean var3, boolean var4) {
         return var3 ? this.family.everywhere(this.index, var1, var2).map((var0) -> {
            return var0;
         }) : Optional.of(RewriteResult.nop(this));
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return var1.apply(this.index);
      }

      public TypeTemplate buildTemplate() {
         return DSL.id(this.index);
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return this.unfold().findChoiceType(var1, this.index);
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return this.unfold().findCheckedType(this.index);
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return this.unfold().findFieldTypeOpt(var1);
      }

      public Optional<A> point(DynamicOps<?> var1) {
         return this.unfold().point(var1);
      }

      public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         return this.family.findType(this.index, var1, var2, var3, var4).mapLeft((var1x) -> {
            if (!Objects.equals(this, var1x.sType())) {
               throw new IllegalStateException(":/");
            } else {
               return var1x;
            }
         });
      }

      private <B, FT, FR> TypedOptic<A, B, FT, FR> wrapOptic(TypedOptic<A, B, FT, FR> var1) {
         return new TypedOptic(var1.bounds(), this, var1.tType(), var1.aType(), var1.bType(), var1.optic());
      }

      public String toString() {
         return "MuType[" + this.family.name() + "_" + this.index + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof RecursivePoint.RecursivePointType)) {
            return false;
         } else {
            RecursivePoint.RecursivePointType var4 = (RecursivePoint.RecursivePointType)var1;
            return (var2 || Objects.equals(this.family, var4.family)) && this.index == var4.index;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.family, this.index});
      }

      public View<A, A> in() {
         return View.create(this.unfold(), this, Functions.in(this));
      }

      public View<A, A> out() {
         return View.create(this, this.unfold(), Functions.out(this));
      }
   }
}
