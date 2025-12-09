package net.minecraft.util;

public class Ease {
   public Ease() {
      super();
   }

   public static float inBack(float var0) {
      float var1 = 1.70158F;
      float var2 = 2.70158F;
      return Mth.square(var0) * (2.70158F * var0 - 1.70158F);
   }

   public static float inBounce(float var0) {
      return 1.0F - outBounce(1.0F - var0);
   }

   public static float inCubic(float var0) {
      return Mth.cube(var0);
   }

   public static float inElastic(float var0) {
      if (var0 == 0.0F) {
         return 0.0F;
      } else if (var0 == 1.0F) {
         return 1.0F;
      } else {
         float var1 = 2.0943952F;
         return (float)(-Math.pow(2.0, 10.0 * (double)var0 - 10.0) * Math.sin(((double)var0 * 10.0 - 10.75) * 2.094395160675049));
      }
   }

   public static float inExpo(float var0) {
      return var0 == 0.0F ? 0.0F : (float)Math.pow(2.0, 10.0 * (double)var0 - 10.0);
   }

   public static float inQuart(float var0) {
      return Mth.square(Mth.square(var0));
   }

   public static float inQuint(float var0) {
      return Mth.square(Mth.square(var0)) * var0;
   }

   public static float inSine(float var0) {
      return 1.0F - Mth.cos((double)(var0 * 1.5707964F));
   }

   public static float inOutBounce(float var0) {
      return var0 < 0.5F ? (1.0F - outBounce(1.0F - 2.0F * var0)) / 2.0F : (1.0F + outBounce(2.0F * var0 - 1.0F)) / 2.0F;
   }

   public static float inOutCirc(float var0) {
      return var0 < 0.5F ? (float)((1.0 - Math.sqrt(1.0 - Math.pow(2.0 * (double)var0, 2.0))) / 2.0) : (float)((Math.sqrt(1.0 - Math.pow(-2.0 * (double)var0 + 2.0, 2.0)) + 1.0) / 2.0);
   }

   public static float inOutCubic(float var0) {
      return var0 < 0.5F ? 4.0F * Mth.cube(var0) : (float)(1.0 - Math.pow(-2.0 * (double)var0 + 2.0, 3.0) / 2.0);
   }

   public static float inOutQuad(float var0) {
      return var0 < 0.5F ? 2.0F * Mth.square(var0) : (float)(1.0 - Math.pow(-2.0 * (double)var0 + 2.0, 2.0) / 2.0);
   }

   public static float inOutQuart(float var0) {
      return var0 < 0.5F ? 8.0F * Mth.square(Mth.square(var0)) : (float)(1.0 - Math.pow(-2.0 * (double)var0 + 2.0, 4.0) / 2.0);
   }

   public static float inOutQuint(float var0) {
      return (double)var0 < 0.5 ? 16.0F * var0 * var0 * var0 * var0 * var0 : (float)(1.0 - Math.pow(-2.0 * (double)var0 + 2.0, 5.0) / 2.0);
   }

   public static float outBounce(float var0) {
      float var1 = 7.5625F;
      float var2 = 2.75F;
      if (var0 < 0.36363637F) {
         return 7.5625F * Mth.square(var0);
      } else if (var0 < 0.72727275F) {
         return 7.5625F * Mth.square(var0 - 0.54545456F) + 0.75F;
      } else {
         return (double)var0 < 0.9090909090909091 ? 7.5625F * Mth.square(var0 - 0.8181818F) + 0.9375F : 7.5625F * Mth.square(var0 - 0.95454544F) + 0.984375F;
      }
   }

   public static float outElastic(float var0) {
      float var1 = 2.0943952F;
      if (var0 == 0.0F) {
         return 0.0F;
      } else {
         return var0 == 1.0F ? 1.0F : (float)(Math.pow(2.0, -10.0 * (double)var0) * Math.sin(((double)var0 * 10.0 - 0.75) * 2.094395160675049) + 1.0);
      }
   }

   public static float outExpo(float var0) {
      return var0 == 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0 * (double)var0);
   }

   public static float outQuad(float var0) {
      return 1.0F - Mth.square(1.0F - var0);
   }

   public static float outQuint(float var0) {
      return 1.0F - (float)Math.pow(1.0 - (double)var0, 5.0);
   }

   public static float outSine(float var0) {
      return Mth.sin((double)(var0 * 1.5707964F));
   }

   public static float inOutSine(float var0) {
      return -(Mth.cos((double)(3.1415927F * var0)) - 1.0F) / 2.0F;
   }

   public static float outBack(float var0) {
      float var1 = 1.70158F;
      float var2 = 2.70158F;
      return 1.0F + 2.70158F * Mth.cube(var0 - 1.0F) + 1.70158F * Mth.square(var0 - 1.0F);
   }

   public static float outQuart(float var0) {
      return 1.0F - Mth.square(Mth.square(1.0F - var0));
   }

   public static float outCubic(float var0) {
      return 1.0F - Mth.cube(1.0F - var0);
   }

   public static float inOutExpo(float var0) {
      if (var0 < 0.5F) {
         return var0 == 0.0F ? 0.0F : (float)(Math.pow(2.0, 20.0 * (double)var0 - 10.0) / 2.0);
      } else {
         return var0 == 1.0F ? 1.0F : (float)((2.0 - Math.pow(2.0, -20.0 * (double)var0 + 10.0)) / 2.0);
      }
   }

   public static float inQuad(float var0) {
      return var0 * var0;
   }

   public static float outCirc(float var0) {
      return (float)Math.sqrt((double)(1.0F - Mth.square(var0 - 1.0F)));
   }

   public static float inOutElastic(float var0) {
      float var1 = 1.3962635F;
      if (var0 == 0.0F) {
         return 0.0F;
      } else if (var0 == 1.0F) {
         return 1.0F;
      } else {
         double var2 = Math.sin((20.0 * (double)var0 - 11.125) * 1.3962634801864624);
         return var0 < 0.5F ? (float)(-(Math.pow(2.0, 20.0 * (double)var0 - 10.0) * var2) / 2.0) : (float)(Math.pow(2.0, -20.0 * (double)var0 + 10.0) * var2 / 2.0 + 1.0);
      }
   }

   public static float inCirc(float var0) {
      return (float)(-Math.sqrt((double)(1.0F - var0 * var0))) + 1.0F;
   }

   public static float inOutBack(float var0) {
      float var1 = 1.70158F;
      float var2 = 2.5949094F;
      if (var0 < 0.5F) {
         return 4.0F * var0 * var0 * (7.189819F * var0 - 2.5949094F) / 2.0F;
      } else {
         float var3 = 2.0F * var0 - 2.0F;
         return (var3 * var3 * (3.5949094F * var3 + 2.5949094F) + 2.0F) / 2.0F;
      }
   }
}
