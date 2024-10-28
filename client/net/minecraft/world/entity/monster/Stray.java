package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class Stray extends AbstractSkeleton {
   public Stray(EntityType<? extends Stray> var1, Level var2) {
      super(var1, var2);
   }

   public static boolean checkStraySpawnRules(EntityType<Stray> var0, ServerLevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3;

      do {
         var5 = var5.above();
      } while(var1.getBlockState(var5).is(Blocks.POWDER_SNOW));

      return checkMonsterSpawnRules(var0, var1, var2, var3, var4) && (MobSpawnType.isSpawner(var2) || var1.canSeeSky(var5.below()));
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

   protected AbstractArrow getArrow(ItemStack var1, float var2, @Nullable ItemStack var3) {
      AbstractArrow var4 = super.getArrow(var1, var2, var3);
      if (var4 instanceof Arrow) {
         ((Arrow)var4).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
      }

      return var4;
   }
}
