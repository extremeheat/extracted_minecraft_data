package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

public class LocateHidingPlaceDuringRaid extends LocateHidingPlace {
   public LocateHidingPlaceDuringRaid(int var1, float var2) {
      super(var1, var2, 1);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Raid var3 = var1.getRaidAt(var2.blockPosition());
      return super.checkExtraStartConditions(var1, var2) && var3 != null && var3.isActive() && !var3.isVictory() && !var3.isLoss();
   }
}
