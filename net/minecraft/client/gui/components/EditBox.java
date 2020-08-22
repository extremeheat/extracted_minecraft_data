package net.minecraft.client.gui.components;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;

public class EditBox extends AbstractWidget implements Widget, GuiEventListener {
   private final Font font;
   private String value;
   private int maxLength;
   private int frame;
   private boolean bordered;
   private boolean canLoseFocus;
   private boolean isEditable;
   private boolean shiftPressed;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor;
   private int textColorUneditable;
   private String suggestion;
   private Consumer responder;
   private Predicate filter;
   private BiFunction formatter;

   public EditBox(Font var1, int var2, int var3, int var4, int var5, String var6) {
      this(var1, var2, var3, var4, var5, (EditBox)null, var6);
   }

   public EditBox(Font var1, int var2, int var3, int var4, int var5, @Nullable EditBox var6, String var7) {
      super(var2, var3, var4, var5, var7);
      this.value = "";
      this.maxLength = 32;
      this.bordered = true;
      this.canLoseFocus = true;
      this.isEditable = true;
      this.textColor = 14737632;
      this.textColorUneditable = 7368816;
      this.filter = Predicates.alwaysTrue();
      this.formatter = (var0, var1x) -> {
         return var0;
      };
      this.font = var1;
      if (var6 != null) {
         this.setValue(var6.getValue());
      }

   }

   public void setResponder(Consumer var1) {
      this.responder = var1;
   }

   public void setFormatter(BiFunction var1) {
      this.formatter = var1;
   }

   public void tick() {
      ++this.frame;
   }

   protected String getNarrationMessage() {
      String var1 = this.getMessage();
      return var1.isEmpty() ? "" : I18n.get("gui.narrate.editBox", var1, this.value);
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
      int var1 = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int var2 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      return this.value.substring(var1, var2);
   }

   public void setFilter(Predicate var1) {
      this.filter = var1;
   }

   public void insertText(String var1) {
      String var2 = "";
      String var3 = SharedConstants.filterText(var1);
      int var4 = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int var5 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      int var6 = this.maxLength - this.value.length() - (var4 - var5);
      if (!this.value.isEmpty()) {
         var2 = var2 + this.value.substring(0, var4);
      }

      int var7;
      if (var6 < var3.length()) {
         var2 = var2 + var3.substring(0, var6);
         var7 = var6;
      } else {
         var2 = var2 + var3;
         var7 = var3.length();
      }

      if (!this.value.isEmpty() && var5 < this.value.length()) {
         var2 = var2 + this.value.substring(var5);
      }

      if (this.filter.test(var2)) {
         this.value = var2;
         this.setCursorPosition(var4 + var7);
         this.setHighlightPos(this.cursorPos);
         this.onValueChange(this.value);
      }
   }

   private void onValueChange(String var1) {
      if (this.responder != null) {
         this.responder.accept(var1);
      }

      this.nextNarration = Util.getMillis() + 500L;
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
            boolean var2 = var1 < 0;
            int var3 = var2 ? this.cursorPos + var1 : this.cursorPos;
            int var4 = var2 ? this.cursorPos : this.cursorPos + var1;
            String var5 = "";
            if (var3 >= 0) {
               var5 = this.value.substring(0, var3);
            }

            if (var4 < this.value.length()) {
               var5 = var5 + this.value.substring(var4);
            }

            if (this.filter.test(var5)) {
               this.value = var5;
               if (var2) {
                  this.moveCursor(var1);
               }

               this.onValueChange(this.value);
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
      this.moveCursorTo(this.cursorPos + var1);
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

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.isVisible()) {
         return false;
      } else {
         boolean var6 = var1 >= (double)this.x && var1 < (double)(this.x + this.width) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
         if (this.canLoseFocus) {
            this.setFocus(var6);
         }

         if (this.isFocused() && var6 && var5 == 0) {
            int var7 = Mth.floor(var1) - this.x;
            if (this.bordered) {
               var7 -= 4;
            }

            String var8 = this.font.substrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.substrByWidth(var8, var7).length() + this.displayPos);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setFocus(boolean var1) {
      super.setFocused(var1);
   }

   public void renderButton(int var1, int var2, float var3) {
      if (this.isVisible()) {
         if (this.isBordered()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int var4 = this.isEditable ? this.textColor : this.textColorUneditable;
         int var5 = this.cursorPos - this.displayPos;
         int var6 = this.highlightPos - this.displayPos;
         String var7 = this.font.substrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var8 = var5 >= 0 && var5 <= var7.length();
         boolean var9 = this.isFocused() && this.frame / 6 % 2 == 0 && var8;
         int var10 = this.bordered ? this.x + 4 : this.x;
         int var11 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
         int var12 = var10;
         if (var6 > var7.length()) {
            var6 = var7.length();
         }

         if (!var7.isEmpty()) {
            String var13 = var8 ? var7.substring(0, var5) : var7;
            var12 = this.font.drawShadow((String)this.formatter.apply(var13, this.displayPos), (float)var10, (float)var11, var4);
         }

         boolean var16 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var14 = var12;
         if (!var8) {
            var14 = var5 > 0 ? var10 + this.width : var10;
         } else if (var16) {
            var14 = var12 - 1;
            --var12;
         }

         if (!var7.isEmpty() && var8 && var5 < var7.length()) {
            this.font.drawShadow((String)this.formatter.apply(var7.substring(var5), this.cursorPos), (float)var12, (float)var11, var4);
         }

         if (!var16 && this.suggestion != null) {
            this.font.drawShadow(this.suggestion, (float)(var14 - 1), (float)var11, -8355712);
         }

         int var10002;
         int var10003;
         if (var9) {
            if (var16) {
               int var10001 = var11 - 1;
               var10002 = var14 + 1;
               var10003 = var11 + 1;
               this.font.getClass();
               GuiComponent.fill(var14, var10001, var10002, var10003 + 9, -3092272);
            } else {
               this.font.drawShadow("_", (float)var14, (float)var11, var4);
            }
         }

         if (var6 != var5) {
            int var15 = var10 + this.font.width(var7.substring(0, var6));
            var10002 = var11 - 1;
            var10003 = var15 - 1;
            int var10004 = var11 + 1;
            this.font.getClass();
            this.renderHighlight(var14, var10002, var10003, var10004 + 9);
         }

      }
   }

   private void renderHighlight(int var1, int var2, int var3, int var4) {
      int var5;
      if (var1 < var3) {
         var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      if (var2 < var4) {
         var5 = var2;
         var2 = var4;
         var4 = var5;
      }

      if (var3 > this.x + this.width) {
         var3 = this.x + this.width;
      }

      if (var1 > this.x + this.width) {
         var1 = this.x + this.width;
      }

      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var6 = var7.getBuilder();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      var6.begin(7, DefaultVertexFormat.POSITION);
      var6.vertex((double)var1, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var2, 0.0D).endVertex();
      var6.vertex((double)var1, (double)var2, 0.0D).endVertex();
      var7.end();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
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

   public boolean changeFocus(boolean var1) {
      return this.visible && this.isEditable ? super.changeFocus(var1) : false;
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.visible && var1 >= (double)this.x && var1 < (double)(this.x + this.width) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
   }

   protected void onFocusedChanged(boolean var1) {
      if (var1) {
         this.frame = 0;
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
         String var4 = this.font.substrByWidth(this.value.substring(this.displayPos), var3);
         int var5 = var4.length() + this.displayPos;
         if (this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.substrByWidth(this.value, var3, true).length();
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
      return var1 > this.value.length() ? this.x : this.x + this.font.width(this.value.substring(0, var1));
   }

   public void setX(int var1) {
      this.x = var1;
   }
}
