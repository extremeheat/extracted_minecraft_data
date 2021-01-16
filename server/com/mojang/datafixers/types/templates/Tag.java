package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
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

public final class Tag implements TypeTemplate {
   private final String name;
   private final TypeTemplate element;

   public Tag(String var1, TypeTemplate var2) {
      super();
      this.name = var1;
      this.element = var2;
   }

   public int size() {
      return this.element.size();
   }

   public TypeFamily apply(final TypeFamily var1) {
      return new TypeFamily() {
         public Type<?> apply(int var1x) {
            return DSL.field(Tag.this.name, Tag.this.element.apply(var1).apply(var1x));
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.element.applyO(var1, var2, var3).apply(var4);
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      if (!Objects.equals(var2, this.name)) {
         return Either.right(new Type.FieldNotFoundException("Names don't match"));
      } else if (this.element instanceof Const) {
         Const var5 = (Const)this.element;
         return Objects.equals(var3, var5.type()) ? Either.left(new Tag(var2, new Const(var4))) : Either.right(new Type.FieldNotFoundException("don't match"));
      } else if (Objects.equals(var3, var4)) {
         return Either.left(this);
      } else {
         if (var3 instanceof RecursivePoint.RecursivePointType && this.element instanceof RecursivePoint && ((RecursivePoint)this.element).index() == ((RecursivePoint.RecursivePointType)var3).index()) {
            if (!(var4 instanceof RecursivePoint.RecursivePointType)) {
               return Either.left(DSL.constType(var4));
            }

            if (((RecursivePoint.RecursivePointType)var4).index() == ((RecursivePoint)this.element).index()) {
               return Either.left(this);
            }
         }

         return Either.right(new Type.FieldNotFoundException("Recursive field"));
      }
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return this.element.hmap(var1, var2);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Tag)) {
         return false;
      } else {
         Tag var2 = (Tag)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.element, var2.element);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.element});
   }

   public String toString() {
      return "NameTag[" + this.name + ": " + this.element + "]";
   }

   public static final class TagType<A> extends Type<A> {
      protected final String name;
      protected final Type<A> element;

      public TagType(String var1, Type<A> var2) {
         super();
         this.name = var1;
         this.element = var2;
      }

      public RewriteResult<A, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         RewriteResult var4 = this.element.rewriteOrNop(var1);
         return RewriteResult.create(this.cap(var4.view()), var4.recData());
      }

      private <B> View<A, ?> cap(View<A, B> var1) {
         return Objects.equals(var1.function(), Functions.id()) ? View.nopView(this) : View.create(this, DSL.field(this.name, var1.newType()), var1.function());
      }

      public Optional<RewriteResult<A, ?>> one(TypeRewriteRule var1) {
         Optional var2 = var1.rewrite(this.element);
         return var2.map((var1x) -> {
            return RewriteResult.create(this.cap(var1x.view()), var1x.recData());
         });
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.field(this.name, this.element.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.field(this.name, this.element.template());
      }

      protected Codec<A> buildCodec() {
         return this.element.codec().fieldOf(this.name).codec();
      }

      public String toString() {
         return "Tag[\"" + this.name + "\", " + this.element + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Tag.TagType var4 = (Tag.TagType)var1;
            return Objects.equals(this.name, var4.name) && this.element.equals(var4.element, var2, var3);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.element});
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return Objects.equals(var1, this.name) ? Optional.of(this.element) : Optional.empty();
      }

      public Optional<A> point(DynamicOps<?> var1) {
         return this.element.point(var1);
      }

      public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         return this.element.findType(var1, var2, var3, var4).mapLeft(this::wrapOptic);
      }

      private <B, FT, FR> TypedOptic<A, B, FT, FR> wrapOptic(TypedOptic<A, B, FT, FR> var1) {
         return new TypedOptic(var1.bounds(), DSL.field(this.name, var1.sType()), DSL.field(this.name, var1.tType()), var1.aType(), var1.bType(), var1.optic());
      }

      public String name() {
         return this.name;
      }

      public Type<A> element() {
         return this.element;
      }
   }
}
