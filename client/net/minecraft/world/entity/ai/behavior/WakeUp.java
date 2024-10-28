package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp {
   public WakeUp() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.point((var0x, var1, var2) -> {
            if (!var1.getBrain().isActive(Activity.REST) && var1.isSleeping()) {
               var1.stopSleeping();
               return true;
            } else {
               return false;
            }
         });
      });
   }
}
