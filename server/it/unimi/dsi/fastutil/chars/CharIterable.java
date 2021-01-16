package it.unimi.dsi.fastutil.chars;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface CharIterable extends Iterable<Character> {
   CharIterator iterator();

   default void forEach(IntConsumer var1) {
      Objects.requireNonNull(var1);
      CharIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept(var2.nextChar());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(final Consumer<? super Character> var1) {
      this.forEach(new IntConsumer() {
         public void accept(int var1x) {
            var1.accept((char)var1x);
         }
      });
   }
}
