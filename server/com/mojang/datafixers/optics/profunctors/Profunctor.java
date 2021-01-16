package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.kinds.Kind2;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Profunctor<P extends K2, Mu extends Profunctor.Mu> extends Kind2<P, Mu> {
   static <P extends K2, Proof extends Profunctor.Mu> Profunctor<P, Proof> unbox(App<Proof, P> var0) {
      return (Profunctor)var0;
   }

   <A, B, C, D> FunctionType<App2<P, A, B>, App2<P, C, D>> dimap(Function<C, A> var1, Function<B, D> var2);

   default <A, B, C, D> App2<P, C, D> dimap(App2<P, A, B> var1, Function<C, A> var2, Function<B, D> var3) {
      return (App2)this.dimap(var2, var3).apply(var1);
   }

   default <A, B, C, D> App2<P, C, D> dimap(Supplier<App2<P, A, B>> var1, Function<C, A> var2, Function<B, D> var3) {
      return (App2)this.dimap(var2, var3).apply(var1.get());
   }

   default <A, B, C> App2<P, C, B> lmap(App2<P, A, B> var1, Function<C, A> var2) {
      return this.dimap(var1, var2, Function.identity());
   }

   default <A, B, D> App2<P, A, D> rmap(App2<P, A, B> var1, Function<B, D> var2) {
      return this.dimap(var1, Function.identity(), var2);
   }

   public interface Mu extends Kind2.Mu {
      TypeToken<Profunctor.Mu> TYPE_TOKEN = new TypeToken<Profunctor.Mu>() {
      };
   }
}
