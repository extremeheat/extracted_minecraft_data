package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ResetRaidStatus extends Behavior {
   public ResetRaidStatus() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return var1.random.nextInt(20) == 0;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      Raid var6 = var1.getRaidAt(new BlockPos(var2));
      if (var6 == null || var6.isStopped() || var6.isLoss()) {
         var5.setDefaultActivity(Activity.IDLE);
         var5.updateActivity(var1.getDayTime(), var1.getGameTime());
      }

   }
}
