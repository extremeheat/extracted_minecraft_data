package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class UpdateActivityFromSchedule extends Behavior<LivingEntity> {
   public UpdateActivityFromSchedule() {
      super(ImmutableMap.of());
   }

   @Override
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      var2.getBrain().updateActivityFromSchedule(var1.getDayTime(), var1.getGameTime());
   }
}
