package net.minecraft.client.animation;

import org.joml.Vector3fc;

public record Keyframe(float timestamp, Vector3fc preTarget, Vector3fc postTarget, AnimationChannel.Interpolation interpolation) {
   public Keyframe(float var1, Vector3fc var2, AnimationChannel.Interpolation var3) {
      this(var1, var2, var2, var3);
   }

   public Keyframe(float var1, Vector3fc var2, Vector3fc var3, AnimationChannel.Interpolation var4) {
      super();
      this.timestamp = var1;
      this.preTarget = var2;
      this.postTarget = var3;
      this.interpolation = var4;
   }
}
