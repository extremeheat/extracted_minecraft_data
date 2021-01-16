package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.optics.ListTraversal;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class CompoundList implements TypeTemplate {
   private final TypeTemplate key;
   private final TypeTemplate element;

   public CompoundList(TypeTemplate var1, TypeTemplate var2) {
      super();
      this.key = var1;
      this.element = var2;
   }

   public int size() {
      return Math.max(this.key.size(), this.element.size());
   }

   public TypeFamily apply(TypeFamily var1) {
      return (var2) -> {
         return DSL.compoundList(this.key.apply(var1).apply(var2), this.element.apply(var1).apply(var2));
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
      return (new ListTraversal()).compose(Optics.proj2()).composeUnchecked(var1);
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return this.element.findFieldOrType(var1, var2, var3, var4).mapLeft((var1x) -> {
         return new CompoundList(this.key, var1x);
      });
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.key.hmap(var1, var2).apply(var3);
         RewriteResult var5 = (RewriteResult)this.element.hmap(var1, var2).apply(var3);
         return this.cap(this.apply(var1).apply(var3), var4, var5);
      };
   }

   private <L, R> RewriteResult<?, ?> cap(Type<?> var1, RewriteResult<L, ?> var2, RewriteResult<R, ?> var3) {
      return ((CompoundList.CompoundListType)var1).mergeViews(var2, var3);
   }

   public boolean equals(Object var1) {
      return var1 instanceof CompoundList && Objects.equals(this.element, ((CompoundList)var1).element);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.element});
   }

   public String toString() {
      return "CompoundList[" + this.element + "]";
   }

   public static final class CompoundListType<K, V> extends Type<java.util.List<Pair<K, V>>> {
      protected final Type<K> key;
      protected final Type<V> element;

      public CompoundListType(Type<K> var1, Type<V> var2) {
         super();
         this.key = var1;
         this.element = var2;
      }

      public RewriteResult<java.util.List<Pair<K, V>>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return this.mergeViews(this.key.rewriteOrNop(var1), this.element.rewriteOrNop(var1));
      }

      public <K2, V2> RewriteResult<java.util.List<Pair<K, V>>, ?> mergeViews(RewriteResult<K, K2> var1, RewriteResult<V, V2> var2) {
         RewriteResult var3 = fixKeys(this, this.key, this.element, var1);
         RewriteResult var4 = fixValues(var3.view().newType(), var1.view().newType(), this.element, var2);
         return var4.compose(var3);
      }

      public Optional<RewriteResult<java.util.List<Pair<K, V>>, ?>> one(TypeRewriteRule var1) {
         return DataFixUtils.or(var1.rewrite(this.key).map((var1x) -> {
            return fixKeys(this, this.key, this.element, var1x);
         }), () -> {
            return var1.rewrite(this.element).map((var1x) -> {
               return fixValues(this, this.key, this.element, var1x);
            });
         });
      }

      private static <K, V, K2> RewriteResult<java.util.List<Pair<K, V>>, java.util.List<Pair<K2, V>>> fixKeys(Type<java.util.List<Pair<K, V>>> var0, Type<K> var1, Type<V> var2, RewriteResult<K, K2> var3) {
         return opticView(var0, var3, TypedOptic.compoundListKeys(var1, var3.view().newType(), var2));
      }

      private static <K, V, V2> RewriteResult<java.util.List<Pair<K, V>>, java.util.List<Pair<K, V2>>> fixValues(Type<java.util.List<Pair<K, V>>> var0, Type<K> var1, Type<V> var2, RewriteResult<V, V2> var3) {
         return opticView(var0, var3, TypedOptic.compoundListElements(var1, var2, var3.view().newType()));
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.compoundList(this.key.updateMu(var1), this.element.updateMu(var1));
      }

      public TypeTemplate buildTemplate() {
         return new CompoundList(this.key.template(), this.element.template());
      }

      public Optional<java.util.List<Pair<K, V>>> point(DynamicOps<?> var1) {
         return Optional.of(ImmutableList.of());
      }

      public <FT, FR> Either<TypedOptic<java.util.List<Pair<K, V>>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         Either var5 = this.key.findType(var1, var2, var3, var4);
         return (Either)var5.map(this::capLeft, (var5x) -> {
            Either var6 = this.element.findType(var1, var2, var3, var4);
            return var6.mapLeft(this::capRight);
         });
      }

      private <FT, K2, FR> Either<TypedOptic<java.util.List<Pair<K, V>>, ?, FT, FR>, Type.FieldNotFoundException> capLeft(TypedOptic<K, K2, FT, FR> var1) {
         return Either.left(TypedOptic.compoundListKeys(var1.sType(), var1.tType(), this.element).compose(var1));
      }

      private <FT, V2, FR> TypedOptic<java.util.List<Pair<K, V>>, ?, FT, FR> capRight(TypedOptic<V, V2, FT, FR> var1) {
         return TypedOptic.compoundListElements(this.key, var1.sType(), var1.tType()).compose(var1);
      }

      protected Codec<java.util.List<Pair<K, V>>> buildCodec() {
         return Codec.compoundList(this.key.codec(), this.element.codec());
      }

      public String toString() {
         return "CompoundList[" + this.key + " -> " + this.element + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof CompoundList.CompoundListType)) {
            return false;
         } else {
            CompoundList.CompoundListType var4 = (CompoundList.CompoundListType)var1;
            return this.key.equals(var4.key, var2, var3) && this.element.equals(var4.element, var2, var3);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.key, this.element});
      }

      public Type<K> getKey() {
         return this.key;
      }

      public Type<V> getElement() {
         return this.element;
      }
   }
}
