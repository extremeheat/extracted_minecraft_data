package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class RemotePlayer extends AbstractClientPlayer {
   private Vec3 lerpDeltaMovement = Vec3.ZERO;
   private int lerpDeltaMovementSteps;

   public RemotePlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
      this.setMaxUpStep(1.0F);
      this.noPhysics = true;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(var3)) {
         var3 = 1.0;
      }

      var3 *= 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      this.calculateEntityAnimation(false);
   }

   @Override
   public void aiStep() {
      if (this.lerpSteps > 0) {
         double var1 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
         double var3 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
         double var5 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
         this.setYRot(this.getYRot() + (float)Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot()) / (float)this.lerpSteps);
         this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(var1, var3, var5);
         this.setRot(this.getYRot(), this.getXRot());
      }

      if (this.lerpHeadSteps > 0) {
         this.yHeadRot += (float)(Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
         --this.lerpHeadSteps;
      }

      if (this.lerpDeltaMovementSteps > 0) {
         this.addDeltaMovement(
            new Vec3(
               (this.lerpDeltaMovement.x - this.getDeltaMovement().x) / (double)this.lerpDeltaMovementSteps,
               (this.lerpDeltaMovement.y - this.getDeltaMovement().y) / (double)this.lerpDeltaMovementSteps,
               (this.lerpDeltaMovement.z - this.getDeltaMovement().z) / (double)this.lerpDeltaMovementSteps
            )
         );
         --this.lerpDeltaMovementSteps;
      }

      this.oBob = this.bob;
      this.updateSwingTime();
      float var7;
      if (this.onGround() && !this.isDeadOrDying()) {
         var7 = (float)Math.min(0.1, this.getDeltaMovement().horizontalDistance());
      } else {
         var7 = 0.0F;
      }

      this.bob += (var7 - this.bob) * 0.4F;
      this.level().getProfiler().push("push");
      this.pushEntities();
      this.level().getProfiler().pop();
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      this.lerpDeltaMovement = new Vec3(var1, var3, var5);
      this.lerpDeltaMovementSteps = this.getType().updateInterval() + 1;
   }

   @Override
   protected void updatePlayerPose() {
   }

   @Override
   public void sendSystemMessage(Component var1) {
      Minecraft var2 = Minecraft.getInstance();
      var2.gui.getChat().addMessage(var1);
   }
}
