package net.minecraft.world.entity;

import net.minecraft.util.Mth;

public class WalkAnimationState {
   private float speedOld;
   private float speed;
   private float position;

   public WalkAnimationState() {
      super();
   }

   public void setSpeed(float var1) {
      this.speed = var1;
   }

   public void update(float var1, float var2) {
      this.speedOld = this.speed;
      this.speed += (var1 - this.speed) * var2;
      this.position += this.speed;
   }

   public float speed() {
      return this.speed;
   }

   public float speed(float var1) {
      return Mth.lerp(var1, this.speedOld, this.speed);
   }

   public float position() {
      return this.position;
   }

   public float position(float var1) {
      return this.position - this.speed * (1.0F - var1);
   }

   public boolean isMoving() {
      return this.speed > 1.0E-5F;
   }
}
