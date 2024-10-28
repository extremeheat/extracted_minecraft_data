package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class ClientTextTooltip implements ClientTooltipComponent {
   private final FormattedCharSequence text;

   public ClientTextTooltip(FormattedCharSequence var1) {
      super();
      this.text = var1;
   }

   public int getWidth(Font var1) {
      return var1.width(this.text);
   }

   public int getHeight() {
      return 10;
   }

   public void renderText(Font var1, int var2, int var3, Matrix4f var4, MultiBufferSource.BufferSource var5) {
      var1.drawInBatch((FormattedCharSequence)this.text, (float)var2, (float)var3, -1, true, var4, var5, Font.DisplayMode.NORMAL, 0, 15728880);
   }
}
