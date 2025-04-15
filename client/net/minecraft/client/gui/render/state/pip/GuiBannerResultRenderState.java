package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public record GuiBannerResultRenderState(ModelPart flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiBannerResultRenderState(ModelPart var1, DyeColor var2, BannerPatternLayers var3, int var4, int var5, int var6, int var7, @Nullable ScreenRectangle var8) {
      super();
      this.flag = var1;
      this.baseColor = var2;
      this.resultBannerPatterns = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.scissorArea = var8;
   }

   public float scale() {
      return 16.0F;
   }
}
