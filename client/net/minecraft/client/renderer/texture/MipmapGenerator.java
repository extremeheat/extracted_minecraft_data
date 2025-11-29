package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class MipmapGenerator {
   private static final String ITEM_PREFIX = "item/";
   private static final float ALPHA_CUTOFF = 0.5F;
   private static final float STRICT_ALPHA_CUTOFF = 0.3F;

   private MipmapGenerator() {
      super();
   }

   private static float alphaTestCoverage(NativeImage var0, float var1, float var2) {
      int var3 = var0.getWidth();
      int var4 = var0.getHeight();
      float var5 = 0.0F;
      boolean var6 = true;

      for(int var7 = 0; var7 < var4 - 1; ++var7) {
         for(int var8 = 0; var8 < var3 - 1; ++var8) {
            float var9 = Math.clamp(ARGB.alphaFloat(var0.getPixel(var8, var7)) * var2, 0.0F, 1.0F);
            float var10 = Math.clamp(ARGB.alphaFloat(var0.getPixel(var8 + 1, var7)) * var2, 0.0F, 1.0F);
            float var11 = Math.clamp(ARGB.alphaFloat(var0.getPixel(var8, var7 + 1)) * var2, 0.0F, 1.0F);
            float var12 = Math.clamp(ARGB.alphaFloat(var0.getPixel(var8 + 1, var7 + 1)) * var2, 0.0F, 1.0F);
            float var13 = 0.0F;

            for(int var14 = 0; var14 < 4; ++var14) {
               float var15 = ((float)var14 + 0.5F) / 4.0F;

               for(int var16 = 0; var16 < 4; ++var16) {
                  float var17 = ((float)var16 + 0.5F) / 4.0F;
                  float var18 = var9 * (1.0F - var17) * (1.0F - var15) + var10 * var17 * (1.0F - var15) + var11 * (1.0F - var17) * var15 + var12 * var17 * var15;
                  if (var18 > var1) {
                     ++var13;
                  }
               }
            }

            var5 += var13 / 16.0F;
         }
      }

      return var5 / (float)((var3 - 1) * (var4 - 1));
   }

   private static void scaleAlphaToCoverage(NativeImage var0, float var1, float var2, float var3) {
      float var4 = 0.0F;
      float var5 = 4.0F;
      float var6 = 1.0F;
      float var7 = 1.0F;
      float var8 = 3.4028235E38F;
      int var9 = var0.getWidth();
      int var10 = var0.getHeight();

      for(int var11 = 0; var11 < 5; ++var11) {
         float var12 = alphaTestCoverage(var0, var2, var6);
         float var13 = Math.abs(var12 - var1);
         if (var13 < var8) {
            var8 = var13;
            var7 = var6;
         }

         if (var12 < var1) {
            var4 = var6;
         } else {
            if (!(var12 > var1)) {
               break;
            }

            var5 = var6;
         }

         var6 = (var4 + var5) * 0.5F;
      }

      for(int var15 = 0; var15 < var10; ++var15) {
         for(int var16 = 0; var16 < var9; ++var16) {
            int var17 = var0.getPixel(var16, var15);
            float var14 = ARGB.alphaFloat(var17);
            var14 = var14 * var7 + var3 + 0.025F;
            var14 = Math.clamp(var14, 0.0F, 1.0F);
            var0.setPixel(var16, var15, ARGB.color(var14, var17));
         }
      }

   }

   public static NativeImage[] generateMipLevels(Identifier var0, NativeImage[] var1, int var2, MipmapStrategy var3, float var4) {
      if (var3 == MipmapStrategy.AUTO) {
         var3 = hasTransparentPixel(var1[0]) ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
      }

      if (var1.length == 1 && !var0.getPath().startsWith("item/")) {
         if (var3 != MipmapStrategy.CUTOUT && var3 != MipmapStrategy.STRICT_CUTOUT) {
            if (var3 == MipmapStrategy.DARK_CUTOUT) {
               TextureUtil.fillEmptyAreasWithDarkColor(var1[0]);
            }
         } else {
            TextureUtil.solidify(var1[0]);
         }
      }

      if (var2 + 1 <= var1.length) {
         return var1;
      } else {
         NativeImage[] var5 = new NativeImage[var2 + 1];
         var5[0] = var1[0];
         boolean var6 = var3 == MipmapStrategy.CUTOUT || var3 == MipmapStrategy.STRICT_CUTOUT || var3 == MipmapStrategy.DARK_CUTOUT;
         float var7 = var3 == MipmapStrategy.STRICT_CUTOUT ? 0.3F : 0.5F;
         float var8 = var6 ? alphaTestCoverage(var1[0], var7, 1.0F) : 0.0F;

         for(int var9 = 1; var9 <= var2; ++var9) {
            if (var9 < var1.length) {
               var5[var9] = var1[var9];
            } else {
               NativeImage var10 = var5[var9 - 1];
               NativeImage var11 = new NativeImage(var10.getWidth() >> 1, var10.getHeight() >> 1, false);
               int var12 = var11.getWidth();
               int var13 = var11.getHeight();

               for(int var14 = 0; var14 < var12; ++var14) {
                  for(int var15 = 0; var15 < var13; ++var15) {
                     int var16 = var10.getPixel(var14 * 2 + 0, var15 * 2 + 0);
                     int var17 = var10.getPixel(var14 * 2 + 1, var15 * 2 + 0);
                     int var18 = var10.getPixel(var14 * 2 + 0, var15 * 2 + 1);
                     int var19 = var10.getPixel(var14 * 2 + 1, var15 * 2 + 1);
                     int var20;
                     if (var3 == MipmapStrategy.DARK_CUTOUT) {
                        var20 = darkenedAlphaBlend(var16, var17, var18, var19);
                     } else {
                        var20 = ARGB.meanLinear(var16, var17, var18, var19);
                     }

                     var11.setPixel(var14, var15, var20);
                  }
               }

               var5[var9] = var11;
            }

            if (var6) {
               scaleAlphaToCoverage(var5[var9], var8, var7, var4);
            }
         }

         return var5;
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
      return ARGB.color(ARGB.linearToSrgbChannel(var4), ARGB.linearToSrgbChannel(var5), ARGB.linearToSrgbChannel(var6), ARGB.linearToSrgbChannel(var7));
   }
}
