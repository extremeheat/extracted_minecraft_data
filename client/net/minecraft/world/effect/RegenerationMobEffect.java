package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;

class RegenerationMobEffect extends MobEffect {
   protected RegenerationMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (var1.getHealth() < var1.getMaxHealth()) {
         var1.heal(1.0F);
      }

      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      int var3 = 50 >> var2;
      return var3 > 0 ? var1 % var3 == 0 : true;
   }
}
