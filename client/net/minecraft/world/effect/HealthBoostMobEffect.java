package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class HealthBoostMobEffect extends MobEffect {
   public HealthBoostMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public void removeAttributeModifiers(LivingEntity var1, AttributeMap var2, int var3) {
      super.removeAttributeModifiers(var1, var2, var3);
      if (var1.getHealth() > var1.getMaxHealth()) {
         var1.setHealth(var1.getMaxHealth());
      }

   }
}
