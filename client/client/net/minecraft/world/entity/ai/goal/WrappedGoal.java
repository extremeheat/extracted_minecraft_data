package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class WrappedGoal extends Goal {
   private final Goal goal;
   private final int priority;
   private boolean isRunning;

   public WrappedGoal(int var1, Goal var2) {
      super();
      this.priority = var1;
      this.goal = var2;
   }

   public boolean canBeReplacedBy(WrappedGoal var1) {
      return this.isInterruptable() && var1.getPriority() < this.getPriority();
   }

   @Override
   public boolean canUse() {
      return this.goal.canUse();
   }

   @Override
   public boolean canContinueToUse() {
      return this.goal.canContinueToUse();
   }

   @Override
   public boolean isInterruptable() {
      return this.goal.isInterruptable();
   }

   @Override
   public void start() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.goal.start();
      }
   }

   @Override
   public void stop() {
      if (this.isRunning) {
         this.isRunning = false;
         this.goal.stop();
      }
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return this.goal.requiresUpdateEveryTick();
   }

   @Override
   protected int adjustedTickDelay(int var1) {
      return this.goal.adjustedTickDelay(var1);
   }

   @Override
   public void tick() {
      this.goal.tick();
   }

   @Override
   public void setFlags(EnumSet<Goal.Flag> var1) {
      this.goal.setFlags(var1);
   }

   @Override
   public EnumSet<Goal.Flag> getFlags() {
      return this.goal.getFlags();
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public int getPriority() {
      return this.priority;
   }

   public Goal getGoal() {
      return this.goal;
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? this.goal.equals(((WrappedGoal)var1).goal) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.goal.hashCode();
   }
}
