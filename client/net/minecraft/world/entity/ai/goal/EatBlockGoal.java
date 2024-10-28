package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class EatBlockGoal extends Goal {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final Predicate<BlockState> IS_TALL_GRASS;
   private final Mob mob;
   private final Level level;
   private int eatAnimationTick;

   public EatBlockGoal(Mob var1) {
      super();
      this.mob = var1;
      this.level = var1.level();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
   }

   public boolean canUse() {
      if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos var1 = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(var1))) {
            return true;
         } else {
            return this.level.getBlockState(var1.below()).is(Blocks.GRASS_BLOCK);
         }
      }
   }

   public void start() {
      this.eatAnimationTick = this.adjustedTickDelay(40);
      this.level.broadcastEntityEvent(this.mob, (byte)10);
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.eatAnimationTick = 0;
   }

   public boolean canContinueToUse() {
      return this.eatAnimationTick > 0;
   }

   public int getEatAnimationTick() {
      return this.eatAnimationTick;
   }

   public void tick() {
      this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      if (this.eatAnimationTick == this.adjustedTickDelay(4)) {
         BlockPos var1 = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(var1))) {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               this.level.destroyBlock(var1, false);
            }

            this.mob.ate();
         } else {
            BlockPos var2 = var1.below();
            if (this.level.getBlockState(var2).is(Blocks.GRASS_BLOCK)) {
               if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                  this.level.levelEvent(2001, var2, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                  this.level.setBlock(var2, Blocks.DIRT.defaultBlockState(), 2);
               }

               this.mob.ate();
            }
         }

      }
   }

   static {
      IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.SHORT_GRASS);
   }
}
