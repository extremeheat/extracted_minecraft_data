package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp {
   public WakeUp() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.point((Trigger)(var0x, var1, var2) -> {
            if (!var1.getBrain().isActive(Activity.REST) && var1.isSleeping()) {
               var1.stopSleeping();
               return true;
            } else {
               return false;
            }
         })));
   }
}
