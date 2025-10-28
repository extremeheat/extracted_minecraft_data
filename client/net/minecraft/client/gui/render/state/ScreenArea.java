package net.minecraft.client.gui.render.state;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public interface ScreenArea {
   @Nullable ScreenRectangle bounds();
}
