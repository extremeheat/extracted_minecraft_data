package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractScrollWidget extends AbstractWidget implements Widget, GuiEventListener {
   private static final int BORDER_COLOR_FOCUSED = -1;
   private static final int BORDER_COLOR = -6250336;
   private static final int BACKGROUND_COLOR = -16777216;
   private static final int INNER_PADDING = 4;
   private double scrollAmount;
   private boolean scrolling;

   public AbstractScrollWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.visible) {
         return false;
      } else {
         boolean var6 = this.withinContentAreaPoint(var1, var3);
         boolean var7 = this.scrollbarVisible() && var1 >= (double)(this.x + this.width) && var1 <= (double)(this.x + this.width + 8) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
         this.setFocused(var6 || var7);
         if (var7 && var5 == 0) {
            this.scrolling = true;
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         this.scrolling = false;
      }

      return super.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.visible && this.isFocused() && this.scrolling) {
         if (var3 < (double)this.y) {
            this.setScrollAmount(0.0);
         } else if (var3 > (double)(this.y + this.height)) {
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

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.visible && this.isFocused()) {
         this.setScrollAmount(this.scrollAmount - var5 * this.scrollRate());
         return true;
      } else {
         return false;
      }
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.renderBackground(var1);
         enableScissor(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1);
         var1.pushPose();
         var1.translate(0.0, -this.scrollAmount, 0.0);
         this.renderContents(var1, var2, var3, var4);
         var1.popPose();
         disableScissor();
         this.renderDecorations(var1);
      }
   }

   private int getScrollBarHeight() {
      return Mth.clamp((int)((int)((float)(this.height * this.height) / (float)this.getContentHeight())), (int)32, (int)this.height);
   }

   protected void renderDecorations(PoseStack var1) {
      if (this.scrollbarVisible()) {
         this.renderScrollBar();
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
      fill(var1, this.x, this.y, this.x + this.width, this.y + this.height, var2);
      fill(var1, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, -16777216);
   }

   private void renderScrollBar() {
      int var1 = this.getScrollBarHeight();
      int var2 = this.x + this.width;
      int var3 = this.x + this.width + 8;
      int var4 = Math.max(this.y, (int)this.scrollAmount * (this.height - var1) / this.getMaxScrollAmount() + this.y);
      int var5 = var4 + var1;
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator var6 = Tesselator.getInstance();
      BufferBuilder var7 = var6.getBuilder();
      var7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var7.vertex((double)var2, (double)var5, 0.0).color(128, 128, 128, 255).endVertex();
      var7.vertex((double)var3, (double)var5, 0.0).color(128, 128, 128, 255).endVertex();
      var7.vertex((double)var3, (double)var4, 0.0).color(128, 128, 128, 255).endVertex();
      var7.vertex((double)var2, (double)var4, 0.0).color(128, 128, 128, 255).endVertex();
      var7.vertex((double)var2, (double)(var5 - 1), 0.0).color(192, 192, 192, 255).endVertex();
      var7.vertex((double)(var3 - 1), (double)(var5 - 1), 0.0).color(192, 192, 192, 255).endVertex();
      var7.vertex((double)(var3 - 1), (double)var4, 0.0).color(192, 192, 192, 255).endVertex();
      var7.vertex((double)var2, (double)var4, 0.0).color(192, 192, 192, 255).endVertex();
      var6.end();
   }

   protected boolean withinContentAreaTopBottom(int var1, int var2) {
      return (double)var2 - this.scrollAmount >= (double)this.y && (double)var1 - this.scrollAmount <= (double)(this.y + this.height);
   }

   protected boolean withinContentAreaPoint(double var1, double var3) {
      return var1 >= (double)this.x && var1 < (double)(this.x + this.width) && var3 >= (double)this.y && var3 < (double)(this.y + this.height);
   }

   protected abstract int getInnerHeight();

   protected abstract boolean scrollbarVisible();

   protected abstract double scrollRate();

   protected abstract void renderContents(PoseStack var1, int var2, int var3, float var4);
}
