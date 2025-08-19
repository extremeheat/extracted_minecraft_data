package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class FittingMultiLineTextWidget extends AbstractTextAreaWidget {
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

   public void minimizeHeight() {
      if (!this.showingScrollBar()) {
         this.setHeight(this.getInnerHeight() + this.totalInnerPadding());
      }

   }

   protected double scrollRate() {
      Objects.requireNonNull(this.font);
      return 9.0;
   }

   protected void renderBackground(GuiGraphics var1) {
      super.renderBackground(var1);
   }

   public boolean showingScrollBar() {
      return super.scrollbarVisible();
   }

   protected void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      var1.pose().pushMatrix();
      var1.pose().translate((float)this.getInnerLeft(), (float)this.getInnerTop());
      this.multilineWidget.render(var1, var2, var3, var4);
      var1.pose().popMatrix();
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getMessage());
   }

   public void setMessage(Component var1) {
      super.setMessage(var1);
      this.multilineWidget.setMessage(var1);
   }
}
