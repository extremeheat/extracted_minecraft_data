package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownTrident extends AbstractArrow {
   private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
   private boolean dealtDamage;
   public int clientSideReturnTridentTickCount;

   public ThrownTrident(EntityType<? extends ThrownTrident> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownTrident(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.TRIDENT, var2, var1, var3);
      this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(var3));
      this.entityData.set(ID_FOIL, var3.hasFoil());
   }

   public ThrownTrident(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.TRIDENT, var2, var4, var6, var1, var8);
      this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(var8));
      this.entityData.set(ID_FOIL, var8.hasFoil());
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_LOYALTY, (byte)0);
      var1.define(ID_FOIL, false);
   }

   @Override
   public void tick() {
      if (this.inGroundTime > 4) {
         this.dealtDamage = true;
      }

      Entity var1 = this.getOwner();
      byte var2 = this.entityData.get(ID_LOYALTY);
      if (var2 > 0 && (this.dealtDamage || this.isNoPhysics()) && var1 != null) {
         if (!this.isAcceptibleReturnOwner()) {
            if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
         } else {
            this.setNoPhysics(true);
            Vec3 var3 = var1.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + var3.y * 0.015 * (double)var2, this.getZ());
            if (this.level().isClientSide) {
               this.yOld = this.getY();
            }

            double var4 = 0.05 * (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(var3.normalize().scale(var4)));
            if (this.clientSideReturnTridentTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            this.clientSideReturnTridentTickCount++;
         }
      }

      super.tick();
   }

   private boolean isAcceptibleReturnOwner() {
      Entity var1 = this.getOwner();
      return var1 == null || !var1.isAlive() ? false : !(var1 instanceof ServerPlayer) || !var1.isSpectator();
   }

   public boolean isFoil() {
      return this.entityData.get(ID_FOIL);
   }

   @Nullable
   @Override
   protected EntityHitResult findHitEntity(Vec3 var1, Vec3 var2) {
      return this.dealtDamage ? null : super.findHitEntity(var1, var2);
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      Entity var2 = var1.getEntity();
      float var3 = 8.0F;
      if (var2 instanceof LivingEntity var4) {
         var3 += EnchantmentHelper.getDamageBonus(this.getPickupItemStackOrigin(), var4.getType());
      }

      Entity var10 = this.getOwner();
      DamageSource var5 = this.damageSources().trident(this, (Entity)(var10 == null ? this : var10));
      this.dealtDamage = true;
      SoundEvent var6 = SoundEvents.TRIDENT_HIT;
      if (var2.hurt(var5, var3)) {
         if (var2.getType() == EntityType.ENDERMAN) {
            return;
         }

         if (var2 instanceof LivingEntity var7) {
            if (var10 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(var7, var10);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)var10, var7);
            }

            this.doPostHurtEffects(var7);
         }
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
      float var11 = 1.0F;
      if (this.level() instanceof ServerLevel && this.level().isThundering() && this.isChanneling()) {
         BlockPos var8 = var2.blockPosition();
         if (this.level().canSeeSky(var8)) {
            LightningBolt var9 = EntityType.LIGHTNING_BOLT.create(this.level());
            if (var9 != null) {
               var9.moveTo(Vec3.atBottomCenterOf(var8));
               var9.setCause(var10 instanceof ServerPlayer ? (ServerPlayer)var10 : null);
               this.level().addFreshEntity(var9);
               var6 = SoundEvents.TRIDENT_THUNDER;
               var11 = 5.0F;
            }
         }
      }

      this.playSound(var6, var11, 1.0F);
   }

   public boolean isChanneling() {
      return EnchantmentHelper.hasChanneling(this.getPickupItemStackOrigin());
   }

   @Override
   protected boolean tryPickup(Player var1) {
      return super.tryPickup(var1) || this.isNoPhysics() && this.ownedBy(var1) && var1.getInventory().add(this.getPickupItem());
   }

   @Override
   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.TRIDENT);
   }

   @Override
   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   @Override
   public void playerTouch(Player var1) {
      if (this.ownedBy(var1) || this.getOwner() == null) {
         super.playerTouch(var1);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.dealtDamage = var1.getBoolean("DealtDamage");
      this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.getPickupItemStackOrigin()));
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("DealtDamage", this.dealtDamage);
   }

   @Override
   public void tickDespawn() {
      byte var1 = this.entityData.get(ID_LOYALTY);
      if (this.pickup != AbstractArrow.Pickup.ALLOWED || var1 <= 0) {
         super.tickDespawn();
      }
   }

   @Override
   protected float getWaterInertia() {
      return 0.99F;
   }

   @Override
   public boolean shouldRender(double var1, double var3, double var5) {
      return true;
   }
}
