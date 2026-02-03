package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface ClientTooltipComponent {
   static ClientTooltipComponent create(final FormattedCharSequence charSequence) {
      return new ClientTextTooltip(charSequence);
   }

   static ClientTooltipComponent create(final TooltipComponent component) {
      Objects.requireNonNull(component);
      byte var2 = 0;
      Object var10000;
      //$FF: var2->value
      //0->net/minecraft/world/inventory/tooltip/BundleTooltip
      //1->net/minecraft/client/gui/screens/inventory/tooltip/ClientActivePlayersTooltip$ActivePlayersTooltip
      switch (component.typeSwitch<invokedynamic>(component, var2)) {
         case 0:
            BundleTooltip bundleTooltip = (BundleTooltip)component;
            var10000 = new ClientBundleTooltip(bundleTooltip.contents());
            break;
         case 1:
            ClientActivePlayersTooltip.ActivePlayersTooltip activePlayersTooltip = (ClientActivePlayersTooltip.ActivePlayersTooltip)component;
            var10000 = new ClientActivePlayersTooltip(activePlayersTooltip);
            break;
         default:
            throw new IllegalArgumentException("Unknown TooltipComponent");
      }

      return (ClientTooltipComponent)var10000;
   }

   int getHeight(final Font font);

   int getWidth(final Font font);

   default boolean showTooltipWithItemInHand() {
      return false;
   }

   default void renderText(final GuiGraphics guiGraphics, final Font font, final int x, final int y) {
   }

   default void renderImage(final Font font, final int x, final int y, final int w, final int h, final GuiGraphics graphics) {
   }
}
