package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface FormattedText {
   Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
   int DEFAULT_MAX_ITERATION = 65536;
   FormattedText EMPTY = new FormattedText() {
      public <T> Optional<T> visitUnbounded(final ContentConsumer<T> output) {
         return Optional.empty();
      }

      public <T> Optional<T> visitUnbounded(final StyledContentConsumer<T> output, final Style parentStyle) {
         return Optional.empty();
      }
   };

   <T> Optional<T> visitUnbounded(ContentConsumer<T> output);

   <T> Optional<T> visitUnbounded(StyledContentConsumer<T> output, final Style parentStyle);

   default <T> Optional<T> visit(final ContentConsumer<T> output, final T terminator) {
      return this.<T>visitUnbounded(output.bounded(65536, terminator));
   }

   default void visitWithoutResult(final ContentConsumer<Unit> ouput) {
      this.visit(ouput, Unit.INSTANCE);
   }

   default <T> Optional<T> visit(final StyledContentConsumer<T> output, final T terminator, final Style parentStyle) {
      return this.<T>visitUnbounded(output.bounded(65536, terminator), parentStyle);
   }

   default void visitWithoutResult(final StyledContentConsumer<Unit> ouput, final Style parentStyle) {
      this.visit(ouput, Unit.INSTANCE, parentStyle);
   }

   static FormattedText of(final String text) {
      return new FormattedText() {
         public <T> Optional<T> visitUnbounded(final ContentConsumer<T> output) {
            return output.accept(text);
         }

         public <T> Optional<T> visitUnbounded(final StyledContentConsumer<T> output, final Style parentStyle) {
            return output.accept(parentStyle, text);
         }
      };
   }

   static FormattedText of(final String text, final Style style) {
      return new FormattedText() {
         public <T> Optional<T> visitUnbounded(final ContentConsumer<T> output) {
            return output.accept(text);
         }

         public <T> Optional<T> visitUnbounded(final StyledContentConsumer<T> output, final Style parentStyle) {
            return output.accept(style.applyTo(parentStyle), text);
         }
      };
   }

   static FormattedText composite(final FormattedText... parts) {
      return composite((List)ImmutableList.copyOf(parts));
   }

   static FormattedText composite(final List<? extends FormattedText> parts) {
      return new FormattedText() {
         public <T> Optional<T> visitUnbounded(final ContentConsumer<T> output) {
            for(FormattedText part : parts) {
               Optional<T> result = part.<T>visitUnbounded(output);
               if (result.isPresent()) {
                  return result;
               }
            }

            return Optional.empty();
         }

         public <T> Optional<T> visitUnbounded(final StyledContentConsumer<T> output, final Style parentStyle) {
            for(FormattedText part : parts) {
               Optional<T> result = part.<T>visitUnbounded(output, parentStyle);
               if (result.isPresent()) {
                  return result;
               }
            }

            return Optional.empty();
         }
      };
   }

   default String getString(final int limit) {
      StringBuilder builder = new StringBuilder();
      this.visitWithoutResult((contents) -> {
         int remaining = limit - builder.length();
         if (remaining <= 0) {
            return STOP_ITERATION;
         } else {
            builder.append(contents.length() <= remaining ? contents : contents.substring(0, remaining));
            return Optional.empty();
         }
      });
      return builder.toString();
   }

   public interface StyledContentConsumer<T> {
      Optional<T> accept(Style style, String contents);

      default StyledContentConsumer<T> bounded(final int limit, final T terminator) {
         return new StyledContentConsumer<T>() {
            private int count;

            {
               Objects.requireNonNull(StyledContentConsumer.this);
            }

            public Optional<T> accept(final Style style, final String contents) {
               return ++this.count > limit ? Optional.of(terminator) : StyledContentConsumer.this.accept(style, contents);
            }
         };
      }
   }

   public interface ContentConsumer<T> {
      Optional<T> accept(String contents);

      default ContentConsumer<T> bounded(final int limit, final T terminator) {
         return new ContentConsumer<T>() {
            private int count;

            {
               Objects.requireNonNull(ContentConsumer.this);
            }

            public Optional<T> accept(final String contents) {
               return ++this.count > limit ? Optional.of(terminator) : ContentConsumer.this.accept(contents);
            }
         };
      }
   }
}
