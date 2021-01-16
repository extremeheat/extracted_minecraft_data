package it.unimi.dsi.fastutil.floats;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterable extends Iterable<Float> {
   FloatIterator iterator();

   default void forEach(DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      FloatIterator var2 = this.iterator();

      while(var2.hasNext()) {
         var1.accept((double)var2.nextFloat());
      }

   }

   /** @deprecated */
   @Deprecated
   default void forEach(final Consumer<? super Float> var1) {
      this.forEach(new DoubleConsumer() {
         public void accept(double var1x) {
            var1.accept((float)var1x);
         }
      });
   }
}
