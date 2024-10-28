package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelReader;

public abstract class MoveToBlockGoal extends Goal {
   private static final int GIVE_UP_TICKS = 1200;
   private static final int STAY_TICKS = 1200;
   private static final int INTERVAL_TICKS = 200;
   protected final PathfinderMob mob;
   public final double speedModifier;
   protected int nextStartTick;
   protected int tryTicks;
   private int maxStayTicks;
   protected BlockPos blockPos;
   private boolean reachedTarget;
   private final int searchRange;
   private final int verticalSearchRange;
   protected int verticalSearchStart;

   public MoveToBlockGoal(PathfinderMob var1, double var2, int var4) {
      this(var1, var2, var4, 1);
   }

   public MoveToBlockGoal(PathfinderMob var1, double var2, int var4, int var5) {
      super();
      this.blockPos = BlockPos.ZERO;
      this.mob = var1;
      this.speedModifier = var2;
      this.searchRange = var4;
      this.verticalSearchStart = 0;
      this.verticalSearchRange = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   public boolean canUse() {
      if (this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return this.findNearestBlock();
      }
   }

   protected int nextStartTick(PathfinderMob var1) {
      return reducedTickDelay(200 + var1.getRandom().nextInt(200));
   }

   public boolean canContinueToUse() {
      return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.isValidTarget(this.mob.level(), this.blockPos);
   }

   public void start() {
      this.moveMobToBlock();
      this.tryTicks = 0;
      this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
   }

   protected void moveMobToBlock() {
      this.mob.getNavigation().moveTo((double)this.blockPos.getX() + 0.5, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5, this.speedModifier);
   }

   public double acceptedDistance() {
      return 1.0;
   }

   protected BlockPos getMoveToTarget() {
      return this.blockPos.above();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      BlockPos var1 = this.getMoveToTarget();
      if (!var1.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
         this.reachedTarget = false;
         ++this.tryTicks;
         if (this.shouldRecalculatePath()) {
            this.mob.getNavigation().moveTo((double)var1.getX() + 0.5, (double)var1.getY(), (double)var1.getZ() + 0.5, this.speedModifier);
         }
      } else {
         this.reachedTarget = true;
         --this.tryTicks;
      }

   }

   public boolean shouldRecalculatePath() {
      return this.tryTicks % 40 == 0;
   }

   protected boolean isReachedTarget() {
      return this.reachedTarget;
   }

   protected boolean findNearestBlock() {
      int var1 = this.searchRange;
      int var2 = this.verticalSearchRange;
      BlockPos var3 = this.mob.blockPosition();
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = this.verticalSearchStart; var5 <= var2; var5 = var5 > 0 ? -var5 : 1 - var5) {
         for(int var6 = 0; var6 < var1; ++var6) {
            for(int var7 = 0; var7 <= var6; var7 = var7 > 0 ? -var7 : 1 - var7) {
               for(int var8 = var7 < var6 && var7 > -var6 ? var6 : 0; var8 <= var6; var8 = var8 > 0 ? -var8 : 1 - var8) {
                  var4.setWithOffset(var3, var7, var5 - 1, var8);
                  if (this.mob.isWithinRestriction(var4) && this.isValidTarget(this.mob.level(), var4)) {
                     this.blockPos = var4;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean isValidTarget(LevelReader var1, BlockPos var2);
}
