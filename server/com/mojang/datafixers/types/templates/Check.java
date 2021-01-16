package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Check implements TypeTemplate {
   private final String name;
   private final int index;
   private final TypeTemplate element;

   public Check(String var1, int var2, TypeTemplate var3) {
      super();
      this.name = var1;
      this.index = var2;
      this.element = var3;
   }

   public int size() {
      return Math.max(this.index + 1, this.element.size());
   }

   public TypeFamily apply(final TypeFamily var1) {
      return new TypeFamily() {
         public Type<?> apply(int var1x) {
            if (var1x < 0) {
               throw new IndexOutOfBoundsException();
            } else {
               return new Check.CheckType(Check.this.name, var1x, Check.this.index, Check.this.element.apply(var1).apply(var1x));
            }
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.element.applyO(var1, var2, var3).apply(var4);
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return var1 == this.index ? this.element.findFieldOrType(var1, var2, var3, var4) : Either.right(new Type.FieldNotFoundException("Not a matching index"));
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.element.hmap(var1, var2).apply(var3);
         return this.cap(var1, var3, var4);
      };
   }

   private <A> RewriteResult<?, ?> cap(TypeFamily var1, int var2, RewriteResult<A, ?> var3) {
      return Check.CheckType.fix((Check.CheckType)this.apply(var1).apply(var2), var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Check)) {
         return false;
      } else {
         Check var2 = (Check)var1;
         return Objects.equals(this.name, var2.name) && this.index == var2.index && Objects.equals(this.element, var2.element);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.index, this.element});
   }

   public String toString() {
      return "Tag[" + this.name + ", " + this.index + ": " + this.element + "]";
   }

   public static final class CheckType<A> extends Type<A> {
      private final String name;
      private final int index;
      private final int expectedIndex;
      private final Type<A> delegate;

      public CheckType(String var1, int var2, int var3, Type<A> var4) {
         super();
         this.name = var1;
         this.index = var2;
         this.expectedIndex = var3;
         this.delegate = var4;
      }

      protected Codec<A> buildCodec() {
         return Codec.of((Encoder)this.delegate.codec(), (Decoder)(this::read));
      }

      private <T> DataResult<Pair<A, T>> read(DynamicOps<T> var1, T var2) {
         return this.index != this.expectedIndex ? DataResult.error("Index mismatch: " + this.index + " != " + this.expectedIndex) : this.delegate.codec().decode(var1, var2);
      }

      public static <A, B> RewriteResult<A, ?> fix(Check.CheckType<A> var0, RewriteResult<A, B> var1) {
         return Objects.equals(var1.view().function(), Functions.id()) ? RewriteResult.nop(var0) : opticView(var0, var1, wrapOptic(var0, TypedOptic.adapter(var1.view().type(), var1.view().newType())));
      }

      public RewriteResult<A, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return var3 && this.index != this.expectedIndex ? RewriteResult.nop(this) : fix(this, this.delegate.rewriteOrNop(var1));
      }

      public Optional<RewriteResult<A, ?>> everywhere(TypeRewriteRule var1, PointFreeRule var2, boolean var3, boolean var4) {
         return var4 && this.index != this.expectedIndex ? Optional.empty() : super.everywhere(var1, var2, var3, var4);
      }

      public Optional<RewriteResult<A, ?>> one(TypeRewriteRule var1) {
         return var1.rewrite(this.delegate).map((var1x) -> {
            return fix(this, var1x);
         });
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return new Check.CheckType(this.name, this.index, this.expectedIndex, this.delegate.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.check(this.name, this.expectedIndex, this.delegate.template());
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return var2 == this.expectedIndex ? this.delegate.findChoiceType(var1, var2) : Optional.empty();
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return var1 == this.expectedIndex ? Optional.of(this.delegate) : Optional.empty();
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return this.index == this.expectedIndex ? this.delegate.findFieldTypeOpt(var1) : Optional.empty();
      }

      public Optional<A> point(DynamicOps<?> var1) {
         return this.index == this.expectedIndex ? this.delegate.point(var1) : Optional.empty();
      }

      public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         return this.index != this.expectedIndex ? Either.right(new Type.FieldNotFoundException("Incorrect index in CheckType")) : this.delegate.findType(var1, var2, var3, var4).mapLeft((var1x) -> {
            return wrapOptic(this, var1x);
         });
      }

      protected static <A, B, FT, FR> TypedOptic<A, B, FT, FR> wrapOptic(Check.CheckType<A> var0, TypedOptic<A, B, FT, FR> var1) {
         return new TypedOptic(var1.bounds(), var0, new Check.CheckType(var0.name, var0.index, var0.expectedIndex, var1.tType()), var1.aType(), var1.bType(), var1.optic());
      }

      public String toString() {
         return "TypeTag[" + this.index + "~" + this.expectedIndex + "][" + this.name + ": " + this.delegate + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof Check.CheckType)) {
            return false;
         } else {
            Check.CheckType var4 = (Check.CheckType)var1;
            if (this.index == var4.index && this.expectedIndex == var4.expectedIndex) {
               if (!var3) {
                  return true;
               }

               if (this.delegate.equals(var4.delegate, var2, var3)) {
                  return true;
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.index, this.expectedIndex, this.delegate});
      }
   }
}
