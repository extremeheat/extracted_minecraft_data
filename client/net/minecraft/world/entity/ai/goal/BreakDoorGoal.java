package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

public class BreakDoorGoal extends DoorInteractGoal {
   private static final int DEFAULT_DOOR_BREAK_TIME = 240;
   private final Predicate<Difficulty> validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress = -1;
   protected int doorBreakTime = -1;

   public BreakDoorGoal(Mob var1, Predicate<Difficulty> var2) {
      super(var1);
      this.validDifficulties = var2;
   }

   public BreakDoorGoal(Mob var1, int var2, Predicate<Difficulty> var3) {
      this(var1, var3);
      this.doorBreakTime = var2;
   }

   protected int getDoorBreakTime() {
      return Math.max(240, this.doorBreakTime);
   }

   @Override
   public boolean canUse() {
      if (!super.canUse()) {
         return false;
      } else {
         return !this.mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            ? false
            : this.isValidDifficulty(this.mob.level().getDifficulty()) && !this.isOpen();
      }
   }

   @Override
   public void start() {
      super.start();
      this.breakTime = 0;
   }

   @Override
   public boolean canContinueToUse() {
      return this.breakTime <= this.getDoorBreakTime()
         && !this.isOpen()
         && this.doorPos.closerToCenterThan(this.mob.position(), 2.0)
         && this.isValidDifficulty(this.mob.level().getDifficulty());
   }

   @Override
   public void stop() {
      super.stop();
      this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.level().levelEvent(1019, this.doorPos, 0);
         if (!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      this.breakTime++;
      int var1 = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);
      if (var1 != this.lastBreakProgress) {
         this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, var1);
         this.lastBreakProgress = var1;
      }

      if (this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level().getDifficulty())) {
         this.mob.level().removeBlock(this.doorPos, false);
         this.mob.level().levelEvent(1021, this.doorPos, 0);
         this.mob.level().levelEvent(2001, this.doorPos, Block.getId(this.mob.level().getBlockState(this.doorPos)));
      }
   }

   private boolean isValidDifficulty(Difficulty var1) {
      return this.validDifficulties.test(var1);
   }
}
