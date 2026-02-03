package net.minecraft.client.gui.render.state.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public record GuiEntityRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiEntityRenderState(final EntityRenderState renderState, final Vector3f translation, final Quaternionf rotation, final @Nullable Quaternionf overrideCameraAngle, final int x0, final int y0, final int x1, final int y1, final float scale, final @Nullable ScreenRectangle scissorArea) {
      this(renderState, translation, rotation, overrideCameraAngle, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
   }

   public GuiEntityRenderState {
      super();
   }
}
