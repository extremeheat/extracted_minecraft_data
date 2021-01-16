package com.mojang.datafixers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Inj1;
import com.mojang.datafixers.optics.Inj2;
import com.mojang.datafixers.optics.InjTagged;
import com.mojang.datafixers.optics.ListTraversal;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Proj1;
import com.mojang.datafixers.optics.Proj2;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class TypedOptic<S, T, A, B> {
   protected final Set<TypeToken<? extends K1>> proofBounds;
   protected final Type<S> sType;
   protected final Type<T> tType;
   protected final Type<A> aType;
   protected final Type<B> bType;
   private final Optic<?, S, T, A, B> optic;

   public TypedOptic(TypeToken<? extends K1> var1, Type<S> var2, Type<T> var3, Type<A> var4, Type<B> var5, Optic<?, S, T, A, B> var6) {
      this((Set)ImmutableSet.of(var1), var2, var3, var4, var5, var6);
   }

   public TypedOptic(Set<TypeToken<? extends K1>> var1, Type<S> var2, Type<T> var3, Type<A> var4, Type<B> var5, Optic<?, S, T, A, B> var6) {
      super();
      this.proofBounds = var1;
      this.sType = var2;
      this.tType = var3;
      this.aType = var4;
      this.bType = var5;
      this.optic = var6;
   }

   public <P extends K2, Proof2 extends K1> App2<P, S, T> apply(TypeToken<Proof2> var1, App<Proof2, P> var2, App2<P, A, B> var3) {
      return (App2)((Optic)this.upCast(var1).orElseThrow(() -> {
         return new IllegalArgumentException("Couldn't upcast");
      })).eval(var2).apply(var3);
   }

   public Optic<?, S, T, A, B> optic() {
      return this.optic;
   }

   public Set<TypeToken<? extends K1>> bounds() {
      return this.proofBounds;
   }

   public Type<S> sType() {
      return this.sType;
   }

   public Type<T> tType() {
      return this.tType;
   }

   public Type<A> aType() {
      return this.aType;
   }

   public Type<B> bType() {
      return this.bType;
   }

   public <A1, B1> TypedOptic<S, T, A1, B1> compose(TypedOptic<A, B, A1, B1> var1) {
      ImmutableSet.Builder var2 = ImmutableSet.builder();
      var2.addAll((Iterable)this.proofBounds);
      var2.addAll((Iterable)var1.proofBounds);
      return new TypedOptic(var2.build(), this.sType, this.tType, var1.aType, var1.bType, this.optic().composeUnchecked(var1.optic()));
   }

   public <Proof2 extends K1> Optional<Optic<? super Proof2, S, T, A, B>> upCast(TypeToken<Proof2> var1) {
      return instanceOf(this.proofBounds, var1) ? Optional.of(this.optic) : Optional.empty();
   }

   public static <Proof2 extends K1> boolean instanceOf(Collection<TypeToken<? extends K1>> var0, TypeToken<Proof2> var1) {
      return var0.stream().allMatch((var1x) -> {
         return var1x.isSupertypeOf(var1);
      });
   }

   public static <S, T> TypedOptic<S, T, S, T> adapter(Type<S> var0, Type<T> var1) {
      return new TypedOptic(Profunctor.Mu.TYPE_TOKEN, var0, var1, var0, var1, Optics.id());
   }

   public static <F, G, F2> TypedOptic<Pair<F, G>, Pair<F2, G>, F, F2> proj1(Type<F> var0, Type<G> var1, Type<F2> var2) {
      return new TypedOptic(Cartesian.Mu.TYPE_TOKEN, DSL.and(var0, var1), DSL.and(var2, var1), var0, var2, new Proj1());
   }

   public static <F, G, G2> TypedOptic<Pair<F, G>, Pair<F, G2>, G, G2> proj2(Type<F> var0, Type<G> var1, Type<G2> var2) {
      return new TypedOptic(Cartesian.Mu.TYPE_TOKEN, DSL.and(var0, var1), DSL.and(var0, var2), var1, var2, new Proj2());
   }

   public static <F, G, F2> TypedOptic<Either<F, G>, Either<F2, G>, F, F2> inj1(Type<F> var0, Type<G> var1, Type<F2> var2) {
      return new TypedOptic(Cocartesian.Mu.TYPE_TOKEN, DSL.or(var0, var1), DSL.or(var2, var1), var0, var2, new Inj1());
   }

   public static <F, G, G2> TypedOptic<Either<F, G>, Either<F, G2>, G, G2> inj2(Type<F> var0, Type<G> var1, Type<G2> var2) {
      return new TypedOptic(Cocartesian.Mu.TYPE_TOKEN, DSL.or(var0, var1), DSL.or(var0, var2), var1, var2, new Inj2());
   }

   public static <K, V, K2> TypedOptic<List<Pair<K, V>>, List<Pair<K2, V>>, K, K2> compoundListKeys(Type<K> var0, Type<K2> var1, Type<V> var2) {
      return new TypedOptic(TraversalP.Mu.TYPE_TOKEN, DSL.compoundList(var0, var2), DSL.compoundList(var1, var2), var0, var1, (new ListTraversal()).compose(Optics.proj1()));
   }

   public static <K, V, V2> TypedOptic<List<Pair<K, V>>, List<Pair<K, V2>>, V, V2> compoundListElements(Type<K> var0, Type<V> var1, Type<V2> var2) {
      return new TypedOptic(TraversalP.Mu.TYPE_TOKEN, DSL.compoundList(var0, var1), DSL.compoundList(var0, var2), var1, var2, (new ListTraversal()).compose(Optics.proj2()));
   }

   public static <A, B> TypedOptic<List<A>, List<B>, A, B> list(Type<A> var0, Type<B> var1) {
      return new TypedOptic(TraversalP.Mu.TYPE_TOKEN, DSL.list(var0), DSL.list(var1), var0, var1, new ListTraversal());
   }

   public static <K, A, B> TypedOptic<Pair<K, ?>, Pair<K, ?>, A, B> tagged(TaggedChoice.TaggedChoiceType<K> var0, K var1, Type<A> var2, Type<B> var3) {
      if (!Objects.equals(var0.types().get(var1), var2)) {
         throw new IllegalArgumentException("Focused type doesn't match.");
      } else {
         HashMap var4 = Maps.newHashMap(var0.types());
         var4.put(var1, var3);
         Type var5 = DSL.taggedChoiceType(var0.getName(), var0.getKeyType(), var4);
         return new TypedOptic(Cocartesian.Mu.TYPE_TOKEN, var0, var5, var2, var3, new InjTagged(var1));
      }
   }
}
