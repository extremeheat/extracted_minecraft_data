package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

public class GoOutsideToCelebrate extends MoveToSkySeeingSpot {
   public GoOutsideToCelebrate(float var1) {
      super(var1);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Raid var3 = var1.getRaidAt(var2.blockPosition());
      return var3 != null && var3.isVictory() && super.checkExtraStartConditions(var1, var2);
   }
}
