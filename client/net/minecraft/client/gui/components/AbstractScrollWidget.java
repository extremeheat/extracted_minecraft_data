package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractScrollWidget extends AbstractWidget implements Renderable, GuiEventListener {
   private static final int BORDER_COLOR_FOCUSED = -1;
   private static final int BORDER_COLOR = -6250336;
   private static final int BACKGROUND_COLOR = -16777216;
   private static final int INNER_PADDING = 4;
   private double scrollAmount;
   private boolean scrolling;

   public AbstractScrollWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.visible) {
         return false;
      } else {
         boolean var6 = this.withinContentAreaPoint(var1, var3);
         boolean var7 = this.scrollbarVisible()
            && var1 >= (double)(this.getX() + this.width)
            && var1 <= (double)(this.getX() + this.width + 8)
            && var3 >= (double)this.getY()
            && var3 < (double)(this.getY() + this.height);
         if (var7 && var5 == 0) {
            this.scrolling = true;
            return true;
         } else {
            return var6 || var7;
         }
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         this.scrolling = false;
      }

      return super.mouseReleased(var1, var3, var5);
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.visible && this.isFocused() && this.scrolling) {
         if (var3 < (double)this.getY()) {
            this.setScrollAmount(0.0);
         } else if (var3 > (double)(this.getY() + this.height)) {
            this.setScrollAmount((double)this.getMaxScrollAmount());
         } else {
            int var10 = this.getScrollBarHeight();
            double var11 = (double)Math.max(1, this.getMaxScrollAmount() / (this.height - var10));
            this.setScrollAmount(this.scrollAmount + var8 * var11);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.visible) {
         return false;
      } else {
         this.setScrollAmount(this.scrollAmount - var5 * this.scrollRate());
         return true;
      }
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.renderBackground(var1);
         enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
         var1.pushPose();
         var1.translate(0.0, -this.scrollAmount, 0.0);
         this.renderContents(var1, var2, var3, var4);
         var1.popPose();
         disableScissor();
         this.renderDecorations(var1);
      }
   }

   private int getScrollBarHeight() {
      return Mth.clamp((int)((float)(this.height * this.height) / (float)this.getContentHeight()), 32, this.height);
   }

   protected void renderDecorations(PoseStack var1) {
      if (this.scrollbarVisible()) {
         this.renderScrollBar(var1);
      }
   }

   protected int innerPadding() {
      return 4;
   }

   protected int totalInnerPadding() {
      return this.innerPadding() * 2;
   }

   protected double scrollAmount() {
      return this.scrollAmount;
   }

   protected void setScrollAmount(double var1) {
      this.scrollAmount = Mth.clamp(var1, 0.0, (double)this.getMaxScrollAmount());
   }

   protected int getMaxScrollAmount() {
      return Math.max(0, this.getContentHeight() - (this.height - 4));
   }

   private int getContentHeight() {
      return this.getInnerHeight() + 4;
   }

   private void renderBackground(PoseStack var1) {
      int var2 = this.isFocused() ? -1 : -6250336;
      fill(var1, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, var2);
      fill(var1, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, -16777216);
   }

   private void renderScrollBar(PoseStack var1) {
      int var2 = this.getScrollBarHeight();
      int var3 = this.getX() + this.width;
      int var4 = this.getX() + this.width + 8;
      int var5 = Math.max(this.getY(), (int)this.scrollAmount * (this.height - var2) / this.getMaxScrollAmount() + this.getY());
      int var6 = var5 + var2;
      fill(var1, var3, var5, var4, var6, -8355712);
      fill(var1, var3, var5, var4 - 1, var6 - 1, -4144960);
   }

   protected boolean withinContentAreaTopBottom(int var1, int var2) {
      return (double)var2 - this.scrollAmount >= (double)this.getY() && (double)var1 - this.scrollAmount <= (double)(this.getY() + this.height);
   }

   protected boolean withinContentAreaPoint(double var1, double var3) {
      return var1 >= (double)this.getX()
         && var1 < (double)(this.getX() + this.width)
         && var3 >= (double)this.getY()
         && var3 < (double)(this.getY() + this.height);
   }

   protected abstract int getInnerHeight();

   protected abstract boolean scrollbarVisible();

   protected abstract double scrollRate();

   protected abstract void renderContents(PoseStack var1, int var2, int var3, float var4);
}
