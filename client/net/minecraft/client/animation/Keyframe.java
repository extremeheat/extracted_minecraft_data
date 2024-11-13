package net.minecraft.client.animation;

import org.joml.Vector3f;

public record Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
   public Keyframe(float var1, Vector3f var2, AnimationChannel.Interpolation var3) {
      super();
      this.timestamp = var1;
      this.target = var2;
      this.interpolation = var3;
   }
}
