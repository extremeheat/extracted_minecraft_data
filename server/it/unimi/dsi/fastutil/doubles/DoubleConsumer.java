package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface DoubleConsumer extends Consumer<Double>, java.util.function.DoubleConsumer {
   /** @deprecated */
   @Deprecated
   default void accept(Double var1) {
      this.accept(var1);
   }

   default DoubleConsumer andThen(java.util.function.DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Double> andThen(Consumer<? super Double> var1) {
      return super.andThen(var1);
   }
}
