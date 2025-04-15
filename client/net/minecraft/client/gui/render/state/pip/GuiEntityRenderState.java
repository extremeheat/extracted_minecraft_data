package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record GuiEntityRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiEntityRenderState(EntityRenderState var1, Vector3f var2, Quaternionf var3, @Nullable Quaternionf var4, int var5, int var6, int var7, int var8, float var9, @Nullable ScreenRectangle var10) {
      super();
      this.renderState = var1;
      this.translation = var2;
      this.rotation = var3;
      this.overrideCameraAngle = var4;
      this.x0 = var5;
      this.y0 = var6;
      this.x1 = var7;
      this.y1 = var8;
      this.scale = var9;
      this.scissorArea = var10;
   }
}
