package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface PictureInPictureRenderState {
   int x0();

   int x1();

   int y0();

   int y1();

   float scale();

   @Nullable
   ScreenRectangle scissorArea();
}
