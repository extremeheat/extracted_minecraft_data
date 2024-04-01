package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VineProjectile extends AbstractHurtingProjectile {
   private static final ItemStack POTATO_STACKO = new ItemStack(Items.POISONOUS_POTATO);
   private static final EntityDataAccessor<Float> STRENGTH = SynchedEntityData.defineId(VineProjectile.class, EntityDataSerializers.FLOAT);
   private int lifetime = 60;

   public VineProjectile(EntityType<? extends VineProjectile> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(STRENGTH, 5.0F);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("lifetime", this.lifetime);
      var1.putFloat("strength", this.strength());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.lifetime = var1.getInt("lifetime");
      this.setStrength(var1.getFloat("strength"));
   }

   public void setStrength(float var1) {
      this.entityData.set(STRENGTH, var1);
   }

   public float strength() {
      return this.entityData.get(STRENGTH);
   }

   @Override
   public void tick() {
      if (!this.level().isClientSide) {
         --this.lifetime;
         if (this.lifetime <= 0) {
            this.discard();
            return;
         }
      }

      HitResult var1 = ProjectileUtil.getHitResultOnMoveVector(this, Entity::isAlive, this.getClipType());
      this.onHit(var1);
      Vec3 var2 = this.position();
      Vec3 var3 = this.getDeltaMovement();
      Vec3 var4 = var2.add(var3);
      Vec3 var5 = var2.add(var3.scale(0.5));
      float var6 = this.strength();
      if (this.random.nextFloat() < var6 / 2.0F) {
         this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, var4.x, var4.y, var4.z, 0.0, 0.0, 0.0);
      }

      if (this.random.nextFloat() < var6 / 2.0F) {
         this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, POTATO_STACKO), var5.x, var5.y, var5.z, 0.0, 0.0, 0.0);
      }

      this.setPos(var4.x, var4.y, var4.z);
   }

   @Override
   protected void onHit(HitResult var1) {
      HitResult.Type var2 = var1.getType();
      if (var2 == HitResult.Type.ENTITY) {
         Entity var3 = ((EntityHitResult)var1).getEntity();
         var3.hurt(this.level().damageSources().potatoMagic(), this.strength());
      } else if (var2 == HitResult.Type.BLOCK) {
         this.discard();
      }
   }
}
