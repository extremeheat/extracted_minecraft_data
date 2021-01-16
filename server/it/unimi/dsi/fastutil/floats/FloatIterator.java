package it.unimi.dsi.fastutil.floats;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface FloatIterator extends Iterator<Float> {
   float nextFloat();

   /** @deprecated */
   @Deprecated
   default Float next() {
      return this.nextFloat();
   }

   default void forEachRemaining(FloatConsumer var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.nextFloat());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Float> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextFloat();
         }

         return var1 - var2 - 1;
      }
   }
}
