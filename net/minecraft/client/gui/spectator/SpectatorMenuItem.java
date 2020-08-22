package net.minecraft.client.gui.spectator;

import net.minecraft.network.chat.Component;

public interface SpectatorMenuItem {
   void selectItem(SpectatorMenu var1);

   Component getName();

   void renderIcon(float var1, int var2);

   boolean isEnabled();
}
