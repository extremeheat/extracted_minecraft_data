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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static ClientTooltipComponent create(TooltipComponent var0) {
      if (var0 instanceof BundleTooltip var1) {
         return new ClientBundleTooltip(var1.contents());
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
