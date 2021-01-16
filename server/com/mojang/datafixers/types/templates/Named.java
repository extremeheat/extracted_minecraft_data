package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Named implements TypeTemplate {
   private final String name;
   private final TypeTemplate element;

   public Named(String var1, TypeTemplate var2) {
      super();
      this.name = var1;
      this.element = var2;
   }

   public int size() {
      return this.element.size();
   }

   public TypeFamily apply(TypeFamily var1) {
      return (var2) -> {
         return DSL.named(this.name, this.element.apply(var1).apply(var2));
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.element.applyO(var1, var2, var3).apply(var4);
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return this.element.findFieldOrType(var1, var2, var3, var4);
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.element.hmap(var1, var2).apply(var3);
         return this.cap(var1, var3, var4);
      };
   }

   private <A> RewriteResult<Pair<String, A>, ?> cap(TypeFamily var1, int var2, RewriteResult<A, ?> var3) {
      return Named.NamedType.fix((Named.NamedType)this.apply(var1).apply(var2), var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Named)) {
         return false;
      } else {
         Named var2 = (Named)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.element, var2.element);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.element});
   }

   public String toString() {
      return "NamedTypeTag[" + this.name + ": " + this.element + "]";
   }

   public static final class NamedType<A> extends Type<Pair<String, A>> {
      protected final String name;
      protected final Type<A> element;

      public NamedType(String var1, Type<A> var2) {
         super();
         this.name = var1;
         this.element = var2;
      }

      public static <A, B> RewriteResult<Pair<String, A>, ?> fix(Named.NamedType<A> var0, RewriteResult<A, B> var1) {
         return Objects.equals(var1.view().function(), Functions.id()) ? RewriteResult.nop(var0) : opticView(var0, var1, wrapOptic(var0.name, TypedOptic.adapter(var1.view().type(), var1.view().newType())));
      }

      public RewriteResult<Pair<String, A>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         RewriteResult var4 = this.element.rewriteOrNop(var1);
         return fix(this, var4);
      }

      public Optional<RewriteResult<Pair<String, A>, ?>> one(TypeRewriteRule var1) {
         Optional var2 = var1.rewrite(this.element);
         return var2.map((var1x) -> {
            return fix(this, var1x);
         });
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.named(this.name, this.element.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.named(this.name, this.element.template());
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return this.element.findChoiceType(var1, var2);
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return this.element.findCheckedType(var1);
      }

      protected Codec<Pair<String, A>> buildCodec() {
         return new Codec<Pair<String, A>>() {
            public <T> DataResult<Pair<Pair<String, A>, T>> decode(DynamicOps<T> var1, T var2) {
               return NamedType.this.element.codec().decode(var1, var2).map((var1x) -> {
                  return var1x.mapFirst((var1) -> {
                     return Pair.of(NamedType.this.name, var1);
                  });
               }).setLifecycle(Lifecycle.experimental());
            }

            public <T> DataResult<T> encode(Pair<String, A> var1, DynamicOps<T> var2, T var3) {
               return !Objects.equals(var1.getFirst(), NamedType.this.name) ? DataResult.error("Named type name doesn't match: expected: " + NamedType.this.name + ", got: " + (String)var1.getFirst(), var3) : NamedType.this.element.codec().encode(var1.getSecond(), var2, var3).setLifecycle(Lifecycle.experimental());
            }
         };
      }

      public String toString() {
         return "NamedType[\"" + this.name + "\", " + this.element + "]";
      }

      public String name() {
         return this.name;
      }

      public Type<A> element() {
         return this.element;
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Named.NamedType)) {
            return false;
         } else {
            Named.NamedType var4 = (Named.NamedType)var1;
            return Objects.equals(this.name, var4.name) && this.element.equals(var4.element, var2, var3);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.element});
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return this.element.findFieldTypeOpt(var1);
      }

      public Optional<Pair<String, A>> point(DynamicOps<?> var1) {
         return this.element.point(var1).map((var1x) -> {
            return Pair.of(this.name, var1x);
         });
      }

      public <FT, FR> Either<TypedOptic<Pair<String, A>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         return this.element.findType(var1, var2, var3, var4).mapLeft((var1x) -> {
            return wrapOptic(this.name, var1x);
         });
      }

      protected static <A, B, FT, FR> TypedOptic<Pair<String, A>, Pair<String, B>, FT, FR> wrapOptic(String var0, TypedOptic<A, B, FT, FR> var1) {
         ImmutableSet.Builder var2 = ImmutableSet.builder();
         var2.addAll((Iterable)var1.bounds());
         var2.add((Object)Cartesian.Mu.TYPE_TOKEN);
         return new TypedOptic(var2.build(), DSL.named(var0, var1.sType()), DSL.named(var0, var1.tType()), var1.aType(), var1.bType(), Optics.proj2().composeUnchecked(var1.optic()));
      }
   }
}
