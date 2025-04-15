package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;

public record GuiTextRenderState(TextRenderState textRenderState, Matrix3x2f pose, int x, int y, @Nullable ScreenRectangle scissorArea) {
   public GuiTextRenderState(TextRenderState var1, Matrix3x2f var2, int var3, int var4, @Nullable ScreenRectangle var5) {
      super();
      this.textRenderState = var1;
      this.pose = var2;
      this.x = var3;
      this.y = var4;
      this.scissorArea = var5;
   }
}
