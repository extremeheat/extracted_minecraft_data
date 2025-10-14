package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.ARGB;

public class MipmapGenerator {
   private static final float LINEAR_ALPHA_CUTOFF = 0.3F;
   private static final int ALPHA_CUTOUT_CUTOFF = 96;

   private MipmapGenerator() {
      super();
   }

   public static NativeImage[] generateMipLevels(NativeImage[] var0, int var1, boolean var2) {
      if (var1 + 1 <= var0.length) {
         return var0;
      } else {
         NativeImage[] var3 = new NativeImage[var1 + 1];
         boolean var4 = hasTransparentPixel(var0[0]);
         if (var0.length == 1 && var4 && !var2) {
            TextureUtil.solidify(var0[0]);
         }

         var3[0] = var0[0];

         for(int var5 = 1; var5 <= var1; ++var5) {
            if (var5 < var0.length) {
               var3[var5] = var0[var5];
            } else {
               NativeImage var6 = var3[var5 - 1];
               NativeImage var7 = new NativeImage(var6.getWidth() >> 1, var6.getHeight() >> 1, false);
               int var8 = var7.getWidth();
               int var9 = var7.getHeight();

               for(int var10 = 0; var10 < var8; ++var10) {
                  for(int var11 = 0; var11 < var9; ++var11) {
                     int var12 = var6.getPixel(var10 * 2 + 0, var11 * 2 + 0);
                     int var13 = var6.getPixel(var10 * 2 + 1, var11 * 2 + 0);
                     int var14 = var6.getPixel(var10 * 2 + 0, var11 * 2 + 1);
                     int var15 = var6.getPixel(var10 * 2 + 1, var11 * 2 + 1);
                     int var16;
                     if (var4) {
                        if (var2) {
                           var16 = darkenedAlphaBlend(var12, var13, var14, var15);
                        } else {
                           var16 = alphaBlend(var12, var13, var14, var15);
                        }
                     } else {
                        var16 = ARGB.meanLinear(var12, var13, var14, var15);
                     }

                     var7.setPixel(var10, var11, var16);
                  }
               }

               var3[var5] = var7;
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

   private static int alphaBlend(int var0, int var1, int var2, int var3) {
      float[] var4 = new float[7];
      accumulate(var0, var4);
      accumulate(var1, var4);
      accumulate(var2, var4);
      accumulate(var3, var4);
      float var5 = var4[0];
      float var6 = var4[1] / var5;
      float var7 = var4[2] / var5;
      float var8 = var4[3] / var5;
      var5 /= 4.0F;
      float var9;
      if (var5 < 0.3F) {
         var9 = 0.0F;
         var6 = var4[4] / 4.0F;
         var7 = var4[5] / 4.0F;
         var8 = var4[6] / 4.0F;
      } else {
         var9 = 1.0F;
      }

      return ARGB.color(var9, ARGB.color(ARGB.linearToSrgbChannel(var6), ARGB.linearToSrgbChannel(var7), ARGB.linearToSrgbChannel(var8)));
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
