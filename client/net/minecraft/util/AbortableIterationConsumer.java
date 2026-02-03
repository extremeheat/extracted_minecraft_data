package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer<T> {
   Continuation accept(T entry);

   static <T> AbortableIterationConsumer<T> forConsumer(final Consumer<T> consumer) {
      return (e) -> {
         consumer.accept(e);
         return AbortableIterationConsumer.Continuation.CONTINUE;
      };
   }

   public static enum Continuation {
      CONTINUE,
      ABORT;

      private Continuation() {
      }

      public boolean shouldAbort() {
         return this == ABORT;
      }

      // $FF: synthetic method
      private static Continuation[] $values() {
         return new Continuation[]{CONTINUE, ABORT};
      }
   }
}
