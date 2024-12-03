package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractTextAreaWidget extends AbstractScrollArea {
   private static final WidgetSprites BACKGROUND_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/text_field"), ResourceLocation.withDefaultNamespace("widget/text_field_highlighted"));
   private static final int INNER_PADDING = 4;

   public AbstractTextAreaWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      boolean var6 = this.updateScrolling(var1, var3, var5);
      return super.mouseClicked(var1, var3, var5) || var6;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = var1 == 265;
      boolean var5 = var1 == 264;
      if (var4 || var5) {
         double var6 = this.scrollAmount();
         this.setScrollAmount(this.scrollAmount() + (double)(var4 ? -1 : 1) * this.scrollRate());
         if (var6 != this.scrollAmount()) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.renderBackground(var1);
         var1.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
         var1.pose().pushPose();
         var1.pose().translate(0.0, -this.scrollAmount(), 0.0);
         this.renderContents(var1, var2, var3, var4);
         var1.pose().popPose();
         var1.disableScissor();
         this.renderDecorations(var1);
      }
   }

   protected void renderDecorations(GuiGraphics var1) {
      this.renderScrollbar(var1);
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
      ResourceLocation var6 = BACKGROUND_SPRITES.get(this.isActive(), this.isFocused());
      var1.blitSprite(RenderType::guiTextured, var6, var2, var3, var4, var5);
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
