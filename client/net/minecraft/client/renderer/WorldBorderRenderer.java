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
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
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
         float var35;
         double var36;
         double var38;
         float var40;
         if (var17 > var9 - var3) {
            var35 = var34;

            for(var36 = var30; var36 < var32; var35 += 0.5F) {
               var38 = Math.min(1.0, var32 - var36);
               var40 = (float)var38 * 0.5F;
               var29.addVertex((float)(var9 - var17), -var21, (float)(var36 - var19)).setUv(var26 - var35, var26 + var28);
               var29.addVertex((float)(var9 - var17), -var21, (float)(var36 + var38 - var19)).setUv(var26 - (var40 + var35), var26 + var28);
               var29.addVertex((float)(var9 - var17), var21, (float)(var36 + var38 - var19)).setUv(var26 - (var40 + var35), var26 + var27);
               var29.addVertex((float)(var9 - var17), var21, (float)(var36 - var19)).setUv(var26 - var35, var26 + var27);
               ++var36;
            }
         }

         if (var17 < var7 + var3) {
            var35 = var34;

            for(var36 = var30; var36 < var32; var35 += 0.5F) {
               var38 = Math.min(1.0, var32 - var36);
               var40 = (float)var38 * 0.5F;
               var29.addVertex((float)(var7 - var17), -var21, (float)(var36 - var19)).setUv(var26 + var35, var26 + var28);
               var29.addVertex((float)(var7 - var17), -var21, (float)(var36 + var38 - var19)).setUv(var26 + var40 + var35, var26 + var28);
               var29.addVertex((float)(var7 - var17), var21, (float)(var36 + var38 - var19)).setUv(var26 + var40 + var35, var26 + var27);
               var29.addVertex((float)(var7 - var17), var21, (float)(var36 - var19)).setUv(var26 + var35, var26 + var27);
               ++var36;
            }
         }

         var30 = Math.max((double)Mth.floor(var17 - var3), var7);
         var32 = Math.min((double)Mth.ceil(var17 + var3), var9);
         var34 = (float)(Mth.floor(var30) & 1) * 0.5F;
         if (var19 > var13 - var3) {
            var35 = var34;

            for(var36 = var30; var36 < var32; var35 += 0.5F) {
               var38 = Math.min(1.0, var32 - var36);
               var40 = (float)var38 * 0.5F;
               var29.addVertex((float)(var36 - var17), -var21, (float)(var13 - var19)).setUv(var26 + var35, var26 + var28);
               var29.addVertex((float)(var36 + var38 - var17), -var21, (float)(var13 - var19)).setUv(var26 + var40 + var35, var26 + var28);
               var29.addVertex((float)(var36 + var38 - var17), var21, (float)(var13 - var19)).setUv(var26 + var40 + var35, var26 + var27);
               var29.addVertex((float)(var36 - var17), var21, (float)(var13 - var19)).setUv(var26 + var35, var26 + var27);
               ++var36;
            }
         }

         if (var19 < var11 + var3) {
            var35 = var34;

            for(var36 = var30; var36 < var32; var35 += 0.5F) {
               var38 = Math.min(1.0, var32 - var36);
               var40 = (float)var38 * 0.5F;
               var29.addVertex((float)(var36 - var17), -var21, (float)(var11 - var19)).setUv(var26 - var35, var26 + var28);
               var29.addVertex((float)(var36 + var38 - var17), -var21, (float)(var11 - var19)).setUv(var26 - (var40 + var35), var26 + var28);
               var29.addVertex((float)(var36 + var38 - var17), var21, (float)(var11 - var19)).setUv(var26 - (var40 + var35), var26 + var27);
               var29.addVertex((float)(var36 - var17), var21, (float)(var11 - var19)).setUv(var26 - var35, var26 + var27);
               ++var36;
            }
         }

         MeshData var41 = var29.build();
         if (var41 != null) {
            BufferUploader.drawWithShader(var41);
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
