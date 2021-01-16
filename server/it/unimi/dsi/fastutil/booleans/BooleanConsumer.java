package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface BooleanConsumer extends Consumer<Boolean> {
   void accept(boolean var1);

   /** @deprecated */
   @Deprecated
   default void accept(Boolean var1) {
      this.accept(var1);
   }

   default BooleanConsumer andThen(BooleanConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Boolean> andThen(Consumer<? super Boolean> var1) {
      return super.andThen(var1);
   }
}
