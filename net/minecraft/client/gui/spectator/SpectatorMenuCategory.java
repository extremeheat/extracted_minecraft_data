package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.network.chat.Component;

public interface SpectatorMenuCategory {
   List getItems();

   Component getPrompt();
}
