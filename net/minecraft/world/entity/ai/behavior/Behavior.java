package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior {
   private final Map entryCondition;
   private Behavior.Status status;
   private long endTimestamp;
   private final int minDuration;
   private final int maxDuration;

   public Behavior(Map var1) {
      this(var1, 60);
   }

   public Behavior(Map var1, int var2) {
      this(var1, var2, var2);
   }

   public Behavior(Map var1, int var2, int var3) {
      this.status = Behavior.Status.STOPPED;
      this.minDuration = var2;
      this.maxDuration = var3;
      this.entryCondition = var1;
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(ServerLevel var1, LivingEntity var2, long var3) {
      if (this.hasRequiredMemories(var2) && this.checkExtraStartConditions(var1, var2)) {
         this.status = Behavior.Status.RUNNING;
         int var5 = this.minDuration + var1.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
         this.endTimestamp = var3 + (long)var5;
         this.start(var1, var2, var3);
         return true;
      } else {
         return false;
      }
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
   }

   public final void tickOrStop(ServerLevel var1, LivingEntity var2, long var3) {
      if (!this.timedOut(var3) && this.canStillUse(var1, var2, var3)) {
         this.tick(var1, var2, var3);
      } else {
         this.doStop(var1, var2, var3);
      }

   }

   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
   }

   public final void doStop(ServerLevel var1, LivingEntity var2, long var3) {
      this.status = Behavior.Status.STOPPED;
      this.stop(var1, var2, var3);
   }

   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
   }

   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return false;
   }

   protected boolean timedOut(long var1) {
      return var1 > this.endTimestamp;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(LivingEntity var1) {
      return this.entryCondition.entrySet().stream().allMatch((var1x) -> {
         MemoryModuleType var2 = (MemoryModuleType)var1x.getKey();
         MemoryStatus var3 = (MemoryStatus)var1x.getValue();
         return var1.getBrain().checkMemory(var2, var3);
      });
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}
