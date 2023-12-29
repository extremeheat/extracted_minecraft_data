package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

class SaturationMobEffect extends InstantenousMobEffect {
   protected SaturationMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void applyEffectTick(LivingEntity var1, int var2) {
      super.applyEffectTick(var1, var2);
      if (!var1.level().isClientSide && var1 instanceof Player var3) {
         var3.getFoodData().eat(var2 + 1, 1.0F);
      }
   }
}
