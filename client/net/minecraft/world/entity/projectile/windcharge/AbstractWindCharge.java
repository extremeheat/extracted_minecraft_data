package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class AbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {
   public static final WindChargeDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new WindChargeDamageCalculator();

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2, Entity var3, double var4, double var6, double var8) {
      super(var1, var4, var6, var8, var2);
      this.setOwner(var3);
   }

   AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, double var2, double var4, double var6, double var8, double var10, double var12, Level var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   protected AABB makeBoundingBox() {
      float var1 = this.getType().getDimensions().width() / 2.0F;
      float var2 = this.getType().getDimensions().height();
      float var3 = 0.15F;
      return new AABB(this.position().x - (double)var1, this.position().y - 0.15000000596046448, this.position().z - (double)var1, this.position().x + (double)var1, this.position().y - 0.15000000596046448 + (double)var2, this.position().z + (double)var1);
   }

   public boolean canCollideWith(Entity var1) {
      return var1 instanceof AbstractWindCharge ? false : super.canCollideWith(var1);
   }

   protected boolean canHitEntity(Entity var1) {
      if (var1 instanceof AbstractWindCharge) {
         return false;
      } else {
         return var1.getType() == EntityType.END_CRYSTAL ? false : super.canHitEntity(var1);
      }
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         Entity var4 = this.getOwner();
         LivingEntity var10000;
         if (var4 instanceof LivingEntity) {
            LivingEntity var3 = (LivingEntity)var4;
            var10000 = var3;
         } else {
            var10000 = null;
         }

         LivingEntity var2 = var10000;
         Entity var5 = (Entity)var1.getEntity().getPassengerClosestTo(var1.getLocation()).orElse(var1.getEntity());
         if (var2 != null) {
            var2.setLastHurtMob(var5);
         }

         var5.hurt(this.damageSources().windCharge(this, var2), 1.0F);
         this.explode();
      }
   }

   public void push(double var1, double var3, double var5) {
   }

   protected abstract void explode();

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level().isClientSide) {
         this.explode();
         this.discard();
      }

   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.discard();
      }

   }

   protected boolean shouldBurn() {
      return false;
   }

   public ItemStack getItem() {
      return ItemStack.EMPTY;
   }

   protected float getInertia() {
      return 1.0F;
   }

   protected float getLiquidInertia() {
      return this.getInertia();
   }

   @Nullable
   protected ParticleOptions getTrailParticle() {
      return null;
   }

   public void tick() {
      if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
         this.explode();
         this.discard();
      } else {
         super.tick();
      }

   }

   public static class WindChargeDamageCalculator extends ExplosionDamageCalculator {
      public WindChargeDamageCalculator() {
         super();
      }

      public boolean shouldDamageEntity(Explosion var1, Entity var2) {
         return false;
      }

      public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
         return var4.is(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS) ? Optional.of(3600000.0F) : Optional.empty();
      }
   }
}
