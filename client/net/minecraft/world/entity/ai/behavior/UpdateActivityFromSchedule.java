package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
   public UpdateActivityFromSchedule() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.point((Trigger)(var0x, var1, var2) -> {
            var1.getBrain().updateActivityFromSchedule(var0x.getDayTime(), var0x.getGameTime());
            return true;
         })));
   }
}
