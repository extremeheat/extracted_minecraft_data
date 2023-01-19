package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MultiLineEditBox extends AbstractScrollWidget {
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   private static final int TEXT_COLOR = -2039584;
   private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
   private final Font font;
   private final Component placeholder;
   private final MultilineTextField textField;
   private int frame;

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

   public void tick() {
      ++this.frame;
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.withinContentAreaPoint(var1, var3) && var5 == 0) {
         this.textField.setSelecting(Screen.hasShiftDown());
         this.seekCursorScreen(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (this.withinContentAreaPoint(var1, var3) && var5 == 0) {
         this.textField.setSelecting(true);
         this.seekCursorScreen(var1, var3);
         this.textField.setSelecting(Screen.hasShiftDown());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.textField.keyPressed(var1);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.visible && this.isFocused() && SharedConstants.isAllowedChatCharacter(var1)) {
         this.textField.insertText(Character.toString(var1));
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void renderContents(PoseStack var1, int var2, int var3, float var4) {
      String var5 = this.textField.value();
      if (var5.isEmpty() && !this.isFocused()) {
         this.font
            .drawWordWrap(
               this.placeholder, this.getX() + this.innerPadding(), this.getY() + this.innerPadding(), this.width - this.totalInnerPadding(), -857677600
            );
      } else {
         int var6 = this.textField.cursor();
         boolean var7 = this.isFocused() && this.frame / 6 % 2 == 0;
         boolean var8 = var6 < var5.length();
         int var9 = 0;
         int var10 = 0;
         int var11 = this.getY() + this.innerPadding();

         for(MultilineTextField.StringView var13 : this.textField.iterateLines()) {
            boolean var14 = this.withinContentAreaTopBottom(var11, var11 + 9);
            if (var7 && var8 && var6 >= var13.beginIndex() && var6 <= var13.endIndex()) {
               if (var14) {
                  var9 = this.font
                        .drawShadow(var1, var5.substring(var13.beginIndex(), var6), (float)(this.getX() + this.innerPadding()), (float)var11, -2039584)
                     - 1;
                  GuiComponent.fill(var1, var9, var11 - 1, var9 + 1, var11 + 1 + 9, -3092272);
                  this.font.drawShadow(var1, var5.substring(var6, var13.endIndex()), (float)var9, (float)var11, -2039584);
               }
            } else {
               if (var14) {
                  var9 = this.font
                        .drawShadow(
                           var1, var5.substring(var13.beginIndex(), var13.endIndex()), (float)(this.getX() + this.innerPadding()), (float)var11, -2039584
                        )
                     - 1;
               }

               var10 = var11;
            }

            var11 += 9;
         }

         if (var7 && !var8 && this.withinContentAreaTopBottom(var10, var10 + 9)) {
            this.font.drawShadow(var1, "_", (float)var9, (float)var10, -3092272);
         }

         if (this.textField.hasSelection()) {
            MultilineTextField.StringView var19 = this.textField.getSelected();
            int var20 = this.getX() + this.innerPadding();
            var11 = this.getY() + this.innerPadding();

            for(MultilineTextField.StringView var15 : this.textField.iterateLines()) {
               if (var19.beginIndex() > var15.endIndex()) {
                  var11 += 9;
               } else {
                  if (var15.beginIndex() > var19.endIndex()) {
                     break;
                  }

                  if (this.withinContentAreaTopBottom(var11, var11 + 9)) {
                     int var16 = this.font.width(var5.substring(var15.beginIndex(), Math.max(var19.beginIndex(), var15.beginIndex())));
                     int var17;
                     if (var19.endIndex() > var15.endIndex()) {
                        var17 = this.width - this.innerPadding();
                     } else {
                        var17 = this.font.width(var5.substring(var15.beginIndex(), var19.endIndex()));
                     }

                     this.renderHighlight(var1, var20 + var16, var11, var20 + var17, var11 + 9);
                  }

                  var11 += 9;
               }
            }
         }
      }
   }

   @Override
   protected void renderDecorations(PoseStack var1) {
      super.renderDecorations(var1);
      if (this.textField.hasCharacterLimit()) {
         int var2 = this.textField.characterLimit();
         MutableComponent var3 = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.value().length(), var2);
         drawString(var1, this.font, var3, this.getX() + this.width - this.font.width(var3), this.getY() + this.height + 4, 10526880);
      }
   }

   @Override
   public int getInnerHeight() {
      return 9 * this.textField.getLineCount();
   }

   @Override
   protected boolean scrollbarVisible() {
      return (double)this.textField.getLineCount() > this.getDisplayableLineCount();
   }

   @Override
   protected double scrollRate() {
      return 9.0 / 2.0;
   }

   private void renderHighlight(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      fill(var1, var2, var3, var4, var5, -16776961);
      RenderSystem.disableColorLogicOp();
   }

   private void scrollToCursor() {
      double var1 = this.scrollAmount();
      MultilineTextField.StringView var3 = this.textField.getLineView((int)(var1 / 9.0));
      if (this.textField.cursor() <= var3.beginIndex()) {
         var1 = (double)(this.textField.getLineAtCursor() * 9);
      } else {
         MultilineTextField.StringView var4 = this.textField.getLineView((int)((var1 + (double)this.height) / 9.0) - 1);
         if (this.textField.cursor() > var4.endIndex()) {
            var1 = (double)(this.textField.getLineAtCursor() * 9 - this.height + 9 + this.totalInnerPadding());
         }
      }

      this.setScrollAmount(var1);
   }

   private double getDisplayableLineCount() {
      return (double)(this.height - this.totalInnerPadding()) / 9.0;
   }

   private void seekCursorScreen(double var1, double var3) {
      double var5 = var1 - (double)this.getX() - (double)this.innerPadding();
      double var7 = var3 - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
      this.textField.seekCursorToPoint(var5, var7);
   }
}
