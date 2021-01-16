package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface LongConsumer extends Consumer<Long>, java.util.function.LongConsumer {
   /** @deprecated */
   @Deprecated
   default void accept(Long var1) {
      this.accept(var1);
   }

   default LongConsumer andThen(java.util.function.LongConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Long> andThen(Consumer<? super Long> var1) {
      return super.andThen(var1);
   }
}
