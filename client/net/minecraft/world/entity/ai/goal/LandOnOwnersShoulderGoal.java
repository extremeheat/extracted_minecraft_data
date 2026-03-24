package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
   private final ShoulderRidingEntity entity;
   private boolean isSittingOnShoulder;

   public LandOnOwnersShoulderGoal(final ShoulderRidingEntity entity) {
      super();
      this.entity = entity;
   }

   public boolean canUse() {
      LivingEntity var2 = this.entity.getOwner();
      if (!(var2 instanceof ServerPlayer owner)) {
         return false;
      } else {
         boolean ownerThatCanBeSatOn = !owner.isSpectator() && !owner.getAbilities().flying && !owner.isInWater() && !owner.isInPowderSnow;
         return !this.entity.isOrderedToSit() && ownerThatCanBeSatOn && this.entity.canSitOnShoulder();
      }
   }

   public boolean isInterruptable() {
      return !this.isSittingOnShoulder;
   }

   public void start() {
      this.isSittingOnShoulder = false;
   }

   public void tick() {
      if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
         LivingEntity var2 = this.entity.getOwner();
         if (var2 instanceof ServerPlayer) {
            ServerPlayer owner = (ServerPlayer)var2;
            if (this.entity.getBoundingBox().intersects(owner.getBoundingBox())) {
               this.isSittingOnShoulder = this.entity.setEntityOnShoulder(owner);
            }
         }

      }
   }
}
