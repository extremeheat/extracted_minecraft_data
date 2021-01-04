package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.global.LightningBolt;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt> {
   public LightningBoltRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(LightningBolt var1, double var2, double var4, double var6, float var8, float var9) {
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
      double[] var12 = new double[8];
      double[] var13 = new double[8];
      double var14 = 0.0D;
      double var16 = 0.0D;
      Random var18 = new Random(var1.seed);

      for(int var19 = 7; var19 >= 0; --var19) {
         var12[var19] = var14;
         var13[var19] = var16;
         var14 += (double)(var18.nextInt(11) - 5);
         var16 += (double)(var18.nextInt(11) - 5);
      }

      for(int var49 = 0; var49 < 4; ++var49) {
         Random var50 = new Random(var1.seed);

         for(int var20 = 0; var20 < 3; ++var20) {
            int var21 = 7;
            int var22 = 0;
            if (var20 > 0) {
               var21 = 7 - var20;
            }

            if (var20 > 0) {
               var22 = var21 - 2;
            }

            double var23 = var12[var21] - var14;
            double var25 = var13[var21] - var16;

            for(int var27 = var21; var27 >= var22; --var27) {
               double var28 = var23;
               double var30 = var25;
               if (var20 == 0) {
                  var23 += (double)(var50.nextInt(11) - 5);
                  var25 += (double)(var50.nextInt(11) - 5);
               } else {
                  var23 += (double)(var50.nextInt(31) - 15);
                  var25 += (double)(var50.nextInt(31) - 15);
               }

               var11.begin(5, DefaultVertexFormat.POSITION_COLOR);
               float var32 = 0.5F;
               float var33 = 0.45F;
               float var34 = 0.45F;
               float var35 = 0.5F;
               double var36 = 0.1D + (double)var49 * 0.2D;
               if (var20 == 0) {
                  var36 *= (double)var27 * 0.1D + 1.0D;
               }

               double var38 = 0.1D + (double)var49 * 0.2D;
               if (var20 == 0) {
                  var38 *= (double)(var27 - 1) * 0.1D + 1.0D;
               }

               for(int var40 = 0; var40 < 5; ++var40) {
                  double var41 = var2 - var36;
                  double var43 = var6 - var36;
                  if (var40 == 1 || var40 == 2) {
                     var41 += var36 * 2.0D;
                  }

                  if (var40 == 2 || var40 == 3) {
                     var43 += var36 * 2.0D;
                  }

                  double var45 = var2 - var38;
                  double var47 = var6 - var38;
                  if (var40 == 1 || var40 == 2) {
                     var45 += var38 * 2.0D;
                  }

                  if (var40 == 2 || var40 == 3) {
                     var47 += var38 * 2.0D;
                  }

                  var11.vertex(var45 + var23, var4 + (double)(var27 * 16), var47 + var25).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                  var11.vertex(var41 + var28, var4 + (double)((var27 + 1) * 16), var43 + var30).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
               }

               var10.end();
            }
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture();
   }

   @Nullable
   protected ResourceLocation getTextureLocation(LightningBolt var1) {
      return null;
   }
}
