package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      public int visitLines(final TextAlignment align, final int anchorX, final int topY, final int lineHeight, final ActiveTextCollector output) {
         return topY;
      }

      public int getLineCount() {
         return 0;
      }

      public int getWidth() {
         return 0;
      }
   };

   static MultiLineLabel create(final Font font, final Component... messages) {
      return create(font, 2147483647, 2147483647, messages);
   }

   static MultiLineLabel create(final Font font, final int maxWidth, final Component... messages) {
      return create(font, maxWidth, 2147483647, messages);
   }

   static MultiLineLabel create(final Font font, final Component message, final int maxWidth) {
      return create(font, maxWidth, 2147483647, message);
   }

   static MultiLineLabel create(final Font font, final int maxWidth, final int maxLines, final Component... messages) {
      return messages.length == 0 ? EMPTY : new MultiLineLabel() {
         private @Nullable List<TextAndWidth> cachedTextAndWidth;
         private @Nullable Language splitWithLanguage;

         public int visitLines(final TextAlignment align, final int anchorX, final int topY, final int lineHeight, final ActiveTextCollector output) {
            int y = topY;

            for(TextAndWidth splitLine : this.getSplitMessage()) {
               int leftX = align.calculateLeft(anchorX, splitLine.width);
               output.accept(leftX, y, splitLine.text);
               y += lineHeight;
            }

            return y;
         }

         private List<TextAndWidth> getSplitMessage() {
            Language currentLanguage = Language.getInstance();
            if (this.cachedTextAndWidth != null && currentLanguage == this.splitWithLanguage) {
               return this.cachedTextAndWidth;
            } else {
               this.splitWithLanguage = currentLanguage;
               List<FormattedText> splitMessage = new ArrayList();

               for(Component message : messages) {
                  splitMessage.addAll(font.splitIgnoringLanguage(message, maxWidth));
               }

               this.cachedTextAndWidth = new ArrayList();
               int actualMaxLines = Math.min(splitMessage.size(), maxLines);
               List<FormattedText> linesToAdd = splitMessage.subList(0, actualMaxLines);

               for(int i = 0; i < linesToAdd.size(); ++i) {
                  FormattedText formattedText = (FormattedText)linesToAdd.get(i);
                  FormattedCharSequence formattedCharSequence = Language.getInstance().getVisualOrder(formattedText);
                  if (i == linesToAdd.size() - 1 && actualMaxLines == maxLines && actualMaxLines != splitMessage.size()) {
                     FormattedText clippedText = font.substrByWidth(formattedText, font.width(formattedText) - font.width((FormattedText)CommonComponents.ELLIPSIS));
                     FormattedText withEllipsis = FormattedText.composite(clippedText, CommonComponents.ELLIPSIS.copy().withStyle(messages[messages.length - 1].getStyle()));
                     this.cachedTextAndWidth.add(new TextAndWidth(Language.getInstance().getVisualOrder(withEllipsis), font.width(withEllipsis)));
                  } else {
                     this.cachedTextAndWidth.add(new TextAndWidth(formattedCharSequence, font.width(formattedCharSequence)));
                  }
               }

               return this.cachedTextAndWidth;
            }
         }

         public int getLineCount() {
            return this.getSplitMessage().size();
         }

         public int getWidth() {
            return Math.min(maxWidth, this.getSplitMessage().stream().mapToInt(TextAndWidth::width).max().orElse(0));
         }
      };
   }

   int visitLines(TextAlignment align, int anchorX, int topY, int lineHeight, ActiveTextCollector output);

   int getLineCount();

   int getWidth();

   public static record TextAndWidth(FormattedCharSequence text, int width) {
      public TextAndWidth {
         super();
      }
   }
}
