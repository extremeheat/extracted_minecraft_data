package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class RemotePlayer extends AbstractClientPlayer {
   private Vec3 lerpDeltaMovement;
   private int lerpDeltaMovementSteps;

   public RemotePlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
      this.lerpDeltaMovement = Vec3.ZERO;
      this.noPhysics = true;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(var3)) {
         var3 = 1.0;
      }

      var3 *= 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   public boolean hurtClient(DamageSource var1) {
      return true;
   }

   public void tick() {
      super.tick();
      this.calculateEntityAnimation(false);
   }

   public void aiStep() {
      if (this.isInterpolating()) {
         this.getInterpolation().interpolate();
      }

      if (this.lerpHeadSteps > 0) {
         this.lerpHeadRotationStep(this.lerpHeadSteps, this.lerpYHeadRot);
         --this.lerpHeadSteps;
      }

      if (this.lerpDeltaMovementSteps > 0) {
         this.addDeltaMovement(new Vec3((this.lerpDeltaMovement.x - this.getDeltaMovement().x) / (double)this.lerpDeltaMovementSteps, (this.lerpDeltaMovement.y - this.getDeltaMovement().y) / (double)this.lerpDeltaMovementSteps, (this.lerpDeltaMovement.z - this.getDeltaMovement().z) / (double)this.lerpDeltaMovementSteps));
         --this.lerpDeltaMovementSteps;
      }

      this.updateSwingTime();
      this.updateBob();

      try (Zone var1 = Profiler.get().zone("push")) {
         this.pushEntities();
      }

   }

   public void lerpMotion(Vec3 var1) {
      this.lerpDeltaMovement = var1;
      this.lerpDeltaMovementSteps = this.getType().updateInterval() + 1;
   }

   protected void updatePlayerPose() {
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setOldPosAndRot();
   }
}
