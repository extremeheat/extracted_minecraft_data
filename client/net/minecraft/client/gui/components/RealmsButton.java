package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class RealmsButton extends Button {
   private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/realms_button"), Identifier.withDefaultNamespace("widget/realms_button_disabled"), Identifier.withDefaultNamespace("widget/realms_button_highlighted"));

   public RealmsButton(final int x, final int y, final int width, final int height, final Component message, final Button.OnPress onPress) {
      super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
   }

   protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      Identifier sprite = SPRITES.get(this.active, this.isHoveredOrFocused());
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
      this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
   }
}
