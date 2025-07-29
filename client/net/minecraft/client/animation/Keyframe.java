package net.minecraft.client.animation;

import org.joml.Vector3f;

public record Keyframe(float timestamp, Vector3f preTarget, Vector3f postTarget, AnimationChannel.Interpolation interpolation) {
   public Keyframe(float var1, Vector3f var2, AnimationChannel.Interpolation var3) {
      this(var1, var2, var2, var3);
   }

   public Keyframe(float var1, Vector3f var2, Vector3f var3, AnimationChannel.Interpolation var4) {
      super();
      this.timestamp = var1;
      this.preTarget = var2;
      this.postTarget = var3;
      this.interpolation = var4;
   }
}
