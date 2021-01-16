package it.unimi.dsi.fastutil.chars;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface CharIterator extends Iterator<Character> {
   char nextChar();

   /** @deprecated */
   @Deprecated
   default Character next() {
      return this.nextChar();
   }

   default void forEachRemaining(CharConsumer var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.nextChar());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Character> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextChar();
         }

         return var1 - var2 - 1;
      }
   }
}
