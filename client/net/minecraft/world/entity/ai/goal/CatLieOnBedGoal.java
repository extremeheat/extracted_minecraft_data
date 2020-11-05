package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatLieOnBedGoal(Cat var1, double var2, int var4) {
      super(var1, var2, var4, 6);
      this.cat = var1;
      this.verticalSearchStart = -2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isLying() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   protected int nextStartTick(PathfinderMob var1) {
      return 40;
   }

   public void stop() {
      super.stop();
      this.cat.setLying(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(false);
      if (!this.isReachedTarget()) {
         this.cat.setLying(false);
      } else if (!this.cat.isLying()) {
         this.cat.setLying(true);
      }

   }

   protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
      return var1.isEmptyBlock(var2.above()) && var1.getBlockState(var2).is(BlockTags.BEDS);
   }
}
