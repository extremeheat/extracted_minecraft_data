package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

public class ClientTextTooltip implements ClientTooltipComponent {
   private final FormattedCharSequence text;

   public ClientTextTooltip(final FormattedCharSequence text) {
      super();
      this.text = text;
   }

   public int getWidth(final Font font) {
      return font.width(this.text);
   }

   public int getHeight(final Font font) {
      return 10;
   }

   public void renderText(final GuiGraphics guiGraphics, final Font font, final int x, final int y) {
      guiGraphics.drawString(font, (FormattedCharSequence)this.text, x, y, -1, true);
   }
}
