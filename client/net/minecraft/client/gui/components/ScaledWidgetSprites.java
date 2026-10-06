package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public record ScaledWidgetSprites(WidgetSprites sprites, int width, int height) {
   public ScaledWidgetSprites(final Identifier sprite, final int width, final int height) {
      this(new WidgetSprites(sprite), width, height);
   }

   public ScaledWidgetSprites(final Identifier sprite, final int size) {
      this(sprite, size, size);
   }

   public ScaledWidgetSprites {
      super();
   }

   public void extractSprite(final AbstractWidget widget, final GuiGraphicsExtractor graphics, final int x, final int y) {
      Identifier texture = this.sprites.get(widget.isActive(), widget.isHoveredOrFocused());
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, this.width, this.height, widget.alpha);
   }

   public void extractCenteredSprite(final AbstractWidget widget, final GuiGraphicsExtractor graphics) {
      int x = widget.getX() + widget.getWidth() / 2 - this.width / 2;
      int y = widget.getY() + widget.getHeight() / 2 - this.height / 2;
      this.extractSprite(widget, graphics, x, y);
   }
}
