package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public abstract class MinecartBehavior {
   protected final AbstractMinecart minecart;

   protected MinecartBehavior(AbstractMinecart var1) {
      super();
      this.minecart = var1;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.setPos(var1, var3, var5);
      this.setYRot(var7 % 360.0F);
      this.setXRot(var8 % 360.0F);
   }

   public double lerpTargetX() {
      return this.getX();
   }

   public double lerpTargetY() {
      return this.getY();
   }

   public double lerpTargetZ() {
      return this.getZ();
   }

   public float lerpTargetXRot() {
      return this.getXRot();
   }

   public float lerpTargetYRot() {
      return this.getYRot();
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
   }

   public abstract void tick();

   public Level level() {
      return this.minecart.level();
   }

   public abstract void moveAlongTrack();

   public abstract double stepAlongTrack(BlockPos var1, RailShape var2, double var3);

   public Vec3 getDeltaMovement() {
      return this.minecart.getDeltaMovement();
   }

   public void setDeltaMovement(Vec3 var1) {
      this.minecart.setDeltaMovement(var1);
   }

   public void setDeltaMovement(double var1, double var3, double var5) {
      this.minecart.setDeltaMovement(var1, var3, var5);
   }

   public Vec3 position() {
      return this.minecart.position();
   }

   public double getX() {
      return this.minecart.getX();
   }

   public double getY() {
      return this.minecart.getY();
   }

   public double getZ() {
      return this.minecart.getZ();
   }

   public void setPos(Vec3 var1) {
      this.minecart.setPos(var1);
   }

   public void setPos(double var1, double var3, double var5) {
      this.minecart.setPos(var1, var3, var5);
   }

   public float getXRot() {
      return this.minecart.getXRot();
   }

   public void setXRot(float var1) {
      this.minecart.setXRot(var1);
   }

   public float getYRot() {
      return this.minecart.getYRot();
   }

   public void setYRot(float var1) {
      this.minecart.setYRot(var1);
   }

   public Direction getMotionDirection() {
      return this.minecart.getDirection();
   }

   public Vec3 getKnownMovement(Vec3 var1) {
      return var1;
   }

   public abstract double getMaxSpeed();

   public abstract double getSlowdownFactor();
}
