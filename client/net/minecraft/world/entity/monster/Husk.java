package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Husk extends Zombie {
   public Husk(EntityType<? extends Husk> var1, Level var2) {
      super(var1, var2);
   }

   public static boolean checkHuskSpawnRules(EntityType<Husk> var0, ServerLevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return checkMonsterSpawnRules(var0, var1, var2, var3, var4) && (EntitySpawnReason.isSpawner(var2) || var1.canSeeSky(var3));
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.HUSK_STEP;
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      boolean var3 = super.doHurtTarget(var1, var2);
      if (var3 && this.getMainHandItem().isEmpty() && var2 instanceof LivingEntity) {
         float var4 = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)var4), this);
      }

      return var3;
   }

   protected boolean convertsInWater() {
      return true;
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.ZOMBIE);
      if (!this.isSilent()) {
         this.level().levelEvent((Player)null, 1041, this.blockPosition(), 0);
      }

   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }
}
