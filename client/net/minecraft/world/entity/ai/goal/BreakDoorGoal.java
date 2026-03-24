package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;

public class BreakDoorGoal extends DoorInteractGoal {
   private static final int DEFAULT_DOOR_BREAK_TIME = 240;
   private final Predicate<Difficulty> validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress;
   protected int doorBreakTime;

   public BreakDoorGoal(final Mob mob, final Predicate<Difficulty> validDifficulties) {
      super(mob);
      this.lastBreakProgress = -1;
      this.doorBreakTime = -1;
      this.validDifficulties = validDifficulties;
   }

   public BreakDoorGoal(final Mob mob, final int seconds, final Predicate<Difficulty> validDifficulties) {
      this(mob, validDifficulties);
      this.doorBreakTime = seconds;
   }

   protected int getDoorBreakTime() {
      return Math.max(240, this.doorBreakTime);
   }

   public boolean canUse() {
      if (!super.canUse()) {
         return false;
      } else if (!(Boolean)getServerLevel(this.mob).getGameRules().get(GameRules.MOB_GRIEFING)) {
         return false;
      } else {
         return this.isValidDifficulty(this.mob.level().getDifficulty()) && !this.isOpen();
      }
   }

   public void start() {
      super.start();
      this.breakTime = 0;
   }

   public boolean canContinueToUse() {
      return this.breakTime <= this.getDoorBreakTime() && !this.isOpen() && this.doorPos.closerToCenterThan(this.mob.position(), 2.0) && this.isValidDifficulty(this.mob.level().getDifficulty());
   }

   public void stop() {
      super.stop();
      this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.level().levelEvent(1019, this.doorPos, 0);
         if (!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      ++this.breakTime;
      int progress = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);
      if (progress != this.lastBreakProgress) {
         this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, progress);
         this.lastBreakProgress = progress;
      }

      if (this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level().getDifficulty())) {
         this.mob.level().removeBlock(this.doorPos, false);
         this.mob.level().levelEvent(1021, this.doorPos, 0);
         this.mob.level().levelEvent(2001, this.doorPos, Block.getId(this.mob.level().getBlockState(this.doorPos)));
      }

   }

   private boolean isValidDifficulty(final Difficulty difficulty) {
      return this.validDifficulties.test(difficulty);
   }
}
