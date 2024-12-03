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
import net.minecraft.world.entity.EquipmentSlot;
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
   private static final EntityDataAccessor<Byte> ID_LOYALTY;
   private static final EntityDataAccessor<Boolean> ID_FOIL;
   private static final float WATER_INERTIA = 0.99F;
   private boolean dealtDamage;
   public int clientSideReturnTridentTickCount;

   public ThrownTrident(EntityType<? extends ThrownTrident> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownTrident(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.TRIDENT, var2, var1, var3, (ItemStack)null);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(var3));
      this.entityData.set(ID_FOIL, var3.hasFoil());
   }

   public ThrownTrident(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.TRIDENT, var2, var4, var6, var1, var8, var8);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(var8));
      this.entityData.set(ID_FOIL, var8.hasFoil());
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_LOYALTY, (byte)0);
      var1.define(ID_FOIL, false);
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.dealtDamage = true;
      }

      Entity var1 = this.getOwner();
      byte var2 = (Byte)this.entityData.get(ID_LOYALTY);
      if (var2 > 0 && (this.dealtDamage || this.isNoPhysics()) && var1 != null) {
         if (!this.isAcceptibleReturnOwner()) {
            Level var4 = this.level();
            if (var4 instanceof ServerLevel) {
               ServerLevel var3 = (ServerLevel)var4;
               if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                  this.spawnAtLocation(var3, this.getPickupItem(), 0.1F);
               }
            }

            this.discard();
         } else {
            if (!(var1 instanceof Player) && this.position().distanceTo(var1.getEyePosition()) < (double)var1.getBbWidth() + 1.0) {
               this.discard();
               return;
            }

            this.setNoPhysics(true);
            Vec3 var6 = var1.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + var6.y * 0.015 * (double)var2, this.getZ());
            double var7 = 0.05 * (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(var6.normalize().scale(var7)));
            if (this.clientSideReturnTridentTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnTridentTickCount;
         }
      }

      super.tick();
   }

   private boolean isAcceptibleReturnOwner() {
      Entity var1 = this.getOwner();
      if (var1 != null && var1.isAlive()) {
         return !(var1 instanceof ServerPlayer) || !var1.isSpectator();
      } else {
         return false;
      }
   }

   public boolean isFoil() {
      return (Boolean)this.entityData.get(ID_FOIL);
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 var1, Vec3 var2) {
      return this.dealtDamage ? null : super.findHitEntity(var1, var2);
   }

   protected void onHitEntity(EntityHitResult var1) {
      Entity var2 = var1.getEntity();
      float var3 = 8.0F;
      Entity var4 = this.getOwner();
      DamageSource var5 = this.damageSources().trident(this, (Entity)(var4 == null ? this : var4));
      Level var7 = this.level();
      if (var7 instanceof ServerLevel var6) {
         var3 = EnchantmentHelper.modifyDamage(var6, this.getWeaponItem(), var2, var5, var3);
      }

      this.dealtDamage = true;
      if (var2.hurtOrSimulate(var5, var3)) {
         if (var2.getType() == EntityType.ENDERMAN) {
            return;
         }

         var7 = this.level();
         if (var7 instanceof ServerLevel) {
            ServerLevel var8 = (ServerLevel)var7;
            EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(var8, var2, var5, this.getWeaponItem(), (var2x) -> this.kill(var8));
         }

         if (var2 instanceof LivingEntity) {
            LivingEntity var9 = (LivingEntity)var2;
            this.doKnockback(var9, var5);
            this.doPostHurtEffects(var9);
         }
      }

      this.deflect(ProjectileDeflection.REVERSE, var2, this.getOwner(), false);
      this.setDeltaMovement(this.getDeltaMovement().multiply(0.02, 0.2, 0.02));
      this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
   }

   protected void hitBlockEnchantmentEffects(ServerLevel var1, BlockHitResult var2, ItemStack var3) {
      Vec3 var4 = var2.getBlockPos().clampLocationWithin(var2.getLocation());
      Entity var6 = this.getOwner();
      LivingEntity var10002;
      if (var6 instanceof LivingEntity var5) {
         var10002 = var5;
      } else {
         var10002 = null;
      }

      EnchantmentHelper.onHitBlock(var1, var3, var10002, this, (EquipmentSlot)null, var4, var1.getBlockState(var2.getBlockPos()), (var2x) -> this.kill(var1));
   }

   public ItemStack getWeaponItem() {
      return this.getPickupItemStackOrigin();
   }

   protected boolean tryPickup(Player var1) {
      return super.tryPickup(var1) || this.isNoPhysics() && this.ownedBy(var1) && var1.getInventory().add(this.getPickupItem());
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.TRIDENT);
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(Player var1) {
      if (this.ownedBy(var1) || this.getOwner() == null) {
         super.playerTouch(var1);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.dealtDamage = var1.getBoolean("DealtDamage");
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("DealtDamage", this.dealtDamage);
   }

   private byte getLoyaltyFromItem(ItemStack var1) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         return (byte)Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(var2, var1, this), 0, 127);
      } else {
         return 0;
      }
   }

   public void tickDespawn() {
      byte var1 = (Byte)this.entityData.get(ID_LOYALTY);
      if (this.pickup != AbstractArrow.Pickup.ALLOWED || var1 <= 0) {
         super.tickDespawn();
      }

   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double var1, double var3, double var5) {
      return true;
   }

   static {
      ID_LOYALTY = SynchedEntityData.<Byte>defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
      ID_FOIL = SynchedEntityData.<Boolean>defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
   }
}
