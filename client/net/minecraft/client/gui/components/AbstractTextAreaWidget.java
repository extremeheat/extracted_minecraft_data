package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
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

   public AbstractTextAreaWidget(final int x, final int y, final int width, final int height, final Component narration, final AbstractScrollArea.ScrollbarSettings scrollbarSettings) {
      super(x, y, width, height, narration, scrollbarSettings);
      this.showBackground = true;
      this.showDecorations = true;
   }

   public AbstractTextAreaWidget(final int x, final int y, final int width, final int height, final Component narration, final AbstractScrollArea.ScrollbarSettings scrollbarSettings, final boolean showBackground, final boolean showDecorations) {
      this(x, y, width, height, narration, scrollbarSettings);
      this.showBackground = showBackground;
      this.showDecorations = showDecorations;
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      boolean scrolling = this.updateScrolling(event);
      return super.mouseClicked(event, doubleClick) || scrolling;
   }

   public boolean keyPressed(final KeyEvent event) {
      boolean isUp = event.isUp();
      boolean isDown = event.isDown();
      if (isUp || isDown) {
         double previousScrollAmount = this.scrollAmount();
         this.setScrollAmount(this.scrollAmount() + (double)(isUp ? -1 : 1) * this.scrollRate());
         if (previousScrollAmount != this.scrollAmount()) {
            return true;
         }
      }

      return super.keyPressed(event);
   }

   public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.visible) {
         if (this.showBackground) {
            this.extractBackground(graphics);
         }

         graphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
         graphics.pose().pushMatrix();
         graphics.pose().translate(0.0F, (float)(-this.scrollAmount()));
         this.extractContents(graphics, mouseX, mouseY, a);
         graphics.pose().popMatrix();
         graphics.disableScissor();
         this.extractScrollbar(graphics, mouseX, mouseY);
         if (this.showDecorations) {
            this.extractDecorations(graphics);
         }

      }
   }

   protected void extractDecorations(final GuiGraphicsExtractor graphics) {
   }

   protected int innerPadding() {
      return 4;
   }

   protected int totalInnerPadding() {
      return this.innerPadding() * 2;
   }

   public boolean isMouseOver(final double mouseX, final double mouseY) {
      return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getRight() + this.scrollbarWidth()) && mouseY < (double)this.getBottom();
   }

   protected int scrollBarX() {
      return this.getRight();
   }

   protected int contentHeight() {
      return this.getInnerHeight() + this.totalInnerPadding();
   }

   protected void extractBackground(final GuiGraphicsExtractor graphics) {
      this.extractBorder(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   protected void extractBorder(final GuiGraphicsExtractor graphics, final int x, final int y, final int width, final int height) {
      Identifier sprite = BACKGROUND_SPRITES.get(this.isActive(), this.isFocused());
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
   }

   protected boolean withinContentAreaTopBottom(final int top, final int bottom) {
      return (double)bottom - this.scrollAmount() >= (double)this.getY() && (double)top - this.scrollAmount() <= (double)(this.getY() + this.height);
   }

   protected abstract int getInnerHeight();

   protected abstract void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a);

   protected int getInnerLeft() {
      return this.getX() + this.innerPadding();
   }

   protected int getInnerTop() {
      return this.getY() + this.innerPadding();
   }

   public void playDownSound(final SoundManager soundManager) {
   }
}
