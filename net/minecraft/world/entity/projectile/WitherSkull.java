package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkull extends AbstractHurtingProjectile {
   private static final EntityDataAccessor DATA_DANGEROUS;

   public WitherSkull(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public WitherSkull(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      super(EntityType.WITHER_SKULL, var2, var3, var5, var7, var1);
   }

   public WitherSkull(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.WITHER_SKULL, var2, var4, var6, var8, var10, var12, var1);
   }

   protected float getInertia() {
      return this.isDangerous() ? 0.73F : super.getInertia();
   }

   public boolean isOnFire() {
      return false;
   }

   public float getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5, float var6) {
      return this.isDangerous() && WitherBoss.canDestroy(var4) ? Math.min(0.8F, var6) : var6;
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         if (var1.getType() == HitResult.Type.ENTITY) {
            Entity var2 = ((EntityHitResult)var1).getEntity();
            if (this.owner != null) {
               if (var2.hurt(DamageSource.mobAttack(this.owner), 8.0F)) {
                  if (var2.isAlive()) {
                     this.doEnchantDamageEffects(this.owner, var2);
                  } else {
                     this.owner.heal(5.0F);
                  }
               }
            } else {
               var2.hurt(DamageSource.MAGIC, 5.0F);
            }

            if (var2 instanceof LivingEntity) {
               byte var3 = 0;
               if (this.level.getDifficulty() == Difficulty.NORMAL) {
                  var3 = 10;
               } else if (this.level.getDifficulty() == Difficulty.HARD) {
                  var3 = 40;
               }

               if (var3 > 0) {
                  ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * var3, 1));
               }
            }
         }

         Explosion.BlockInteraction var4 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
         this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, var4);
         this.remove();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_DANGEROUS, false);
   }

   public boolean isDangerous() {
      return (Boolean)this.entityData.get(DATA_DANGEROUS);
   }

   public void setDangerous(boolean var1) {
      this.entityData.set(DATA_DANGEROUS, var1);
   }

   protected boolean shouldBurn() {
      return false;
   }

   static {
      DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);
   }
}
