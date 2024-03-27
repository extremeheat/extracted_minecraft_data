package net.minecraft.world.item.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;

public class WindBurstEnchantment extends Enchantment {
   private static final WindBurstEnchantment.WindBurstEnchantmentDamageCalculator[] damageCalculators = new WindBurstEnchantment.WindBurstEnchantmentDamageCalculator[]{
      new WindBurstEnchantment.WindBurstEnchantmentDamageCalculator(0.5F),
      new WindBurstEnchantment.WindBurstEnchantmentDamageCalculator(0.75F),
      new WindBurstEnchantment.WindBurstEnchantmentDamageCalculator(1.0F)
   };

   public WindBurstEnchantment() {
      super(
         Enchantment.definition(
            ItemTags.MACE_ENCHANTABLE,
            2,
            3,
            Enchantment.dynamicCost(15, 9),
            Enchantment.dynamicCost(65, 9),
            4,
            FeatureFlagSet.of(FeatureFlags.UPDATE_1_21),
            EquipmentSlot.MAINHAND
         )
      );
   }

   @Override
   public void doPostItemStackHurt(LivingEntity var1, Entity var2, int var3) {
      var1.level()
         .explode(
            null,
            null,
            damageCalculators[var3 - 1],
            var1.getX(),
            var1.getY(),
            var1.getZ(),
            3.5F,
            false,
            Level.ExplosionInteraction.BLOW,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            SoundEvents.WIND_CHARGE_BURST
         );
   }

   static final class WindBurstEnchantmentDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
      private final float knockBackPower;

      public WindBurstEnchantmentDamageCalculator(float var1) {
         super();
         this.knockBackPower = var1;
      }

      @Override
      public float getKnockbackMultiplier() {
         return this.knockBackPower;
      }
   }
}
