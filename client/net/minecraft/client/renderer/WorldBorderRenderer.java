package net.minecraft.client.renderer;

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
   public static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");

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
         RenderType var22 = RenderType.worldBorder(Minecraft.useShaderTransparency());
         var22.setupRenderState();
         int var23 = var1.getStatus().getColor();
         float var24 = (float)ARGB.red(var23) / 255.0F;
         float var25 = (float)ARGB.green(var23) / 255.0F;
         float var26 = (float)ARGB.blue(var23) / 255.0F;
         RenderSystem.setShaderColor(var24, var25, var26, (float)var15);
         float var27 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var28 = (float)(-Mth.frac(var2.y * 0.5));
         float var29 = var28 + var21;
         BufferBuilder var30 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         double var31 = Math.max((double)Mth.floor(var19 - var3), var11);
         double var33 = Math.min((double)Mth.ceil(var19 + var3), var13);
         float var35 = (float)(Mth.floor(var31) & 1) * 0.5F;
         if (var17 > var9 - var3) {
            float var36 = var35;

            for(double var37 = var31; var37 < var33; var36 += 0.5F) {
               double var39 = Math.min(1.0, var33 - var37);
               float var41 = (float)var39 * 0.5F;
               var30.addVertex((float)(var9 - var17), -var21, (float)(var37 - var19)).setUv(var27 - var36, var27 + var29);
               var30.addVertex((float)(var9 - var17), -var21, (float)(var37 + var39 - var19)).setUv(var27 - (var41 + var36), var27 + var29);
               var30.addVertex((float)(var9 - var17), var21, (float)(var37 + var39 - var19)).setUv(var27 - (var41 + var36), var27 + var28);
               var30.addVertex((float)(var9 - var17), var21, (float)(var37 - var19)).setUv(var27 - var36, var27 + var28);
               ++var37;
            }
         }

         if (var17 < var7 + var3) {
            float var47 = var35;

            for(double var51 = var31; var51 < var33; var47 += 0.5F) {
               double var54 = Math.min(1.0, var33 - var51);
               float var57 = (float)var54 * 0.5F;
               var30.addVertex((float)(var7 - var17), -var21, (float)(var51 - var19)).setUv(var27 + var47, var27 + var29);
               var30.addVertex((float)(var7 - var17), -var21, (float)(var51 + var54 - var19)).setUv(var27 + var57 + var47, var27 + var29);
               var30.addVertex((float)(var7 - var17), var21, (float)(var51 + var54 - var19)).setUv(var27 + var57 + var47, var27 + var28);
               var30.addVertex((float)(var7 - var17), var21, (float)(var51 - var19)).setUv(var27 + var47, var27 + var28);
               ++var51;
            }
         }

         var31 = Math.max((double)Mth.floor(var17 - var3), var7);
         var33 = Math.min((double)Mth.ceil(var17 + var3), var9);
         var35 = (float)(Mth.floor(var31) & 1) * 0.5F;
         if (var19 > var13 - var3) {
            float var48 = var35;

            for(double var52 = var31; var52 < var33; var48 += 0.5F) {
               double var55 = Math.min(1.0, var33 - var52);
               float var58 = (float)var55 * 0.5F;
               var30.addVertex((float)(var52 - var17), -var21, (float)(var13 - var19)).setUv(var27 + var48, var27 + var29);
               var30.addVertex((float)(var52 + var55 - var17), -var21, (float)(var13 - var19)).setUv(var27 + var58 + var48, var27 + var29);
               var30.addVertex((float)(var52 + var55 - var17), var21, (float)(var13 - var19)).setUv(var27 + var58 + var48, var27 + var28);
               var30.addVertex((float)(var52 - var17), var21, (float)(var13 - var19)).setUv(var27 + var48, var27 + var28);
               ++var52;
            }
         }

         if (var19 < var11 + var3) {
            float var49 = var35;

            for(double var53 = var31; var53 < var33; var49 += 0.5F) {
               double var56 = Math.min(1.0, var33 - var53);
               float var59 = (float)var56 * 0.5F;
               var30.addVertex((float)(var53 - var17), -var21, (float)(var11 - var19)).setUv(var27 - var49, var27 + var29);
               var30.addVertex((float)(var53 + var56 - var17), -var21, (float)(var11 - var19)).setUv(var27 - (var59 + var49), var27 + var29);
               var30.addVertex((float)(var53 + var56 - var17), var21, (float)(var11 - var19)).setUv(var27 - (var59 + var49), var27 + var28);
               var30.addVertex((float)(var53 - var17), var21, (float)(var11 - var19)).setUv(var27 - var49, var27 + var28);
               ++var53;
            }
         }

         MeshData var50 = var30.build();
         if (var50 != null) {
            BufferUploader.drawWithShader(var50);
         }

         var22.clearRenderState();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
