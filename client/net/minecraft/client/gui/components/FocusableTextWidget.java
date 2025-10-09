package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;

public class FocusableTextWidget extends MultiLineTextWidget {
   public static final int DEFAULT_PADDING = 4;
   private final int padding;
   private final int maxWidth;
   private final boolean alwaysShowBorder;
   private final BackgroundFill backgroundFill;

   FocusableTextWidget(Component var1, Font var2, int var3, int var4, BackgroundFill var5, boolean var6) {
      super(var1, var2);
      this.active = true;
      this.padding = var3;
      this.maxWidth = var4;
      this.alwaysShowBorder = var6;
      this.backgroundFill = var5;
      this.updateWidth();
      this.updateHeight();
      this.setCentered(true);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getMessage());
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = ARGB.color(this.alpha, this.alwaysShowBorder ? (this.isFocused() ? -1 : -6250336) : -1);
      switch (this.backgroundFill.ordinal()) {
         case 0:
            var1.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ARGB.color(this.alpha, -16777216));
            break;
         case 1:
            if (this.isFocused()) {
               var1.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ARGB.color(this.alpha, -16777216));
            }
         case 2:
      }

      if (this.isFocused() || this.alwaysShowBorder) {
         var1.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), var5);
      }

      super.renderWidget(var1, var2, var3, var4);
   }

   protected int getTextX() {
      return this.getX() + this.padding;
   }

   protected int getTextY() {
      return super.getTextY() + this.padding;
   }

   public MultiLineTextWidget setMaxWidth(int var1) {
      return super.setMaxWidth(var1 - this.padding * 2);
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
      int var1 = 9 * this.getFont().split(this.getMessage(), super.getWidth()).size();
      this.setHeight(var1 + this.padding * 2);
   }

   public void setMessage(Component var1) {
      this.message = var1;
      int var2;
      if (this.maxWidth != -1) {
         var2 = this.maxWidth;
      } else {
         var2 = this.getFont().width((FormattedText)var1) + this.padding * 2;
      }

      this.setWidth(var2);
      this.updateHeight();
   }

   public void playDownSound(SoundManager var1) {
   }

   public static Builder builder(Component var0, Font var1) {
      return new Builder(var0, var1);
   }

   public static Builder builder(Component var0, Font var1, int var2) {
      return new Builder(var0, var1, var2);
   }

   public static class Builder {
      private final Component message;
      private final Font font;
      private final int padding;
      private int maxWidth;
      private boolean alwaysShowBorder;
      private BackgroundFill backgroundFill;

      Builder(Component var1, Font var2) {
         this(var1, var2, 4);
      }

      Builder(Component var1, Font var2, int var3) {
         super();
         this.maxWidth = -1;
         this.alwaysShowBorder = true;
         this.backgroundFill = FocusableTextWidget.BackgroundFill.ALWAYS;
         this.message = var1;
         this.font = var2;
         this.padding = var3;
      }

      public Builder maxWidth(int var1) {
         this.maxWidth = var1;
         return this;
      }

      public Builder textWidth(int var1) {
         this.maxWidth = var1 + this.padding * 2;
         return this;
      }

      public Builder alwaysShowBorder(boolean var1) {
         this.alwaysShowBorder = var1;
         return this;
      }

      public Builder backgroundFill(BackgroundFill var1) {
         this.backgroundFill = var1;
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
