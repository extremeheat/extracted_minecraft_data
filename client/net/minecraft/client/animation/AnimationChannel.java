package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public record AnimationChannel(AnimationChannel.Target a, Keyframe... b) {
   private final AnimationChannel.Target target;
   private final Keyframe[] keyframes;

   public AnimationChannel(AnimationChannel.Target var1, Keyframe... var2) {
      super();
      this.target = var1;
      this.keyframes = var2;
   }

   public interface Interpolation {
      Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
   }

   public static class Interpolations {
      public static final AnimationChannel.Interpolation LINEAR = (var0, var1, var2, var3, var4, var5) -> {
         Vector3f var6 = var2[var3].target();
         Vector3f var7 = var2[var4].target();
         return var6.lerp(var7, var1, var0).mul(var5);
      };
      public static final AnimationChannel.Interpolation CATMULLROM = (var0, var1, var2, var3, var4, var5) -> {
         Vector3f var6 = var2[Math.max(0, var3 - 1)].target();
         Vector3f var7 = var2[var3].target();
         Vector3f var8 = var2[var4].target();
         Vector3f var9 = var2[Math.min(var2.length - 1, var4 + 1)].target();
         var0.set(
            Mth.catmullrom(var1, var6.x(), var7.x(), var8.x(), var9.x()) * var5,
            Mth.catmullrom(var1, var6.y(), var7.y(), var8.y(), var9.y()) * var5,
            Mth.catmullrom(var1, var6.z(), var7.z(), var8.z(), var9.z()) * var5
         );
         return var0;
      };

      public Interpolations() {
         super();
      }
   }

   public interface Target {
      void apply(ModelPart var1, Vector3f var2);
   }

   public static class Targets {
      public static final AnimationChannel.Target POSITION = ModelPart::offsetPos;
      public static final AnimationChannel.Target ROTATION = ModelPart::offsetRotation;
      public static final AnimationChannel.Target SCALE = ModelPart::offsetScale;

      public Targets() {
         super();
      }
   }
}
