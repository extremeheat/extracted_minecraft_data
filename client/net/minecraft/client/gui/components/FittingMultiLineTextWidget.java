package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class FittingMultiLineTextWidget extends AbstractScrollWidget {
   private final Font font;
   private final MultiLineTextWidget multilineWidget;

   public FittingMultiLineTextWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5);
      this.font = var6;
      this.multilineWidget = (new MultiLineTextWidget(var5, var6)).setMaxWidth(this.getWidth() - this.totalInnerPadding());
   }

   public FittingMultiLineTextWidget setColor(int var1) {
      this.multilineWidget.setColor(var1);
      return this;
   }

   public void setWidth(int var1) {
      super.setWidth(var1);
      this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
   }

   protected int getInnerHeight() {
      return this.multilineWidget.getHeight();
   }

   protected double scrollRate() {
      Objects.requireNonNull(this.font);
      return 9.0;
   }

   protected void renderBackground(GuiGraphics var1) {
      if (this.scrollbarVisible()) {
         super.renderBackground(var1);
      } else if (this.isFocused()) {
         this.renderBorder(var1, this.getX() - this.innerPadding(), this.getY() - this.innerPadding(), this.getWidth() + this.totalInnerPadding(), this.getHeight() + this.totalInnerPadding());
      }

   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.visible) {
         if (!this.scrollbarVisible()) {
            this.renderBackground(var1);
            var1.pose().pushPose();
            var1.pose().translate((float)this.getX(), (float)this.getY(), 0.0F);
            this.multilineWidget.render(var1, var2, var3, var4);
            var1.pose().popPose();
         } else {
            super.renderWidget(var1, var2, var3, var4);
         }

      }
   }

   public boolean showingScrollBar() {
      return super.scrollbarVisible();
   }

   protected void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      var1.pose().pushPose();
      var1.pose().translate((float)(this.getX() + this.innerPadding()), (float)(this.getY() + this.innerPadding()), 0.0F);
      this.multilineWidget.render(var1, var2, var3, var4);
      var1.pose().popPose();
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getMessage());
   }
}
