package it.unimi.dsi.fastutil.ints;

import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;

public interface IntIterator extends OfInt {
   int nextInt();

   /** @deprecated */
   @Deprecated
   default Integer next() {
      return this.nextInt();
   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Integer> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextInt();
         }

         return var1 - var2 - 1;
      }
   }
}
