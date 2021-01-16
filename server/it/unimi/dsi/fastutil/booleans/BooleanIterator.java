package it.unimi.dsi.fastutil.booleans;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface BooleanIterator extends Iterator<Boolean> {
   boolean nextBoolean();

   /** @deprecated */
   @Deprecated
   default Boolean next() {
      return this.nextBoolean();
   }

   default void forEachRemaining(BooleanConsumer var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.nextBoolean());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Boolean> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextBoolean();
         }

         return var1 - var2 - 1;
      }
   }
}
