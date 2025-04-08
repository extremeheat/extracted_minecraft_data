package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

public class ClientTextTooltip implements ClientTooltipComponent {
   private final FormattedCharSequence text;

   public ClientTextTooltip(FormattedCharSequence var1) {
      super();
      this.text = var1;
   }

   public int getWidth(Font var1) {
      return var1.width(this.text);
   }

   public int getHeight(Font var1) {
      return 10;
   }

   public void renderText(GuiGraphics var1, Font var2, int var3, int var4) {
      var1.drawString(var2, (FormattedCharSequence)this.text, var3, var4, -1, true);
   }
}
