package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DoNothing extends Behavior<LivingEntity> {
   public DoNothing(int var1, int var2) {
      super(ImmutableMap.of(), var1, var2);
   }

   @Override
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return true;
   }
}
