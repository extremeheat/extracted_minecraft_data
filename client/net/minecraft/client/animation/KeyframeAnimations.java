package net.minecraft.client.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.model.Model;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class KeyframeAnimations {
   public KeyframeAnimations() {
      super();
   }

   public static void animate(Model var0, AnimationDefinition var1, long var2, float var4, Vector3f var5) {
      float var6 = getElapsedSeconds(var1, var2);

      for(Map.Entry var8 : var1.boneAnimations().entrySet()) {
         Optional var9 = var0.getAnyDescendantWithName((String)var8.getKey());
         List var10 = (List)var8.getValue();
         var9.ifPresent((var4x) -> var10.forEach((var4xx) -> {
               Keyframe[] var5x = var4xx.keyframes();
               int var6x = Math.max(0, Mth.binarySearch(0, var5x.length, (var2) -> var6 <= var5x[var2].timestamp()) - 1);
               int var7 = Math.min(var5x.length - 1, var6x + 1);
               Keyframe var8 = var5x[var6x];
               Keyframe var9 = var5x[var7];
               float var10 = var6 - var8.timestamp();
               float var11;
               if (var7 != var6x) {
                  var11 = Mth.clamp(var10 / (var9.timestamp() - var8.timestamp()), 0.0F, 1.0F);
               } else {
                  var11 = 0.0F;
               }

               var9.interpolation().apply(var5, var11, var5x, var6x, var7, var4);
               var4xx.target().apply(var4x, var5);
            }));
      }

   }

   private static float getElapsedSeconds(AnimationDefinition var0, long var1) {
      float var3 = (float)var1 / 1000.0F;
      return var0.looping() ? var3 % var0.lengthInSeconds() : var3;
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
