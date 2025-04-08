package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;

public record GuiSkinRenderState(PlayerModel playerModel, ResourceLocation texture, float rotationX, float rotationY, float pivotY, int x0, int y0, int x1, int y1, float z, float scale, GuiLayer layer, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiSkinRenderState(PlayerModel var1, ResourceLocation var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9, float var10, float var11, GuiLayer var12, @Nullable ScreenRectangle var13) {
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
      this.z = var10;
      this.scale = var11;
      this.layer = var12;
      this.scissorArea = var13;
   }
}
