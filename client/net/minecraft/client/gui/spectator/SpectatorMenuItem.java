package net.minecraft.client.gui.spectator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface SpectatorMenuItem {
   void selectItem(SpectatorMenu menu);

   Component getName();

   void renderIcon(final GuiGraphics graphics, float brightness, float alpha);

   boolean isEnabled();
}
