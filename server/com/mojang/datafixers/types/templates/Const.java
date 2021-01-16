package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Const implements TypeTemplate {
   private final Type<?> type;

   public Const(Type<?> var1) {
      super();
      this.type = var1;
   }

   public int size() {
      return 0;
   }

   public TypeFamily apply(TypeFamily var1) {
      return new TypeFamily() {
         public Type<?> apply(int var1) {
            return Const.this.type;
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      if (Objects.equals(this.type, var2)) {
         return TypeFamily.familyOptic((var0) -> {
            return new OpticParts(ImmutableSet.of(Profunctor.Mu.TYPE_TOKEN), Optics.id());
         });
      } else {
         TypedOptic var4 = this.makeIgnoreOptic(this.type, var2, var3);
         return TypeFamily.familyOptic((var1x) -> {
            return new OpticParts(var4.bounds(), var4.optic());
         });
      }
   }

   private <T, A, B> TypedOptic<T, T, A, B> makeIgnoreOptic(Type<T> var1, Type<A> var2, Type<B> var3) {
      return new TypedOptic(AffineP.Mu.TYPE_TOKEN, var1, var1, var2, var3, Optics.affine(Either::left, (var0, var1x) -> {
         return var1x;
      }));
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return DSL.fieldFinder(var2, var3).findType(this.type, var4, false).mapLeft((var0) -> {
         return new Const(var0.tType());
      });
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var1x) -> {
         return RewriteResult.nop(this.type);
      };
   }

   public boolean equals(Object var1) {
      return var1 instanceof Const && Objects.equals(this.type, ((Const)var1).type);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type});
   }

   public String toString() {
      return "Const[" + this.type + "]";
   }

   public Type<?> type() {
      return this.type;
   }

   public static final class PrimitiveType<A> extends Type<A> {
      private final Codec<A> codec;

      public PrimitiveType(Codec<A> var1) {
         super();
         this.codec = var1;
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         return this == var1;
      }

      public TypeTemplate buildTemplate() {
         return DSL.constType(this);
      }

      protected Codec<A> buildCodec() {
         return this.codec;
      }

      public String toString() {
         return this.codec.toString();
      }
   }
}
