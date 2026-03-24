package net.minecraft.world.entity.monster.skeleton;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class Parched extends AbstractSkeleton {
   public Parched(final EntityType<? extends AbstractSkeleton> type, final Level level) {
      super(type, level);
   }

   protected AbstractArrow getArrow(final ItemStack projectile, final float power, final @Nullable ItemStack firingWeapon) {
      AbstractArrow arrow = super.getArrow(projectile, power, firingWeapon);
      if (arrow instanceof Arrow) {
         ((Arrow)arrow).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
      }

      return arrow;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PARCHED_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.PARCHED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PARCHED_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.PARCHED_STEP;
   }

   protected int getHardAttackInterval() {
      return 50;
   }

   protected int getAttackInterval() {
      return 70;
   }

   public boolean canBeAffected(final MobEffectInstance newEffect) {
      return newEffect.getEffect() == MobEffects.WEAKNESS ? false : super.canBeAffected(newEffect);
   }
}
