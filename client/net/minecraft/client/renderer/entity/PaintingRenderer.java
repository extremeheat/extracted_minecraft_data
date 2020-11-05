package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;

public class PaintingRenderer extends EntityRenderer<Painting> {
   public PaintingRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(Painting var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.mulPose(Vector3f.YP.rotationDegrees(180.0F - var2));
      Motive var7 = var1.motive;
      float var8 = 0.0625F;
      var4.scale(0.0625F, 0.0625F, 0.0625F);
      VertexConsumer var9 = var5.getBuffer(RenderType.entitySolid(this.getTextureLocation(var1)));
      PaintingTextureManager var10 = Minecraft.getInstance().getPaintingTextures();
      this.renderPainting(var4, var9, var1, var7.getWidth(), var7.getHeight(), var10.get(var7), var10.getBackSprite());
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Painting var1) {
      return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location();
   }

   private void renderPainting(PoseStack var1, VertexConsumer var2, Painting var3, int var4, int var5, TextureAtlasSprite var6, TextureAtlasSprite var7) {
      PoseStack.Pose var8 = var1.last();
      Matrix4f var9 = var8.pose();
      Matrix3f var10 = var8.normal();
      float var11 = (float)(-var4) / 2.0F;
      float var12 = (float)(-var5) / 2.0F;
      float var13 = 0.5F;
      float var14 = var7.getU0();
      float var15 = var7.getU1();
      float var16 = var7.getV0();
      float var17 = var7.getV1();
      float var18 = var7.getU0();
      float var19 = var7.getU1();
      float var20 = var7.getV0();
      float var21 = var7.getV(1.0D);
      float var22 = var7.getU0();
      float var23 = var7.getU(1.0D);
      float var24 = var7.getV0();
      float var25 = var7.getV1();
      int var26 = var4 / 16;
      int var27 = var5 / 16;
      double var28 = 16.0D / (double)var26;
      double var30 = 16.0D / (double)var27;

      for(int var32 = 0; var32 < var26; ++var32) {
         for(int var33 = 0; var33 < var27; ++var33) {
            float var34 = var11 + (float)((var32 + 1) * 16);
            float var35 = var11 + (float)(var32 * 16);
            float var36 = var12 + (float)((var33 + 1) * 16);
            float var37 = var12 + (float)(var33 * 16);
            int var38 = var3.getBlockX();
            int var39 = Mth.floor(var3.getY() + (double)((var36 + var37) / 2.0F / 16.0F));
            int var40 = var3.getBlockZ();
            Direction var41 = var3.getDirection();
            if (var41 == Direction.NORTH) {
               var38 = Mth.floor(var3.getX() + (double)((var34 + var35) / 2.0F / 16.0F));
            }

            if (var41 == Direction.WEST) {
               var40 = Mth.floor(var3.getZ() - (double)((var34 + var35) / 2.0F / 16.0F));
            }

            if (var41 == Direction.SOUTH) {
               var38 = Mth.floor(var3.getX() - (double)((var34 + var35) / 2.0F / 16.0F));
            }

            if (var41 == Direction.EAST) {
               var40 = Mth.floor(var3.getZ() + (double)((var34 + var35) / 2.0F / 16.0F));
            }

            int var42 = LevelRenderer.getLightColor(var3.level, new BlockPos(var38, var39, var40));
            float var43 = var6.getU(var28 * (double)(var26 - var32));
            float var44 = var6.getU(var28 * (double)(var26 - (var32 + 1)));
            float var45 = var6.getV(var30 * (double)(var27 - var33));
            float var46 = var6.getV(var30 * (double)(var27 - (var33 + 1)));
            this.vertex(var9, var10, var2, var34, var37, var44, var45, -0.5F, 0, 0, -1, var42);
            this.vertex(var9, var10, var2, var35, var37, var43, var45, -0.5F, 0, 0, -1, var42);
            this.vertex(var9, var10, var2, var35, var36, var43, var46, -0.5F, 0, 0, -1, var42);
            this.vertex(var9, var10, var2, var34, var36, var44, var46, -0.5F, 0, 0, -1, var42);
            this.vertex(var9, var10, var2, var34, var36, var14, var16, 0.5F, 0, 0, 1, var42);
            this.vertex(var9, var10, var2, var35, var36, var15, var16, 0.5F, 0, 0, 1, var42);
            this.vertex(var9, var10, var2, var35, var37, var15, var17, 0.5F, 0, 0, 1, var42);
            this.vertex(var9, var10, var2, var34, var37, var14, var17, 0.5F, 0, 0, 1, var42);
            this.vertex(var9, var10, var2, var34, var36, var18, var20, -0.5F, 0, 1, 0, var42);
            this.vertex(var9, var10, var2, var35, var36, var19, var20, -0.5F, 0, 1, 0, var42);
            this.vertex(var9, var10, var2, var35, var36, var19, var21, 0.5F, 0, 1, 0, var42);
            this.vertex(var9, var10, var2, var34, var36, var18, var21, 0.5F, 0, 1, 0, var42);
            this.vertex(var9, var10, var2, var34, var37, var18, var20, 0.5F, 0, -1, 0, var42);
            this.vertex(var9, var10, var2, var35, var37, var19, var20, 0.5F, 0, -1, 0, var42);
            this.vertex(var9, var10, var2, var35, var37, var19, var21, -0.5F, 0, -1, 0, var42);
            this.vertex(var9, var10, var2, var34, var37, var18, var21, -0.5F, 0, -1, 0, var42);
            this.vertex(var9, var10, var2, var34, var36, var23, var24, 0.5F, -1, 0, 0, var42);
            this.vertex(var9, var10, var2, var34, var37, var23, var25, 0.5F, -1, 0, 0, var42);
            this.vertex(var9, var10, var2, var34, var37, var22, var25, -0.5F, -1, 0, 0, var42);
            this.vertex(var9, var10, var2, var34, var36, var22, var24, -0.5F, -1, 0, 0, var42);
            this.vertex(var9, var10, var2, var35, var36, var23, var24, -0.5F, 1, 0, 0, var42);
            this.vertex(var9, var10, var2, var35, var37, var23, var25, -0.5F, 1, 0, 0, var42);
            this.vertex(var9, var10, var2, var35, var37, var22, var25, 0.5F, 1, 0, 0, var42);
            this.vertex(var9, var10, var2, var35, var36, var22, var24, 0.5F, 1, 0, 0, var42);
         }
      }

   }

   private void vertex(Matrix4f var1, Matrix3f var2, VertexConsumer var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10, int var11, int var12) {
      var3.vertex(var1, var4, var5, var8).color(255, 255, 255, 255).uv(var6, var7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var12).normal(var2, (float)var9, (float)var10, (float)var11).endVertex();
   }
}
