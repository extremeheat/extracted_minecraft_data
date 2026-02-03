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
      return BehaviorBuilder.create((Function)((i) -> i.point((Trigger)(level, body, timestamp) -> {
            body.getBrain().updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
            return true;
         })));
   }
}
