package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;

public class PaintingRenderer extends EntityRenderer<Painting> {
   public PaintingRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(Painting var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translated(var2, var4, var6);
      GlStateManager.rotatef(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindTexture(var1);
      Motive var10 = var1.motive;
      float var11 = 0.0625F;
      GlStateManager.scalef(0.0625F, 0.0625F, 0.0625F);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      PaintingTextureManager var12 = Minecraft.getInstance().getPaintingTextures();
      this.renderPainting(var1, var10.getWidth(), var10.getHeight(), var12.get(var10), var12.getBackSprite());
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Painting var1) {
      return TextureAtlas.LOCATION_PAINTINGS;
   }

   private void renderPainting(Painting var1, int var2, int var3, TextureAtlasSprite var4, TextureAtlasSprite var5) {
      float var6 = (float)(-var2) / 2.0F;
      float var7 = (float)(-var3) / 2.0F;
      float var8 = 0.5F;
      float var9 = var5.getU0();
      float var10 = var5.getU1();
      float var11 = var5.getV0();
      float var12 = var5.getV1();
      float var13 = var5.getU0();
      float var14 = var5.getU1();
      float var15 = var5.getV0();
      float var16 = var5.getV(1.0D);
      float var17 = var5.getU0();
      float var18 = var5.getU(1.0D);
      float var19 = var5.getV0();
      float var20 = var5.getV1();
      int var21 = var2 / 16;
      int var22 = var3 / 16;
      double var23 = 16.0D / (double)var21;
      double var25 = 16.0D / (double)var22;

      for(int var27 = 0; var27 < var21; ++var27) {
         for(int var28 = 0; var28 < var22; ++var28) {
            float var29 = var6 + (float)((var27 + 1) * 16);
            float var30 = var6 + (float)(var27 * 16);
            float var31 = var7 + (float)((var28 + 1) * 16);
            float var32 = var7 + (float)(var28 * 16);
            this.setBrightness(var1, (var29 + var30) / 2.0F, (var31 + var32) / 2.0F);
            float var33 = var4.getU(var23 * (double)(var21 - var27));
            float var34 = var4.getU(var23 * (double)(var21 - (var27 + 1)));
            float var35 = var4.getV(var25 * (double)(var22 - var28));
            float var36 = var4.getV(var25 * (double)(var22 - (var28 + 1)));
            Tesselator var37 = Tesselator.getInstance();
            BufferBuilder var38 = var37.getBuilder();
            var38.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
            var38.vertex((double)var29, (double)var32, -0.5D).uv((double)var34, (double)var35).normal(0.0F, 0.0F, -1.0F).endVertex();
            var38.vertex((double)var30, (double)var32, -0.5D).uv((double)var33, (double)var35).normal(0.0F, 0.0F, -1.0F).endVertex();
            var38.vertex((double)var30, (double)var31, -0.5D).uv((double)var33, (double)var36).normal(0.0F, 0.0F, -1.0F).endVertex();
            var38.vertex((double)var29, (double)var31, -0.5D).uv((double)var34, (double)var36).normal(0.0F, 0.0F, -1.0F).endVertex();
            var38.vertex((double)var29, (double)var31, 0.5D).uv((double)var9, (double)var11).normal(0.0F, 0.0F, 1.0F).endVertex();
            var38.vertex((double)var30, (double)var31, 0.5D).uv((double)var10, (double)var11).normal(0.0F, 0.0F, 1.0F).endVertex();
            var38.vertex((double)var30, (double)var32, 0.5D).uv((double)var10, (double)var12).normal(0.0F, 0.0F, 1.0F).endVertex();
            var38.vertex((double)var29, (double)var32, 0.5D).uv((double)var9, (double)var12).normal(0.0F, 0.0F, 1.0F).endVertex();
            var38.vertex((double)var29, (double)var31, -0.5D).uv((double)var13, (double)var15).normal(0.0F, 1.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var31, -0.5D).uv((double)var14, (double)var15).normal(0.0F, 1.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var31, 0.5D).uv((double)var14, (double)var16).normal(0.0F, 1.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var31, 0.5D).uv((double)var13, (double)var16).normal(0.0F, 1.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var32, 0.5D).uv((double)var13, (double)var15).normal(0.0F, -1.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var32, 0.5D).uv((double)var14, (double)var15).normal(0.0F, -1.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var32, -0.5D).uv((double)var14, (double)var16).normal(0.0F, -1.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var32, -0.5D).uv((double)var13, (double)var16).normal(0.0F, -1.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var31, 0.5D).uv((double)var18, (double)var19).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var32, 0.5D).uv((double)var18, (double)var20).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var32, -0.5D).uv((double)var17, (double)var20).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var29, (double)var31, -0.5D).uv((double)var17, (double)var19).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var31, -0.5D).uv((double)var18, (double)var19).normal(1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var32, -0.5D).uv((double)var18, (double)var20).normal(1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var32, 0.5D).uv((double)var17, (double)var20).normal(1.0F, 0.0F, 0.0F).endVertex();
            var38.vertex((double)var30, (double)var31, 0.5D).uv((double)var17, (double)var19).normal(1.0F, 0.0F, 0.0F).endVertex();
            var37.end();
         }
      }

   }

   private void setBrightness(Painting var1, float var2, float var3) {
      int var4 = Mth.floor(var1.x);
      int var5 = Mth.floor(var1.y + (double)(var3 / 16.0F));
      int var6 = Mth.floor(var1.z);
      Direction var7 = var1.getDirection();
      if (var7 == Direction.NORTH) {
         var4 = Mth.floor(var1.x + (double)(var2 / 16.0F));
      }

      if (var7 == Direction.WEST) {
         var6 = Mth.floor(var1.z - (double)(var2 / 16.0F));
      }

      if (var7 == Direction.SOUTH) {
         var4 = Mth.floor(var1.x - (double)(var2 / 16.0F));
      }

      if (var7 == Direction.EAST) {
         var6 = Mth.floor(var1.z + (double)(var2 / 16.0F));
      }

      int var8 = this.entityRenderDispatcher.level.getLightColor(new BlockPos(var4, var5, var6), 0);
      int var9 = var8 % 65536;
      int var10 = var8 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var9, (float)var10);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
   }
}
