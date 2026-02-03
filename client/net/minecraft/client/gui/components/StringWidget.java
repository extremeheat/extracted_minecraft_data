package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class StringWidget extends AbstractStringWidget {
   private static final int TEXT_MARGIN = 2;
   private int maxWidth;
   private int cachedWidth;
   private boolean cachedWidthDirty;
   private TextOverflow textOverflow;

   public StringWidget(final Component message, final Font font) {
      int var10003 = font.width(message.getVisualOrderText());
      Objects.requireNonNull(font);
      this(0, 0, var10003, 9, message, font);
   }

   public StringWidget(final int width, final int height, final Component message, final Font font) {
      this(0, 0, width, height, message, font);
   }

   public StringWidget(final int x, final int y, final int width, final int height, final Component message, final Font font) {
      super(x, y, width, height, message, font);
      this.maxWidth = 0;
      this.cachedWidth = 0;
      this.cachedWidthDirty = true;
      this.textOverflow = StringWidget.TextOverflow.CLAMPED;
      this.active = false;
   }

   public void setMessage(final Component message) {
      super.setMessage(message);
      this.cachedWidthDirty = true;
   }

   public StringWidget setMaxWidth(final int maxWidth) {
      return this.setMaxWidth(maxWidth, StringWidget.TextOverflow.CLAMPED);
   }

   public StringWidget setMaxWidth(final int maxWidth, final TextOverflow textOverflow) {
      this.maxWidth = maxWidth;
      this.textOverflow = textOverflow;
      return this;
   }

   public int getWidth() {
      if (this.maxWidth > 0) {
         if (this.cachedWidthDirty) {
            this.cachedWidth = Math.min(this.maxWidth, this.getFont().width(this.getMessage().getVisualOrderText()));
            this.cachedWidthDirty = false;
         }

         return this.cachedWidth;
      } else {
         return super.getWidth();
      }
   }

   public void visitLines(final ActiveTextCollector output) {
      Component message = this.getMessage();
      Font font = this.getFont();
      int maxWidth = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
      int textWidth = font.width((FormattedText)message);
      int x = this.getX();
      int var10000 = this.getY();
      int var10001 = this.getHeight();
      Objects.requireNonNull(font);
      int y = var10000 + (var10001 - 9) / 2;
      boolean textOverflow = textWidth > maxWidth;
      if (textOverflow) {
         switch (this.textOverflow.ordinal()) {
            case 0 -> output.accept(x, y, clipText(message, font, maxWidth));
            case 1 -> this.renderScrollingStringOverContents(output, message, 2);
         }
      } else {
         output.accept(x, y, message.getVisualOrderText());
      }

   }

   public static FormattedCharSequence clipText(final Component text, final Font font, final int width) {
      FormattedText clippedText = font.substrByWidth(text, width - font.width((FormattedText)CommonComponents.ELLIPSIS));
      return Language.getInstance().getVisualOrder(FormattedText.composite(clippedText, CommonComponents.ELLIPSIS));
   }

   public static enum TextOverflow {
      CLAMPED,
      SCROLLING;

      private TextOverflow() {
      }

      // $FF: synthetic method
      private static TextOverflow[] $values() {
         return new TextOverflow[]{CLAMPED, SCROLLING};
      }
   }
}
