package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import org.jspecify.annotations.Nullable;

public class WrappedGoal extends Goal {
   private final Goal goal;
   private final int priority;
   private boolean isRunning;

   public WrappedGoal(final int priority, final Goal goal) {
      super();
      this.priority = priority;
      this.goal = goal;
   }

   public boolean canBeReplacedBy(final WrappedGoal goal) {
      return this.isInterruptable() && goal.getPriority() < this.getPriority();
   }

   public boolean canUse() {
      return this.goal.canUse();
   }

   public boolean canContinueToUse() {
      return this.goal.canContinueToUse();
   }

   public boolean isInterruptable() {
      return this.goal.isInterruptable();
   }

   public void start() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.goal.start();
      }
   }

   public void stop() {
      if (this.isRunning) {
         this.isRunning = false;
         this.goal.stop();
      }
   }

   public boolean requiresUpdateEveryTick() {
      return this.goal.requiresUpdateEveryTick();
   }

   protected int adjustedTickDelay(final int ticks) {
      return this.goal.adjustedTickDelay(ticks);
   }

   public void tick() {
      this.goal.tick();
   }

   public void setFlags(final EnumSet<Goal.Flag> requiredControlFlags) {
      this.goal.setFlags(requiredControlFlags);
   }

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

   public boolean equals(final @Nullable Object o) {
      if (this == o) {
         return true;
      } else {
         return o != null && this.getClass() == o.getClass() ? this.goal.equals(((WrappedGoal)o).goal) : false;
      }
   }

   public int hashCode() {
      return this.goal.hashCode();
   }
}
