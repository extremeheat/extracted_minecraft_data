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

   public void visitLines(ActiveTextCollector var1) {
      Component var2 = this.getMessage();
      Font var3 = this.getFont();
      int var4 = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
      int var5 = var3.width((FormattedText)var2);
      int var6 = this.getX();
      int var10000 = this.getY();
      int var10001 = this.getHeight();
      Objects.requireNonNull(var3);
      int var7 = var10000 + (var10001 - 9) / 2;
      boolean var8 = var5 > var4;
      if (var8) {
         switch (this.textOverflow.ordinal()) {
            case 0 -> var1.accept(var6, var7, clipText(var2, var3, var4));
            case 1 -> this.renderScrollingStringOverContents(var1, var2, 2);
         }
      } else {
         var1.accept(var6, var7, var2.getVisualOrderText());
      }

   }

   public static FormattedCharSequence clipText(Component var0, Font var1, int var2) {
      FormattedText var3 = var1.substrByWidth(var0, var2 - var1.width((FormattedText)CommonComponents.ELLIPSIS));
      return Language.getInstance().getVisualOrder(FormattedText.composite(var3, CommonComponents.ELLIPSIS));
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
