package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ResetRaidStatus {
   public ResetRaidStatus() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.point((Trigger)(level, body, timestamp) -> {
            if (level.getRandom().nextInt(20) != 0) {
               return false;
            } else {
               Brain<?> brain = body.getBrain();
               Raid nearbyRaid = level.getRaidAt(body.blockPosition());
               if (nearbyRaid == null || nearbyRaid.isStopped() || nearbyRaid.isLoss()) {
                  brain.setDefaultActivity(Activity.IDLE);
                  brain.updateActivityFromSchedule(level.environmentAttributes(), level.getGameTime(), body.position());
               }

               return true;
            }
         })));
   }
}
