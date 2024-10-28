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

   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2 instanceof ServerPlayer var4) {
         if (!var4.isSpectator() && var1.getDifficulty() != Difficulty.PEACEFUL && var1.isVillage(var4.blockPosition())) {
            Raid var5 = var1.getRaidAt(var4.blockPosition());
            if (var5 == null || var5.getRaidOmenLevel() < var5.getMaxRaidOmenLevel()) {
               var4.addEffect(new MobEffectInstance(MobEffects.RAID_OMEN, 600, var3));
               var4.setRaidOmenPosition(var4.blockPosition());
               return false;
            }
         }
      }

      return true;
   }
}
