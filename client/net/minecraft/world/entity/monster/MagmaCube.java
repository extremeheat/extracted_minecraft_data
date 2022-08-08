package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public class MagmaCube extends Slime {
   public MagmaCube(EntityType<? extends MagmaCube> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean checkMagmaCubeSpawnRules(EntityType<MagmaCube> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this) && !var1.containsAnyLiquid(this.getBoundingBox());
   }

   public void setSize(int var1, boolean var2) {
      super.setSize(var1, var2);
      this.getAttribute(Attributes.ARMOR).setBaseValue((double)(var1 * 3));
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.FLAME;
   }

   public boolean isOnFire() {
      return false;
   }

   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.9F;
   }

   protected void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, (double)(this.getJumpPower() + (float)this.getSize() * 0.1F), var1.z);
      this.hasImpulse = true;
   }

   protected void jumpInLiquid(TagKey<Fluid> var1) {
      if (var1 == FluidTags.LAVA) {
         Vec3 var2 = this.getDeltaMovement();
         this.setDeltaMovement(var2.x, (double)(0.22F + (float)this.getSize() * 0.05F), var2.z);
         this.hasImpulse = true;
      } else {
         super.jumpInLiquid(var1);
      }

   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   protected boolean isDealsDamage() {
      return this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return super.getAttackDamage() + 2.0F;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_HURT_SMALL : SoundEvents.MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_DEATH_SMALL : SoundEvents.MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny() ? SoundEvents.MAGMA_CUBE_SQUISH_SMALL : SoundEvents.MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.MAGMA_CUBE_JUMP;
   }
}
