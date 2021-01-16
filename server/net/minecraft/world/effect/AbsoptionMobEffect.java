package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AbsoptionMobEffect extends MobEffect {
   protected AbsoptionMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public void removeAttributeModifiers(LivingEntity var1, AttributeMap var2, int var3) {
      var1.setAbsorptionAmount(var1.getAbsorptionAmount() - (float)(4 * (var3 + 1)));
      super.removeAttributeModifiers(var1, var2, var3);
   }

   public void addAttributeModifiers(LivingEntity var1, AttributeMap var2, int var3) {
      var1.setAbsorptionAmount(var1.getAbsorptionAmount() + (float)(4 * (var3 + 1)));
      super.addAttributeModifiers(var1, var2, var3);
   }
}
