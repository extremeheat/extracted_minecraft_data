package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.optics.ListTraversal;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class List implements TypeTemplate {
   private final TypeTemplate element;

   public List(TypeTemplate var1) {
      super();
      this.element = var1;
   }

   public int size() {
      return this.element.size();
   }

   public TypeFamily apply(final TypeFamily var1) {
      return new TypeFamily() {
         public Type<?> apply(int var1x) {
            return DSL.list(List.this.element.apply(var1).apply(var1x));
         }
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         OpticParts var5 = this.element.applyO(var1, var2, var3).apply(var4);
         HashSet var6 = Sets.newHashSet((Iterable)var5.bounds());
         var6.add(TraversalP.Mu.TYPE_TOKEN);
         return new OpticParts(var6, this.cap(var5.optic()));
      });
   }

   private <S, T, A, B> Optic<?, ?, ?, A, B> cap(Optic<?, S, T, A, B> var1) {
      return (new ListTraversal()).composeUnchecked(var1);
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return this.element.findFieldOrType(var1, var2, var3, var4).mapLeft(List::new);
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.element.hmap(var1, var2).apply(var3);
         return this.cap(this.apply(var1).apply(var3), var4);
      };
   }

   private <E> RewriteResult<?, ?> cap(Type<?> var1, RewriteResult<E, ?> var2) {
      return ((List.ListType)var1).fix(var2);
   }

   public boolean equals(Object var1) {
      return var1 instanceof List && Objects.equals(this.element, ((List)var1).element);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.element});
   }

   public String toString() {
      return "List[" + this.element + "]";
   }

   public static final class ListType<A> extends Type<java.util.List<A>> {
      protected final Type<A> element;

      public ListType(Type<A> var1) {
         super();
         this.element = var1;
      }

      public RewriteResult<java.util.List<A>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         RewriteResult var4 = this.element.rewriteOrNop(var1);
         return this.fix(var4);
      }

      public Optional<RewriteResult<java.util.List<A>, ?>> one(TypeRewriteRule var1) {
         return var1.rewrite(this.element).map(this::fix);
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.list(this.element.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return DSL.list(this.element.template());
      }

      public Optional<java.util.List<A>> point(DynamicOps<?> var1) {
         return Optional.of(ImmutableList.of());
      }

      public <FT, FR> Either<TypedOptic<java.util.List<A>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         Either var5 = this.element.findType(var1, var2, var3, var4);
         return var5.mapLeft(this::capLeft);
      }

      private <FT, FR, B> TypedOptic<java.util.List<A>, ?, FT, FR> capLeft(TypedOptic<A, B, FT, FR> var1) {
         return TypedOptic.list(var1.sType(), var1.tType()).compose(var1);
      }

      public <B> RewriteResult<java.util.List<A>, ?> fix(RewriteResult<A, B> var1) {
         return opticView(this, var1, TypedOptic.list(this.element, var1.view().newType()));
      }

      public Codec<java.util.List<A>> buildCodec() {
         return Codec.list(this.element.codec());
      }

      public String toString() {
         return "List[" + this.element + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         return var1 instanceof List.ListType && this.element.equals(((List.ListType)var1).element, var2, var3);
      }

      public int hashCode() {
         return this.element.hashCode();
      }

      public Type<A> getElement() {
         return this.element;
      }
   }
}
