package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;

public class PaintingRenderer extends EntityRenderer<Painting, PaintingRenderState> {
   public PaintingRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(PaintingRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      PaintingVariant var5 = var1.variant;
      if (var5 != null) {
         var2.pushPose();
         var2.mulPose(Axis.YP.rotationDegrees((float)(180 - var1.direction.get2DDataValue() * 90)));
         PaintingTextureManager var6 = Minecraft.getInstance().getPaintingTextures();
         TextureAtlasSprite var7 = var6.getBackSprite();
         VertexConsumer var8 = var3.getBuffer(RenderType.entitySolidZOffsetForward(var7.atlasLocation()));
         this.renderPainting(var2, var8, var1.lightCoords, var5.width(), var5.height(), var6.get(var5), var7);
         var2.popPose();
         super.render(var1, var2, var3, var4);
      }
   }

   public PaintingRenderState createRenderState() {
      return new PaintingRenderState();
   }

   public void extractRenderState(Painting var1, PaintingRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      Direction var4 = var1.getDirection();
      PaintingVariant var5 = (PaintingVariant)var1.getVariant().value();
      var2.direction = var4;
      var2.variant = var5;
      int var6 = var5.width();
      int var7 = var5.height();
      if (var2.lightCoords.length != var6 * var7) {
         var2.lightCoords = new int[var6 * var7];
      }

      float var8 = (float)(-var6) / 2.0F;
      float var9 = (float)(-var7) / 2.0F;
      Level var10 = var1.level();

      for(int var11 = 0; var11 < var7; ++var11) {
         for(int var12 = 0; var12 < var6; ++var12) {
            float var13 = (float)var12 + var8 + 0.5F;
            float var14 = (float)var11 + var9 + 0.5F;
            int var15 = var1.getBlockX();
            int var16 = Mth.floor(var1.getY() + (double)var14);
            int var17 = var1.getBlockZ();
            switch (var4) {
               case NORTH -> var15 = Mth.floor(var1.getX() + (double)var13);
               case WEST -> var17 = Mth.floor(var1.getZ() - (double)var13);
               case SOUTH -> var15 = Mth.floor(var1.getX() - (double)var13);
               case EAST -> var17 = Mth.floor(var1.getZ() + (double)var13);
            }

            var2.lightCoords[var12 + var11 * var6] = LevelRenderer.getLightColor(var10, new BlockPos(var15, var16, var17));
         }
      }

   }

   private void renderPainting(PoseStack var1, VertexConsumer var2, int[] var3, int var4, int var5, TextureAtlasSprite var6, TextureAtlasSprite var7) {
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
            int var34 = var3[var28 + var29 * var4];
            float var35 = var6.getU((float)(var24 * (double)(var4 - var28)));
            float var36 = var6.getU((float)(var24 * (double)(var4 - (var28 + 1))));
            float var37 = var6.getV((float)(var26 * (double)(var5 - var29)));
            float var38 = var6.getV((float)(var26 * (double)(var5 - (var29 + 1))));
            this.vertex(var8, var2, var30, var33, var36, var37, -0.03125F, 0, 0, -1, var34);
            this.vertex(var8, var2, var31, var33, var35, var37, -0.03125F, 0, 0, -1, var34);
            this.vertex(var8, var2, var31, var32, var35, var38, -0.03125F, 0, 0, -1, var34);
            this.vertex(var8, var2, var30, var32, var36, var38, -0.03125F, 0, 0, -1, var34);
            this.vertex(var8, var2, var30, var32, var13, var14, 0.03125F, 0, 0, 1, var34);
            this.vertex(var8, var2, var31, var32, var12, var14, 0.03125F, 0, 0, 1, var34);
            this.vertex(var8, var2, var31, var33, var12, var15, 0.03125F, 0, 0, 1, var34);
            this.vertex(var8, var2, var30, var33, var13, var15, 0.03125F, 0, 0, 1, var34);
            this.vertex(var8, var2, var30, var32, var16, var18, -0.03125F, 0, 1, 0, var34);
            this.vertex(var8, var2, var31, var32, var17, var18, -0.03125F, 0, 1, 0, var34);
            this.vertex(var8, var2, var31, var32, var17, var19, 0.03125F, 0, 1, 0, var34);
            this.vertex(var8, var2, var30, var32, var16, var19, 0.03125F, 0, 1, 0, var34);
            this.vertex(var8, var2, var30, var33, var16, var18, 0.03125F, 0, -1, 0, var34);
            this.vertex(var8, var2, var31, var33, var17, var18, 0.03125F, 0, -1, 0, var34);
            this.vertex(var8, var2, var31, var33, var17, var19, -0.03125F, 0, -1, 0, var34);
            this.vertex(var8, var2, var30, var33, var16, var19, -0.03125F, 0, -1, 0, var34);
            this.vertex(var8, var2, var30, var32, var21, var22, 0.03125F, -1, 0, 0, var34);
            this.vertex(var8, var2, var30, var33, var21, var23, 0.03125F, -1, 0, 0, var34);
            this.vertex(var8, var2, var30, var33, var20, var23, -0.03125F, -1, 0, 0, var34);
            this.vertex(var8, var2, var30, var32, var20, var22, -0.03125F, -1, 0, 0, var34);
            this.vertex(var8, var2, var31, var32, var21, var22, -0.03125F, 1, 0, 0, var34);
            this.vertex(var8, var2, var31, var33, var21, var23, -0.03125F, 1, 0, 0, var34);
            this.vertex(var8, var2, var31, var33, var20, var23, 0.03125F, 1, 0, 0, var34);
            this.vertex(var8, var2, var31, var32, var20, var22, 0.03125F, 1, 0, 0, var34);
         }
      }

   }

   private void vertex(PoseStack.Pose var1, VertexConsumer var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11) {
      var2.addVertex(var1, var3, var4, var7).setColor(-1).setUv(var5, var6).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var11).setNormal(var1, (float)var8, (float)var9, (float)var10);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
