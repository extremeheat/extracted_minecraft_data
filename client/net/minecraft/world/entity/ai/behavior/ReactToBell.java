package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell {
   public ReactToBell() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.HEARD_BELL_TIME)).apply(i, (heartTime) -> (level, body, timestamp) -> {
               Raid nearbyRaid = level.getRaidAt(body.blockPosition());
               if (nearbyRaid == null) {
                  body.getBrain().setActiveActivityIfPossible(Activity.HIDE);
               }

               return true;
            })));
   }
}
