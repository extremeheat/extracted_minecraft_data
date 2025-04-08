package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;

public interface PictureInPictureRenderState {
   int x0();

   int x1();

   int y0();

   int y1();

   float z();

   float scale();

   @Nullable
   ScreenRectangle scissorArea();

   GuiLayer layer();
}
