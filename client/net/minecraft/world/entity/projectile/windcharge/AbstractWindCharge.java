package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {
   public static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
      true, false, Optional.empty(), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
   );
   public static final double JUMP_SCALE = 0.25;

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
      this.accelerationPower = 0.0;
   }

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2, Entity var3, double var4, double var6, double var8) {
      super(var1, var4, var6, var8, var2);
      this.setOwner(var3);
      this.accelerationPower = 0.0;
   }

   AbstractWindCharge(EntityType<? extends AbstractWindCharge> var1, double var2, double var4, double var6, Vec3 var8, Level var9) {
      super(var1, var2, var4, var6, var8, var9);
      this.accelerationPower = 0.0;
   }

   @Override
   protected AABB makeBoundingBox() {
      float var1 = this.getType().getDimensions().width() / 2.0F;
      float var2 = this.getType().getDimensions().height();
      float var3 = 0.15F;
      return new AABB(
         this.position().x - (double)var1,
         this.position().y - 0.15000000596046448,
         this.position().z - (double)var1,
         this.position().x + (double)var1,
         this.position().y - 0.15000000596046448 + (double)var2,
         this.position().z + (double)var1
      );
   }

   @Override
   public boolean canCollideWith(Entity var1) {
      return var1 instanceof AbstractWindCharge ? false : super.canCollideWith(var1);
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      if (var1 instanceof AbstractWindCharge) {
         return false;
      } else {
         return var1.getType() == EntityType.END_CRYSTAL ? false : super.canHitEntity(var1);
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         LivingEntity var2 = this.getOwner() instanceof LivingEntity var3 ? var3 : null;
         Entity var6 = var1.getEntity();
         if (var2 != null) {
            var2.setLastHurtMob(var6);
         }

         DamageSource var7 = this.damageSources().windCharge(this, var2);
         if (var6.hurt(var7, 1.0F) && var6 instanceof LivingEntity var5) {
            EnchantmentHelper.doPostAttackEffects((ServerLevel)this.level(), var5, var7);
         }

         this.explode(this.position());
      }
   }

   @Override
   public void push(double var1, double var3, double var5) {
   }

   protected abstract void explode(Vec3 var1);

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level().isClientSide) {
         Vec3i var2 = var1.getDirection().getUnitVec3i();
         Vec3 var3 = Vec3.atLowerCornerOf(var2).multiply(0.25, 0.25, 0.25);
         Vec3 var4 = var1.getLocation().add(var3);
         this.explode(var4);
         this.discard();
      }
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.discard();
      }
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }

   @Override
   public ItemStack getItem() {
      return ItemStack.EMPTY;
   }

   @Override
   protected float getInertia() {
      return 1.0F;
   }

   @Override
   protected float getLiquidInertia() {
      return this.getInertia();
   }

   @Nullable
   @Override
   protected ParticleOptions getTrailParticle() {
      return null;
   }

   @Override
   public void tick() {
      if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxY() + 30) {
         this.explode(this.position());
         this.discard();
      } else {
         super.tick();
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }
}
