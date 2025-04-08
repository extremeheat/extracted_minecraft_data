package net.minecraft.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ARGB {
   public ARGB() {
      super();
   }

   public static int alpha(int var0) {
      return var0 >>> 24;
   }

   public static int red(int var0) {
      return var0 >> 16 & 255;
   }

   public static int green(int var0) {
      return var0 >> 8 & 255;
   }

   public static int blue(int var0) {
      return var0 & 255;
   }

   public static int color(int var0, int var1, int var2, int var3) {
      return var0 << 24 | var1 << 16 | var2 << 8 | var3;
   }

   public static int color(int var0, int var1, int var2) {
      return color(255, var0, var1, var2);
   }

   public static int color(Vec3 var0) {
      return color(as8BitChannel((float)var0.x()), as8BitChannel((float)var0.y()), as8BitChannel((float)var0.z()));
   }

   public static int multiply(int var0, int var1) {
      if (var0 == -1) {
         return var1;
      } else {
         return var1 == -1 ? var0 : color(alpha(var0) * alpha(var1) / 255, red(var0) * red(var1) / 255, green(var0) * green(var1) / 255, blue(var0) * blue(var1) / 255);
      }
   }

   public static int scaleRGB(int var0, float var1) {
      return scaleRGB(var0, var1, var1, var1);
   }

   public static int scaleRGB(int var0, float var1, float var2, float var3) {
      return color(alpha(var0), Math.clamp((long)((int)((float)red(var0) * var1)), 0, 255), Math.clamp((long)((int)((float)green(var0) * var2)), 0, 255), Math.clamp((long)((int)((float)blue(var0) * var3)), 0, 255));
   }

   public static int scaleRGB(int var0, int var1) {
      return color(alpha(var0), Math.clamp((long)red(var0) * (long)var1 / 255L, 0, 255), Math.clamp((long)green(var0) * (long)var1 / 255L, 0, 255), Math.clamp((long)blue(var0) * (long)var1 / 255L, 0, 255));
   }

   public static int greyscale(int var0) {
      int var1 = (int)((float)red(var0) * 0.3F + (float)green(var0) * 0.59F + (float)blue(var0) * 0.11F);
      return color(var1, var1, var1);
   }

   public static int lerp(float var0, int var1, int var2) {
      int var3 = Mth.lerpInt(var0, alpha(var1), alpha(var2));
      int var4 = Mth.lerpInt(var0, red(var1), red(var2));
      int var5 = Mth.lerpInt(var0, green(var1), green(var2));
      int var6 = Mth.lerpInt(var0, blue(var1), blue(var2));
      return color(var3, var4, var5, var6);
   }

   public static int opaque(int var0) {
      return var0 | -16777216;
   }

   public static int transparent(int var0) {
      return var0 & 16777215;
   }

   public static int color(int var0, int var1) {
      return var0 << 24 | var1 & 16777215;
   }

   public static int white(float var0) {
      return as8BitChannel(var0) << 24 | 16777215;
   }

   public static int colorFromFloat(float var0, float var1, float var2, float var3) {
      return color(as8BitChannel(var0), as8BitChannel(var1), as8BitChannel(var2), as8BitChannel(var3));
   }

   public static Vector3f vector3fFromRGB24(int var0) {
      float var1 = (float)red(var0) / 255.0F;
      float var2 = (float)green(var0) / 255.0F;
      float var3 = (float)blue(var0) / 255.0F;
      return new Vector3f(var1, var2, var3);
   }

   public static int average(int var0, int var1) {
      return color((alpha(var0) + alpha(var1)) / 2, (red(var0) + red(var1)) / 2, (green(var0) + green(var1)) / 2, (blue(var0) + blue(var1)) / 2);
   }

   public static int as8BitChannel(float var0) {
      return Mth.floor(var0 * 255.0F);
   }

   public static float alphaFloat(int var0) {
      return from8BitChannel(alpha(var0));
   }

   public static float redFloat(int var0) {
      return from8BitChannel(red(var0));
   }

   public static float greenFloat(int var0) {
      return from8BitChannel(green(var0));
   }

   public static float blueFloat(int var0) {
      return from8BitChannel(blue(var0));
   }

   private static float from8BitChannel(int var0) {
      return (float)var0 / 255.0F;
   }

   public static int toABGR(int var0) {
      return var0 & -16711936 | (var0 & 16711680) >> 16 | (var0 & 255) << 16;
   }

   public static int fromABGR(int var0) {
      return toABGR(var0);
   }

   public static int setBrightness(int var0, float var1) {
      int var2 = red(var0);
      int var3 = green(var0);
      int var4 = blue(var0);
      int var5 = alpha(var0);
      int var6 = Math.max(Math.max(var2, var3), var4);
      int var7 = Math.min(Math.min(var2, var3), var4);
      float var8 = (float)(var6 - var7);
      float var9;
      if (var6 != 0) {
         var9 = var8 / (float)var6;
      } else {
         var9 = 0.0F;
      }

      float var10;
      if (var9 == 0.0F) {
         var10 = 0.0F;
      } else {
         float var11 = (float)(var6 - var2) / var8;
         float var12 = (float)(var6 - var3) / var8;
         float var13 = (float)(var6 - var4) / var8;
         if (var2 == var6) {
            var10 = var13 - var12;
         } else if (var3 == var6) {
            var10 = 2.0F + var11 - var13;
         } else {
            var10 = 4.0F + var12 - var11;
         }

         var10 /= 6.0F;
         if (var10 < 0.0F) {
            ++var10;
         }
      }

      if (var9 == 0.0F) {
         var2 = var3 = var4 = Math.round(var1 * 255.0F);
         return color(var5, var2, var3, var4);
      } else {
         float var20 = (var10 - (float)Math.floor((double)var10)) * 6.0F;
         float var21 = var20 - (float)Math.floor((double)var20);
         float var22 = var1 * (1.0F - var9);
         float var14 = var1 * (1.0F - var9 * var21);
         float var15 = var1 * (1.0F - var9 * (1.0F - var21));
         switch ((int)var20) {
            case 0:
               var2 = Math.round(var1 * 255.0F);
               var3 = Math.round(var15 * 255.0F);
               var4 = Math.round(var22 * 255.0F);
               break;
            case 1:
               var2 = Math.round(var14 * 255.0F);
               var3 = Math.round(var1 * 255.0F);
               var4 = Math.round(var22 * 255.0F);
               break;
            case 2:
               var2 = Math.round(var22 * 255.0F);
               var3 = Math.round(var1 * 255.0F);
               var4 = Math.round(var15 * 255.0F);
               break;
            case 3:
               var2 = Math.round(var22 * 255.0F);
               var3 = Math.round(var14 * 255.0F);
               var4 = Math.round(var1 * 255.0F);
               break;
            case 4:
               var2 = Math.round(var15 * 255.0F);
               var3 = Math.round(var22 * 255.0F);
               var4 = Math.round(var1 * 255.0F);
               break;
            case 5:
               var2 = Math.round(var1 * 255.0F);
               var3 = Math.round(var22 * 255.0F);
               var4 = Math.round(var14 * 255.0F);
         }

         return color(var5, var2, var3, var4);
      }
   }
}
