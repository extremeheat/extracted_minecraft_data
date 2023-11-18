package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;

class BadOmenMobEffect extends MobEffect {
   protected BadOmenMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }

   @Override
   public void applyEffectTick(LivingEntity var1, int var2) {
      super.applyEffectTick(var1, var2);
      if (var1 instanceof ServerPlayer var3 && !var1.isSpectator()) {
         ServerLevel var4 = ((ServerPlayer)var3).serverLevel();
         if (var4.getDifficulty() == Difficulty.PEACEFUL) {
            return;
         }

         if (var4.isVillage(var1.blockPosition())) {
            var4.getRaids().createOrExtendRaid((ServerPlayer)var3);
         }
      }
   }
}
