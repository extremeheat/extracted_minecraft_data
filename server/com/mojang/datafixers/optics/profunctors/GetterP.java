package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import java.util.function.Function;

public interface GetterP<P extends K2, Mu extends GetterP.Mu> extends Profunctor<P, Mu>, Bicontravariant<P, Mu> {
   static <P extends K2, Proof extends GetterP.Mu> GetterP<P, Proof> unbox(App<Proof, P> var0) {
      return (GetterP)var0;
   }

   default <A, B, C> App2<P, C, A> secondPhantom(App2<P, C, B> var1) {
      return this.cimap(() -> {
         return this.rmap(var1, (var0) -> {
            return (Void)null;
         });
      }, Function.identity(), (var0) -> {
         return null;
      });
   }

   public interface Mu extends Profunctor.Mu, Bicontravariant.Mu {
   }
}
