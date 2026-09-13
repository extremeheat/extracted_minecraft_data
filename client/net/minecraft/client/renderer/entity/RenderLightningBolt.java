package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLightningBolt extends Render {
   public RenderLightningBolt() {
      super();
   }

   public void func_76986_a(EntityLightningBolt var1, double var2, double var4, double var6, float var8, float var9) {
      Tessellator var10 = Tessellator.field_78398_a;
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 1);
      double[] var11 = new double[8];
      double[] var12 = new double[8];
      double var13 = 0.0;
      double var15 = 0.0;
      Random var17 = new Random(var1.field_70264_a);

      for(int var18 = 7; var18 >= 0; --var18) {
         var11[var18] = var13;
         var12[var18] = var15;
         var13 += (double)(var17.nextInt(11) - 5);
         var15 += (double)(var17.nextInt(11) - 5);
      }

      for(int var45 = 0; var45 < 4; ++var45) {
         Random var46 = new Random(var1.field_70264_a);

         for(int var19 = 0; var19 < 3; ++var19) {
            int var20 = 7;
            int var21 = 0;
            if (var19 > 0) {
               var20 = 7 - var19;
            }

            if (var19 > 0) {
               var21 = var20 - 2;
            }

            double var22 = var11[var20] - var13;
            double var24 = var12[var20] - var15;

            for(int var26 = var20; var26 >= var21; --var26) {
               double var27 = var22;
               double var29 = var24;
               if (var19 == 0) {
                  var22 += (double)(var46.nextInt(11) - 5);
                  var24 += (double)(var46.nextInt(11) - 5);
               } else {
                  var22 += (double)(var46.nextInt(31) - 15);
                  var24 += (double)(var46.nextInt(31) - 15);
               }

               var10.func_78371_b(5);
               float var31 = 0.5F;
               var10.func_78369_a(0.9F * var31, 0.9F * var31, 1.0F * var31, 0.3F);
               double var32 = 0.1 + (double)var45 * 0.2;
               if (var19 == 0) {
                  var32 *= (double)var26 * 0.1 + 1.0;
               }

               double var34 = 0.1 + (double)var45 * 0.2;
               if (var19 == 0) {
                  var34 *= (double)(var26 - 1) * 0.1 + 1.0;
               }

               for(int var36 = 0; var36 < 5; ++var36) {
                  double var37 = var2 + 0.5 - var32;
                  double var39 = var6 + 0.5 - var32;
                  if (var36 == 1 || var36 == 2) {
                     var37 += var32 * 2.0;
                  }

                  if (var36 == 2 || var36 == 3) {
                     var39 += var32 * 2.0;
                  }

                  double var41 = var2 + 0.5 - var34;
                  double var43 = var6 + 0.5 - var34;
                  if (var36 == 1 || var36 == 2) {
                     var41 += var34 * 2.0;
                  }

                  if (var36 == 2 || var36 == 3) {
                     var43 += var34 * 2.0;
                  }

                  var10.func_78377_a(var41 + var22, var4 + (double)(var26 * 16), var43 + var24);
                  var10.func_78377_a(var37 + var27, var4 + (double)((var26 + 1) * 16), var39 + var29);
               }

               var10.func_78381_a();
            }
         }
      }

      GL11.glDisable(3042);
      GL11.glEnable(2896);
      GL11.glEnable(3553);
   }

   protected ResourceLocation func_110775_a(EntityLightningBolt var1) {
      return null;
   }
}
