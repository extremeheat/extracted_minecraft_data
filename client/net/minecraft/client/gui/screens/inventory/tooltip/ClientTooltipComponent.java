package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.Objects;
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
      Objects.requireNonNull(var0);
      byte var2 = 0;
      Object var10000;
      //$FF: var2->value
      //0->net/minecraft/world/inventory/tooltip/BundleTooltip
      //1->net/minecraft/client/gui/screens/inventory/tooltip/ClientActivePlayersTooltip$ActivePlayersTooltip
      switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
         case 0:
            BundleTooltip var3 = (BundleTooltip)var0;
            var10000 = new ClientBundleTooltip(var3.contents());
            break;
         case 1:
            ClientActivePlayersTooltip.ActivePlayersTooltip var4 = (ClientActivePlayersTooltip.ActivePlayersTooltip)var0;
            var10000 = new ClientActivePlayersTooltip(var4);
            break;
         default:
            throw new IllegalArgumentException("Unknown TooltipComponent");
      }

      return (ClientTooltipComponent)var10000;
   }

   int getHeight(Font var1);

   int getWidth(Font var1);

   default boolean showTooltipWithItemInHand() {
      return false;
   }

   default void renderText(Font var1, int var2, int var3, Matrix4f var4, MultiBufferSource.BufferSource var5) {
   }

   default void renderImage(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
   }
}
