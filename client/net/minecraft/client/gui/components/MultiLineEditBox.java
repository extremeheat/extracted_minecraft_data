package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class MultiLineEditBox extends AbstractTextAreaWidget {
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   private static final int TEXT_COLOR = -2039584;
   private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
   private static final int CURSOR_BLINK_INTERVAL_MS = 300;
   private final Font font;
   private final Component placeholder;
   private final MultilineTextField textField;
   private final int textColor;
   private final boolean textShadow;
   private final int cursorColor;
   private long focusedTime = Util.getMillis();

   MultiLineEditBox(Font var1, int var2, int var3, int var4, int var5, Component var6, Component var7, int var8, boolean var9, int var10, boolean var11, boolean var12) {
      super(var2, var3, var4, var5, var7, var11, var12);
      this.font = var1;
      this.textShadow = var9;
      this.textColor = var8;
      this.cursorColor = var10;
      this.placeholder = var6;
      this.textField = new MultilineTextField(var1, var4 - this.totalInnerPadding());
      this.textField.setCursorListener(this::scrollToCursor);
   }

   public void setCharacterLimit(int var1) {
      this.textField.setCharacterLimit(var1);
   }

   public void setLineLimit(int var1) {
      this.textField.setLineLimit(var1);
   }

   public void setValueListener(Consumer<String> var1) {
      this.textField.setValueListener(var1);
   }

   public void setValue(String var1) {
      this.setValue(var1, false);
   }

   public void setValue(String var1, boolean var2) {
      this.textField.setValue(var1, var2);
   }

   public String getValue() {
      return this.textField.value();
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
   }

   public void onClick(MouseButtonEvent var1, boolean var2) {
      if (var2) {
         this.textField.selectWordAtCursor();
      } else {
         this.textField.setSelecting(var1.hasShiftDown());
         this.seekCursorScreen(var1.x(), var1.y());
      }

   }

   protected void onDrag(MouseButtonEvent var1, double var2, double var4) {
      this.textField.setSelecting(true);
      this.seekCursorScreen(var1.x(), var1.y());
      this.textField.setSelecting(var1.hasShiftDown());
   }

   public boolean keyPressed(KeyEvent var1) {
      return this.textField.keyPressed(var1);
   }

   public boolean charTyped(CharacterEvent var1) {
      if (this.visible && this.isFocused() && var1.isAllowedChatCharacter()) {
         this.textField.insertText(var1.codepointAsString());
         return true;
      } else {
         return false;
      }
   }

   protected void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      String var5 = this.textField.value();
      if (var5.isEmpty() && !this.isFocused()) {
         var1.drawWordWrap(this.font, this.placeholder, this.getInnerLeft(), this.getInnerTop(), this.width - this.totalInnerPadding(), -857677600);
      } else {
         int var6 = this.textField.cursor();
         boolean var7 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L;
         boolean var8 = var6 < var5.length();
         int var9 = 0;
         int var10 = 0;
         int var11 = this.getInnerTop();
         boolean var12 = false;

         for(MultilineTextField.StringView var14 : this.textField.iterateLines()) {
            Objects.requireNonNull(this.font);
            boolean var15 = this.withinContentAreaTopBottom(var11, var11 + 9);
            int var16 = this.getInnerLeft();
            if (var7 && var8 && var6 >= var14.beginIndex() && var6 <= var14.endIndex()) {
               if (var15) {
                  String var24 = var5.substring(var14.beginIndex(), var6);
                  var1.drawString(this.font, var24, var16, var11, this.textColor, this.textShadow);
                  var9 = var16 + this.font.width(var24);
                  if (!var12) {
                     int var10002 = var11 - 1;
                     int var10003 = var9 + 1;
                     int var10004 = var11 + 1;
                     Objects.requireNonNull(this.font);
                     var1.fill(var9, var10002, var10003, var10004 + 9, this.cursorColor);
                     var12 = true;
                  }

                  var1.drawString(this.font, var5.substring(var6, var14.endIndex()), var9, var11, this.textColor, this.textShadow);
               }
            } else {
               if (var15) {
                  String var17 = var5.substring(var14.beginIndex(), var14.endIndex());
                  var1.drawString(this.font, var17, var16, var11, this.textColor, this.textShadow);
                  var9 = var16 + this.font.width(var17) - 1;
               }

               var10 = var11;
            }

            Objects.requireNonNull(this.font);
            var11 += 9;
         }

         if (var7 && !var8) {
            Objects.requireNonNull(this.font);
            if (this.withinContentAreaTopBottom(var10, var10 + 9)) {
               var1.drawString(this.font, "_", var9 + 1, var10, this.cursorColor, this.textShadow);
            }
         }

         if (this.textField.hasSelection()) {
            MultilineTextField.StringView var20 = this.textField.getSelected();
            int var21 = this.getInnerLeft();
            var11 = this.getInnerTop();

            for(MultilineTextField.StringView var23 : this.textField.iterateLines()) {
               if (var20.beginIndex() > var23.endIndex()) {
                  Objects.requireNonNull(this.font);
                  var11 += 9;
               } else {
                  if (var23.beginIndex() > var20.endIndex()) {
                     break;
                  }

                  Objects.requireNonNull(this.font);
                  if (this.withinContentAreaTopBottom(var11, var11 + 9)) {
                     int var25 = this.font.width(var5.substring(var23.beginIndex(), Math.max(var20.beginIndex(), var23.beginIndex())));
                     int var18;
                     if (var20.endIndex() > var23.endIndex()) {
                        var18 = this.width - this.innerPadding();
                     } else {
                        var18 = this.font.width(var5.substring(var23.beginIndex(), var20.endIndex()));
                     }

                     int var10001 = var21 + var25;
                     int var26 = var21 + var18;
                     Objects.requireNonNull(this.font);
                     var1.textHighlight(var10001, var11, var26, var11 + 9, true);
                  }

                  Objects.requireNonNull(this.font);
                  var11 += 9;
               }
            }
         }

         if (this.isHovered()) {
            var1.requestCursor(CursorTypes.IBEAM);
         }

      }
   }

   protected void renderDecorations(GuiGraphics var1) {
      super.renderDecorations(var1);
      if (this.textField.hasCharacterLimit()) {
         int var2 = this.textField.characterLimit();
         MutableComponent var3 = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.value().length(), var2);
         var1.drawString(this.font, (Component)var3, this.getX() + this.width - this.font.width((FormattedText)var3), this.getY() + this.height + 4, -6250336);
      }

   }

   public int getInnerHeight() {
      Objects.requireNonNull(this.font);
      return 9 * this.textField.getLineCount();
   }

   protected double scrollRate() {
      Objects.requireNonNull(this.font);
      return 9.0 / 2.0;
   }

   private void scrollToCursor() {
      double var1 = this.scrollAmount();
      MultilineTextField var10000 = this.textField;
      Objects.requireNonNull(this.font);
      MultilineTextField.StringView var3 = var10000.getLineView((int)(var1 / 9.0));
      if (this.textField.cursor() <= var3.beginIndex()) {
         int var5 = this.textField.getLineAtCursor();
         Objects.requireNonNull(this.font);
         var1 = (double)(var5 * 9);
      } else {
         var10000 = this.textField;
         double var10001 = var1 + (double)this.height;
         Objects.requireNonNull(this.font);
         MultilineTextField.StringView var4 = var10000.getLineView((int)(var10001 / 9.0) - 1);
         if (this.textField.cursor() > var4.endIndex()) {
            int var7 = this.textField.getLineAtCursor();
            Objects.requireNonNull(this.font);
            var7 = var7 * 9 - this.height;
            Objects.requireNonNull(this.font);
            var1 = (double)(var7 + 9 + this.totalInnerPadding());
         }
      }

      this.setScrollAmount(var1);
   }

   private void seekCursorScreen(double var1, double var3) {
      double var5 = var1 - (double)this.getX() - (double)this.innerPadding();
      double var7 = var3 - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
      this.textField.seekCursorToPoint(var5, var7);
   }

   public void setFocused(boolean var1) {
      super.setFocused(var1);
      if (var1) {
         this.focusedTime = Util.getMillis();
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int x;
      private int y;
      private Component placeholder;
      private int textColor;
      private boolean textShadow;
      private int cursorColor;
      private boolean showBackground;
      private boolean showDecorations;

      public Builder() {
         super();
         this.placeholder = CommonComponents.EMPTY;
         this.textColor = -2039584;
         this.textShadow = true;
         this.cursorColor = -3092272;
         this.showBackground = true;
         this.showDecorations = true;
      }

      public Builder setX(int var1) {
         this.x = var1;
         return this;
      }

      public Builder setY(int var1) {
         this.y = var1;
         return this;
      }

      public Builder setPlaceholder(Component var1) {
         this.placeholder = var1;
         return this;
      }

      public Builder setTextColor(int var1) {
         this.textColor = var1;
         return this;
      }

      public Builder setTextShadow(boolean var1) {
         this.textShadow = var1;
         return this;
      }

      public Builder setCursorColor(int var1) {
         this.cursorColor = var1;
         return this;
      }

      public Builder setShowBackground(boolean var1) {
         this.showBackground = var1;
         return this;
      }

      public Builder setShowDecorations(boolean var1) {
         this.showDecorations = var1;
         return this;
      }

      public MultiLineEditBox build(Font var1, int var2, int var3, Component var4) {
         return new MultiLineEditBox(var1, this.x, this.y, var2, var3, this.placeholder, var4, this.textColor, this.textShadow, this.cursorColor, this.showBackground, this.showDecorations);
      }
   }
}
