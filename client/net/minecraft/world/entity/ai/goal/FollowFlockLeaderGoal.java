package net.minecraft.world.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;

public class FollowFlockLeaderGoal extends Goal {
   private static final int INTERVAL_TICKS = 200;
   private final AbstractSchoolingFish mob;
   private int timeToRecalcPath;
   private int nextStartTick;

   public FollowFlockLeaderGoal(AbstractSchoolingFish var1) {
      super();
      this.mob = var1;
      this.nextStartTick = this.nextStartTick(var1);
   }

   protected int nextStartTick(AbstractSchoolingFish var1) {
      return reducedTickDelay(200 + var1.getRandom().nextInt(200) % 20);
   }

   @Override
   public boolean canUse() {
      if (this.mob.hasFollowers()) {
         return false;
      } else if (this.mob.isFollower()) {
         return true;
      } else if (this.nextStartTick > 0) {
         this.nextStartTick--;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         Predicate var1 = var0 -> var0.canBeFollowed() || !var0.isFollower();
         List var2 = this.mob.level().getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0), var1);
         AbstractSchoolingFish var3 = (AbstractSchoolingFish)DataFixUtils.orElse(var2.stream().filter(AbstractSchoolingFish::canBeFollowed).findAny(), this.mob);
         var3.addFollowers(var2.stream().filter(var0 -> !var0.isFollower()));
         return this.mob.isFollower();
      }
   }

   @Override
   public boolean canContinueToUse() {
      return this.mob.isFollower() && this.mob.inRangeOfLeader();
   }

   @Override
   public void start() {
      this.timeToRecalcPath = 0;
   }

   @Override
   public void stop() {
      this.mob.stopFollowing();
   }

   @Override
   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         this.mob.pathToLeader();
      }
   }
}
