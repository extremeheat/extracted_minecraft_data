package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp extends Behavior<LivingEntity> {
   public WakeUp() {
      super(ImmutableMap.of());
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return !var2.getBrain().isActive(Activity.REST) && var2.isSleeping();
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      var2.stopSleeping();
   }
}
