package it.unimi.dsi.fastutil.bytes;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterable extends Iterable<Byte> {
   ByteIterator iterator();

   default void forEach(IntConsumer var1) {
      Objects.requireNonNull(var1);
      ByteIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextByte());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(final Consumer<? super Byte> var1) {
      this.forEach(new IntConsumer() {
         public void accept(int var1x) {
            var1.accept((byte)var1x);
         }
      });
   }
}
