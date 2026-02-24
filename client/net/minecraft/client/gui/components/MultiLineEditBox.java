package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class MultiLineEditBox extends AbstractTextAreaWidget {
   private static final int CURSOR_COLOR = -3092272;
   private static final int PLACEHOLDER_TEXT_COLOR = ARGB.color(204, -2039584);
   private final Font font;
   private final Component placeholder;
   private final MultilineTextField textField;
   private final int textColor;
   private final boolean textShadow;
   private final int cursorColor;
   private @Nullable IMEPreeditOverlay preeditOverlay;
   private long focusedTime;

   private MultiLineEditBox(final Font font, final int x, final int y, final int width, final int height, final Component placeholder, final Component narration, final int textColor, final boolean textShadow, final int cursorColor, final boolean showBackground, final boolean showDecorations) {
      Objects.requireNonNull(font);
      super(x, y, width, height, narration, AbstractScrollArea.defaultSettings((int)(9.0 / 2.0)), showBackground, showDecorations);
      this.focusedTime = Util.getMillis();
      this.font = font;
      this.textShadow = textShadow;
      this.textColor = textColor;
      this.cursorColor = cursorColor;
      this.placeholder = placeholder;
      this.textField = new MultilineTextField(font, width - this.totalInnerPadding());
      this.textField.setCursorListener(this::scrollToCursor);
   }

   public void setCharacterLimit(final int characterLimit) {
      this.textField.setCharacterLimit(characterLimit);
   }

   public void setLineLimit(final int lineLimit) {
      this.textField.setLineLimit(lineLimit);
   }

   public void setValueListener(final Consumer<String> valueListener) {
      this.textField.setValueListener(valueListener);
   }

   public void setValue(final String value) {
      this.setValue(value, false);
   }

   public void setValue(final String value, final boolean allowOverflowLineLimit) {
      this.textField.setValue(value, allowOverflowLineLimit);
   }

   public String getValue() {
      return this.textField.value();
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
   }

   public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
      if (doubleClick) {
         this.textField.selectWordAtCursor();
      } else {
         this.textField.setSelecting(event.hasShiftDown());
         this.seekCursorScreen(event.x(), event.y());
      }

   }

   protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
      this.textField.setSelecting(true);
      this.seekCursorScreen(event.x(), event.y());
      this.textField.setSelecting(event.hasShiftDown());
   }

   public boolean keyPressed(final KeyEvent event) {
      return this.textField.keyPressed(event);
   }

   public boolean charTyped(final CharacterEvent event) {
      if (this.visible && this.isFocused() && event.isAllowedChatCharacter()) {
         this.textField.insertText(event.codepointAsString());
         return true;
      } else {
         return false;
      }
   }

   public boolean preeditUpdated(final @Nullable PreeditEvent event) {
      IMEPreeditOverlay var10001;
      if (event != null) {
         Font var10004 = this.font;
         Objects.requireNonNull(this.font);
         var10001 = new IMEPreeditOverlay(event, var10004, 9 + 1);
      } else {
         var10001 = null;
      }

      this.preeditOverlay = var10001;
      return true;
   }

   protected void renderContents(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      String value = this.textField.value();
      if (value.isEmpty() && !this.isFocused()) {
         graphics.drawWordWrap(this.font, this.placeholder, this.getInnerLeft(), this.getInnerTop(), this.width - this.totalInnerPadding(), PLACEHOLDER_TEXT_COLOR);
      } else {
         int cursor = this.textField.cursor();
         boolean showCursor = this.isFocused() && TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime);
         boolean needsValidCursorPos = this.preeditOverlay != null;
         boolean insertCursor = cursor < value.length();
         int cursorX = 0;
         int cursorY = 0;
         int drawTop = this.getInnerTop();
         int innerLeft = this.getInnerLeft();
         boolean hasDrawnCursor = false;

         for(MultilineTextField.StringView lineView : this.textField.iterateLines()) {
            Objects.requireNonNull(this.font);
            boolean lineWithinVisibleBounds = this.withinContentAreaTopBottom(drawTop, drawTop + 9);
            if (!hasDrawnCursor && (needsValidCursorPos || showCursor) && insertCursor && cursor >= lineView.beginIndex() && cursor <= lineView.endIndex()) {
               if (lineWithinVisibleBounds) {
                  String textBeforeCursor = value.substring(lineView.beginIndex(), cursor);
                  int textBeforeCursorPosRight = innerLeft + this.font.width(textBeforeCursor);
                  String textAfterCursor = value.substring(cursor, lineView.endIndex());
                  graphics.drawString(this.font, textBeforeCursor, innerLeft, drawTop, this.textColor, this.textShadow);
                  graphics.drawString(this.font, textAfterCursor, textBeforeCursorPosRight, drawTop, this.textColor, this.textShadow);
                  cursorX = textBeforeCursorPosRight;
                  cursorY = drawTop;
                  if (showCursor) {
                     int var10003 = this.cursorColor;
                     Objects.requireNonNull(this.font);
                     TextCursorUtils.drawInsertCursor(graphics, textBeforeCursorPosRight, drawTop, var10003, 9 + 1);
                  }

                  hasDrawnCursor = true;
               }
            } else if (lineWithinVisibleBounds) {
               String substring = value.substring(lineView.beginIndex(), lineView.endIndex());
               graphics.drawString(this.font, substring, innerLeft, drawTop, this.textColor, this.textShadow);
               if ((needsValidCursorPos || showCursor) && !insertCursor) {
                  cursorX = innerLeft + this.font.width(substring);
                  cursorY = drawTop;
               }
            }

            Objects.requireNonNull(this.font);
            drawTop += 9;
         }

         if (showCursor && !insertCursor) {
            Objects.requireNonNull(this.font);
            if (this.withinContentAreaTopBottom(cursorY, cursorY + 9)) {
               TextCursorUtils.drawAppendCursor(graphics, this.font, cursorX, cursorY, this.cursorColor, this.textShadow);
            }
         }

         if (this.textField.hasSelection()) {
            MultilineTextField.StringView selection = this.textField.getSelected();
            int drawX = this.getInnerLeft();
            drawTop = this.getInnerTop();

            for(MultilineTextField.StringView lineView : this.textField.iterateLines()) {
               if (selection.beginIndex() > lineView.endIndex()) {
                  Objects.requireNonNull(this.font);
                  drawTop += 9;
               } else {
                  if (lineView.beginIndex() > selection.endIndex()) {
                     break;
                  }

                  Objects.requireNonNull(this.font);
                  if (this.withinContentAreaTopBottom(drawTop, drawTop + 9)) {
                     int drawBegin = this.font.width(value.substring(lineView.beginIndex(), Math.max(selection.beginIndex(), lineView.beginIndex())));
                     int drawEnd;
                     if (selection.endIndex() > lineView.endIndex()) {
                        drawEnd = this.width - this.innerPadding();
                     } else {
                        drawEnd = this.font.width(value.substring(lineView.beginIndex(), selection.endIndex()));
                     }

                     int var10001 = drawX + drawBegin;
                     int var29 = drawX + drawEnd;
                     Objects.requireNonNull(this.font);
                     graphics.textHighlight(var10001, drawTop, var29, drawTop + 9, true);
                  }

                  Objects.requireNonNull(this.font);
                  drawTop += 9;
               }
            }
         }

         if (this.isHovered()) {
            graphics.requestCursor(CursorTypes.IBEAM);
         }

         if (this.preeditOverlay != null) {
            this.preeditOverlay.updateInputPosition(cursorX, cursorY);
            graphics.setPreeditOverlay(this.preeditOverlay);
         }

      }
   }

   protected void renderDecorations(final GuiGraphics graphics) {
      super.renderDecorations(graphics);
      if (this.textField.hasCharacterLimit()) {
         int characterLimit = this.textField.characterLimit();
         Component countText = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.value().length(), characterLimit);
         graphics.drawString(this.font, countText, this.getX() + this.width - this.font.width((FormattedText)countText), this.getY() + this.height + 4, -6250336);
      }

   }

   public int getInnerHeight() {
      Objects.requireNonNull(this.font);
      return 9 * this.textField.getLineCount();
   }

   private void scrollToCursor() {
      double scrollAmount = this.scrollAmount();
      MultilineTextField var10000 = this.textField;
      Objects.requireNonNull(this.font);
      MultilineTextField.StringView firstFullyVisibleLine = var10000.getLineView((int)(scrollAmount / 9.0));
      if (this.textField.cursor() <= firstFullyVisibleLine.beginIndex()) {
         int var5 = this.textField.getLineAtCursor();
         Objects.requireNonNull(this.font);
         scrollAmount = (double)(var5 * 9);
      } else {
         var10000 = this.textField;
         double var10001 = scrollAmount + (double)this.height;
         Objects.requireNonNull(this.font);
         MultilineTextField.StringView lastFullyVisibleLine = var10000.getLineView((int)(var10001 / 9.0) - 1);
         if (this.textField.cursor() > lastFullyVisibleLine.endIndex()) {
            int var7 = this.textField.getLineAtCursor();
            Objects.requireNonNull(this.font);
            var7 = var7 * 9 - this.height;
            Objects.requireNonNull(this.font);
            scrollAmount = (double)(var7 + 9 + this.totalInnerPadding());
         }
      }

      this.setScrollAmount(scrollAmount);
   }

   private void seekCursorScreen(final double x, final double y) {
      double mouseX = x - (double)this.getX() - (double)this.innerPadding();
      double mouseY = y - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
      this.textField.seekCursorToPoint(mouseX, mouseY);
   }

   public void setFocused(final boolean focused) {
      super.setFocused(focused);
      if (focused) {
         this.focusedTime = Util.getMillis();
      }

      Minecraft.getInstance().getWindow().onTextInputFocusChange(focused);
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

      public Builder setX(final int x) {
         this.x = x;
         return this;
      }

      public Builder setY(final int y) {
         this.y = y;
         return this;
      }

      public Builder setPlaceholder(final Component placeholder) {
         this.placeholder = placeholder;
         return this;
      }

      public Builder setTextColor(final int textColor) {
         this.textColor = textColor;
         return this;
      }

      public Builder setTextShadow(final boolean textShadow) {
         this.textShadow = textShadow;
         return this;
      }

      public Builder setCursorColor(final int cursorColor) {
         this.cursorColor = cursorColor;
         return this;
      }

      public Builder setShowBackground(final boolean showBackground) {
         this.showBackground = showBackground;
         return this;
      }

      public Builder setShowDecorations(final boolean showDecorations) {
         this.showDecorations = showDecorations;
         return this;
      }

      public MultiLineEditBox build(final Font font, final int width, final int height, final Component narration) {
         return new MultiLineEditBox(font, this.x, this.y, width, height, this.placeholder, narration, this.textColor, this.textShadow, this.cursorColor, this.showBackground, this.showDecorations);
      }
   }
}
