package it.unimi.dsi.fastutil.ints;

import java.util.Objects;
import java.util.function.Consumer;

public interface IntIterable extends Iterable<Integer> {
   IntIterator iterator();

   default void forEach(java.util.function.IntConsumer var1) {
      Objects.requireNonNull(var1);
      IntIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextInt());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(Consumer<? super Integer> var1) {
      Objects.requireNonNull(var1);
      this.forEach(var1::accept);
   }
}
