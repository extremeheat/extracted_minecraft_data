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
   public Parched(EntityType<? extends AbstractSkeleton> var1, Level var2) {
      super(var1, var2);
   }

   protected AbstractArrow getArrow(ItemStack var1, float var2, @Nullable ItemStack var3) {
      AbstractArrow var4 = super.getArrow(var1, var2, var3);
      if (var4 instanceof Arrow) {
         ((Arrow)var4).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
      }

      return var4;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PARCHED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
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

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.WEAKNESS ? false : super.canBeAffected(var1);
   }
}
