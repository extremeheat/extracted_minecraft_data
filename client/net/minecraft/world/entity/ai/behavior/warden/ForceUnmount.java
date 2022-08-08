package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class ForceUnmount extends Behavior<LivingEntity> {
   public ForceUnmount() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return var2.isPassenger();
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      var2.unRide();
   }
}
