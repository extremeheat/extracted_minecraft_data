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
      PaintingVariant var7 = var1.getVariant().value();
      float var8 = 0.0625F;
      var4.scale(0.0625F, 0.0625F, 0.0625F);
      VertexConsumer var9 = var5.getBuffer(RenderType.entitySolid(this.getTextureLocation(var1)));
      PaintingTextureManager var10 = Minecraft.getInstance().getPaintingTextures();
      this.renderPainting(var4, var9, var1, var7.getWidth(), var7.getHeight(), var10.get(var7), var10.getBackSprite());
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
      float var11 = 0.5F;
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
      int var24 = var4 / 16;
      int var25 = var5 / 16;
      double var26 = 1.0 / (double)var24;
      double var28 = 1.0 / (double)var25;

      for(int var30 = 0; var30 < var24; ++var30) {
         for(int var31 = 0; var31 < var25; ++var31) {
            float var32 = var9 + (float)((var30 + 1) * 16);
            float var33 = var9 + (float)(var30 * 16);
            float var34 = var10 + (float)((var31 + 1) * 16);
            float var35 = var10 + (float)(var31 * 16);
            int var36 = var3.getBlockX();
            int var37 = Mth.floor(var3.getY() + (double)((var34 + var35) / 2.0F / 16.0F));
            int var38 = var3.getBlockZ();
            Direction var39 = var3.getDirection();
            if (var39 == Direction.NORTH) {
               var36 = Mth.floor(var3.getX() + (double)((var32 + var33) / 2.0F / 16.0F));
            }

            if (var39 == Direction.WEST) {
               var38 = Mth.floor(var3.getZ() - (double)((var32 + var33) / 2.0F / 16.0F));
            }

            if (var39 == Direction.SOUTH) {
               var36 = Mth.floor(var3.getX() - (double)((var32 + var33) / 2.0F / 16.0F));
            }

            if (var39 == Direction.EAST) {
               var38 = Mth.floor(var3.getZ() + (double)((var32 + var33) / 2.0F / 16.0F));
            }

            int var40 = LevelRenderer.getLightColor(var3.level(), new BlockPos(var36, var37, var38));
            float var41 = var6.getU((float)(var26 * (double)(var24 - var30)));
            float var42 = var6.getU((float)(var26 * (double)(var24 - (var30 + 1))));
            float var43 = var6.getV((float)(var28 * (double)(var25 - var31)));
            float var44 = var6.getV((float)(var28 * (double)(var25 - (var31 + 1))));
            this.vertex(var8, var2, var32, var35, var42, var43, -0.5F, 0, 0, -1, var40);
            this.vertex(var8, var2, var33, var35, var41, var43, -0.5F, 0, 0, -1, var40);
            this.vertex(var8, var2, var33, var34, var41, var44, -0.5F, 0, 0, -1, var40);
            this.vertex(var8, var2, var32, var34, var42, var44, -0.5F, 0, 0, -1, var40);
            this.vertex(var8, var2, var32, var34, var13, var14, 0.5F, 0, 0, 1, var40);
            this.vertex(var8, var2, var33, var34, var12, var14, 0.5F, 0, 0, 1, var40);
            this.vertex(var8, var2, var33, var35, var12, var15, 0.5F, 0, 0, 1, var40);
            this.vertex(var8, var2, var32, var35, var13, var15, 0.5F, 0, 0, 1, var40);
            this.vertex(var8, var2, var32, var34, var16, var18, -0.5F, 0, 1, 0, var40);
            this.vertex(var8, var2, var33, var34, var17, var18, -0.5F, 0, 1, 0, var40);
            this.vertex(var8, var2, var33, var34, var17, var19, 0.5F, 0, 1, 0, var40);
            this.vertex(var8, var2, var32, var34, var16, var19, 0.5F, 0, 1, 0, var40);
            this.vertex(var8, var2, var32, var35, var16, var18, 0.5F, 0, -1, 0, var40);
            this.vertex(var8, var2, var33, var35, var17, var18, 0.5F, 0, -1, 0, var40);
            this.vertex(var8, var2, var33, var35, var17, var19, -0.5F, 0, -1, 0, var40);
            this.vertex(var8, var2, var32, var35, var16, var19, -0.5F, 0, -1, 0, var40);
            this.vertex(var8, var2, var32, var34, var21, var22, 0.5F, -1, 0, 0, var40);
            this.vertex(var8, var2, var32, var35, var21, var23, 0.5F, -1, 0, 0, var40);
            this.vertex(var8, var2, var32, var35, var20, var23, -0.5F, -1, 0, 0, var40);
            this.vertex(var8, var2, var32, var34, var20, var22, -0.5F, -1, 0, 0, var40);
            this.vertex(var8, var2, var33, var34, var21, var22, -0.5F, 1, 0, 0, var40);
            this.vertex(var8, var2, var33, var35, var21, var23, -0.5F, 1, 0, 0, var40);
            this.vertex(var8, var2, var33, var35, var20, var23, 0.5F, 1, 0, 0, var40);
            this.vertex(var8, var2, var33, var34, var20, var22, 0.5F, 1, 0, 0, var40);
         }
      }
   }

   private void vertex(
      PoseStack.Pose var1, VertexConsumer var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11
   ) {
      var2.vertex(var1, var3, var4, var7)
         .color(255, 255, 255, 255)
         .uv(var5, var6)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(var11)
         .normal(var1, (float)var8, (float)var9, (float)var10)
         .endVertex();
   }
}
