package net.minecraft.world.entity.projectile.hurtingprojectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WitherSkull extends AbstractHurtingProjectile {
   private static final EntityDataAccessor<Boolean> DATA_DANGEROUS;
   private static final boolean DEFAULT_DANGEROUS = false;

   public WitherSkull(final EntityType<? extends WitherSkull> type, final Level level) {
      super(type, level);
   }

   public WitherSkull(final Level level, final LivingEntity mob, final Vec3 direction) {
      super(EntityType.WITHER_SKULL, mob, direction, level);
   }

   protected float getInertia() {
      return this.isDangerous() ? 0.73F : super.getInertia();
   }

   public boolean isOnFire() {
      return false;
   }

   public float getBlockExplosionResistance(final Explosion explosion, final BlockGetter level, final BlockPos pos, final BlockState block, final FluidState fluid, final float resistance) {
      return this.isDangerous() && WitherBoss.canDestroy(block) ? Math.min(0.8F, resistance) : resistance;
   }

   protected void onHitEntity(final EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         Entity var8 = hitResult.getEntity();
         Entity owner = this.getOwner();
         boolean wasHurt;
         if (owner instanceof LivingEntity livingOwner) {
            DamageSource damageSource = this.damageSources().witherSkull(this, livingOwner);
            wasHurt = var8.hurtServer(serverLevel, damageSource, 8.0F);
            if (wasHurt) {
               if (var8.isAlive()) {
                  EnchantmentHelper.doPostAttackEffects(serverLevel, var8, damageSource);
               } else {
                  livingOwner.heal(5.0F);
               }
            }
         } else {
            wasHurt = var8.hurtServer(serverLevel, this.damageSources().magic(), 5.0F);
         }

         if (wasHurt && var8 instanceof LivingEntity livingEntity) {
            int witherSeconds = 0;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               witherSeconds = 10;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               witherSeconds = 40;
            }

            if (witherSeconds > 0) {
               livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * witherSeconds, 1), this.getEffectSource());
            }
         }

      }
   }

   protected void onHit(final HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide()) {
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, Level.ExplosionInteraction.MOB);
         this.discard();
      }

   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_DANGEROUS, false);
   }

   public boolean isDangerous() {
      return (Boolean)this.entityData.get(DATA_DANGEROUS);
   }

   public void setDangerous(final boolean value) {
      this.entityData.set(DATA_DANGEROUS, value);
   }

   protected boolean shouldBurn() {
      return false;
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("dangerous", this.isDangerous());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setDangerous(input.getBooleanOr("dangerous", false));
   }

   static {
      DATA_DANGEROUS = SynchedEntityData.<Boolean>defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);
   }
}
