package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractScrollArea extends AbstractWidget {
   public static final int SCROLLBAR_WIDTH = 6;
   private double scrollAmount;
   private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
   private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
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

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.scrolling) {
         if (var3 < (double)this.getY()) {
            this.setScrollAmount(0.0);
         } else if (var3 > (double)this.getBottom()) {
            this.setScrollAmount((double)this.maxScrollAmount());
         } else {
            double var10 = (double)Math.max(1, this.maxScrollAmount());
            int var12 = this.scrollerHeight();
            double var13 = Math.max(1.0, var10 / (double)(this.height - var12));
            this.setScrollAmount(this.scrollAmount() + var8 * var13);
         }

         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public void onRelease(double var1, double var3) {
      this.scrolling = false;
   }

   public double scrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double var1) {
      this.scrollAmount = Mth.clamp(var1, 0.0, (double)this.maxScrollAmount());
   }

   public boolean updateScrolling(double var1, double var3, int var5) {
      this.scrolling = this.scrollbarVisible() && this.isValidClickButton(var5) && var1 >= (double)this.scrollBarX() && var1 <= (double)(this.scrollBarX() + 6) && var3 >= (double)this.getY() && var3 < (double)this.getBottom();
      return this.scrolling;
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

   protected void renderScrollbar(GuiGraphics var1) {
      if (this.scrollbarVisible()) {
         int var2 = this.scrollBarX();
         int var3 = this.scrollerHeight();
         int var4 = this.scrollBarY();
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)SCROLLER_BACKGROUND_SPRITE, var2, this.getY(), 6, this.getHeight());
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)SCROLLER_SPRITE, var2, var4, 6, var3);
      }

   }

   protected abstract int contentHeight();

   protected abstract double scrollRate();
}
