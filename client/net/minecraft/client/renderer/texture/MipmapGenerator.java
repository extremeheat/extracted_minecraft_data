package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;

public class MipmapGenerator {
   private static final int ALPHA_CUTOUT_CUTOFF = 96;
   private static final float[] POW22 = (float[])Util.make(new float[256], (var0) -> {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = (float)Math.pow((double)((float)var1 / 255.0F), 2.2D);
      }

   });

   private MipmapGenerator() {
      super();
   }

   public static NativeImage[] generateMipLevels(NativeImage var0, int var1) {
      NativeImage[] var2 = new NativeImage[var1 + 1];
      var2[0] = var0;
      if (var1 > 0) {
         boolean var3 = false;

         int var4;
         label51:
         for(var4 = 0; var4 < var0.getWidth(); ++var4) {
            for(int var5 = 0; var5 < var0.getHeight(); ++var5) {
               if (var0.getPixelRGBA(var4, var5) >> 24 == 0) {
                  var3 = true;
                  break label51;
               }
            }
         }

         for(var4 = 1; var4 <= var1; ++var4) {
            NativeImage var11 = var2[var4 - 1];
            NativeImage var6 = new NativeImage(var11.getWidth() >> 1, var11.getHeight() >> 1, false);
            int var7 = var6.getWidth();
            int var8 = var6.getHeight();

            for(int var9 = 0; var9 < var7; ++var9) {
               for(int var10 = 0; var10 < var8; ++var10) {
                  var6.setPixelRGBA(var9, var10, alphaBlend(var11.getPixelRGBA(var9 * 2 + 0, var10 * 2 + 0), var11.getPixelRGBA(var9 * 2 + 1, var10 * 2 + 0), var11.getPixelRGBA(var9 * 2 + 0, var10 * 2 + 1), var11.getPixelRGBA(var9 * 2 + 1, var10 * 2 + 1), var3));
               }
            }

            var2[var4] = var6;
         }
      }

      return var2;
   }

   private static int alphaBlend(int var0, int var1, int var2, int var3, boolean var4) {
      if (var4) {
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         if (var0 >> 24 != 0) {
            var13 += getPow22(var0 >> 24);
            var14 += getPow22(var0 >> 16);
            var15 += getPow22(var0 >> 8);
            var16 += getPow22(var0 >> 0);
         }

         if (var1 >> 24 != 0) {
            var13 += getPow22(var1 >> 24);
            var14 += getPow22(var1 >> 16);
            var15 += getPow22(var1 >> 8);
            var16 += getPow22(var1 >> 0);
         }

         if (var2 >> 24 != 0) {
            var13 += getPow22(var2 >> 24);
            var14 += getPow22(var2 >> 16);
            var15 += getPow22(var2 >> 8);
            var16 += getPow22(var2 >> 0);
         }

         if (var3 >> 24 != 0) {
            var13 += getPow22(var3 >> 24);
            var14 += getPow22(var3 >> 16);
            var15 += getPow22(var3 >> 8);
            var16 += getPow22(var3 >> 0);
         }

         var13 /= 4.0F;
         var14 /= 4.0F;
         var15 /= 4.0F;
         var16 /= 4.0F;
         int var9 = (int)(Math.pow((double)var13, 0.45454545454545453D) * 255.0D);
         int var10 = (int)(Math.pow((double)var14, 0.45454545454545453D) * 255.0D);
         int var11 = (int)(Math.pow((double)var15, 0.45454545454545453D) * 255.0D);
         int var12 = (int)(Math.pow((double)var16, 0.45454545454545453D) * 255.0D);
         if (var9 < 96) {
            var9 = 0;
         }

         return var9 << 24 | var10 << 16 | var11 << 8 | var12;
      } else {
         int var5 = gammaBlend(var0, var1, var2, var3, 24);
         int var6 = gammaBlend(var0, var1, var2, var3, 16);
         int var7 = gammaBlend(var0, var1, var2, var3, 8);
         int var8 = gammaBlend(var0, var1, var2, var3, 0);
         return var5 << 24 | var6 << 16 | var7 << 8 | var8;
      }
   }

   private static int gammaBlend(int var0, int var1, int var2, int var3, int var4) {
      float var5 = getPow22(var0 >> var4);
      float var6 = getPow22(var1 >> var4);
      float var7 = getPow22(var2 >> var4);
      float var8 = getPow22(var3 >> var4);
      float var9 = (float)((double)((float)Math.pow((double)(var5 + var6 + var7 + var8) * 0.25D, 0.45454545454545453D)));
      return (int)((double)var9 * 255.0D);
   }

   private static float getPow22(int var0) {
      return POW22[var0 & 255];
   }
}
