package net.minecraft.world.item.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;

public class WindBurstEnchantment extends Enchantment {
   public WindBurstEnchantment() {
      super(Enchantment.definition(ItemTags.MACE_ENCHANTABLE, 2, 3, Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4, FeatureFlagSet.of(FeatureFlags.UPDATE_1_21), EquipmentSlot.MAINHAND));
   }

   public void doPostItemStackHurt(LivingEntity var1, Entity var2, int var3) {
      float var4 = 0.25F + 0.25F * (float)var3;
      var1.level().explode((Entity)null, (DamageSource)null, new WindBurstEnchantmentDamageCalculator(var4), var1.getX(), var1.getY(), var1.getZ(), 3.5F, false, Level.ExplosionInteraction.BLOW, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
   }

   public boolean isTradeable() {
      return false;
   }

   public boolean isDiscoverable() {
      return false;
   }

   private static final class WindBurstEnchantmentDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
      private final float knockBackPower;

      public WindBurstEnchantmentDamageCalculator(float var1) {
         super();
         this.knockBackPower = var1;
      }

      public float getKnockbackMultiplier(Entity var1) {
         boolean var10000;
         label17: {
            if (var1 instanceof Player var3) {
               if (var3.getAbilities().flying) {
                  var10000 = true;
                  break label17;
               }
            }

            var10000 = false;
         }

         boolean var2 = var10000;
         return !var2 ? this.knockBackPower : 0.0F;
      }
   }
}
