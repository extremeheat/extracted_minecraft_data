package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class EditBox extends AbstractWidget implements Renderable {
   private static final WidgetSprites SPRITES = new WidgetSprites(
      new ResourceLocation("widget/text_field"), new ResourceLocation("widget/text_field_highlighted")
   );
   public static final int BACKWARDS = -1;
   public static final int FORWARDS = 1;
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   public static final int DEFAULT_TEXT_COLOR = 14737632;
   private static final int CURSOR_BLINK_INTERVAL_MS = 300;
   private final Font font;
   private String value = "";
   private int maxLength = 32;
   private boolean bordered = true;
   private boolean canLoseFocus = true;
   private boolean isEditable = true;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor = 14737632;
   private int textColorUneditable = 7368816;
   @Nullable
   private String suggestion;
   @Nullable
   private Consumer<String> responder;
   private Predicate<String> filter = Objects::nonNull;
   private BiFunction<String, Integer, FormattedCharSequence> formatter = (var0, var1x) -> FormattedCharSequence.forward(var0, Style.EMPTY);
   @Nullable
   private Component hint;
   private long focusedTime = Util.getMillis();

   public EditBox(Font var1, int var2, int var3, Component var4) {
      this(var1, 0, 0, var2, var3, var4);
   }

   public EditBox(Font var1, int var2, int var3, int var4, int var5, Component var6) {
      this(var1, var2, var3, var4, var5, null, var6);
   }

   public EditBox(Font var1, int var2, int var3, int var4, int var5, @Nullable EditBox var6, Component var7) {
      super(var2, var3, var4, var5, var7);
      this.font = var1;
      if (var6 != null) {
         this.setValue(var6.getValue());
      }
   }

   public void setResponder(Consumer<String> var1) {
      this.responder = var1;
   }

   public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> var1) {
      this.formatter = var1;
   }

   @Override
   protected MutableComponent createNarrationMessage() {
      Component var1 = this.getMessage();
      return Component.translatable("gui.narrate.editBox", var1, this.value);
   }

   public void setValue(String var1) {
      if (this.filter.test(var1)) {
         if (var1.length() > this.maxLength) {
            this.value = var1.substring(0, this.maxLength);
         } else {
            this.value = var1;
         }

         this.moveCursorToEnd(false);
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(var1);
      }
   }

   public String getValue() {
      return this.value;
   }

   public String getHighlighted() {
      int var1 = Math.min(this.cursorPos, this.highlightPos);
      int var2 = Math.max(this.cursorPos, this.highlightPos);
      return this.value.substring(var1, var2);
   }

   public void setFilter(Predicate<String> var1) {
      this.filter = var1;
   }

   public void insertText(String var1) {
      int var2 = Math.min(this.cursorPos, this.highlightPos);
      int var3 = Math.max(this.cursorPos, this.highlightPos);
      int var4 = this.maxLength - this.value.length() - (var2 - var3);
      if (var4 > 0) {
         String var5 = SharedConstants.filterText(var1);
         int var6 = var5.length();
         if (var4 < var6) {
            if (Character.isHighSurrogate(var5.charAt(var4 - 1))) {
               --var4;
            }

            var5 = var5.substring(0, var4);
            var6 = var4;
         }

         String var7 = new StringBuilder(this.value).replace(var2, var3, var5).toString();
         if (this.filter.test(var7)) {
            this.value = var7;
            this.setCursorPosition(var2 + var6);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
         }
      }
   }

   private void onValueChange(String var1) {
      if (this.responder != null) {
         this.responder.accept(var1);
      }
   }

   private void deleteText(int var1) {
      if (Screen.hasControlDown()) {
         this.deleteWords(var1);
      } else {
         this.deleteChars(var1);
      }
   }

   public void deleteWords(int var1) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            this.deleteCharsToPos(this.getWordPosition(var1));
         }
      }
   }

   public void deleteChars(int var1) {
      this.deleteCharsToPos(this.getCursorPos(var1));
   }

   public void deleteCharsToPos(int var1) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            int var2 = Math.min(var1, this.cursorPos);
            int var3 = Math.max(var1, this.cursorPos);
            if (var2 != var3) {
               String var4 = new StringBuilder(this.value).delete(var2, var3).toString();
               if (this.filter.test(var4)) {
                  this.value = var4;
                  this.moveCursorTo(var2, false);
               }
            }
         }
      }
   }

   public int getWordPosition(int var1) {
      return this.getWordPosition(var1, this.getCursorPosition());
   }

   private int getWordPosition(int var1, int var2) {
      return this.getWordPosition(var1, var2, true);
   }

   private int getWordPosition(int var1, int var2, boolean var3) {
      int var4 = var2;
      boolean var5 = var1 < 0;
      int var6 = Math.abs(var1);

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!var5) {
            int var8 = this.value.length();
            var4 = this.value.indexOf(32, var4);
            if (var4 == -1) {
               var4 = var8;
            } else {
               while(var3 && var4 < var8 && this.value.charAt(var4) == ' ') {
                  ++var4;
               }
            }
         } else {
            while(var3 && var4 > 0 && this.value.charAt(var4 - 1) == ' ') {
               --var4;
            }

            while(var4 > 0 && this.value.charAt(var4 - 1) != ' ') {
               --var4;
            }
         }
      }

      return var4;
   }

   public void moveCursor(int var1, boolean var2) {
      this.moveCursorTo(this.getCursorPos(var1), var2);
   }

   private int getCursorPos(int var1) {
      return Util.offsetByCodepoints(this.value, this.cursorPos, var1);
   }

   public void moveCursorTo(int var1, boolean var2) {
      this.setCursorPosition(var1);
      if (!var2) {
         this.setHighlightPos(this.cursorPos);
      }

      this.onValueChange(this.value);
   }

   public void setCursorPosition(int var1) {
      this.cursorPos = Mth.clamp(var1, 0, this.value.length());
      this.scrollTo(this.cursorPos);
   }

   public void moveCursorToStart(boolean var1) {
      this.moveCursorTo(0, var1);
   }

   public void moveCursorToEnd(boolean var1) {
      this.moveCursorTo(this.value.length(), var1);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.isActive() && this.isFocused()) {
         switch(var1) {
            case 259:
               if (this.isEditable) {
                  this.deleteText(-1);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (Screen.isSelectAll(var1)) {
                  this.moveCursorToEnd(false);
                  this.setHighlightPos(0);
                  return true;
               } else if (Screen.isCopy(var1)) {
                  Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                  return true;
               } else if (Screen.isPaste(var1)) {
                  if (this.isEditable()) {
                     this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                  }

                  return true;
               } else {
                  if (Screen.isCut(var1)) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                     if (this.isEditable()) {
                        this.insertText("");
                     }

                     return true;
                  }

                  return false;
               }
            case 261:
               if (this.isEditable) {
                  this.deleteText(1);
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
               } else {
                  this.moveCursor(1, Screen.hasShiftDown());
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
               } else {
                  this.moveCursor(-1, Screen.hasShiftDown());
               }

               return true;
            case 268:
               this.moveCursorToStart(Screen.hasShiftDown());
               return true;
            case 269:
               this.moveCursorToEnd(Screen.hasShiftDown());
               return true;
         }
      } else {
         return false;
      }
   }

   public boolean canConsumeInput() {
      return this.isActive() && this.isFocused() && this.isEditable();
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (!this.canConsumeInput()) {
         return false;
      } else if (SharedConstants.isAllowedChatCharacter(var1)) {
         if (this.isEditable) {
            this.insertText(Character.toString(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onClick(double var1, double var3) {
      int var5 = Mth.floor(var1) - this.getX();
      if (this.bordered) {
         var5 -= 4;
      }

      String var6 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
      this.moveCursorTo(this.font.plainSubstrByWidth(var6, var5).length() + this.displayPos, Screen.hasShiftDown());
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         if (this.isBordered()) {
            ResourceLocation var5 = SPRITES.get(this.isActive(), this.isFocused());
            var1.blitSprite(var5, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         }

         int var17 = this.isEditable ? this.textColor : this.textColorUneditable;
         int var6 = this.cursorPos - this.displayPos;
         String var7 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var8 = var6 >= 0 && var6 <= var7.length();
         boolean var9 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && var8;
         int var10 = this.bordered ? this.getX() + 4 : this.getX();
         int var11 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
         int var12 = var10;
         int var13 = Mth.clamp(this.highlightPos - this.displayPos, 0, var7.length());
         if (!var7.isEmpty()) {
            String var14 = var8 ? var7.substring(0, var6) : var7;
            var12 = var1.drawString(this.font, this.formatter.apply(var14, this.displayPos), var10, var11, var17);
         }

         boolean var18 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var15 = var12;
         if (!var8) {
            var15 = var6 > 0 ? var10 + this.width : var10;
         } else if (var18) {
            var15 = var12 - 1;
            --var12;
         }

         if (!var7.isEmpty() && var8 && var6 < var7.length()) {
            var1.drawString(this.font, this.formatter.apply(var7.substring(var6), this.cursorPos), var12, var11, var17);
         }

         if (this.hint != null && var7.isEmpty() && !this.isFocused()) {
            var1.drawString(this.font, this.hint, var12, var11, var17);
         }

         if (!var18 && this.suggestion != null) {
            var1.drawString(this.font, this.suggestion, var15 - 1, var11, -8355712);
         }

         if (var9) {
            if (var18) {
               var1.fill(RenderType.guiOverlay(), var15, var11 - 1, var15 + 1, var11 + 1 + 9, -3092272);
            } else {
               var1.drawString(this.font, "_", var15, var11, var17);
            }
         }

         if (var13 != var6) {
            int var16 = var10 + this.font.width(var7.substring(0, var13));
            this.renderHighlight(var1, var15, var11 - 1, var16 - 1, var11 + 1 + 9);
         }
      }
   }

   private void renderHighlight(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (var2 < var4) {
         int var6 = var2;
         var2 = var4;
         var4 = var6;
      }

      if (var3 < var5) {
         int var7 = var3;
         var3 = var5;
         var5 = var7;
      }

      if (var4 > this.getX() + this.width) {
         var4 = this.getX() + this.width;
      }

      if (var2 > this.getX() + this.width) {
         var2 = this.getX() + this.width;
      }

      var1.fill(RenderType.guiTextHighlight(), var2, var3, var4, var5, -16776961);
   }

   public void setMaxLength(int var1) {
      this.maxLength = var1;
      if (this.value.length() > var1) {
         this.value = this.value.substring(0, var1);
         this.onValueChange(this.value);
      }
   }

   private int getMaxLength() {
      return this.maxLength;
   }

   public int getCursorPosition() {
      return this.cursorPos;
   }

   public boolean isBordered() {
      return this.bordered;
   }

   public void setBordered(boolean var1) {
      this.bordered = var1;
   }

   public void setTextColor(int var1) {
      this.textColor = var1;
   }

   public void setTextColorUneditable(int var1) {
      this.textColorUneditable = var1;
   }

   @Override
   public void setFocused(boolean var1) {
      if (this.canLoseFocus || var1) {
         super.setFocused(var1);
         if (var1) {
            this.focusedTime = Util.getMillis();
         }
      }
   }

   private boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean var1) {
      this.isEditable = var1;
   }

   public int getInnerWidth() {
      return this.isBordered() ? this.width - 8 : this.width;
   }

   public void setHighlightPos(int var1) {
      this.highlightPos = Mth.clamp(var1, 0, this.value.length());
      this.scrollTo(this.highlightPos);
   }

   private void scrollTo(int var1) {
      if (this.font != null) {
         this.displayPos = Math.min(this.displayPos, this.value.length());
         int var2 = this.getInnerWidth();
         String var3 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), var2);
         int var4 = var3.length() + this.displayPos;
         if (var1 == this.displayPos) {
            this.displayPos -= this.font.plainSubstrByWidth(this.value, var2, true).length();
         }

         if (var1 > var4) {
            this.displayPos += var1 - var4;
         } else if (var1 <= this.displayPos) {
            this.displayPos -= this.displayPos - var1;
         }

         this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
      }
   }

   public void setCanLoseFocus(boolean var1) {
      this.canLoseFocus = var1;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public void setSuggestion(@Nullable String var1) {
      this.suggestion = var1;
   }

   public int getScreenX(int var1) {
      return var1 > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, var1));
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.createNarrationMessage());
   }

   public void setHint(Component var1) {
      this.hint = var1;
   }
}
