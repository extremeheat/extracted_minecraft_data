package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ShortConsumer extends Consumer<Short>, IntConsumer {
   void accept(short var1);

   /** @deprecated */
   @Deprecated
   default void accept(int var1) {
      this.accept(SafeMath.safeIntToShort(var1));
   }

   /** @deprecated */
   @Deprecated
   default void accept(Short var1) {
      this.accept(var1);
   }

   default ShortConsumer andThen(ShortConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default ShortConsumer andThen(IntConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Short> andThen(Consumer<? super Short> var1) {
      return super.andThen(var1);
   }
}
