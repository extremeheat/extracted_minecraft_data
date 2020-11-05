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
   private static final EntityDataAccessor<Byte> ID_LOYALTY;
   private static final EntityDataAccessor<Boolean> ID_FOIL;
   private ItemStack tridentItem;
   private boolean dealtDamage;
   public int clientSideReturnTridentTickCount;

   public ThrownTrident(EntityType<? extends ThrownTrident> var1, Level var2) {
      super(var1, var2);
      this.tridentItem = new ItemStack(Items.TRIDENT);
   }

   public ThrownTrident(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.TRIDENT, var2, var1);
      this.tridentItem = new ItemStack(Items.TRIDENT);
      this.tridentItem = var3.copy();
      this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(var3));
      this.entityData.set(ID_FOIL, var3.hasFoil());
   }

   public ThrownTrident(Level var1, double var2, double var4, double var6) {
      super(EntityType.TRIDENT, var2, var4, var6, var1);
      this.tridentItem = new ItemStack(Items.TRIDENT);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_LOYALTY, (byte)0);
      this.entityData.define(ID_FOIL, false);
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.dealtDamage = true;
      }

      Entity var1 = this.getOwner();
      if ((this.dealtDamage || this.isNoPhysics()) && var1 != null) {
         byte var2 = (Byte)this.entityData.get(ID_LOYALTY);
         if (var2 > 0 && !this.isAcceptibleReturnOwner()) {
            if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         } else if (var2 > 0) {
            this.setNoPhysics(true);
            Vec3 var3 = new Vec3(var1.getX() - this.getX(), var1.getEyeY() - this.getY(), var1.getZ() - this.getZ());
            this.setPosRaw(this.getX(), this.getY() + var3.y * 0.015D * (double)var2, this.getZ());
            if (this.level.isClientSide) {
               this.yOld = this.getY();
            }

            double var4 = 0.05D * (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(var3.normalize().scale(var4)));
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

   protected ItemStack getPickupItem() {
      return this.tridentItem.copy();
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
      if (var2 instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)var2;
         var3 += EnchantmentHelper.getDamageBonus(this.tridentItem, var4.getMobType());
      }

      Entity var10 = this.getOwner();
      DamageSource var5 = DamageSource.trident(this, (Entity)(var10 == null ? this : var10));
      this.dealtDamage = true;
      SoundEvent var6 = SoundEvents.TRIDENT_HIT;
      if (var2.hurt(var5, var3)) {
         if (var2.getType() == EntityType.ENDERMAN) {
            return;
         }

         if (var2 instanceof LivingEntity) {
            LivingEntity var7 = (LivingEntity)var2;
            if (var10 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(var7, var10);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)var10, var7);
            }

            this.doPostHurtEffects(var7);
         }
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
      float var11 = 1.0F;
      if (this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.tridentItem)) {
         BlockPos var8 = var2.blockPosition();
         if (this.level.canSeeSky(var8)) {
            LightningBolt var9 = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.level);
            var9.moveTo(Vec3.atBottomCenterOf(var8));
            var9.setCause(var10 instanceof ServerPlayer ? (ServerPlayer)var10 : null);
            this.level.addFreshEntity(var9);
            var6 = SoundEvents.TRIDENT_THUNDER;
            var11 = 5.0F;
         }
      }

      this.playSound(var6, var11, 1.0F);
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(Player var1) {
      Entity var2 = this.getOwner();
      if (var2 == null || var2.getUUID() == var1.getUUID()) {
         super.playerTouch(var1);
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Trident", 10)) {
         this.tridentItem = ItemStack.of(var1.getCompound("Trident"));
      }

      this.dealtDamage = var1.getBoolean("DealtDamage");
      this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentItem));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("Trident", this.tridentItem.save(new CompoundTag()));
      var1.putBoolean("DealtDamage", this.dealtDamage);
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
      ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
      ID_FOIL = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BOOLEAN);
   }
}
