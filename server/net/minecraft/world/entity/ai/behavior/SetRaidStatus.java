package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class SetRaidStatus extends Behavior<LivingEntity> {
   public SetRaidStatus() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return var1.random.nextInt(20) == 0;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      Raid var6 = var1.getRaidAt(var2.blockPosition());
      if (var6 != null) {
         if (var6.hasFirstWaveSpawned() && !var6.isBetweenWaves()) {
            var5.setDefaultActivity(Activity.RAID);
            var5.setActiveActivityIfPossible(Activity.RAID);
         } else {
            var5.setDefaultActivity(Activity.PRE_RAID);
            var5.setActiveActivityIfPossible(Activity.PRE_RAID);
         }
      }

   }
}
