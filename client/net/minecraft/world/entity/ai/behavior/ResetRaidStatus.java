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
      return BehaviorBuilder.create((Function)((var0) -> var0.point((Trigger)(var0x, var1, var2) -> {
            if (var0x.random.nextInt(20) != 0) {
               return false;
            } else {
               Brain var4 = var1.getBrain();
               Raid var5 = var0x.getRaidAt(var1.blockPosition());
               if (var5 == null || var5.isStopped() || var5.isLoss()) {
                  var4.setDefaultActivity(Activity.IDLE);
                  var4.updateActivityFromSchedule(var0x.getDayTime(), var0x.getGameTime());
               }

               return true;
            }
         })));
   }
}
