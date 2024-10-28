package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public abstract class OneShot<E extends LivingEntity> implements BehaviorControl<E>, Trigger<E> {
   private Behavior.Status status;

   public OneShot() {
      super();
      this.status = Behavior.Status.STOPPED;
   }

   public final Behavior.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(ServerLevel var1, E var2, long var3) {
      if (this.trigger(var1, var2, var3)) {
         this.status = Behavior.Status.RUNNING;
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(ServerLevel var1, E var2, long var3) {
      this.doStop(var1, var2, var3);
   }

   public final void doStop(ServerLevel var1, E var2, long var3) {
      this.status = Behavior.Status.STOPPED;
   }

   public String debugString() {
      return this.getClass().getSimpleName();
   }
}
