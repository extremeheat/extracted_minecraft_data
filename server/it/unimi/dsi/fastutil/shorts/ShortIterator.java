package it.unimi.dsi.fastutil.shorts;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface ShortIterator extends Iterator<Short> {
   short nextShort();

   /** @deprecated */
   @Deprecated
   default Short next() {
      return this.nextShort();
   }

   default void forEachRemaining(ShortConsumer var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.nextShort());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Short> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextShort();
         }

         return var1 - var2 - 1;
      }
   }
}
