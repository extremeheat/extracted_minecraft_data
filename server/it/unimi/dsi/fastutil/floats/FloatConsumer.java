package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface FloatConsumer extends Consumer<Float>, DoubleConsumer {
   void accept(float var1);

   /** @deprecated */
   @Deprecated
   default void accept(double var1) {
      this.accept(SafeMath.safeDoubleToFloat(var1));
   }

   /** @deprecated */
   @Deprecated
   default void accept(Float var1) {
      this.accept(var1);
   }

   default FloatConsumer andThen(FloatConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default FloatConsumer andThen(DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept((double)var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Float> andThen(Consumer<? super Float> var1) {
      return super.andThen(var1);
   }
}
