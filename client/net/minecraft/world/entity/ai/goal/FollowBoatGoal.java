package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FollowBoatGoal extends Goal {
   private int timeToRecalcPath;
   private final PathfinderMob mob;
   private @Nullable Player following;
   private BoatGoals currentGoal;

   public FollowBoatGoal(PathfinderMob var1) {
      super();
      this.mob = var1;
   }

   public boolean canUse() {
      if (this.following != null && this.following.hasMovedHorizontallyRecently()) {
         return true;
      } else {
         for(AbstractBoat var3 : this.mob.level().getEntitiesOfClass(AbstractBoat.class, this.mob.getBoundingBox().inflate(5.0))) {
            LivingEntity var5 = var3.getControllingPassenger();
            if (var5 instanceof Player) {
               Player var4 = (Player)var5;
               if (var4.hasMovedHorizontallyRecently()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean isInterruptable() {
      return true;
   }

   public boolean canContinueToUse() {
      return this.following != null && this.following.isPassenger() && this.following.hasMovedHorizontallyRecently();
   }

   public void start() {
      for(AbstractBoat var3 : this.mob.level().getEntitiesOfClass(AbstractBoat.class, this.mob.getBoundingBox().inflate(5.0))) {
         LivingEntity var5 = var3.getControllingPassenger();
         if (var5 instanceof Player var4) {
            this.following = var4;
            break;
         }
      }

      this.timeToRecalcPath = 0;
      this.currentGoal = BoatGoals.GO_TO_BOAT;
   }

   public void stop() {
      this.following = null;
   }

   public void tick() {
      float var1 = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION ? 0.01F : 0.015F;
      this.mob.moveRelative(var1, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos var2 = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            var2 = var2.offset(0, -1, 0);
            this.mob.getNavigation().moveTo((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) < 4.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction var5 = this.following.getMotionDirection();
            BlockPos var3 = this.following.blockPosition().relative((Direction)var5, 10);
            this.mob.getNavigation().moveTo((double)var3.getX(), (double)(var3.getY() - 1), (double)var3.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) > 12.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
