package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class AbstractTextAreaWidget extends AbstractScrollArea {
   private static final WidgetSprites BACKGROUND_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/text_field"), Identifier.withDefaultNamespace("widget/text_field_highlighted"));
   private static final int INNER_PADDING = 4;
   public static final int DEFAULT_TOTAL_PADDING = 8;
   private boolean showBackground;
   private boolean showDecorations;

   public AbstractTextAreaWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
      this.showBackground = true;
      this.showDecorations = true;
   }

   public AbstractTextAreaWidget(int var1, int var2, int var3, int var4, Component var5, boolean var6, boolean var7) {
      this(var1, var2, var3, var4, var5);
      this.showBackground = var6;
      this.showDecorations = var7;
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      boolean var3 = this.updateScrolling(var1);
      return super.mouseClicked(var1, var2) || var3;
   }

   public boolean keyPressed(KeyEvent var1) {
      boolean var2 = var1.isUp();
      boolean var3 = var1.isDown();
      if (var2 || var3) {
         double var4 = this.scrollAmount();
         this.setScrollAmount(this.scrollAmount() + (double)(var2 ? -1 : 1) * this.scrollRate());
         if (var4 != this.scrollAmount()) {
            return true;
         }
      }

      return super.keyPressed(var1);
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.visible) {
         if (this.showBackground) {
            this.renderBackground(var1);
         }

         var1.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
         var1.pose().pushMatrix();
         var1.pose().translate(0.0F, (float)(-this.scrollAmount()));
         this.renderContents(var1, var2, var3, var4);
         var1.pose().popMatrix();
         var1.disableScissor();
         this.renderScrollbar(var1, var2, var3);
         if (this.showDecorations) {
            this.renderDecorations(var1);
         }

      }
   }

   protected void renderDecorations(GuiGraphics var1) {
   }

   protected int innerPadding() {
      return 4;
   }

   protected int totalInnerPadding() {
      return this.innerPadding() * 2;
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.active && this.visible && var1 >= (double)this.getX() && var3 >= (double)this.getY() && var1 < (double)(this.getRight() + 6) && var3 < (double)this.getBottom();
   }

   protected int scrollBarX() {
      return this.getRight();
   }

   protected int contentHeight() {
      return this.getInnerHeight() + this.totalInnerPadding();
   }

   protected void renderBackground(GuiGraphics var1) {
      this.renderBorder(var1, this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   protected void renderBorder(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      Identifier var6 = BACKGROUND_SPRITES.get(this.isActive(), this.isFocused());
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, var6, var2, var3, var4, var5);
   }

   protected boolean withinContentAreaTopBottom(int var1, int var2) {
      return (double)var2 - this.scrollAmount() >= (double)this.getY() && (double)var1 - this.scrollAmount() <= (double)(this.getY() + this.height);
   }

   protected abstract int getInnerHeight();

   protected abstract void renderContents(GuiGraphics var1, int var2, int var3, float var4);

   protected int getInnerLeft() {
      return this.getX() + this.innerPadding();
   }

   protected int getInnerTop() {
      return this.getY() + this.innerPadding();
   }

   public void playDownSound(SoundManager var1) {
   }
}
