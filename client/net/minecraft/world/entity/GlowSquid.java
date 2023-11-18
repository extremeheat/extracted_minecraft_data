package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class GlowSquid extends Squid {
   private static final EntityDataAccessor<Integer> DATA_DARK_TICKS_REMAINING = SynchedEntityData.defineId(GlowSquid.class, EntityDataSerializers.INT);

   public GlowSquid(EntityType<? extends GlowSquid> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected ParticleOptions getInkParticle() {
      return ParticleTypes.GLOW_SQUID_INK;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_DARK_TICKS_REMAINING, 0);
   }

   @Override
   protected SoundEvent getSquirtSound() {
      return SoundEvents.GLOW_SQUID_SQUIRT;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.GLOW_SQUID_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.GLOW_SQUID_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.GLOW_SQUID_DEATH;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setDarkTicks(var1.getInt("DarkTicksRemaining"));
   }

   @Override
   public void aiStep() {
      super.aiStep();
      int var1 = this.getDarkTicksRemaining();
      if (var1 > 0) {
         this.setDarkTicks(var1 - 1);
      }

      this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(0.6), this.getRandomY(), this.getRandomZ(0.6), 0.0, 0.0, 0.0);
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (var3) {
         this.setDarkTicks(100);
      }

      return var3;
   }

   private void setDarkTicks(int var1) {
      this.entityData.set(DATA_DARK_TICKS_REMAINING, var1);
   }

   public int getDarkTicksRemaining() {
      return this.entityData.get(DATA_DARK_TICKS_REMAINING);
   }

   public static boolean checkGlowSquidSpawnRules(
      EntityType<? extends LivingEntity> var0, ServerLevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4
   ) {
      return var3.getY() <= var1.getSeaLevel() - 33 && var1.getRawBrightness(var3, 0) == 0 && var1.getBlockState(var3).is(Blocks.WATER);
   }
}
