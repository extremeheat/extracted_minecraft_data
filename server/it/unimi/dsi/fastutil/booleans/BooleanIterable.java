package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Consumer;

public interface BooleanIterable extends Iterable<Boolean> {
   BooleanIterator iterator();

   default void forEach(BooleanConsumer var1) {
      Objects.requireNonNull(var1);
      BooleanIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextBoolean());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(Consumer<? super Boolean> var1) {
      Objects.requireNonNull(var1);
      this.forEach(var1::accept);
   }
}
