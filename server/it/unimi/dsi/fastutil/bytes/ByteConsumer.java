package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ByteConsumer extends Consumer<Byte>, IntConsumer {
   void accept(byte var1);

   /** @deprecated */
   @Deprecated
   default void accept(int var1) {
      this.accept(SafeMath.safeIntToByte(var1));
   }

   /** @deprecated */
   @Deprecated
   default void accept(Byte var1) {
      this.accept(var1);
   }

   default ByteConsumer andThen(ByteConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default ByteConsumer andThen(IntConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }

   /** @deprecated */
   @Deprecated
   default Consumer<Byte> andThen(Consumer<? super Byte> var1) {
      return super.andThen(var1);
   }
}
