package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingRenderer extends EntityRenderer<Painting> {
   public PaintingRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(Painting var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.mulPose(Axis.YP.rotationDegrees(180.0F - var2));
      PaintingVariant var7 = (PaintingVariant)var1.getVariant().value();
      VertexConsumer var8 = var5.getBuffer(RenderType.entitySolid(this.getTextureLocation(var1)));
      PaintingTextureManager var9 = Minecraft.getInstance().getPaintingTextures();
      this.renderPainting(var4, var8, var1, var7.width(), var7.height(), var9.get(var7), var9.getBackSprite());
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Painting var1) {
      return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
   }

   private void renderPainting(PoseStack var1, VertexConsumer var2, Painting var3, int var4, int var5, TextureAtlasSprite var6, TextureAtlasSprite var7) {
      PoseStack.Pose var8 = var1.last();
      float var9 = (float)(-var4) / 2.0F;
      float var10 = (float)(-var5) / 2.0F;
      float var11 = 0.03125F;
      float var12 = var7.getU0();
      float var13 = var7.getU1();
      float var14 = var7.getV0();
      float var15 = var7.getV1();
      float var16 = var7.getU0();
      float var17 = var7.getU1();
      float var18 = var7.getV0();
      float var19 = var7.getV(0.0625F);
      float var20 = var7.getU0();
      float var21 = var7.getU(0.0625F);
      float var22 = var7.getV0();
      float var23 = var7.getV1();
      double var24 = 1.0 / (double)var4;
      double var26 = 1.0 / (double)var5;

      for(int var28 = 0; var28 < var4; ++var28) {
         for(int var29 = 0; var29 < var5; ++var29) {
            float var30 = var9 + (float)(var28 + 1);
            float var31 = var9 + (float)var28;
            float var32 = var10 + (float)(var29 + 1);
            float var33 = var10 + (float)var29;
            int var34 = var3.getBlockX();
            int var35 = Mth.floor(var3.getY() + (double)((var32 + var33) / 2.0F));
            int var36 = var3.getBlockZ();
            Direction var37 = var3.getDirection();
            if (var37 == Direction.NORTH) {
               var34 = Mth.floor(var3.getX() + (double)((var30 + var31) / 2.0F));
            }

            if (var37 == Direction.WEST) {
               var36 = Mth.floor(var3.getZ() - (double)((var30 + var31) / 2.0F));
            }

            if (var37 == Direction.SOUTH) {
               var34 = Mth.floor(var3.getX() - (double)((var30 + var31) / 2.0F));
            }

            if (var37 == Direction.EAST) {
               var36 = Mth.floor(var3.getZ() + (double)((var30 + var31) / 2.0F));
            }

            int var38 = LevelRenderer.getLightColor(var3.level(), new BlockPos(var34, var35, var36));
            float var39 = var6.getU((float)(var24 * (double)(var4 - var28)));
            float var40 = var6.getU((float)(var24 * (double)(var4 - (var28 + 1))));
            float var41 = var6.getV((float)(var26 * (double)(var5 - var29)));
            float var42 = var6.getV((float)(var26 * (double)(var5 - (var29 + 1))));
            this.vertex(var8, var2, var30, var33, var40, var41, -0.03125F, 0, 0, -1, var38);
            this.vertex(var8, var2, var31, var33, var39, var41, -0.03125F, 0, 0, -1, var38);
            this.vertex(var8, var2, var31, var32, var39, var42, -0.03125F, 0, 0, -1, var38);
            this.vertex(var8, var2, var30, var32, var40, var42, -0.03125F, 0, 0, -1, var38);
            this.vertex(var8, var2, var30, var32, var13, var14, 0.03125F, 0, 0, 1, var38);
            this.vertex(var8, var2, var31, var32, var12, var14, 0.03125F, 0, 0, 1, var38);
            this.vertex(var8, var2, var31, var33, var12, var15, 0.03125F, 0, 0, 1, var38);
            this.vertex(var8, var2, var30, var33, var13, var15, 0.03125F, 0, 0, 1, var38);
            this.vertex(var8, var2, var30, var32, var16, var18, -0.03125F, 0, 1, 0, var38);
            this.vertex(var8, var2, var31, var32, var17, var18, -0.03125F, 0, 1, 0, var38);
            this.vertex(var8, var2, var31, var32, var17, var19, 0.03125F, 0, 1, 0, var38);
            this.vertex(var8, var2, var30, var32, var16, var19, 0.03125F, 0, 1, 0, var38);
            this.vertex(var8, var2, var30, var33, var16, var18, 0.03125F, 0, -1, 0, var38);
            this.vertex(var8, var2, var31, var33, var17, var18, 0.03125F, 0, -1, 0, var38);
            this.vertex(var8, var2, var31, var33, var17, var19, -0.03125F, 0, -1, 0, var38);
            this.vertex(var8, var2, var30, var33, var16, var19, -0.03125F, 0, -1, 0, var38);
            this.vertex(var8, var2, var30, var32, var21, var22, 0.03125F, -1, 0, 0, var38);
            this.vertex(var8, var2, var30, var33, var21, var23, 0.03125F, -1, 0, 0, var38);
            this.vertex(var8, var2, var30, var33, var20, var23, -0.03125F, -1, 0, 0, var38);
            this.vertex(var8, var2, var30, var32, var20, var22, -0.03125F, -1, 0, 0, var38);
            this.vertex(var8, var2, var31, var32, var21, var22, -0.03125F, 1, 0, 0, var38);
            this.vertex(var8, var2, var31, var33, var21, var23, -0.03125F, 1, 0, 0, var38);
            this.vertex(var8, var2, var31, var33, var20, var23, 0.03125F, 1, 0, 0, var38);
            this.vertex(var8, var2, var31, var32, var20, var22, 0.03125F, 1, 0, 0, var38);
         }
      }

   }

   private void vertex(PoseStack.Pose var1, VertexConsumer var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11) {
      var2.vertex(var1, var3, var4, var7).color(255, 255, 255, 255).uv(var5, var6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var11).normal(var1, (float)var8, (float)var9, (float)var10).endVertex();
   }
}
