package net.minecraft.client.animation;

import com.mojang.math.Vector3f;

public record Keyframe(float a, Vector3f b, AnimationChannel.Interpolation c) {
   private final float timestamp;
   private final Vector3f target;
   private final AnimationChannel.Interpolation interpolation;

   public Keyframe(float var1, Vector3f var2, AnimationChannel.Interpolation var3) {
      super();
      this.timestamp = var1;
      this.target = var2;
      this.interpolation = var3;
   }
}
