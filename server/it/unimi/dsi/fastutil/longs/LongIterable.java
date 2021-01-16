package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

public interface LongIterable extends Iterable<Long> {
   LongIterator iterator();

   default void forEach(java.util.function.LongConsumer var1) {
      Objects.requireNonNull(var1);
      LongIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextLong());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(Consumer<? super Long> var1) {
      Objects.requireNonNull(var1);
      this.forEach(var1::accept);
   }
}
