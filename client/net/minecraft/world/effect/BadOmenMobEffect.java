package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

class BadOmenMobEffect extends MobEffect {
   protected BadOmenMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (var1 instanceof ServerPlayer var3) {
         if (!var3.isSpectator()) {
            ServerLevel var4 = var3.serverLevel();
            if (var4.getDifficulty() != Difficulty.PEACEFUL && var4.isVillage(var3.blockPosition())) {
               Raid var5 = var4.getRaidAt(var3.blockPosition());
               if (var5 == null || var5.getRaidOmenLevel() < var5.getMaxRaidOmenLevel()) {
                  var3.addEffect(new MobEffectInstance(MobEffects.RAID_OMEN, 600, var2));
                  var3.setRaidOmenPosition(var3.blockPosition());
                  return false;
               }
            }
         }
      }

      return true;
   }
}
