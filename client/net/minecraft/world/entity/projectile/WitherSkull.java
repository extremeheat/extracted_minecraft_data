package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkull extends AbstractHurtingProjectile {
   private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);

   public WitherSkull(EntityType<? extends WitherSkull> var1, Level var2) {
      super(var1, var2);
   }

   public WitherSkull(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      super(EntityType.WITHER_SKULL, var2, var3, var5, var7, var1);
   }

   @Override
   protected float getInertia() {
      return this.isDangerous() ? 0.73F : super.getInertia();
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   public float getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5, float var6) {
      return this.isDangerous() && WitherBoss.canDestroy(var4) ? Math.min(0.8F, var6) : var6;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         Entity var2 = var1.getEntity();
         Entity var3 = this.getOwner();
         boolean var4;
         if (var3 instanceof LivingEntity var5) {
            var4 = var2.hurt(this.damageSources().witherSkull(this, (Entity)var5), 8.0F);
            if (var4) {
               if (var2.isAlive()) {
                  this.doEnchantDamageEffects((LivingEntity)var5, var2);
               } else {
                  ((LivingEntity)var5).heal(5.0F);
               }
            }
         } else {
            var4 = var2.hurt(this.damageSources().magic(), 5.0F);
         }

         if (var4 && var2 instanceof LivingEntity var7) {
            byte var6 = 0;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               var6 = 10;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               var6 = 40;
            }

            if (var6 > 0) {
               var7.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * var6, 1), this.getEffectSource());
            }
         }
      }
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, Level.ExplosionInteraction.MOB);
         this.discard();
      }
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(DATA_DANGEROUS, false);
   }

   public boolean isDangerous() {
      return this.entityData.get(DATA_DANGEROUS);
   }

   public void setDangerous(boolean var1) {
      this.entityData.set(DATA_DANGEROUS, var1);
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("dangerous", this.isDangerous());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setDangerous(var1.getBoolean("dangerous"));
   }
}
