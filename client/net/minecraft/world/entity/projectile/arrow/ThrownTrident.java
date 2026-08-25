package net.minecraft.world.entity.projectile.arrow;

import java.util.Collection;
import java.util.List;
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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ThrownTrident extends AbstractArrow {
   private static final EntityDataAccessor<Byte> ID_LOYALTY;
   private static final EntityDataAccessor<Boolean> ID_FOIL;
   private static final float WATER_INERTIA = 0.99F;
   private static final boolean DEFAULT_DEALT_DAMAGE = false;
   private boolean dealtDamage = false;
   public int clientSideReturnTridentTickCount;

   public ThrownTrident(final EntityType<? extends ThrownTrident> type, final Level level) {
      super(type, level);
   }

   public ThrownTrident(final Level level, final LivingEntity owner, final ItemStack tridentItem) {
      super(EntityTypes.TRIDENT, owner, level, tridentItem, (ItemStack)null);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(tridentItem));
      this.entityData.set(ID_FOIL, tridentItem.hasFoil());
   }

   public ThrownTrident(final Level level, final double x, final double y, final double z, final ItemStack tridentItem) {
      super(EntityTypes.TRIDENT, x, y, z, level, tridentItem, tridentItem);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(tridentItem));
      this.entityData.set(ID_FOIL, tridentItem.hasFoil());
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(ID_LOYALTY, (byte)0);
      entityData.define(ID_FOIL, false);
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.dealtDamage = true;
      }

      Entity currentOwner = this.getOwner();
      int loyalty = (Byte)this.entityData.get(ID_LOYALTY);
      if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && currentOwner != null) {
         if (!this.isAcceptibleReturnOwner()) {
            Level var4 = this.level();
            if (var4 instanceof ServerLevel) {
               ServerLevel level = (ServerLevel)var4;
               if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                  this.spawnAtLocation(level, this.getPickupItem(), 0.1F);
               }
            }

            this.discard();
         } else {
            if (!(currentOwner instanceof Player) && this.position().distanceTo(currentOwner.getEyePosition()) < (double)currentOwner.getBbWidth() + 1.0) {
               this.discard();
               return;
            }

            this.setNoPhysics(true);
            Vec3 vec = currentOwner.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + vec.y * 0.015 * (double)loyalty, this.getZ());
            double accel = 0.05 * (double)loyalty;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec.normalize().scale(accel)));
            if (this.clientSideReturnTridentTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnTridentTickCount;
         }
      }

      super.tick();
   }

   private boolean isAcceptibleReturnOwner() {
      Entity currentOwner = this.getOwner();
      if (currentOwner != null && currentOwner.isAlive()) {
         return !(currentOwner instanceof ServerPlayer) || !currentOwner.isSpectator();
      } else {
         return false;
      }
   }

   public boolean isFoil() {
      return (Boolean)this.entityData.get(ID_FOIL);
   }

   protected @Nullable EntityHitResult findHitEntity(final Vec3 from, final Vec3 to) {
      return this.dealtDamage ? null : super.findHitEntity(from, to);
   }

   protected Collection<EntityHitResult> findHitEntities(final Vec3 from, final Vec3 to) {
      EntityHitResult e = this.findHitEntity(from, to);
      return e != null ? List.of(e) : List.of();
   }

   protected void onHitEntity(final EntityHitResult hitResult) {
      Entity entity = hitResult.getEntity();
      float dmg = 8.0F;
      Entity currentOwner = this.getOwner();
      DamageSource damageSource = this.damageSources().trident(this, (Entity)(currentOwner == null ? this : currentOwner));
      Level var7 = this.level();
      if (var7 instanceof ServerLevel serverLevel) {
         dmg = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damageSource, dmg);
      }

      this.dealtDamage = true;
      if (entity.hurtOrSimulate(damageSource, dmg)) {
         var7 = this.level();
         if (var7 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var7;
            EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(serverLevel, entity, damageSource, this.getWeaponItem(), (weapon) -> this.kill(serverLevel));
         }

         if (entity instanceof LivingEntity) {
            LivingEntity mob = (LivingEntity)entity;
            this.doKnockback(mob, damageSource);
            this.doPostHurtEffects(mob);
         }

         this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
      }

      this.deflect(ProjectileDeflection.REVERSE, entity, this.owner, false, new Vec3(0.02, 0.2, 0.02));
   }

   protected void hitBlockEnchantmentEffects(final ServerLevel level, final BlockHitResult hitResult, final ItemStack weapon) {
      Vec3 compensatedHitPosition = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
      Entity var6 = this.getOwner();
      LivingEntity var10002;
      if (var6 instanceof LivingEntity livingOwner) {
         var10002 = livingOwner;
      } else {
         var10002 = null;
      }

      EnchantmentHelper.onHitBlock(level, weapon, var10002, this, (EquipmentSlot)null, compensatedHitPosition, level.getBlockState(hitResult.getBlockPos()), (item) -> this.kill(level));
   }

   public ItemStack getWeaponItem() {
      return this.getPickupItemStackOrigin();
   }

   protected boolean tryPickup(final Player player) {
      return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.TRIDENT);
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(final Player player) {
      if (this.ownedBy(player) || this.getOwner() == null) {
         super.playerTouch(player);
      }

   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.dealtDamage = input.getBooleanOr("DealtDamage", false);
      this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("DealtDamage", this.dealtDamage);
   }

   private byte getLoyaltyFromItem(final ItemStack tridentItem) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         return (byte)Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, tridentItem, this), 0, 127);
      } else {
         return 0;
      }
   }

   public void tickDespawn() {
      int loyalty = (Byte)this.entityData.get(ID_LOYALTY);
      if (this.pickup != AbstractArrow.Pickup.ALLOWED || loyalty <= 0) {
         super.tickDespawn();
      }

   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(final double camX, final double camY, final double camZ) {
      return true;
   }

   static {
      ID_LOYALTY = SynchedEntityData.<Byte>defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
      ID_FOIL = SynchedEntityData.<Boolean>defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
   }
}
