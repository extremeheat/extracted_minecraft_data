package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketEntity extends Projectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;
   private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET;
   private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE;
   private int life;
   private int lifetime;
   @Nullable
   private LivingEntity attachedToEntity;

   public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> var1, Level var2) {
      super(var1, var2);
   }

   public FireworkRocketEntity(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.FIREWORK_ROCKET, var1);
      this.life = 0;
      this.setPos(var2, var4, var6);
      int var9 = 1;
      if (!var8.isEmpty() && var8.hasTag()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, var8.copy());
         var9 += var8.getOrCreateTagElement("Fireworks").getByte("Flight");
      }

      this.setDeltaMovement(this.random.triangle(0.0, 0.002297), 0.05, this.random.triangle(0.0, 0.002297));
      this.lifetime = 10 * var9 + this.random.nextInt(6) + this.random.nextInt(7);
   }

   public FireworkRocketEntity(Level var1, @Nullable Entity var2, double var3, double var5, double var7, ItemStack var9) {
      this(var1, var3, var5, var7, var9);
      this.setOwner(var2);
   }

   public FireworkRocketEntity(Level var1, ItemStack var2, LivingEntity var3) {
      this(var1, var3, var3.getX(), var3.getY(), var3.getZ(), var2);
      this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(var3.getId()));
      this.attachedToEntity = var3;
   }

   public FireworkRocketEntity(Level var1, ItemStack var2, double var3, double var5, double var7, boolean var9) {
      this(var1, var3, var5, var7, var2);
      this.entityData.set(DATA_SHOT_AT_ANGLE, var9);
   }

   public FireworkRocketEntity(Level var1, ItemStack var2, Entity var3, double var4, double var6, double var8, boolean var10) {
      this(var1, var2, var4, var6, var8, var10);
      this.setOwner(var3);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
      this.entityData.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
      this.entityData.define(DATA_SHOT_AT_ANGLE, false);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 4096.0 && !this.isAttachedToEntity();
   }

   public boolean shouldRender(double var1, double var3, double var5) {
      return super.shouldRender(var1, var3, var5) && !this.isAttachedToEntity();
   }

   public void tick() {
      super.tick();
      Vec3 var1;
      if (this.isAttachedToEntity()) {
         if (this.attachedToEntity == null) {
            ((OptionalInt)this.entityData.get(DATA_ATTACHED_TO_TARGET)).ifPresent((var1x) -> {
               Entity var2 = this.level.getEntity(var1x);
               if (var2 instanceof LivingEntity) {
                  this.attachedToEntity = (LivingEntity)var2;
               }

            });
         }

         if (this.attachedToEntity != null) {
            if (this.attachedToEntity.isFallFlying()) {
               Vec3 var2 = this.attachedToEntity.getLookAngle();
               double var3 = 1.5;
               double var5 = 0.1;
               Vec3 var7 = this.attachedToEntity.getDeltaMovement();
               this.attachedToEntity.setDeltaMovement(var7.add(var2.x * 0.1 + (var2.x * 1.5 - var7.x) * 0.5, var2.y * 0.1 + (var2.y * 1.5 - var7.y) * 0.5, var2.z * 0.1 + (var2.z * 1.5 - var7.z) * 0.5));
               var1 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);
            } else {
               var1 = Vec3.ZERO;
            }

            this.setPos(this.attachedToEntity.getX() + var1.x, this.attachedToEntity.getY() + var1.y, this.attachedToEntity.getZ() + var1.z);
            this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
         }
      } else {
         if (!this.isShotAtAngle()) {
            double var8 = this.horizontalCollision ? 1.0 : 1.15;
            this.setDeltaMovement(this.getDeltaMovement().multiply(var8, 1.0, var8).add(0.0, 0.04, 0.0));
         }

         var1 = this.getDeltaMovement();
         this.move(MoverType.SELF, var1);
         this.setDeltaMovement(var1);
      }

      HitResult var9 = ProjectileUtil.getHitResult(this, this::canHitEntity);
      if (!this.noPhysics) {
         this.onHit(var9);
         this.hasImpulse = true;
      }

      this.updateRotation();
      if (this.life == 0 && !this.isSilent()) {
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
      }

      ++this.life;
      if (this.level.isClientSide && this.life % 2 < 2) {
         this.level.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, -this.getDeltaMovement().y * 0.5, this.random.nextGaussian() * 0.05);
      }

      if (!this.level.isClientSide && this.life > this.lifetime) {
         this.explode();
      }

   }

   private void explode() {
      this.level.broadcastEntityEvent(this, (byte)17);
      this.gameEvent(GameEvent.EXPLODE, this.getOwner());
      this.dealExplosionDamage();
      this.discard();
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level.isClientSide) {
         this.explode();
      }
   }

   protected void onHitBlock(BlockHitResult var1) {
      BlockPos var2 = new BlockPos(var1.getBlockPos());
      this.level.getBlockState(var2).entityInside(this.level, var2, this);
      if (!this.level.isClientSide() && this.hasExplosion()) {
         this.explode();
      }

      super.onHitBlock(var1);
   }

   private boolean hasExplosion() {
      ItemStack var1 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundTag var2 = var1.isEmpty() ? null : var1.getTagElement("Fireworks");
      ListTag var3 = var2 != null ? var2.getList("Explosions", 10) : null;
      return var3 != null && !var3.isEmpty();
   }

   private void dealExplosionDamage() {
      float var1 = 0.0F;
      ItemStack var2 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundTag var3 = var2.isEmpty() ? null : var2.getTagElement("Fireworks");
      ListTag var4 = var3 != null ? var3.getList("Explosions", 10) : null;
      if (var4 != null && !var4.isEmpty()) {
         var1 = 5.0F + (float)(var4.size() * 2);
      }

      if (var1 > 0.0F) {
         if (this.attachedToEntity != null) {
            this.attachedToEntity.hurt(DamageSource.fireworks(this, this.getOwner()), 5.0F + (float)(var4.size() * 2));
         }

         double var5 = 5.0;
         Vec3 var7 = this.position();
         List var8 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0));
         Iterator var9 = var8.iterator();

         while(true) {
            LivingEntity var10;
            do {
               do {
                  if (!var9.hasNext()) {
                     return;
                  }

                  var10 = (LivingEntity)var9.next();
               } while(var10 == this.attachedToEntity);
            } while(this.distanceToSqr(var10) > 25.0);

            boolean var11 = false;

            for(int var12 = 0; var12 < 2; ++var12) {
               Vec3 var13 = new Vec3(var10.getX(), var10.getY(0.5 * (double)var12), var10.getZ());
               BlockHitResult var14 = this.level.clip(new ClipContext(var7, var13, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
               if (var14.getType() == HitResult.Type.MISS) {
                  var11 = true;
                  break;
               }
            }

            if (var11) {
               float var15 = var1 * (float)Math.sqrt((5.0 - (double)this.distanceTo(var10)) / 5.0);
               var10.hurt(DamageSource.fireworks(this, this.getOwner()), var15);
            }
         }
      }
   }

   private boolean isAttachedToEntity() {
      return ((OptionalInt)this.entityData.get(DATA_ATTACHED_TO_TARGET)).isPresent();
   }

   public boolean isShotAtAngle() {
      return (Boolean)this.entityData.get(DATA_SHOT_AT_ANGLE);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 17 && this.level.isClientSide) {
         if (!this.hasExplosion()) {
            for(int var2 = 0; var2 < this.random.nextInt(3) + 2; ++var2) {
               this.level.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
            }
         } else {
            ItemStack var5 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
            CompoundTag var3 = var5.isEmpty() ? null : var5.getTagElement("Fireworks");
            Vec3 var4 = this.getDeltaMovement();
            this.level.createFireworks(this.getX(), this.getY(), this.getZ(), var4.x, var4.y, var4.z, var3);
         }
      }

      super.handleEntityEvent(var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Life", this.life);
      var1.putInt("LifeTime", this.lifetime);
      ItemStack var2 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      if (!var2.isEmpty()) {
         var1.put("FireworksItem", var2.save(new CompoundTag()));
      }

      var1.putBoolean("ShotAtAngle", (Boolean)this.entityData.get(DATA_SHOT_AT_ANGLE));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getInt("Life");
      this.lifetime = var1.getInt("LifeTime");
      ItemStack var2 = ItemStack.of(var1.getCompound("FireworksItem"));
      if (!var2.isEmpty()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, var2);
      }

      if (var1.contains("ShotAtAngle")) {
         this.entityData.set(DATA_SHOT_AT_ANGLE, var1.getBoolean("ShotAtAngle"));
      }

   }

   public ItemStack getItem() {
      ItemStack var1 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      return var1.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : var1;
   }

   public boolean isAttackable() {
      return false;
   }

   static {
      DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
      DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DATA_SHOT_AT_ANGLE = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
   }
}
