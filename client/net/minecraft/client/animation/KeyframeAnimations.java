package net.minecraft.client.animation;

import org.joml.Vector3f;

public class KeyframeAnimations {
   public KeyframeAnimations() {
      super();
   }

   public static Vector3f posVec(float var0, float var1, float var2) {
      return new Vector3f(var0, -var1, var2);
   }

   public static Vector3f degreeVec(float var0, float var1, float var2) {
      return new Vector3f(var0 * 0.017453292F, var1 * 0.017453292F, var2 * 0.017453292F);
   }

   public static Vector3f scaleVec(double var0, double var2, double var4) {
      return new Vector3f((float)(var0 - 1.0), (float)(var2 - 1.0), (float)(var4 - 1.0));
   }
}
