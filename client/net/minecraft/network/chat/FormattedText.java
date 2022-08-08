package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
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
            Iterator var2 = var0.iterator();

            Optional var4;
            do {
               if (!var2.hasNext()) {
                  return Optional.empty();
               }

               FormattedText var3 = (FormattedText)var2.next();
               var4 = var3.visit(var1);
            } while(!var4.isPresent());

            return var4;
         }

         public <T> Optional<T> visit(StyledContentConsumer<T> var1, Style var2) {
            Iterator var3 = var0.iterator();

            Optional var5;
            do {
               if (!var3.hasNext()) {
                  return Optional.empty();
               }

               FormattedText var4 = (FormattedText)var3.next();
               var5 = var4.visit(var1, var2);
            } while(!var5.isPresent());

            return var5;
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
