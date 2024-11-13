package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;

public class MultiLineEditBox extends AbstractTextAreaWidget {
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   private static final int TEXT_COLOR = -2039584;
   private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
   private static final int CURSOR_BLINK_INTERVAL_MS = 300;
   private final Font font;
   private final Component placeholder;
   private final MultilineTextField textField;
   private long focusedTime = Util.getMillis();

   public MultiLineEditBox(Font var1, int var2, int var3, int var4, int var5, Component var6, Component var7) {
      super(var2, var3, var4, var5, var7);
      this.font = var1;
      this.placeholder = var6;
      this.textField = new MultilineTextField(var1, var4 - this.totalInnerPadding());
      this.textField.setCursorListener(this::scrollToCursor);
   }

   public void setCharacterLimit(int var1) {
      this.textField.setCharacterLimit(var1);
   }

   public void setValueListener(Consumer<String> var1) {
      this.textField.setValueListener(var1);
   }

   public void setValue(String var1) {
      this.textField.setValue(var1);
   }

   public String getValue() {
      return this.textField.value();
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
   }

   public void onClick(double var1, double var3) {
      this.textField.setSelecting(Screen.hasShiftDown());
      this.seekCursorScreen(var1, var3);
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
      this.textField.setSelecting(true);
      this.seekCursorScreen(var1, var3);
      this.textField.setSelecting(Screen.hasShiftDown());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.textField.keyPressed(var1);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.visible && this.isFocused() && StringUtil.isAllowedChatCharacter(var1)) {
         this.textField.insertText(Character.toString(var1));
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

         for(MultilineTextField.StringView var13 : this.textField.iterateLines()) {
            Objects.requireNonNull(this.font);
            boolean var14 = this.withinContentAreaTopBottom(var11, var11 + 9);
            if (var7 && var8 && var6 >= var13.beginIndex() && var6 <= var13.endIndex()) {
               if (var14) {
                  var9 = var1.drawString(this.font, var5.substring(var13.beginIndex(), var6), this.getInnerLeft(), var11, -2039584) - 1;
                  int var10002 = var11 - 1;
                  int var10003 = var9 + 1;
                  int var10004 = var11 + 1;
                  Objects.requireNonNull(this.font);
                  var1.fill(var9, var10002, var10003, var10004 + 9, -3092272);
                  var1.drawString(this.font, var5.substring(var6, var13.endIndex()), var9, var11, -2039584);
               }
            } else {
               if (var14) {
                  var9 = var1.drawString(this.font, var5.substring(var13.beginIndex(), var13.endIndex()), this.getInnerLeft(), var11, -2039584) - 1;
               }

               var10 = var11;
            }

            Objects.requireNonNull(this.font);
            var11 += 9;
         }

         if (var7 && !var8) {
            Objects.requireNonNull(this.font);
            if (this.withinContentAreaTopBottom(var10, var10 + 9)) {
               var1.drawString(this.font, "_", var9, var10, -3092272);
            }
         }

         if (this.textField.hasSelection()) {
            MultilineTextField.StringView var19 = this.textField.getSelected();
            int var20 = this.getInnerLeft();
            var11 = this.getInnerTop();

            for(MultilineTextField.StringView var15 : this.textField.iterateLines()) {
               if (var19.beginIndex() > var15.endIndex()) {
                  Objects.requireNonNull(this.font);
                  var11 += 9;
               } else {
                  if (var15.beginIndex() > var19.endIndex()) {
                     break;
                  }

                  Objects.requireNonNull(this.font);
                  if (this.withinContentAreaTopBottom(var11, var11 + 9)) {
                     int var16 = this.font.width(var5.substring(var15.beginIndex(), Math.max(var19.beginIndex(), var15.beginIndex())));
                     int var17;
                     if (var19.endIndex() > var15.endIndex()) {
                        var17 = this.width - this.innerPadding();
                     } else {
                        var17 = this.font.width(var5.substring(var15.beginIndex(), var19.endIndex()));
                     }

                     int var22 = var20 + var16;
                     int var23 = var20 + var17;
                     Objects.requireNonNull(this.font);
                     this.renderHighlight(var1, var22, var11, var23, var11 + 9);
                  }

                  Objects.requireNonNull(this.font);
                  var11 += 9;
               }
            }
         }

      }
   }

   protected void renderDecorations(GuiGraphics var1) {
      super.renderDecorations(var1);
      if (this.textField.hasCharacterLimit()) {
         int var2 = this.textField.characterLimit();
         MutableComponent var3 = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.value().length(), var2);
         var1.drawString(this.font, (Component)var3, this.getX() + this.width - this.font.width((FormattedText)var3), this.getY() + this.height + 4, 10526880);
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

   private void renderHighlight(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      var1.fill(RenderType.guiTextHighlight(), var2, var3, var4, var5, -16776961);
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
}
