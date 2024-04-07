package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DoNothing implements BehaviorControl<LivingEntity> {
   private final int minDuration;
   private final int maxDuration;
   private Behavior.Status status = Behavior.Status.STOPPED;
   private long endTimestamp;

   public DoNothing(int var1, int var2) {
      super();
      this.minDuration = var1;
      this.maxDuration = var2;
   }

   @Override
   public Behavior.Status getStatus() {
      return this.status;
   }

   @Override
   public final boolean tryStart(ServerLevel var1, LivingEntity var2, long var3) {
      this.status = Behavior.Status.RUNNING;
      int var5 = this.minDuration + var1.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
      this.endTimestamp = var3 + (long)var5;
      return true;
   }

   @Override
   public final void tickOrStop(ServerLevel var1, LivingEntity var2, long var3) {
      if (var3 > this.endTimestamp) {
         this.doStop(var1, var2, var3);
      }
   }

   @Override
   public final void doStop(ServerLevel var1, LivingEntity var2, long var3) {
      this.status = Behavior.Status.STOPPED;
   }

   @Override
   public String debugString() {
      return this.getClass().getSimpleName();
   }
}
