package net.minecraft.world.entity.projectile.throwableitemprojectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class ThrowableBallProjectile extends ThrowableItemProjectile {
   public ThrowableBallProjectile(final EntityType<? extends ThrowableItemProjectile> type, final Level level) {
      super(type, level);
   }

   public ThrowableBallProjectile(final EntityType<? extends ThrowableItemProjectile> type, final LivingEntity mob, final Level level, final ItemStack itemStack) {
      super(type, mob, level, itemStack);
   }

   public ThrowableBallProjectile(final EntityType<? extends ThrowableItemProjectile> type, final double x, final double y, final double z, final Level level, final ItemStack itemStack) {
      super(type, x, y, z, level, itemStack);
   }

   protected abstract int getDamage(final EntityHitResult hitResult);

   protected ParticleOptions getParticle() {
      return new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(this.getItem()));
   }

   public void handleEntityEvent(final @EntityEvent.Value byte id) {
      if (id == 3) {
         ParticleOptions particle = this.getParticle();

         for(int i = 0; i < 8; ++i) {
            this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         }
      }

   }

   protected void onHitEntity(final EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         int damage = this.getDamage(hitResult);
         Entity entity = hitResult.getEntity();
         DamageSource source = this.damageSources().thrown(this, this.getOwner());
         entity.hurtServer(serverLevel, source, (float)damage);
         if (damage > 0 && entity instanceof LivingEntity mob) {
            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, mob, source, this.getWeaponItem());
         }
      }

   }

   protected void onHit(final HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)3);
         Vec3 hitLocation = hitResult.getLocation();
         if (this.getHitSound() != null) {
            this.level().playSound((Entity)null, hitLocation.x, hitLocation.y, hitLocation.z, this.getHitSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (this.level().getRandom().nextFloat() * 0.4F + 0.8F));
         }

         this.discard();
      }

   }

   protected @Nullable SoundEvent getHitSound() {
      return null;
   }

   public @Nullable ItemStack getWeaponItem() {
      return this.getItem();
   }
}
