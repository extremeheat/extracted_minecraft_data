package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

class HungerMobEffect extends MobEffect {
   protected HungerMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void applyEffectTick(LivingEntity var1, int var2) {
      super.applyEffectTick(var1, var2);
      if (var1 instanceof Player var3) {
         var3.causeFoodExhaustion(0.005F * (float)(var2 + 1));
      }
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }
}
