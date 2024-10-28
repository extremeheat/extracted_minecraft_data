package net.minecraft.world.entity;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob extends Mob {
   protected static final float DEFAULT_WALK_TARGET_VALUE = 0.0F;

   protected PathfinderMob(EntityType<? extends PathfinderMob> var1, Level var2) {
      super(var1, var2);
   }

   public float getWalkTargetValue(BlockPos var1) {
      return this.getWalkTargetValue(var1, this.level());
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   public boolean checkSpawnRules(LevelAccessor var1, EntitySpawnReason var2) {
      return this.getWalkTargetValue(this.blockPosition(), var1) >= 0.0F;
   }

   public boolean isPathFinding() {
      return !this.getNavigation().isDone();
   }

   public boolean isPanicking() {
      if (this.brain.hasMemoryValue(MemoryModuleType.IS_PANICKING)) {
         return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
      } else {
         Iterator var1 = this.goalSelector.getAvailableGoals().iterator();

         WrappedGoal var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (WrappedGoal)var1.next();
         } while(!var2.isRunning() || !(var2.getGoal() instanceof PanicGoal));

         return true;
      }
   }

   protected boolean shouldStayCloseToLeashHolder() {
      return true;
   }

   public void closeRangeLeashBehaviour(Entity var1) {
      super.closeRangeLeashBehaviour(var1);
      if (this.shouldStayCloseToLeashHolder() && !this.isPanicking()) {
         this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
         float var2 = 2.0F;
         float var3 = this.distanceTo(var1);
         Vec3 var4 = (new Vec3(var1.getX() - this.getX(), var1.getY() - this.getY(), var1.getZ() - this.getZ())).normalize().scale((double)Math.max(var3 - 2.0F, 0.0F));
         this.getNavigation().moveTo(this.getX() + var4.x, this.getY() + var4.y, this.getZ() + var4.z, this.followLeashSpeed());
      }

   }

   public boolean handleLeashAtDistance(Entity var1, float var2) {
      this.restrictTo(var1.blockPosition(), 5);
      return true;
   }

   protected double followLeashSpeed() {
      return 1.0;
   }
}
