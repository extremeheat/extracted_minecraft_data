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

   public RemotePlayer(final ClientLevel level, final GameProfile gameProfile) {
      super(level, gameProfile);
      this.lerpDeltaMovement = Vec3.ZERO;
      this.noPhysics = true;
   }

   public boolean shouldRenderAtSqrDistance(final double distance) {
      double size = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(size)) {
         size = 1.0;
      }

      size *= 64.0 * getViewScale();
      return distance < size * size;
   }

   public boolean hurtClient(final DamageSource source) {
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

      try (Zone ignored = Profiler.get().zone("push")) {
         this.pushEntities();
      }

   }

   public void lerpMotion(final Vec3 movement) {
      this.lerpDeltaMovement = movement;
      this.lerpDeltaMovementSteps = this.getType().updateInterval() + 1;
   }

   protected void updatePlayerPose() {
   }

   public void recreateFromPacket(final ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      this.setOldPosAndRot();
   }
}
