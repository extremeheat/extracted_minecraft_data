package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2ic;

public interface ClientTooltipPositioner {
   Vector2ic positionTooltip(Screen var1, int var2, int var3, int var4, int var5);
}
