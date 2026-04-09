package net.minecraft.client.renderer.state.gui.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public record OversizedItemRenderState(GuiItemRenderState guiItemRenderState, int x0, int y0, int x1, int y1) implements PictureInPictureRenderState {
   public OversizedItemRenderState {
      super();
   }

   public float scale() {
      return 16.0F;
   }

   public Matrix3x2fc pose() {
      return this.guiItemRenderState.pose();
   }

   public @Nullable ScreenRectangle scissorArea() {
      return this.guiItemRenderState.scissorArea();
   }

   public @Nullable ScreenRectangle bounds() {
      return this.guiItemRenderState.bounds();
   }
}
