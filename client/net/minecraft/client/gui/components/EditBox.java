package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class EditBox extends AbstractWidget implements Renderable {
   public static final int BACKWARDS = -1;
   public static final int FORWARDS = 1;
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   public static final int DEFAULT_TEXT_COLOR = 14737632;
   private static final int BORDER_COLOR_FOCUSED = -1;
   private static final int BORDER_COLOR = -6250336;
   private static final int BACKGROUND_COLOR = -16777216;
   private final Font font;
   private String value = "";
   private int maxLength = 32;
   private int frame;
   private boolean bordered = true;
   private boolean canLoseFocus = true;
   private boolean isEditable = true;
   private boolean shiftPressed;
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

   public void tick() {
      ++this.frame;
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

         this.moveCursorToEnd();
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
      String var5 = SharedConstants.filterText(var1);
      int var6 = var5.length();
      if (var4 < var6) {
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
            this.deleteChars(this.getWordPosition(var1) - this.cursorPos);
         }
      }
   }

   public void deleteChars(int var1) {
      if (!this.value.isEmpty()) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            int var2 = this.getCursorPos(var1);
            int var3 = Math.min(var2, this.cursorPos);
            int var4 = Math.max(var2, this.cursorPos);
            if (var3 != var4) {
               String var5 = new StringBuilder(this.value).delete(var3, var4).toString();
               if (this.filter.test(var5)) {
                  this.value = var5;
                  this.moveCursorTo(var3);
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

   public void moveCursor(int var1) {
      this.moveCursorTo(this.getCursorPos(var1));
   }

   private int getCursorPos(int var1) {
      return Util.offsetByCodepoints(this.value, this.cursorPos, var1);
   }

   public void moveCursorTo(int var1) {
      this.setCursorPosition(var1);
      if (!this.shiftPressed) {
         this.setHighlightPos(this.cursorPos);
      }

      this.onValueChange(this.value);
   }

   public void setCursorPosition(int var1) {
      this.cursorPos = Mth.clamp(var1, 0, this.value.length());
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.value.length());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.canConsumeInput()) {
         return false;
      } else {
         this.shiftPressed = Screen.hasShiftDown();
         if (Screen.isSelectAll(var1)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
         } else if (Screen.isCopy(var1)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
         } else if (Screen.isPaste(var1)) {
            if (this.isEditable) {
               this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }

            return true;
         } else if (Screen.isCut(var1)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
               this.insertText("");
            }

            return true;
         } else {
            switch(var1) {
               case 259:
                  if (this.isEditable) {
                     this.shiftPressed = false;
                     this.deleteText(-1);
                     this.shiftPressed = Screen.hasShiftDown();
                  }

                  return true;
               case 260:
               case 264:
               case 265:
               case 266:
               case 267:
               default:
                  return false;
               case 261:
                  if (this.isEditable) {
                     this.shiftPressed = false;
                     this.deleteText(1);
                     this.shiftPressed = Screen.hasShiftDown();
                  }

                  return true;
               case 262:
                  if (Screen.hasControlDown()) {
                     this.moveCursorTo(this.getWordPosition(1));
                  } else {
                     this.moveCursor(1);
                  }

                  return true;
               case 263:
                  if (Screen.hasControlDown()) {
                     this.moveCursorTo(this.getWordPosition(-1));
                  } else {
                     this.moveCursor(-1);
                  }

                  return true;
               case 268:
                  this.moveCursorToStart();
                  return true;
               case 269:
                  this.moveCursorToEnd();
                  return true;
            }
         }
      }
   }

   public boolean canConsumeInput() {
      return this.isVisible() && this.isFocused() && this.isEditable();
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
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.isVisible() && var5 == 0) {
         boolean var6 = var1 >= (double)this.getX()
            && var1 < (double)(this.getX() + this.width)
            && var3 >= (double)this.getY()
            && var3 < (double)(this.getY() + this.height);
         if (this.canLoseFocus) {
            this.setFocused(var6);
         }

         if (this.isFocused() && var6 && var5 == 0) {
            int var7 = Mth.floor(var1) - this.getX();
            if (this.bordered) {
               var7 -= 4;
            }

            String var8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth(var8, var7).length() + this.displayPos);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void renderWidget(PoseStack var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         if (this.isBordered()) {
            int var5 = this.isFocused() ? -1 : -6250336;
            fill(var1, this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, var5);
            fill(var1, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
         }

         int var17 = this.isEditable ? this.textColor : this.textColorUneditable;
         int var6 = this.cursorPos - this.displayPos;
         int var7 = this.highlightPos - this.displayPos;
         String var8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var9 = var6 >= 0 && var6 <= var8.length();
         boolean var10 = this.isFocused() && this.frame / 6 % 2 == 0 && var9;
         int var11 = this.bordered ? this.getX() + 4 : this.getX();
         int var12 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
         int var13 = var11;
         if (var7 > var8.length()) {
            var7 = var8.length();
         }

         if (!var8.isEmpty()) {
            String var14 = var9 ? var8.substring(0, var6) : var8;
            var13 = this.font.drawShadow(var1, this.formatter.apply(var14, this.displayPos), (float)var11, (float)var12, var17);
         }

         boolean var18 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var15 = var13;
         if (!var9) {
            var15 = var6 > 0 ? var11 + this.width : var11;
         } else if (var18) {
            var15 = var13 - 1;
            --var13;
         }

         if (!var8.isEmpty() && var9 && var6 < var8.length()) {
            this.font.drawShadow(var1, this.formatter.apply(var8.substring(var6), this.cursorPos), (float)var13, (float)var12, var17);
         }

         if (this.hint != null && var8.isEmpty() && !this.isFocused()) {
            this.font.drawShadow(var1, this.hint, (float)var13, (float)var12, var17);
         }

         if (!var18 && this.suggestion != null) {
            this.font.drawShadow(var1, this.suggestion, (float)(var15 - 1), (float)var12, -8355712);
         }

         if (var10) {
            if (var18) {
               GuiComponent.fill(var1, var15, var12 - 1, var15 + 1, var12 + 1 + 9, -3092272);
            } else {
               this.font.drawShadow(var1, "_", (float)var15, (float)var12, var17);
            }
         }

         if (var7 != var6) {
            int var16 = var11 + this.font.width(var8.substring(0, var7));
            this.renderHighlight(var1, var15, var12 - 1, var16 - 1, var12 + 1 + 9);
         }
      }
   }

   private void renderHighlight(PoseStack var1, int var2, int var3, int var4, int var5) {
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

      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      fill(var1, var2, var3, var4, var5, -16776961);
      RenderSystem.disableColorLogicOp();
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

   private boolean isBordered() {
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

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return this.visible && this.isEditable ? super.nextFocusPath(var1) : null;
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return this.visible
         && var1 >= (double)this.getX()
         && var1 < (double)(this.getX() + this.width)
         && var3 >= (double)this.getY()
         && var3 < (double)(this.getY() + this.height);
   }

   @Override
   public void setFocused(boolean var1) {
      if (this.canLoseFocus || var1) {
         super.setFocused(var1);
         if (var1) {
            this.frame = 0;
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
      int var2 = this.value.length();
      this.highlightPos = Mth.clamp(var1, 0, var2);
      if (this.font != null) {
         if (this.displayPos > var2) {
            this.displayPos = var2;
         }

         int var3 = this.getInnerWidth();
         String var4 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), var3);
         int var5 = var4.length() + this.displayPos;
         if (this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.plainSubstrByWidth(this.value, var3, true).length();
         }

         if (this.highlightPos > var5) {
            this.displayPos += this.highlightPos - var5;
         } else if (this.highlightPos <= this.displayPos) {
            this.displayPos -= this.displayPos - this.highlightPos;
         }

         this.displayPos = Mth.clamp(this.displayPos, 0, var2);
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
