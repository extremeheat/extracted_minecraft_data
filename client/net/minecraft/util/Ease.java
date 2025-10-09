package net.minecraft.util;

public class Ease {
   public Ease() {
      super();
   }

   public static float inOutSine(float var0) {
      return -(Mth.cos(3.1415927F * var0) - 1.0F) / 2.0F;
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
