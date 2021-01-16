package net.minecraft.network.chat;

import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
   Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
   FormattedText EMPTY = new FormattedText() {
      public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
         return Optional.empty();
      }
   };

   <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1);

   static FormattedText of(final String var0) {
      return new FormattedText() {
         public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
            return var1.accept(var0);
         }
      };
   }

   default String getString() {
      StringBuilder var1 = new StringBuilder();
      this.visit((var1x) -> {
         var1.append(var1x);
         return Optional.empty();
      });
      return var1.toString();
   }

   public interface ContentConsumer<T> {
      Optional<T> accept(String var1);
   }
}
