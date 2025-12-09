package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
   private final ShoulderRidingEntity entity;
   private boolean isSittingOnShoulder;

   public LandOnOwnersShoulderGoal(ShoulderRidingEntity var1) {
      super();
      this.entity = var1;
   }

   public boolean canUse() {
      LivingEntity var2 = this.entity.getOwner();
      if (!(var2 instanceof ServerPlayer var1)) {
         return false;
      } else {
         boolean var3 = !var1.isSpectator() && !var1.getAbilities().flying && !var1.isInWater() && !var1.isInPowderSnow;
         return !this.entity.isOrderedToSit() && var3 && this.entity.canSitOnShoulder();
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
            ServerPlayer var1 = (ServerPlayer)var2;
            if (this.entity.getBoundingBox().intersects(var1.getBoundingBox())) {
               this.isSittingOnShoulder = this.entity.setEntityOnShoulder(var1);
            }
         }

      }
   }
}
