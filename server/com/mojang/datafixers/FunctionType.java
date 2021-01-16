package com.mojang.datafixers;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.kinds.Representable;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Procompose;
import com.mojang.datafixers.optics.Wander;
import com.mojang.datafixers.optics.profunctors.Mapping;
import com.mojang.datafixers.optics.profunctors.MonoidProfunctor;
import com.mojang.datafixers.optics.profunctors.Monoidal;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface FunctionType<A, B> extends Function<A, B>, App2<FunctionType.Mu, A, B>, App<FunctionType.ReaderMu<A>, B> {
   static <A, B> FunctionType<A, B> create(Function<? super A, ? extends B> var0) {
      return var0::apply;
   }

   static <A, B> Function<A, B> unbox(App2<FunctionType.Mu, A, B> var0) {
      return (FunctionType)var0;
   }

   static <A, B> Function<A, B> unbox(App<FunctionType.ReaderMu<A>, B> var0) {
      return (FunctionType)var0;
   }

   @Nonnull
   B apply(@Nonnull A var1);

   public static enum Instance implements TraversalP<FunctionType.Mu, FunctionType.Instance.Mu>, MonoidProfunctor<FunctionType.Mu, FunctionType.Instance.Mu>, Mapping<FunctionType.Mu, FunctionType.Instance.Mu>, Monoidal<FunctionType.Mu, FunctionType.Instance.Mu>, App<FunctionType.Instance.Mu, FunctionType.Mu> {
      INSTANCE;

      private Instance() {
      }

      public <A, B, C, D> FunctionType<App2<FunctionType.Mu, A, B>, App2<FunctionType.Mu, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return FunctionType.create(var2.compose(Optics.getFunc(var2x)).compose(var1));
         };
      }

      public <A, B, C> App2<FunctionType.Mu, Pair<A, C>, Pair<B, C>> first(App2<FunctionType.Mu, A, B> var1) {
         return FunctionType.create((var1x) -> {
            return Pair.of(Optics.getFunc(var1).apply(var1x.getFirst()), var1x.getSecond());
         });
      }

      public <A, B, C> App2<FunctionType.Mu, Pair<C, A>, Pair<C, B>> second(App2<FunctionType.Mu, A, B> var1) {
         return FunctionType.create((var1x) -> {
            return Pair.of(var1x.getFirst(), Optics.getFunc(var1).apply(var1x.getSecond()));
         });
      }

      public <S, T, A, B> App2<FunctionType.Mu, S, T> wander(Wander<S, T, A, B> var1, App2<FunctionType.Mu, A, B> var2) {
         return FunctionType.create((var2x) -> {
            return IdF.get((App)var1.wander(IdF.Instance.INSTANCE, (var1x) -> {
               return IdF.create(Optics.getFunc(var2).apply(var1x));
            }).apply(var2x));
         });
      }

      public <A, B, C> App2<FunctionType.Mu, Either<A, C>, Either<B, C>> left(App2<FunctionType.Mu, A, B> var1) {
         return FunctionType.create((var1x) -> {
            return var1x.mapLeft(Optics.getFunc(var1));
         });
      }

      public <A, B, C> App2<FunctionType.Mu, Either<C, A>, Either<C, B>> right(App2<FunctionType.Mu, A, B> var1) {
         return FunctionType.create((var1x) -> {
            return var1x.mapRight(Optics.getFunc(var1));
         });
      }

      public <A, B, C, D> App2<FunctionType.Mu, Pair<A, C>, Pair<B, D>> par(App2<FunctionType.Mu, A, B> var1, Supplier<App2<FunctionType.Mu, C, D>> var2) {
         return FunctionType.create((var2x) -> {
            return Pair.of(Optics.getFunc(var1).apply(var2x.getFirst()), Optics.getFunc((App2)var2.get()).apply(var2x.getSecond()));
         });
      }

      public App2<FunctionType.Mu, Void, Void> empty() {
         return FunctionType.create(Function.identity());
      }

      public <A, B> App2<FunctionType.Mu, A, B> zero(App2<FunctionType.Mu, A, B> var1) {
         return var1;
      }

      public <A, B> App2<FunctionType.Mu, A, B> plus(App2<Procompose.Mu<FunctionType.Mu, FunctionType.Mu>, A, B> var1) {
         Procompose var2 = Procompose.unbox(var1);
         return this.cap(var2);
      }

      private <A, B, C> App2<FunctionType.Mu, A, B> cap(Procompose<FunctionType.Mu, FunctionType.Mu, A, B, C> var1) {
         return FunctionType.create(Optics.getFunc(var1.second()).compose(Optics.getFunc((App2)var1.first().get())));
      }

      public <A, B, F extends K1> App2<FunctionType.Mu, App<F, A>, App<F, B>> mapping(Functor<F, ?> var1, App2<FunctionType.Mu, A, B> var2) {
         return FunctionType.create((var2x) -> {
            return var1.map(Optics.getFunc(var2), var2x);
         });
      }

      public static final class Mu implements TraversalP.Mu, MonoidProfunctor.Mu, Mapping.Mu, Monoidal.Mu {
         public static final TypeToken<FunctionType.Instance.Mu> TYPE_TOKEN = new TypeToken<FunctionType.Instance.Mu>() {
         };

         public Mu() {
            super();
         }
      }
   }

   public static final class ReaderInstance<R> implements Representable<FunctionType.ReaderMu<R>, R, FunctionType.ReaderInstance.Mu<R>> {
      public ReaderInstance() {
         super();
      }

      public <T, R2> App<FunctionType.ReaderMu<R>, R2> map(Function<? super T, ? extends R2> var1, App<FunctionType.ReaderMu<R>, T> var2) {
         return FunctionType.create(var1.compose(FunctionType.unbox(var2)));
      }

      public <B> App<FunctionType.ReaderMu<R>, B> to(App<FunctionType.ReaderMu<R>, B> var1) {
         return var1;
      }

      public <B> App<FunctionType.ReaderMu<R>, B> from(App<FunctionType.ReaderMu<R>, B> var1) {
         return var1;
      }

      public static final class Mu<A> implements Representable.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class ReaderMu<A> implements K1 {
      public ReaderMu() {
         super();
      }
   }

   public static final class Mu implements K2 {
      public Mu() {
         super();
      }
   }
}
