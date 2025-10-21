package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.ARGB;

public class MipmapGenerator {
   private static final float ALPHA_CUTOFF = 0.2F;
   private static final float STRICT_ALPHA_CUTOFF = 0.6F;
   private static final int ALPHA_CUTOUT_CUTOFF = 96;

   private MipmapGenerator() {
      super();
   }

   public static NativeImage[] generateMipLevels(NativeImage[] var0, int var1, MipmapStrategy var2) {
      if (var1 + 1 <= var0.length) {
         return var0;
      } else {
         NativeImage[] var3 = new NativeImage[var1 + 1];
         if (var2 == MipmapStrategy.AUTO) {
            var2 = hasTransparentPixel(var0[0]) ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
         }

         if (var0.length == 1 && (var2 == MipmapStrategy.CUTOUT || var2 == MipmapStrategy.STRICT_CUTOUT)) {
            TextureUtil.solidify(var0[0]);
         }

         var3[0] = var0[0];

         for(int var4 = 1; var4 <= var1; ++var4) {
            if (var4 < var0.length) {
               var3[var4] = var0[var4];
            } else {
               NativeImage var5 = var3[var4 - 1];
               NativeImage var6 = new NativeImage(var5.getWidth() >> 1, var5.getHeight() >> 1, false);
               int var7 = var6.getWidth();
               int var8 = var6.getHeight();

               for(int var9 = 0; var9 < var7; ++var9) {
                  for(int var10 = 0; var10 < var8; ++var10) {
                     int var11;
                     if (var2 != MipmapStrategy.MEAN && var2 != MipmapStrategy.DARK_CUTOUT) {
                        var11 = alphaBlend(var9, var10, var3[0], var4, var2);
                     } else {
                        int var12 = var5.getPixel(var9 * 2 + 0, var10 * 2 + 0);
                        int var13 = var5.getPixel(var9 * 2 + 1, var10 * 2 + 0);
                        int var14 = var5.getPixel(var9 * 2 + 0, var10 * 2 + 1);
                        int var15 = var5.getPixel(var9 * 2 + 1, var10 * 2 + 1);
                        if (var2 == MipmapStrategy.DARK_CUTOUT) {
                           var11 = darkenedAlphaBlend(var12, var13, var14, var15);
                        } else {
                           var11 = ARGB.meanLinear(var12, var13, var14, var15);
                        }
                     }

                     var6.setPixel(var9, var10, var11);
                  }
               }

               var3[var4] = var6;
            }
         }

         return var3;
      }
   }

   private static boolean hasTransparentPixel(NativeImage var0) {
      for(int var1 = 0; var1 < var0.getWidth(); ++var1) {
         for(int var2 = 0; var2 < var0.getHeight(); ++var2) {
            if (ARGB.alpha(var0.getPixel(var1, var2)) == 0) {
               return true;
            }
         }
      }

      return false;
   }

   private static int darkenedAlphaBlend(int var0, int var1, int var2, int var3) {
      float var4 = 0.0F;
      float var5 = 0.0F;
      float var6 = 0.0F;
      float var7 = 0.0F;
      if (ARGB.alpha(var0) != 0) {
         var4 += ARGB.srgbToLinearChannel(ARGB.alpha(var0));
         var5 += ARGB.srgbToLinearChannel(ARGB.red(var0));
         var6 += ARGB.srgbToLinearChannel(ARGB.green(var0));
         var7 += ARGB.srgbToLinearChannel(ARGB.blue(var0));
      }

      if (ARGB.alpha(var1) != 0) {
         var4 += ARGB.srgbToLinearChannel(ARGB.alpha(var1));
         var5 += ARGB.srgbToLinearChannel(ARGB.red(var1));
         var6 += ARGB.srgbToLinearChannel(ARGB.green(var1));
         var7 += ARGB.srgbToLinearChannel(ARGB.blue(var1));
      }

      if (ARGB.alpha(var2) != 0) {
         var4 += ARGB.srgbToLinearChannel(ARGB.alpha(var2));
         var5 += ARGB.srgbToLinearChannel(ARGB.red(var2));
         var6 += ARGB.srgbToLinearChannel(ARGB.green(var2));
         var7 += ARGB.srgbToLinearChannel(ARGB.blue(var2));
      }

      if (ARGB.alpha(var3) != 0) {
         var4 += ARGB.srgbToLinearChannel(ARGB.alpha(var3));
         var5 += ARGB.srgbToLinearChannel(ARGB.red(var3));
         var6 += ARGB.srgbToLinearChannel(ARGB.green(var3));
         var7 += ARGB.srgbToLinearChannel(ARGB.blue(var3));
      }

      var4 /= 4.0F;
      var5 /= 4.0F;
      var6 /= 4.0F;
      var7 /= 4.0F;
      int var8 = ARGB.linearToSrgbChannel(var4);
      if (var8 < 96) {
         var8 = 0;
      }

      return ARGB.color(var8, ARGB.linearToSrgbChannel(var5), ARGB.linearToSrgbChannel(var6), ARGB.linearToSrgbChannel(var7));
   }

   private static int alphaBlend(int var0, int var1, NativeImage var2, int var3, MipmapStrategy var4) {
      float[] var5 = new float[7];
      int var6 = 1 << var3;

      for(int var7 = var0 * var6; var7 < var0 * var6 + var6; ++var7) {
         for(int var8 = var1 * var6; var8 < var1 * var6 + var6; ++var8) {
            accumulate(var2.getPixel(var7, var8), var5);
         }
      }

      float var12 = var5[0];
      float var14 = var5[1] / var12;
      float var9 = var5[2] / var12;
      float var10 = var5[3] / var12;
      var12 /= (float)(var6 * var6);
      float var11;
      if (var12 < (var4 == MipmapStrategy.STRICT_CUTOUT ? 0.6F : 0.2F)) {
         var11 = 0.0F;
         var14 = var5[4] / (float)(var6 * var6);
         var9 = var5[5] / (float)(var6 * var6);
         var10 = var5[6] / (float)(var6 * var6);
      } else {
         var11 = 1.0F;
      }

      return ARGB.color(var11, ARGB.color(ARGB.linearToSrgbChannel(var14), ARGB.linearToSrgbChannel(var9), ARGB.linearToSrgbChannel(var10)));
   }

   private static void accumulate(int var0, float[] var1) {
      float var2 = ARGB.alphaFloat(var0);
      var1[0] += var2;
      float var3 = ARGB.srgbToLinearChannel(ARGB.red(var0));
      var1[1] += var3 * var2;
      var1[4] += var3;
      float var4 = ARGB.srgbToLinearChannel(ARGB.green(var0));
      var1[2] += var4 * var2;
      var1[5] += var4;
      float var5 = ARGB.srgbToLinearChannel(ARGB.blue(var0));
      var1[3] += var5 * var2;
      var1[6] += var5;
   }
}
