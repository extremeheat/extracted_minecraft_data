package it.unimi.dsi.fastutil.ints;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface IntConsumer extends Consumer<Integer>, java.util.function.IntConsumer {
   /** @deprecated */
   @Deprecated
   default void accept(Integer var1) {
      this.accept(var1);
   }

   default IntConsumer andThen(java.util.function.IntConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Integer> andThen(Consumer<? super Integer> var1) {
      return super.andThen(var1);
   }
}
