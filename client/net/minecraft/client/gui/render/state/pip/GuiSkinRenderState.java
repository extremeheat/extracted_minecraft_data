package net.minecraft.client.gui.render.state.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public record GuiSkinRenderState(PlayerModel playerModel, ResourceLocation texture, float rotationX, float rotationY, float pivotY, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiSkinRenderState(PlayerModel var1, ResourceLocation var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9, float var10, @Nullable ScreenRectangle var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, PictureInPictureRenderState.getBounds(var6, var7, var8, var9, var11));
   }

   public GuiSkinRenderState(PlayerModel var1, ResourceLocation var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9, float var10, @Nullable ScreenRectangle var11, @Nullable ScreenRectangle var12) {
      super();
      this.playerModel = var1;
      this.texture = var2;
      this.rotationX = var3;
      this.rotationY = var4;
      this.pivotY = var5;
      this.x0 = var6;
      this.y0 = var7;
      this.x1 = var8;
      this.y1 = var9;
      this.scale = var10;
      this.scissorArea = var11;
      this.bounds = var12;
   }
}
