package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
      super(EntityType.TRIDENT, var2, var1, var3, null);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(var3));
      this.entityData.set(ID_FOIL, var3.hasFoil());
   }

   public ThrownTrident(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.TRIDENT, var2, var4, var6, var1, var8, var8);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(var8));
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
            if (this.level() instanceof ServerLevel var3 && this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(var3, this.getPickupItem(), 0.1F);
            }

            this.discard();
         } else {
            this.setNoPhysics(true);
            Vec3 var6 = var1.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + var6.y * 0.015 * (double)var2, this.getZ());
            double var7 = 0.05 * (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(var6.normalize().scale(var7)));
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
      Entity var4 = this.getOwner();
      DamageSource var5 = this.damageSources().trident(this, (Entity)(var4 == null ? this : var4));
      if (this.level() instanceof ServerLevel var6) {
         var3 = EnchantmentHelper.modifyDamage(var6, this.getWeaponItem(), var2, var5, var3);
      }

      this.dealtDamage = true;
      if (var2.hurtOrSimulate(var5, var3)) {
         if (var2.getType() == EntityType.ENDERMAN) {
            return;
         }

         if (this.level() instanceof ServerLevel var8) {
            EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(var8, var2, var5, this.getWeaponItem(), var2x -> this.kill(var8));
         }

         if (var2 instanceof LivingEntity var9) {
            this.doKnockback(var9, var5);
            this.doPostHurtEffects(var9);
         }
      }

      this.deflect(ProjectileDeflection.REVERSE, var2, this.getOwner(), false);
      this.setDeltaMovement(this.getDeltaMovement().multiply(0.02, 0.2, 0.02));
      this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
   }

   @Override
   protected void hitBlockEnchantmentEffects(ServerLevel var1, BlockHitResult var2, ItemStack var3) {
      Vec3 var4 = var2.getBlockPos().clampLocationWithin(var2.getLocation());
      EnchantmentHelper.onHitBlock(
         var1,
         var3,
         this.getOwner() instanceof LivingEntity var5 ? var5 : null,
         this,
         null,
         var4,
         var1.getBlockState(var2.getBlockPos()),
         var2x -> this.kill(var1)
      );
   }

   @Override
   public ItemStack getWeaponItem() {
      return this.getPickupItemStackOrigin();
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
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("DealtDamage", this.dealtDamage);
   }

   private byte getLoyaltyFromItem(ItemStack var1) {
      return this.level() instanceof ServerLevel var2 ? (byte)Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(var2, var1, this), 0, 127) : 0;
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
