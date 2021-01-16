package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.PrimitiveIterator.OfDouble;
import java.util.function.Consumer;

public interface DoubleIterator extends OfDouble {
   double nextDouble();

   /** @deprecated */
   @Deprecated
   default Double next() {
      return this.nextDouble();
   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Double> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextDouble();
         }

         return var1 - var2 - 1;
      }
   }
}
