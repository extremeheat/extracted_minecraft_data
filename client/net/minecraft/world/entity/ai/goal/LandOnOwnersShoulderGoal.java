package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
   private final ShoulderRidingEntity entity;
   private ServerPlayer owner;
   private boolean isSittingOnShoulder;

   public LandOnOwnersShoulderGoal(ShoulderRidingEntity var1) {
      super();
      this.entity = var1;
   }

   public boolean canUse() {
      ServerPlayer var1 = (ServerPlayer)this.entity.getOwner();
      boolean var2 = var1 != null && !var1.isSpectator() && !var1.getAbilities().flying && !var1.isInWater() && !var1.isInPowderSnow;
      return !this.entity.isOrderedToSit() && var2 && this.entity.canSitOnShoulder();
   }

   public boolean isInterruptable() {
      return !this.isSittingOnShoulder;
   }

   public void start() {
      this.owner = (ServerPlayer)this.entity.getOwner();
      this.isSittingOnShoulder = false;
   }

   public void tick() {
      if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
         if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
         }

      }
   }
}
