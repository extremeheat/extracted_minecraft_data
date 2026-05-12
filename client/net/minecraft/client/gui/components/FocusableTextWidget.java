package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;

public class FocusableTextWidget extends MultiLineTextWidget {
   public static final int DEFAULT_PADDING = 4;
   private final int padding;
   private final int maxWidth;
   private final boolean alwaysShowBorder;
   private final BackgroundFill backgroundFill;

   private FocusableTextWidget(final Component message, final Font font, final int padding, final int maxWidth, final BackgroundFill backgroundFill, final boolean alwaysShowBorder) {
      super(message, font);
      this.active = true;
      this.padding = padding;
      this.maxWidth = maxWidth;
      this.alwaysShowBorder = alwaysShowBorder;
      this.backgroundFill = backgroundFill;
      this.updateWidth();
      this.updateHeight();
      this.setCentered(true);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, this.getMessage());
   }

   public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      int borderColor = this.alwaysShowBorder && !this.isFocused() ? ARGB.color(this.alpha, -6250336) : ARGB.white(this.alpha);
      switch (this.backgroundFill.ordinal()) {
         case 0:
            graphics.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ARGB.black(this.alpha));
            break;
         case 1:
            if (this.isFocused()) {
               graphics.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ARGB.black(this.alpha));
            }
         case 2:
      }

      if (this.isFocused() || this.alwaysShowBorder) {
         graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), borderColor);
      }

      super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
   }

   protected int getTextX() {
      return this.getX() + this.padding;
   }

   protected int getTextY() {
      return super.getTextY() + this.padding;
   }

   public MultiLineTextWidget setMaxWidth(final int maxWidth) {
      return super.setMaxWidth(maxWidth - this.padding * 2);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getPadding() {
      return this.padding;
   }

   public void updateWidth() {
      if (this.maxWidth != -1) {
         this.setWidth(this.maxWidth);
         this.setMaxWidth(this.maxWidth);
      } else {
         this.setWidth(this.getFont().width((FormattedText)this.getMessage()) + this.padding * 2);
      }

   }

   public void updateHeight() {
      Objects.requireNonNull(this.getFont());
      int textHeight = 9 * this.getFont().split(this.getMessage(), super.getWidth()).size();
      this.setHeight(textHeight + this.padding * 2);
   }

   public void setMessage(final Component message) {
      this.message = message;
      int width;
      if (this.maxWidth != -1) {
         width = this.maxWidth;
      } else {
         width = this.getFont().width((FormattedText)message) + this.padding * 2;
      }

      this.setWidth(width);
      this.updateHeight();
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   public boolean keyPressed(final KeyEvent event) {
      if (this.isActive() && event.isSelection()) {
         Optional<Style> clickableStyle = this.getMessage().<Style>visit((style, text) -> style.getClickEvent() != null ? Optional.of(style) : Optional.empty(), Style.EMPTY);
         if (clickableStyle.isPresent() && this.handleStyleClick((Style)clickableStyle.get())) {
            return true;
         }
      }

      return super.keyPressed(event);
   }

   public static Builder builder(final Component message, final Font font) {
      return new Builder(message, font);
   }

   public static Builder builder(final Component message, final Font font, final int padding) {
      return new Builder(message, font, padding);
   }

   public static class Builder {
      private final Component message;
      private final Font font;
      private final int padding;
      private int maxWidth;
      private boolean alwaysShowBorder;
      private BackgroundFill backgroundFill;

      private Builder(final Component message, final Font font) {
         this(message, font, 4);
      }

      private Builder(final Component message, final Font font, final int padding) {
         super();
         this.maxWidth = -1;
         this.alwaysShowBorder = true;
         this.backgroundFill = FocusableTextWidget.BackgroundFill.ALWAYS;
         this.message = message;
         this.font = font;
         this.padding = padding;
      }

      public Builder maxWidth(final int maxWidth) {
         this.maxWidth = maxWidth;
         return this;
      }

      public Builder textWidth(final int textWidth) {
         this.maxWidth = textWidth + this.padding * 2;
         return this;
      }

      public Builder alwaysShowBorder(final boolean alwaysShowBorder) {
         this.alwaysShowBorder = alwaysShowBorder;
         return this;
      }

      public Builder backgroundFill(final BackgroundFill backgroundFill) {
         this.backgroundFill = backgroundFill;
         return this;
      }

      public FocusableTextWidget build() {
         return new FocusableTextWidget(this.message, this.font, this.padding, this.maxWidth, this.backgroundFill, this.alwaysShowBorder);
      }
   }

   public static enum BackgroundFill {
      ALWAYS,
      ON_FOCUS,
      NEVER;

      private BackgroundFill() {
      }

      // $FF: synthetic method
      private static BackgroundFill[] $values() {
         return new BackgroundFill[]{ALWAYS, ON_FOCUS, NEVER};
      }
   }
}
