package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class GlowSquid extends Squid {
   private static final EntityDataAccessor<Integer> DATA_DARK_TICKS_REMAINING;

   public GlowSquid(EntityType<? extends GlowSquid> var1, Level var2) {
      super(var1, var2);
   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.GLOW_SQUID_INK;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_DARK_TICKS_REMAINING, 0);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.GLOW_SQUID.create(var1, EntitySpawnReason.BREEDING);
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.GLOW_SQUID_SQUIRT;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GLOW_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.GLOW_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GLOW_SQUID_DEATH;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setDarkTicks(var1.getInt("DarkTicksRemaining"));
   }

   public void aiStep() {
      super.aiStep();
      int var1 = this.getDarkTicksRemaining();
      if (var1 > 0) {
         this.setDarkTicks(var1 - 1);
      }

      this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(0.6), this.getRandomY(), this.getRandomZ(0.6), 0.0, 0.0, 0.0);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      boolean var4 = super.hurtServer(var1, var2, var3);
      if (var4) {
         this.setDarkTicks(100);
      }

      return var4;
   }

   private void setDarkTicks(int var1) {
      this.entityData.set(DATA_DARK_TICKS_REMAINING, var1);
   }

   public int getDarkTicksRemaining() {
      return (Integer)this.entityData.get(DATA_DARK_TICKS_REMAINING);
   }

   public static boolean checkGlowSquidSpawnRules(EntityType<? extends LivingEntity> var0, ServerLevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var3.getY() <= var1.getSeaLevel() - 33 && var1.getRawBrightness(var3, 0) == 0 && var1.getBlockState(var3).is(Blocks.WATER);
   }

   static {
      DATA_DARK_TICKS_REMAINING = SynchedEntityData.defineId(GlowSquid.class, EntityDataSerializers.INT);
   }
}
