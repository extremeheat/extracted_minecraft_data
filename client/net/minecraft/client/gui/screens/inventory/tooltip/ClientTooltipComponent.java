package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public interface ClientTooltipComponent {
   static ClientTooltipComponent create(FormattedCharSequence var0) {
      return new ClientTextTooltip(var0);
   }

   static ClientTooltipComponent create(TooltipComponent var0) {
      if (var0 instanceof BundleTooltip) {
         return new ClientBundleTooltip((BundleTooltip)var0);
      } else {
         throw new IllegalArgumentException("Unknown TooltipComponent");
      }
   }

   int getHeight();

   int getWidth(Font var1);

   default void renderText(Font var1, int var2, int var3, Matrix4f var4, MultiBufferSource.BufferSource var5) {
   }

   default void renderImage(Font var1, int var2, int var3, GuiGraphics var4) {
   }
}
