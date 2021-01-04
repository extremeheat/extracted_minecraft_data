package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

public abstract class ScrolledSelectionList extends AbstractContainerEventHandler implements Widget {
   protected static final int NO_DRAG = -1;
   protected static final int DRAG_OUTSIDE = -2;
   protected final Minecraft minecraft;
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected final int itemHeight;
   protected boolean centerListVertically = true;
   protected int yDrag = -2;
   protected double yo;
   protected boolean visible = true;
   protected boolean renderSelection = true;
   protected boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;

   public ScrolledSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.minecraft = var1;
      this.width = var2;
      this.height = var3;
      this.y0 = var4;
      this.y1 = var5;
      this.itemHeight = var6;
      this.x0 = 0;
      this.x1 = var2;
   }

   public void updateSize(int var1, int var2, int var3, int var4) {
      this.width = var1;
      this.height = var2;
      this.y0 = var3;
      this.y1 = var4;
      this.x0 = 0;
      this.x1 = var1;
   }

   public void setRenderSelection(boolean var1) {
      this.renderSelection = var1;
   }

   protected void setRenderHeader(boolean var1, int var2) {
      this.renderHeader = var1;
      this.headerHeight = var2;
      if (!var1) {
         this.headerHeight = 0;
      }

   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public boolean isVisible() {
      return this.visible;
   }

   protected abstract int getItemCount();

   public List<? extends GuiEventListener> children() {
      return Collections.emptyList();
   }

   protected boolean selectItem(int var1, int var2, double var3, double var5) {
      return true;
   }

   protected abstract boolean isSelectedItem(int var1);

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected abstract void renderBackground();

   protected void updateItemPosition(int var1, int var2, int var3, float var4) {
   }

   protected abstract void renderItem(int var1, int var2, int var3, int var4, int var5, int var6, float var7);

   protected void renderHeader(int var1, int var2, Tesselator var3) {
   }

   protected void clickedHeader(int var1, int var2) {
   }

   protected void renderDecorations(int var1, int var2) {
   }

   public int getItemAtPosition(double var1, double var3) {
      int var5 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
      int var6 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
      int var7 = Mth.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.yo - 4;
      int var8 = var7 / this.itemHeight;
      return var1 < (double)this.getScrollbarPosition() && var1 >= (double)var5 && var1 <= (double)var6 && var8 >= 0 && var7 >= 0 && var8 < this.getItemCount() ? var8 : -1;
   }

   protected void capYPosition() {
      this.yo = Mth.clamp(this.yo, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public void centerScrollOn(int var1) {
      this.yo = (double)(var1 * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
      this.capYPosition();
   }

   public int getScroll() {
      return (int)this.yo;
   }

   public boolean isMouseInList(double var1, double var3) {
      return var3 >= (double)this.y0 && var3 <= (double)this.y1 && var1 >= (double)this.x0 && var1 <= (double)this.x1;
   }

   public int getScrollBottom() {
      return (int)this.yo - this.height - this.headerHeight;
   }

   public void scroll(int var1) {
      this.yo += (double)var1;
      this.capYPosition();
      this.yDrag = -2;
   }

   public void render(int var1, int var2, float var3) {
      if (this.visible) {
         this.renderBackground();
         int var4 = this.getScrollbarPosition();
         int var5 = var4 + 6;
         this.capYPosition();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tesselator var6 = Tesselator.getInstance();
         BufferBuilder var7 = var6.getBuilder();
         this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)this.x0, (double)this.y1, 0.0D).uv((double)((float)this.x0 / 32.0F), (double)((float)(this.y1 + (int)this.yo) / 32.0F)).color(32, 32, 32, 255).endVertex();
         var7.vertex((double)this.x1, (double)this.y1, 0.0D).uv((double)((float)this.x1 / 32.0F), (double)((float)(this.y1 + (int)this.yo) / 32.0F)).color(32, 32, 32, 255).endVertex();
         var7.vertex((double)this.x1, (double)this.y0, 0.0D).uv((double)((float)this.x1 / 32.0F), (double)((float)(this.y0 + (int)this.yo) / 32.0F)).color(32, 32, 32, 255).endVertex();
         var7.vertex((double)this.x0, (double)this.y0, 0.0D).uv((double)((float)this.x0 / 32.0F), (double)((float)(this.y0 + (int)this.yo) / 32.0F)).color(32, 32, 32, 255).endVertex();
         var6.end();
         int var9 = this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
         int var10 = this.y0 + 4 - (int)this.yo;
         if (this.renderHeader) {
            this.renderHeader(var9, var10, var6);
         }

         this.renderList(var9, var10, var1, var2, var3);
         GlStateManager.disableDepthTest();
         this.renderHoleBackground(0, this.y0, 255, 255);
         this.renderHoleBackground(this.y1, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture();
         boolean var11 = true;
         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         var7.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         var7.vertex((double)this.x1, (double)this.y0, 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)this.x0, (double)this.y0, 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var6.end();
         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)this.x0, (double)this.y1, 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)this.x1, (double)this.y1, 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
         var7.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
         var6.end();
         int var12 = this.getMaxScroll();
         if (var12 > 0) {
            int var13 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            var13 = Mth.clamp(var13, 32, this.y1 - this.y0 - 8);
            int var14 = (int)this.yo * (this.y1 - this.y0 - var13) / var12 + this.y0;
            if (var14 < this.y0) {
               var14 = this.y0;
            }

            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)this.y1, 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var5, (double)this.y1, 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var5, (double)this.y0, 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var4, (double)this.y0, 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            var6.end();
            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)(var14 + var13), 0.0D).uv(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var5, (double)(var14 + var13), 0.0D).uv(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var5, (double)var14, 0.0D).uv(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var4, (double)var14, 0.0D).uv(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            var6.end();
            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)(var14 + var13 - 1), 0.0D).uv(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)(var5 - 1), (double)(var14 + var13 - 1), 0.0D).uv(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)(var5 - 1), (double)var14, 0.0D).uv(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)var4, (double)var14, 0.0D).uv(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            var6.end();
         }

         this.renderDecorations(var1, var2);
         GlStateManager.enableTexture();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
      }
   }

   protected void updateScrollingState(double var1, double var3, int var5) {
      this.scrolling = var5 == 0 && var1 >= (double)this.getScrollbarPosition() && var1 < (double)(this.getScrollbarPosition() + 6);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.updateScrollingState(var1, var3, var5);
      if (this.isVisible() && this.isMouseInList(var1, var3)) {
         int var6 = this.getItemAtPosition(var1, var3);
         if (var6 == -1 && var5 == 0) {
            this.clickedHeader((int)(var1 - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(var3 - (double)this.y0) + (int)this.yo - 4);
            return true;
         } else if (var6 != -1 && this.selectItem(var6, var5, var1, var3)) {
            if (this.children().size() > var6) {
               this.setFocused((GuiEventListener)this.children().get(var6));
            }

            this.setDragging(true);
            return true;
         } else {
            return this.scrolling;
         }
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (this.isVisible() && var5 == 0 && this.scrolling) {
         if (var3 < (double)this.y0) {
            this.yo = 0.0D;
         } else if (var3 > (double)this.y1) {
            this.yo = (double)this.getMaxScroll();
         } else {
            double var10 = (double)this.getMaxScroll();
            if (var10 < 1.0D) {
               var10 = 1.0D;
            }

            int var12 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            var12 = Mth.clamp(var12, 32, this.y1 - this.y0 - 8);
            double var13 = var10 / (double)(this.y1 - this.y0 - var12);
            if (var13 < 1.0D) {
               var13 = 1.0D;
            }

            this.yo += var8 * var13;
            this.capYPosition();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.isVisible()) {
         return false;
      } else {
         this.yo -= var5 * (double)this.itemHeight / 2.0D;
         return true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.isVisible()) {
         return false;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 == 264) {
         this.moveSelection(1);
         return true;
      } else if (var1 == 265) {
         this.moveSelection(-1);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(int var1) {
   }

   public boolean charTyped(char var1, int var2) {
      return !this.isVisible() ? false : super.charTyped(var1, var2);
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.isMouseInList(var1, var3);
   }

   public int getRowWidth() {
      return 220;
   }

   protected void renderList(int var1, int var2, int var3, int var4, float var5) {
      int var6 = this.getItemCount();
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();

      for(int var9 = 0; var9 < var6; ++var9) {
         int var10 = var2 + var9 * this.itemHeight + this.headerHeight;
         int var11 = this.itemHeight - 4;
         if (var10 > this.y1 || var10 + var11 < this.y0) {
            this.updateItemPosition(var9, var1, var10, var5);
         }

         if (this.renderSelection && this.isSelectedItem(var9)) {
            int var12 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
            int var13 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
            GlStateManager.disableTexture();
            float var14 = this.isFocused() ? 1.0F : 0.5F;
            GlStateManager.color4f(var14, var14, var14, 1.0F);
            var8.begin(7, DefaultVertexFormat.POSITION);
            var8.vertex((double)var12, (double)(var10 + var11 + 2), 0.0D).endVertex();
            var8.vertex((double)var13, (double)(var10 + var11 + 2), 0.0D).endVertex();
            var8.vertex((double)var13, (double)(var10 - 2), 0.0D).endVertex();
            var8.vertex((double)var12, (double)(var10 - 2), 0.0D).endVertex();
            var7.end();
            GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
            var8.begin(7, DefaultVertexFormat.POSITION);
            var8.vertex((double)(var12 + 1), (double)(var10 + var11 + 1), 0.0D).endVertex();
            var8.vertex((double)(var13 - 1), (double)(var10 + var11 + 1), 0.0D).endVertex();
            var8.vertex((double)(var13 - 1), (double)(var10 - 1), 0.0D).endVertex();
            var8.vertex((double)(var12 + 1), (double)(var10 - 1), 0.0D).endVertex();
            var7.end();
            GlStateManager.enableTexture();
         }

         this.renderItem(var9, var1, var10, var11, var3, var4, var5);
      }

   }

   protected boolean isFocused() {
      return false;
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   protected void renderHoleBackground(int var1, int var2, int var3, int var4) {
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = 32.0F;
      var6.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var6.vertex((double)this.x0, (double)var2, 0.0D).uv(0.0D, (double)((float)var2 / 32.0F)).color(64, 64, 64, var4).endVertex();
      var6.vertex((double)(this.x0 + this.width), (double)var2, 0.0D).uv((double)((float)this.width / 32.0F), (double)((float)var2 / 32.0F)).color(64, 64, 64, var4).endVertex();
      var6.vertex((double)(this.x0 + this.width), (double)var1, 0.0D).uv((double)((float)this.width / 32.0F), (double)((float)var1 / 32.0F)).color(64, 64, 64, var3).endVertex();
      var6.vertex((double)this.x0, (double)var1, 0.0D).uv(0.0D, (double)((float)var1 / 32.0F)).color(64, 64, 64, var3).endVertex();
      var5.end();
   }

   public void setLeftPos(int var1) {
      this.x0 = var1;
      this.x1 = var1 + this.width;
   }

   public int getItemHeight() {
      return this.itemHeight;
   }
}
