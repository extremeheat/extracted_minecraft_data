package it.unimi.dsi.fastutil.bytes;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface ByteIterator extends Iterator<Byte> {
   byte nextByte();

   /** @deprecated */
   @Deprecated
   default Byte next() {
      return this.nextByte();
   }

   default void forEachRemaining(ByteConsumer var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.nextByte());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEachRemaining(Consumer<? super Byte> var1) {
      Objects.requireNonNull(var1);
      this.forEachRemaining(var1::accept);
   }

   default int skip(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + var1);
      } else {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextByte();
         }

         return var1 - var2 - 1;
      }
   }
}
