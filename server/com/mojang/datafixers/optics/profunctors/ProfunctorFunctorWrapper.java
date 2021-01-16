package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import java.util.function.Function;

public class ProfunctorFunctorWrapper<P extends K2, F extends K1, G extends K1, A, B> implements App2<ProfunctorFunctorWrapper.Mu<P, F, G>, A, B> {
   private final App2<P, App<F, A>, App<G, B>> value;

   public static <P extends K2, F extends K1, G extends K1, A, B> ProfunctorFunctorWrapper<P, F, G, A, B> unbox(App2<ProfunctorFunctorWrapper.Mu<P, F, G>, A, B> var0) {
      return (ProfunctorFunctorWrapper)var0;
   }

   public ProfunctorFunctorWrapper(App2<P, App<F, A>, App<G, B>> var1) {
      super();
      this.value = var1;
   }

   public App2<P, App<F, A>, App<G, B>> value() {
      return this.value;
   }

   public static final class Instance<P extends K2, F extends K1, G extends K1> implements Profunctor<ProfunctorFunctorWrapper.Mu<P, F, G>, ProfunctorFunctorWrapper.Instance.Mu>, App<ProfunctorFunctorWrapper.Instance.Mu, ProfunctorFunctorWrapper.Mu<P, F, G>> {
      private final Profunctor<P, ? extends Profunctor.Mu> profunctor;
      private final Functor<F, ?> fFunctor;
      private final Functor<G, ?> gFunctor;

      public Instance(App<? extends Profunctor.Mu, P> var1, Functor<F, ?> var2, Functor<G, ?> var3) {
         super();
         this.profunctor = Profunctor.unbox(var1);
         this.fFunctor = var2;
         this.gFunctor = var3;
      }

      public <A, B, C, D> FunctionType<App2<ProfunctorFunctorWrapper.Mu<P, F, G>, A, B>, App2<ProfunctorFunctorWrapper.Mu<P, F, G>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var3) -> {
            App2 var4 = ProfunctorFunctorWrapper.unbox(var3).value();
            App2 var5 = this.profunctor.dimap(var4, (var2x) -> {
               return this.fFunctor.map(var1, var2x);
            }, (var2x) -> {
               return this.gFunctor.map(var2, var2x);
            });
            return new ProfunctorFunctorWrapper(var5);
         };
      }

      public static final class Mu implements Profunctor.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class Mu<P extends K2, F extends K1, G extends K1> implements K2 {
      public Mu() {
         super();
      }
   }
}
