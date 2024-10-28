package net.minecraft.world;

import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class TickRateManager {
   public static final float MIN_TICKRATE = 1.0F;
   protected float tickrate = 20.0F;
   protected long nanosecondsPerTick;
   protected int frozenTicksToRun;
   protected boolean runGameElements;
   protected boolean isFrozen;

   public TickRateManager() {
      super();
      this.nanosecondsPerTick = TimeUtil.NANOSECONDS_PER_SECOND / 20L;
      this.frozenTicksToRun = 0;
      this.runGameElements = true;
      this.isFrozen = false;
   }

   public void setTickRate(float var1) {
      this.tickrate = Math.max(var1, 1.0F);
      this.nanosecondsPerTick = (long)((double)TimeUtil.NANOSECONDS_PER_SECOND / (double)this.tickrate);
   }

   public float tickrate() {
      return this.tickrate;
   }

   public float millisecondsPerTick() {
      return (float)this.nanosecondsPerTick / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND;
   }

   public long nanosecondsPerTick() {
      return this.nanosecondsPerTick;
   }

   public boolean runsNormally() {
      return this.runGameElements;
   }

   public boolean isSteppingForward() {
      return this.frozenTicksToRun > 0;
   }

   public void setFrozenTicksToRun(int var1) {
      this.frozenTicksToRun = var1;
   }

   public int frozenTicksToRun() {
      return this.frozenTicksToRun;
   }

   public void setFrozen(boolean var1) {
      this.isFrozen = var1;
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   public void tick() {
      this.runGameElements = !this.isFrozen || this.frozenTicksToRun > 0;
      if (this.frozenTicksToRun > 0) {
         --this.frozenTicksToRun;
      }

   }

   public boolean isEntityFrozen(Entity var1) {
      return !this.runsNormally() && !(var1 instanceof Player) && var1.countPlayerPassengers() <= 0;
   }
}
