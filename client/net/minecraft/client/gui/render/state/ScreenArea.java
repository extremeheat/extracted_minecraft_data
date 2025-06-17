package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface ScreenArea {
   @Nullable
   ScreenRectangle bounds();
}
