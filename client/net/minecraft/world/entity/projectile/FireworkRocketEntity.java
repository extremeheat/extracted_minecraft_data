package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
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
      this.entityData.set(DATA_ID_FIREWORKS_ITEM, var8.copy());
      int var9 = 1;
      Fireworks var10 = (Fireworks)var8.get(DataComponents.FIREWORKS);
      if (var10 != null) {
         var9 += var10.flightDuration();
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

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ID_FIREWORKS_ITEM, getDefaultItem());
      var1.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
      var1.define(DATA_SHOT_AT_ANGLE, false);
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
               Entity var2 = this.level().getEntity(var1x);
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

      HitResult var9 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (!this.noPhysics) {
         this.hitOrDeflect(var9);
         this.hasImpulse = true;
      }

      this.updateRotation();
      if (this.life == 0 && !this.isSilent()) {
         this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
      }

      ++this.life;
      if (this.level().isClientSide && this.life % 2 < 2) {
         this.level().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, -this.getDeltaMovement().y * 0.5, this.random.nextGaussian() * 0.05);
      }

      if (!this.level().isClientSide && this.life > this.lifetime) {
         this.explode();
      }

   }

   private void explode() {
      this.level().broadcastEntityEvent(this, (byte)17);
      this.gameEvent(GameEvent.EXPLODE, this.getOwner());
      this.dealExplosionDamage();
      this.discard();
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         this.explode();
      }
   }

   protected void onHitBlock(BlockHitResult var1) {
      BlockPos var2 = new BlockPos(var1.getBlockPos());
      this.level().getBlockState(var2).entityInside(this.level(), var2, this);
      if (!this.level().isClientSide() && this.hasExplosion()) {
         this.explode();
      }

      super.onHitBlock(var1);
   }

   private boolean hasExplosion() {
      return !this.getExplosions().isEmpty();
   }

   private void dealExplosionDamage() {
      float var1 = 0.0F;
      List var2 = this.getExplosions();
      if (!var2.isEmpty()) {
         var1 = 5.0F + (float)(var2.size() * 2);
      }

      if (var1 > 0.0F) {
         if (this.attachedToEntity != null) {
            this.attachedToEntity.hurt(this.damageSources().fireworks(this, this.getOwner()), 5.0F + (float)(var2.size() * 2));
         }

         double var3 = 5.0;
         Vec3 var5 = this.position();
         List var6 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0));
         Iterator var7 = var6.iterator();

         while(true) {
            LivingEntity var8;
            do {
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  var8 = (LivingEntity)var7.next();
               } while(var8 == this.attachedToEntity);
            } while(this.distanceToSqr(var8) > 25.0);

            boolean var9 = false;

            for(int var10 = 0; var10 < 2; ++var10) {
               Vec3 var11 = new Vec3(var8.getX(), var8.getY(0.5 * (double)var10), var8.getZ());
               BlockHitResult var12 = this.level().clip(new ClipContext(var5, var11, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
               if (((HitResult)var12).getType() == HitResult.Type.MISS) {
                  var9 = true;
                  break;
               }
            }

            if (var9) {
               float var13 = var1 * (float)Math.sqrt((5.0 - (double)this.distanceTo(var8)) / 5.0);
               var8.hurt(this.damageSources().fireworks(this, this.getOwner()), var13);
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
      if (var1 == 17 && this.level().isClientSide) {
         Vec3 var2 = this.getDeltaMovement();
         this.level().createFireworks(this.getX(), this.getY(), this.getZ(), var2.x, var2.y, var2.z, this.getExplosions());
      }

      super.handleEntityEvent(var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Life", this.life);
      var1.putInt("LifeTime", this.lifetime);
      var1.put("FireworksItem", this.getItem().save(this.registryAccess()));
      var1.putBoolean("ShotAtAngle", (Boolean)this.entityData.get(DATA_SHOT_AT_ANGLE));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getInt("Life");
      this.lifetime = var1.getInt("LifeTime");
      if (var1.contains("FireworksItem", 10)) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, (ItemStack)ItemStack.parse(this.registryAccess(), var1.getCompound("FireworksItem")).orElseGet(FireworkRocketEntity::getDefaultItem));
      } else {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, getDefaultItem());
      }

      if (var1.contains("ShotAtAngle")) {
         this.entityData.set(DATA_SHOT_AT_ANGLE, var1.getBoolean("ShotAtAngle"));
      }

   }

   private List<FireworkExplosion> getExplosions() {
      ItemStack var1 = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      Fireworks var2 = (Fireworks)var1.get(DataComponents.FIREWORKS);
      return var2 != null ? var2.explosions() : List.of();
   }

   public ItemStack getItem() {
      return (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
   }

   public boolean isAttackable() {
      return false;
   }

   private static ItemStack getDefaultItem() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   static {
      DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
      DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DATA_SHOT_AT_ANGLE = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
   }
}
