package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Consumer;

public interface DoubleIterable extends Iterable<Double> {
   DoubleIterator iterator();

   default void forEach(java.util.function.DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      DoubleIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextDouble());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(Consumer<? super Double> var1) {
      Objects.requireNonNull(var1);
      this.forEach(var1::accept);
   }
}
