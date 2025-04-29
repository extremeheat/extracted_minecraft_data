package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.ScreenArea;

public interface PictureInPictureRenderState extends ScreenArea {
   int x0();

   int x1();

   int y0();

   int y1();

   float scale();

   @Nullable
   ScreenRectangle scissorArea();

   @Nullable
   static ScreenRectangle getBounds(int var0, int var1, int var2, int var3, @Nullable ScreenRectangle var4) {
      ScreenRectangle var5 = new ScreenRectangle(var0, var1, var2 - var0, var3 - var1);
      return var4 != null ? var4.intersection(var5) : var5;
   }
}
