package net.minecraft.client.renderer.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;

public class RenderLightningBolt extends Render<EntityLightningBolt> {
   public RenderLightningBolt(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityLightningBolt var1, double var2, double var4, double var6, float var8, float var9) {
      Tessellator var10 = Tessellator.func_178181_a();
      BufferBuilder var11 = var10.func_178180_c();
      GlStateManager.func_179090_x();
      GlStateManager.func_179140_f();
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
      double[] var12 = new double[8];
      double[] var13 = new double[8];
      double var14 = 0.0D;
      double var16 = 0.0D;
      Random var18 = new Random(var1.field_70264_a);

      for(int var19 = 7; var19 >= 0; --var19) {
         var12[var19] = var14;
         var13[var19] = var16;
         var14 += (double)(var18.nextInt(11) - 5);
         var16 += (double)(var18.nextInt(11) - 5);
      }

      for(int var49 = 0; var49 < 4; ++var49) {
         Random var50 = new Random(var1.field_70264_a);

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

               var11.func_181668_a(5, DefaultVertexFormats.field_181706_f);
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

                  var11.func_181662_b(var45 + var23, var4 + (double)(var27 * 16), var47 + var25).func_181666_a(0.45F, 0.45F, 0.5F, 0.3F).func_181675_d();
                  var11.func_181662_b(var41 + var28, var4 + (double)((var27 + 1) * 16), var43 + var30).func_181666_a(0.45F, 0.45F, 0.5F, 0.3F).func_181675_d();
               }

               var10.func_78381_a();
            }
         }
      }

      GlStateManager.func_179084_k();
      GlStateManager.func_179145_e();
      GlStateManager.func_179098_w();
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityLightningBolt var1) {
      return null;
   }
}
