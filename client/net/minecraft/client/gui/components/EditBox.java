package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Objects;
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
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class EditBox extends AbstractWidget implements Widget, GuiEventListener {
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
   @Nullable
   private String suggestion;
   @Nullable
   private Consumer<String> responder;
   private Predicate<String> filter;
   private BiFunction<String, Integer, FormattedCharSequence> formatter;

   public EditBox(Font var1, int var2, int var3, int var4, int var5, Component var6) {
      this(var1, var2, var3, var4, var5, (EditBox)null, var6);
   }

   public EditBox(Font var1, int var2, int var3, int var4, int var5, @Nullable EditBox var6, Component var7) {
      super(var2, var3, var4, var5, var7);
      this.value = "";
      this.maxLength = 32;
      this.bordered = true;
      this.canLoseFocus = true;
      this.isEditable = true;
      this.textColor = 14737632;
      this.textColorUneditable = 7368816;
      this.filter = Objects::nonNull;
      this.formatter = (var0, var1x) -> {
         return FormattedCharSequence.forward(var0, Style.EMPTY);
      };
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

   protected MutableComponent createNarrationMessage() {
      Component var1 = this.getMessage();
      return new TranslatableComponent("gui.narrate.editBox", new Object[]{var1, this.value});
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

      String var7 = (new StringBuilder(this.value)).replace(var2, var3, var5).toString();
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
               String var5 = (new StringBuilder(this.value)).delete(var3, var4).toString();
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
      this.cursorPos = Mth.clamp((int)var1, (int)0, (int)this.value.length());
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

            String var8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth(var8, var7).length() + this.displayPos);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setFocus(boolean var1) {
      this.setFocused(var1);
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         int var5;
         if (this.isBordered()) {
            var5 = this.isFocused() ? -1 : -6250336;
            fill(var1, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, var5);
            fill(var1, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         var5 = this.isEditable ? this.textColor : this.textColorUneditable;
         int var6 = this.cursorPos - this.displayPos;
         int var7 = this.highlightPos - this.displayPos;
         String var8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var9 = var6 >= 0 && var6 <= var8.length();
         boolean var10 = this.isFocused() && this.frame / 6 % 2 == 0 && var9;
         int var11 = this.bordered ? this.x + 4 : this.x;
         int var12 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
         int var13 = var11;
         if (var7 > var8.length()) {
            var7 = var8.length();
         }

         if (!var8.isEmpty()) {
            String var14 = var9 ? var8.substring(0, var6) : var8;
            var13 = this.font.drawShadow(var1, (FormattedCharSequence)this.formatter.apply(var14, this.displayPos), (float)var11, (float)var12, var5);
         }

         boolean var17 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var15 = var13;
         if (!var9) {
            var15 = var6 > 0 ? var11 + this.width : var11;
         } else if (var17) {
            var15 = var13 - 1;
            --var13;
         }

         if (!var8.isEmpty() && var9 && var6 < var8.length()) {
            this.font.drawShadow(var1, (FormattedCharSequence)this.formatter.apply(var8.substring(var6), this.cursorPos), (float)var13, (float)var12, var5);
         }

         if (!var17 && this.suggestion != null) {
            this.font.drawShadow(var1, this.suggestion, (float)(var15 - 1), (float)var12, -8355712);
         }

         int var10002;
         int var10003;
         int var10004;
         if (var10) {
            if (var17) {
               var10002 = var12 - 1;
               var10003 = var15 + 1;
               var10004 = var12 + 1;
               Objects.requireNonNull(this.font);
               GuiComponent.fill(var1, var15, var10002, var10003, var10004 + 9, -3092272);
            } else {
               this.font.drawShadow(var1, "_", (float)var15, (float)var12, var5);
            }
         }

         if (var7 != var6) {
            int var16 = var11 + this.font.width(var8.substring(0, var7));
            var10002 = var12 - 1;
            var10003 = var16 - 1;
            var10004 = var12 + 1;
            Objects.requireNonNull(this.font);
            this.renderHighlight(var15, var10002, var10003, var10004 + 9);
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
      RenderSystem.setShader(GameRenderer::getPositionShader);
      RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      var6.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
      var6.vertex((double)var1, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var2, 0.0D).endVertex();
      var6.vertex((double)var1, (double)var2, 0.0D).endVertex();
      var7.end();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
      this.highlightPos = Mth.clamp((int)var1, (int)0, (int)var2);
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

         this.displayPos = Mth.clamp((int)this.displayPos, (int)0, (int)var2);
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

   public void updateNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)(new TranslatableComponent("narration.edit_box", new Object[]{this.getValue()})));
   }
}
