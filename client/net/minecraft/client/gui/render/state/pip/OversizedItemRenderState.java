package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import org.joml.Matrix3x2f;

public record OversizedItemRenderState(GuiItemRenderState guiItemRenderState, int x0, int y0, int x1, int y1) implements PictureInPictureRenderState {
   public OversizedItemRenderState(GuiItemRenderState var1, int var2, int var3, int var4, int var5) {
      super();
      this.guiItemRenderState = var1;
      this.x0 = var2;
      this.y0 = var3;
      this.x1 = var4;
      this.y1 = var5;
   }

   public float scale() {
      return 16.0F;
   }

   public Matrix3x2f pose() {
      return this.guiItemRenderState.pose();
   }

   @Nullable
   public ScreenRectangle scissorArea() {
      return this.guiItemRenderState.scissorArea();
   }

   @Nullable
   public ScreenRectangle bounds() {
      return this.guiItemRenderState.bounds();
   }
}
