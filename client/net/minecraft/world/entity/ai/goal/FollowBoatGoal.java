package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class FollowBoatGoal extends Goal {
   private int timeToRecalcPath;
   private final PathfinderMob mob;
   @Nullable
   private Player following;
   private BoatGoals currentGoal;

   public FollowBoatGoal(PathfinderMob var1) {
      super();
      this.mob = var1;
   }

   public boolean canUse() {
      List var1 = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0D));
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Boat var4 = (Boat)var3.next();
         Entity var5 = var4.getControllingPassenger();
         if (var5 instanceof Player && (Mth.abs(((Player)var5).xxa) > 0.0F || Mth.abs(((Player)var5).zza) > 0.0F)) {
            var2 = true;
            break;
         }
      }

      return this.following != null && (Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F) || var2;
   }

   public boolean isInterruptable() {
      return true;
   }

   public boolean canContinueToUse() {
      return this.following != null && this.following.isPassenger() && (Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F);
   }

   public void start() {
      List var1 = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0D));
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Boat var3 = (Boat)var2.next();
         if (var3.getControllingPassenger() != null && var3.getControllingPassenger() instanceof Player) {
            this.following = (Player)var3.getControllingPassenger();
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
      boolean var1 = Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F;
      float var2 = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION ? (var1 ? 0.01F : 0.0F) : 0.015F;
      this.mob.moveRelative(var2, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos var3 = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            var3 = var3.offset(0, -1, 0);
            this.mob.getNavigation().moveTo((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 1.0D);
            if (this.mob.distanceTo(this.following) < 4.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction var5 = this.following.getMotionDirection();
            BlockPos var4 = this.following.blockPosition().relative((Direction)var5, 10);
            this.mob.getNavigation().moveTo((double)var4.getX(), (double)(var4.getY() - 1), (double)var4.getZ(), 1.0D);
            if (this.mob.distanceTo(this.following) > 12.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
