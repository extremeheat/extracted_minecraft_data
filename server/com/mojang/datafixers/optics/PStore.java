package com.mojang.datafixers.optics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.K1;
import java.util.function.Function;

interface PStore<I, J, X> extends App<PStore.Mu<I, J>, X> {
   static <I, J, X> PStore<I, J, X> unbox(App<PStore.Mu<I, J>, X> var0) {
      return (PStore)var0;
   }

   X peek(J var1);

   I pos();

   public static final class Instance<I, J> implements Functor<PStore.Mu<I, J>, PStore.Instance.Mu<I, J>> {
      public Instance() {
         super();
      }

      public <T, R> App<PStore.Mu<I, J>, R> map(Function<? super T, ? extends R> var1, App<PStore.Mu<I, J>, T> var2) {
         PStore var3 = PStore.unbox(var2);
         var3.getClass();
         Function var10000 = var1.compose(var3::peek)::apply;
         var3.getClass();
         return Optics.pStore(var10000, var3::pos);
      }

      public static final class Mu<I, J> implements Functor.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class Mu<I, J> implements K1 {
      public Mu() {
         super();
      }
   }
}
