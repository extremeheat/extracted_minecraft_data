package it.unimi.dsi.fastutil.shorts;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ShortIterable extends Iterable<Short> {
   ShortIterator iterator();

   default void forEach(IntConsumer var1) {
      Objects.requireNonNull(var1);
      ShortIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextShort());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(final Consumer<? super Short> var1) {
      this.forEach(new IntConsumer() {
         public void accept(int var1x) {
            var1.accept((short)var1x);
         }
      });
   }
}
