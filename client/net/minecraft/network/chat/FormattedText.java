package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
   Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
   FormattedText EMPTY = new FormattedText() {
      public <T> Optional<T> visit(ContentConsumer<T> var1) {
         return Optional.empty();
      }

      public <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2) {
         return Optional.empty();
      }
   };

   <T> Optional<T> visit(ContentConsumer<T> var1);

   <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2);

   static FormattedText of(final String var0) {
      return new FormattedText() {
         public <T> Optional<T> visit(ContentConsumer<T> var1) {
            return var1.accept(var0);
         }

         public <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2) {
            return var1.accept(var2, var0);
         }
      };
   }

   static FormattedText of(final String var0, final Style var1) {
      return new FormattedText() {
         public <T> Optional<T> visit(ContentConsumer<T> var1x) {
            return var1x.accept(var0);
         }

         public <T> Optional<T> visit(StyledContentConsumer<T> var1x, Style var2) {
            return var1x.accept(var1.applyTo(var2), var0);
         }
      };
   }

   static FormattedText composite(FormattedText... var0) {
      return composite((List)ImmutableList.copyOf(var0));
   }

   static FormattedText composite(final List<? extends FormattedText> var0) {
      return new FormattedText() {
         public <T> Optional<T> visit(ContentConsumer<T> var1) {
            for(FormattedText var3 : var0) {
               Optional var4 = var3.visit(var1);
               if (var4.isPresent()) {
                  return var4;
               }
            }

            return Optional.empty();
         }

         public <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2) {
            for(FormattedText var4 : var0) {
               Optional var5 = var4.visit(var1, var2);
               if (var5.isPresent()) {
                  return var5;
               }
            }

            return Optional.empty();
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

   public interface StyledContentConsumer<T> {
      Optional<T> accept(Style var1, String var2);
   }
}
