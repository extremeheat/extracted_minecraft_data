package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob extends Mob {
   protected PathfinderMob(EntityType<? extends PathfinderMob> var1, Level var2) {
      super(var1, var2);
   }

   public float getWalkTargetValue(BlockPos var1) {
      return this.getWalkTargetValue(var1, this.level);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   public boolean checkSpawnRules(LevelAccessor var1, MobSpawnType var2) {
      return this.getWalkTargetValue(this.blockPosition(), var1) >= 0.0F;
   }

   public boolean isPathFinding() {
      return !this.getNavigation().isDone();
   }

   protected void tickLeash() {
      super.tickLeash();
      Entity var1 = this.getLeashHolder();
      if (var1 != null && var1.level == this.level) {
         this.restrictTo(var1.blockPosition(), 5);
         float var2 = this.distanceTo(var1);
         if (this instanceof TamableAnimal && ((TamableAnimal)this).isInSittingPose()) {
            if (var2 > 10.0F) {
               this.dropLeash(true, true);
            }

            return;
         }

         this.onLeashDistance(var2);
         if (var2 > 10.0F) {
            this.dropLeash(true, true);
            this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
         } else if (var2 > 6.0F) {
            double var3 = (var1.getX() - this.getX()) / (double)var2;
            double var5 = (var1.getY() - this.getY()) / (double)var2;
            double var7 = (var1.getZ() - this.getZ()) / (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(var3 * var3 * 0.4D, var3), Math.copySign(var5 * var5 * 0.4D, var5), Math.copySign(var7 * var7 * 0.4D, var7)));
         } else {
            this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            float var9 = 2.0F;
            Vec3 var4 = (new Vec3(var1.getX() - this.getX(), var1.getY() - this.getY(), var1.getZ() - this.getZ())).normalize().scale((double)Math.max(var2 - 2.0F, 0.0F));
            this.getNavigation().moveTo(this.getX() + var4.x, this.getY() + var4.y, this.getZ() + var4.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float var1) {
   }
}
