package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Stray extends AbstractSkeleton {
   public Stray(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public static boolean checkStraySpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return checkMonsterSpawnRules(var0, var1, var2, var3, var4) && (var2 == MobSpawnType.SPAWNER || var1.canSeeSky(var3));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRAY_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.STRAY_STEP;
   }

   protected AbstractArrow getArrow(ItemStack var1, float var2) {
      AbstractArrow var3 = super.getArrow(var1, var2);
      if (var3 instanceof Arrow) {
         ((Arrow)var3).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
      }

      return var3;
   }
}
