package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public abstract class AbstractScrollArea extends AbstractWidget {
   public static final int SCROLLBAR_WIDTH = 6;
   private double scrollAmount;
   private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("widget/scroller");
   private static final Identifier SCROLLER_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("widget/scroller_background");
   private boolean scrolling;

   public AbstractScrollArea(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (!this.visible) {
         return false;
      } else {
         this.setScrollAmount(this.scrollAmount() - var7 * this.scrollRate());
         return true;
      }
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      if (this.scrolling) {
         if (var1.y() < (double)this.getY()) {
            this.setScrollAmount(0.0);
         } else if (var1.y() > (double)this.getBottom()) {
            this.setScrollAmount((double)this.maxScrollAmount());
         } else {
            double var6 = (double)Math.max(1, this.maxScrollAmount());
            int var8 = this.scrollerHeight();
            double var9 = Math.max(1.0, var6 / (double)(this.height - var8));
            this.setScrollAmount(this.scrollAmount() + var4 * var9);
         }

         return true;
      } else {
         return super.mouseDragged(var1, var2, var4);
      }
   }

   public void onRelease(MouseButtonEvent var1) {
      this.scrolling = false;
   }

   public double scrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double var1) {
      this.scrollAmount = Mth.clamp(var1, 0.0, (double)this.maxScrollAmount());
   }

   public boolean updateScrolling(MouseButtonEvent var1) {
      this.scrolling = this.scrollbarVisible() && this.isValidClickButton(var1.buttonInfo()) && this.isOverScrollbar(var1.x(), var1.y());
      return this.scrolling;
   }

   protected boolean isOverScrollbar(double var1, double var3) {
      return var1 >= (double)this.scrollBarX() && var1 <= (double)(this.scrollBarX() + 6) && var3 >= (double)this.getY() && var3 < (double)this.getBottom();
   }

   public void refreshScrollAmount() {
      this.setScrollAmount(this.scrollAmount);
   }

   public int maxScrollAmount() {
      return Math.max(0, this.contentHeight() - this.height);
   }

   protected boolean scrollbarVisible() {
      return this.maxScrollAmount() > 0;
   }

   protected int scrollerHeight() {
      return Mth.clamp((int)((float)(this.height * this.height) / (float)this.contentHeight()), 32, this.height - 8);
   }

   protected int scrollBarX() {
      return this.getRight() - 6;
   }

   protected int scrollBarY() {
      return Math.max(this.getY(), (int)this.scrollAmount * (this.height - this.scrollerHeight()) / this.maxScrollAmount() + this.getY());
   }

   protected void renderScrollbar(GuiGraphics var1, int var2, int var3) {
      if (this.scrollbarVisible()) {
         int var4 = this.scrollBarX();
         int var5 = this.scrollerHeight();
         int var6 = this.scrollBarY();
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SCROLLER_BACKGROUND_SPRITE, var4, this.getY(), 6, this.getHeight());
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SCROLLER_SPRITE, var4, var6, 6, var5);
         if (this.isOverScrollbar((double)var2, (double)var3)) {
            var1.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
         }
      }

   }

   protected abstract int contentHeight();

   protected abstract double scrollRate();
}
