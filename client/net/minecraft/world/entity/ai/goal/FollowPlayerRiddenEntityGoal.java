package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FollowPlayerRiddenEntityGoal extends Goal {
   private int timeToRecalcPath;
   private final PathfinderMob mob;
   private final Class<? extends Entity> entityTypeToFollow;
   private @Nullable Player following;
   private FollowEntityGoal currentGoal;

   public FollowPlayerRiddenEntityGoal(final PathfinderMob mob, final Class<? extends Entity> entityTypeToFollow) {
      super();
      this.mob = mob;
      this.entityTypeToFollow = entityTypeToFollow;
   }

   public boolean canUse() {
      if (this.following != null && this.following.hasMovedHorizontallyRecently()) {
         return true;
      } else {
         for(Entity entity : this.mob.level().getEntitiesOfClass(this.entityTypeToFollow, this.mob.getBoundingBox().inflate(5.0))) {
            LivingEntity var5 = entity.getControllingPassenger();
            if (var5 instanceof Player) {
               Player controllingPlayer = (Player)var5;
               if (controllingPlayer.hasMovedHorizontallyRecently()) {
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
      for(Entity entity : this.mob.level().getEntitiesOfClass(this.entityTypeToFollow, this.mob.getBoundingBox().inflate(5.0))) {
         LivingEntity var5 = entity.getControllingPassenger();
         if (var5 instanceof Player player) {
            this.following = player;
            break;
         }
      }

      this.timeToRecalcPath = 0;
      this.currentGoal = FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_TO_ENTITY;
   }

   public void stop() {
      this.following = null;
   }

   public void tick() {
      float speed = this.currentGoal == FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_IN_ENTITY_DIRECTION ? 0.01F : 0.015F;
      this.mob.moveRelative(speed, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         if (this.currentGoal == FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_TO_ENTITY) {
            BlockPos behindEntityPos = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            behindEntityPos = behindEntityPos.offset(0, -1, 0);
            this.mob.getNavigation().moveTo((double)behindEntityPos.getX(), (double)behindEntityPos.getY(), (double)behindEntityPos.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) < 4.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_IN_ENTITY_DIRECTION;
            }
         } else if (this.currentGoal == FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_IN_ENTITY_DIRECTION) {
            Direction direction = this.following.getMotionDirection();
            BlockPos goTo = this.following.blockPosition().relative((Direction)direction, 10);
            this.mob.getNavigation().moveTo((double)goTo.getX(), (double)(goTo.getY() - 1), (double)goTo.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) > 12.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = FollowPlayerRiddenEntityGoal.FollowEntityGoal.GO_TO_ENTITY;
            }
         }

      }
   }

   private static enum FollowEntityGoal {
      GO_TO_ENTITY,
      GO_IN_ENTITY_DIRECTION;

      private FollowEntityGoal() {
      }

      // $FF: synthetic method
      private static FollowEntityGoal[] $values() {
         return new FollowEntityGoal[]{GO_TO_ENTITY, GO_IN_ENTITY_DIRECTION};
      }
   }
}
