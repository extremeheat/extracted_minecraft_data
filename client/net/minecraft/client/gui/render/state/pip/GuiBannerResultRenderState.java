package net.minecraft.client.gui.render.state.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jspecify.annotations.Nullable;

public record GuiBannerResultRenderState(BannerFlagModel flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiBannerResultRenderState(BannerFlagModel var1, DyeColor var2, BannerPatternLayers var3, int var4, int var5, int var6, int var7, @Nullable ScreenRectangle var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, PictureInPictureRenderState.getBounds(var4, var5, var6, var7, var8));
   }

   public GuiBannerResultRenderState(BannerFlagModel var1, DyeColor var2, BannerPatternLayers var3, int var4, int var5, int var6, int var7, @Nullable ScreenRectangle var8, @Nullable ScreenRectangle var9) {
      super();
      this.flag = var1;
      this.baseColor = var2;
      this.resultBannerPatterns = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.scissorArea = var8;
      this.bounds = var9;
   }

   public float scale() {
      return 16.0F;
   }
}
