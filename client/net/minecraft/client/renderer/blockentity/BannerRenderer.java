package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BannerRenderer extends BlockEntityRenderer<BannerBlockEntity> {
   private final BannerModel bannerModel = new BannerModel();

   public BannerRenderer() {
      super();
   }

   public void render(BannerBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = 0.6666667F;
      boolean var11 = var1.getLevel() == null;
      GlStateManager.pushMatrix();
      ModelPart var14 = this.bannerModel.getPole();
      long var12;
      if (var11) {
         var12 = 0L;
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         var14.visible = true;
      } else {
         var12 = var1.getLevel().getGameTime();
         BlockState var15 = var1.getBlockState();
         if (var15.getBlock() instanceof BannerBlock) {
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.rotatef((float)(-(Integer)var15.getValue(BannerBlock.ROTATION) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
            var14.visible = true;
         } else {
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 - 0.16666667F, (float)var6 + 0.5F);
            GlStateManager.rotatef(-((Direction)var15.getValue(WallBannerBlock.FACING)).toYRot(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
            var14.visible = false;
         }
      }

      BlockPos var18 = var1.getBlockPos();
      float var16 = (float)((long)(var18.getX() * 7 + var18.getY() * 9 + var18.getZ() * 13) + var12) + var8;
      this.bannerModel.getFlag().xRot = (-0.0125F + 0.01F * Mth.cos(var16 * 3.1415927F * 0.02F)) * 3.1415927F;
      GlStateManager.enableRescaleNormal();
      ResourceLocation var17 = this.getTextureLocation(var1);
      if (var17 != null) {
         this.bindTexture(var17);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
         this.bannerModel.render();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   @Nullable
   private ResourceLocation getTextureLocation(BannerBlockEntity var1) {
      return BannerTextures.BANNER_CACHE.getTextureLocation(var1.getTextureHashName(), var1.getPatterns(), var1.getColors());
   }
}
