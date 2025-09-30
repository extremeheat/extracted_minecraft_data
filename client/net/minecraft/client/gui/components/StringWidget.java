package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class StringWidget extends AbstractStringWidget {
   private int maxWidth;
   private int cachedWidth;
   private boolean cachedWidthDirty;
   private TextOverflow textOverflow;

   public StringWidget(Component var1, Font var2) {
      int var10003 = var2.width(var1.getVisualOrderText());
      Objects.requireNonNull(var2);
      this(0, 0, var10003, 9, var1, var2);
   }

   public StringWidget(int var1, int var2, Component var3, Font var4) {
      this(0, 0, var1, var2, var3, var4);
   }

   public StringWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.maxWidth = 0;
      this.cachedWidth = 0;
      this.cachedWidthDirty = true;
      this.textOverflow = StringWidget.TextOverflow.CLAMPED;
      this.active = false;
   }

   public StringWidget setColor(int var1) {
      super.setColor(var1);
      return this;
   }

   public void setMessage(Component var1) {
      super.setMessage(var1);
      this.cachedWidthDirty = true;
   }

   public StringWidget setMaxWidth(int var1) {
      return this.setMaxWidth(var1, StringWidget.TextOverflow.CLAMPED);
   }

   public StringWidget setMaxWidth(int var1, TextOverflow var2) {
      this.maxWidth = var1;
      this.textOverflow = var2;
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

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Component var5 = this.getMessage();
      Font var6 = this.getFont();
      int var7 = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
      int var8 = var6.width((FormattedText)var5);
      int var9 = this.getX();
      int var10000 = this.getY();
      int var10001 = this.getHeight();
      Objects.requireNonNull(var6);
      int var10 = var10000 + (var10001 - 9) / 2;
      boolean var11 = var8 > var7;
      if (var11) {
         switch (this.textOverflow.ordinal()) {
            case 0 -> var1.drawString(var6, this.clipText(var5, var7), var9, var10, this.getColor());
            case 1 -> this.renderScrollingString(var1, var6, 2, this.getColor());
         }
      } else {
         var1.drawString(var6, var5.getVisualOrderText(), var9, var10, this.getColor());
      }

   }

   private FormattedCharSequence clipText(Component var1, int var2) {
      Font var3 = this.getFont();
      FormattedText var4 = var3.substrByWidth(var1, var2 - var3.width((FormattedText)CommonComponents.ELLIPSIS));
      return Language.getInstance().getVisualOrder(FormattedText.composite(var4, CommonComponents.ELLIPSIS));
   }

   // $FF: synthetic method
   public AbstractStringWidget setColor(final int var1) {
      return this.setColor(var1);
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
