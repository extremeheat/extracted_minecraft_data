package net.minecraft.client.gui.spectator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface SpectatorMenuItem {
   void selectItem(SpectatorMenu var1);

   Component getName();

   void renderIcon(GuiGraphics var1, float var2, int var3);

   boolean isEnabled();
}
