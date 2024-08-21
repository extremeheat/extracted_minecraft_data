package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

public class WorldBorderRenderer {
   private static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");

   public WorldBorderRenderer() {
      super();
   }

   public void render(WorldBorder var1, Vec3 var2, double var3, double var5) {
      double var7 = var1.getMinX();
      double var9 = var1.getMaxX();
      double var11 = var1.getMinZ();
      double var13 = var1.getMaxZ();
      if (!(var2.x < var9 - var3) || !(var2.x > var7 + var3) || !(var2.z < var13 - var3) || !(var2.z > var11 + var3)) {
         double var15 = 1.0 - var1.getDistanceToBorder(var2.x, var2.z) / var3;
         var15 = Math.pow(var15, 4.0);
         var15 = Mth.clamp(var15, 0.0, 1.0);
         double var17 = var2.x;
         double var19 = var2.z;
         float var21 = (float)var5;
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
         );
         RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         int var22 = var1.getStatus().getColor();
         float var23 = (float)ARGB.red(var22) / 255.0F;
         float var24 = (float)ARGB.green(var22) / 255.0F;
         float var25 = (float)ARGB.blue(var22) / 255.0F;
         RenderSystem.setShaderColor(var23, var24, var25, (float)var15);
         RenderSystem.setShader(CoreShaders.POSITION_TEX);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         RenderSystem.disableCull();
         float var26 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var27 = (float)(-Mth.frac(var2.y * 0.5));
         float var28 = var27 + var21;
         BufferBuilder var29 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         double var30 = Math.max((double)Mth.floor(var19 - var3), var11);
         double var32 = Math.min((double)Mth.ceil(var19 + var3), var13);
         float var34 = (float)(Mth.floor(var30) & 1) * 0.5F;
         if (var17 > var9 - var3) {
            float var35 = var34;

            for (double var36 = var30; var36 < var32; var35 += 0.5F) {
               double var38 = Math.min(1.0, var32 - var36);
               float var40 = (float)var38 * 0.5F;
               var29.addVertex((float)(var9 - var17), -var21, (float)(var36 - var19)).setUv(var26 - var35, var26 + var28);
               var29.addVertex((float)(var9 - var17), -var21, (float)(var36 + var38 - var19)).setUv(var26 - (var40 + var35), var26 + var28);
               var29.addVertex((float)(var9 - var17), var21, (float)(var36 + var38 - var19)).setUv(var26 - (var40 + var35), var26 + var27);
               var29.addVertex((float)(var9 - var17), var21, (float)(var36 - var19)).setUv(var26 - var35, var26 + var27);
               var36++;
            }
         }

         if (var17 < var7 + var3) {
            float var46 = var34;

            for (double var50 = var30; var50 < var32; var46 += 0.5F) {
               double var53 = Math.min(1.0, var32 - var50);
               float var56 = (float)var53 * 0.5F;
               var29.addVertex((float)(var7 - var17), -var21, (float)(var50 - var19)).setUv(var26 + var46, var26 + var28);
               var29.addVertex((float)(var7 - var17), -var21, (float)(var50 + var53 - var19)).setUv(var26 + var56 + var46, var26 + var28);
               var29.addVertex((float)(var7 - var17), var21, (float)(var50 + var53 - var19)).setUv(var26 + var56 + var46, var26 + var27);
               var29.addVertex((float)(var7 - var17), var21, (float)(var50 - var19)).setUv(var26 + var46, var26 + var27);
               var50++;
            }
         }

         var30 = Math.max((double)Mth.floor(var17 - var3), var7);
         var32 = Math.min((double)Mth.ceil(var17 + var3), var9);
         var34 = (float)(Mth.floor(var30) & 1) * 0.5F;
         if (var19 > var13 - var3) {
            float var47 = var34;

            for (double var51 = var30; var51 < var32; var47 += 0.5F) {
               double var54 = Math.min(1.0, var32 - var51);
               float var57 = (float)var54 * 0.5F;
               var29.addVertex((float)(var51 - var17), -var21, (float)(var13 - var19)).setUv(var26 + var47, var26 + var28);
               var29.addVertex((float)(var51 + var54 - var17), -var21, (float)(var13 - var19)).setUv(var26 + var57 + var47, var26 + var28);
               var29.addVertex((float)(var51 + var54 - var17), var21, (float)(var13 - var19)).setUv(var26 + var57 + var47, var26 + var27);
               var29.addVertex((float)(var51 - var17), var21, (float)(var13 - var19)).setUv(var26 + var47, var26 + var27);
               var51++;
            }
         }

         if (var19 < var11 + var3) {
            float var48 = var34;

            for (double var52 = var30; var52 < var32; var48 += 0.5F) {
               double var55 = Math.min(1.0, var32 - var52);
               float var58 = (float)var55 * 0.5F;
               var29.addVertex((float)(var52 - var17), -var21, (float)(var11 - var19)).setUv(var26 - var48, var26 + var28);
               var29.addVertex((float)(var52 + var55 - var17), -var21, (float)(var11 - var19)).setUv(var26 - (var58 + var48), var26 + var28);
               var29.addVertex((float)(var52 + var55 - var17), var21, (float)(var11 - var19)).setUv(var26 - (var58 + var48), var26 + var27);
               var29.addVertex((float)(var52 - var17), var21, (float)(var11 - var19)).setUv(var26 - var48, var26 + var27);
               var52++;
            }
         }

         MeshData var49 = var29.build();
         if (var49 != null) {
            BufferUploader.drawWithShader(var49);
         }

         RenderSystem.enableCull();
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.depthMask(true);
      }
   }
}
