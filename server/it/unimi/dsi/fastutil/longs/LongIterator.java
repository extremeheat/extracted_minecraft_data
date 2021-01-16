package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.Consumer;

public interface LongIterator extends OfLong {
   long nextLong();

   /** @deprecated */
   @Deprecated
   default Long next() {
      return this.nextLong();
   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Long> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextLong();
         }

         return var1 - var2 - 1;
      }
   }
}
