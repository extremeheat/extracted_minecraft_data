package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;

public class RunSometimes<E extends LivingEntity> extends Behavior<E> {
   private boolean resetTicks;
   private boolean wasRunning;
   private final UniformInt interval;
   private final Behavior<? super E> wrappedBehavior;
   private int ticksUntilNextStart;

   public RunSometimes(Behavior<? super E> var1, UniformInt var2) {
      this(var1, false, var2);
   }

   public RunSometimes(Behavior<? super E> var1, boolean var2, UniformInt var3) {
      super(var1.entryCondition);
      this.wrappedBehavior = var1;
      this.resetTicks = !var2;
      this.interval = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      if (!this.wrappedBehavior.checkExtraStartConditions(var1, var2)) {
         return false;
      } else {
         if (this.resetTicks) {
            this.resetTicksUntilNextStart(var1);
            this.resetTicks = false;
         }

         if (this.ticksUntilNextStart > 0) {
            --this.ticksUntilNextStart;
         }

         return !this.wasRunning && this.ticksUntilNextStart == 0;
      }
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      this.wrappedBehavior.start(var1, var2, var3);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return this.wrappedBehavior.canStillUse(var1, var2, var3);
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      this.wrappedBehavior.tick(var1, var2, var3);
      this.wasRunning = this.wrappedBehavior.getStatus() == Behavior.Status.RUNNING;
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      this.resetTicksUntilNextStart(var1);
      this.wrappedBehavior.stop(var1, var2, var3);
   }

   private void resetTicksUntilNextStart(ServerLevel var1) {
      this.ticksUntilNextStart = this.interval.sample(var1.random);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   public String toString() {
      return "RunSometimes: " + this.wrappedBehavior;
   }
}
