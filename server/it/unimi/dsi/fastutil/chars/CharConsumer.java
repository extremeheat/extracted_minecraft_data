package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface CharConsumer extends Consumer<Character>, IntConsumer {
   void accept(char var1);

   /** @deprecated */
   @Deprecated
   default void accept(int var1) {
      this.accept(SafeMath.safeIntToChar(var1));
   }

   /** @deprecated */
   @Deprecated
   default void accept(Character var1) {
      this.accept(var1);
   }

   default CharConsumer andThen(CharConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default CharConsumer andThen(IntConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Character> andThen(Consumer<? super Character> var1) {
      return super.andThen(var1);
   }
}
