package net.minecraft.world.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.flag.FeatureFlags;

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
            if (!var4.enabledFeatures().contains(FeatureFlags.UPDATE_1_21)) {
               return this.legacyApplyEffectTick(var3, var4);
            }

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

   private boolean legacyApplyEffectTick(ServerPlayer var1, ServerLevel var2) {
      BlockPos var3 = var1.blockPosition();
      if (var2.getDifficulty() != Difficulty.PEACEFUL && var2.isVillage(var3)) {
         return var2.getRaids().createOrExtendRaid(var1, var3) == null;
      } else {
         return true;
      }
   }
}
