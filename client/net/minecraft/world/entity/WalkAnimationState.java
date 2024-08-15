package net.minecraft.world.entity;

import net.minecraft.util.Mth;

public class WalkAnimationState {
   private float speedOld;
   private float speed;
   private float position;
   private float positionScale = 1.0F;

   public WalkAnimationState() {
      super();
   }

   public void setSpeed(float var1) {
      this.speed = var1;
   }

   public void update(float var1, float var2, float var3) {
      this.speedOld = this.speed;
      this.speed = this.speed + (var1 - this.speed) * var2;
      this.position = this.position + this.speed;
      this.positionScale = var3;
   }

   public void stop() {
      this.speedOld = 0.0F;
      this.speed = 0.0F;
      this.position = 0.0F;
   }

   public float speed() {
      return this.speed;
   }

   public float speed(float var1) {
      return Math.min(Mth.lerp(var1, this.speedOld, this.speed), 1.0F);
   }

   public float position() {
      return this.position * this.positionScale;
   }

   public float position(float var1) {
      return (this.position - this.speed * (1.0F - var1)) * this.positionScale;
   }

   public boolean isMoving() {
      return this.speed > 1.0E-5F;
   }
}
