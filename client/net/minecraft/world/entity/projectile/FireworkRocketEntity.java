package net.minecraft.world.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
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
import net.minecraft.server.level.ServerLevel;
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
      HitResult var1;
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
            Vec3 var2;
            if (this.attachedToEntity.isFallFlying()) {
               Vec3 var3 = this.attachedToEntity.getLookAngle();
               double var4 = 1.5;
               double var6 = 0.1;
               Vec3 var8 = this.attachedToEntity.getDeltaMovement();
               this.attachedToEntity.setDeltaMovement(var8.add(var3.x * 0.1 + (var3.x * 1.5 - var8.x) * 0.5, var3.y * 0.1 + (var3.y * 1.5 - var8.y) * 0.5, var3.z * 0.1 + (var3.z * 1.5 - var8.z) * 0.5));
               var2 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);
            } else {
               var2 = Vec3.ZERO;
            }

            this.setPos(this.attachedToEntity.getX() + var2.x, this.attachedToEntity.getY() + var2.y, this.attachedToEntity.getZ() + var2.z);
            this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
         }

         var1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      } else {
         if (!this.isShotAtAngle()) {
            double var9 = this.horizontalCollision ? 1.0 : 1.15;
            this.setDeltaMovement(this.getDeltaMovement().multiply(var9, 1.0, var9).add(0.0, 0.04, 0.0));
         }

         Vec3 var10 = this.getDeltaMovement();
         var1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
         this.move(MoverType.SELF, var10);
         this.applyEffectsFromBlocks();
         this.setDeltaMovement(var10);
      }

      if (!this.noPhysics && this.isAlive() && var1.getType() != HitResult.Type.MISS) {
         this.hitTargetOrDeflectSelf(var1);
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

      if (this.life > this.lifetime) {
         Level var12 = this.level();
         if (var12 instanceof ServerLevel) {
            ServerLevel var11 = (ServerLevel)var12;
            this.explode(var11);
         }
      }

   }

   private void explode(ServerLevel var1) {
      var1.broadcastEntityEvent(this, (byte)17);
      this.gameEvent(GameEvent.EXPLODE, this.getOwner());
      this.dealExplosionDamage(var1);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         this.explode(var2);
      }

   }

   protected void onHitBlock(BlockHitResult var1) {
      BlockPos var2 = new BlockPos(var1.getBlockPos());
      this.level().getBlockState(var2).entityInside(this.level(), var2, this);
      Level var4 = this.level();
      if (var4 instanceof ServerLevel var3) {
         if (this.hasExplosion()) {
            this.explode(var3);
         }
      }

      super.onHitBlock(var1);
   }

   private boolean hasExplosion() {
      return !this.getExplosions().isEmpty();
   }

   private void dealExplosionDamage(ServerLevel var1) {
      float var2 = 0.0F;
      List var3 = this.getExplosions();
      if (!var3.isEmpty()) {
         var2 = 5.0F + (float)(var3.size() * 2);
      }

      if (var2 > 0.0F) {
         if (this.attachedToEntity != null) {
            this.attachedToEntity.hurtServer(var1, this.damageSources().fireworks(this, this.getOwner()), 5.0F + (float)(var3.size() * 2));
         }

         double var4 = 5.0;
         Vec3 var6 = this.position();

         for(LivingEntity var9 : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0))) {
            if (var9 != this.attachedToEntity && !(this.distanceToSqr(var9) > 25.0)) {
               boolean var10 = false;

               for(int var11 = 0; var11 < 2; ++var11) {
                  Vec3 var12 = new Vec3(var9.getX(), var9.getY(0.5 * (double)var11), var9.getZ());
                  BlockHitResult var13 = this.level().clip(new ClipContext(var6, var12, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                  if (((HitResult)var13).getType() == HitResult.Type.MISS) {
                     var10 = true;
                     break;
                  }
               }

               if (var10) {
                  float var14 = var2 * (float)Math.sqrt((5.0 - (double)this.distanceTo(var9)) / 5.0);
                  var9.hurtServer(var1, this.damageSources().fireworks(this, this.getOwner()), var14);
               }
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

   public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity var1, DamageSource var2) {
      double var3 = var1.position().x - this.position().x;
      double var5 = var1.position().z - this.position().z;
      return DoubleDoubleImmutablePair.of(var3, var5);
   }

   static {
      DATA_ID_FIREWORKS_ITEM = SynchedEntityData.<ItemStack>defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
      DATA_ATTACHED_TO_TARGET = SynchedEntityData.<OptionalInt>defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DATA_SHOT_AT_ANGLE = SynchedEntityData.<Boolean>defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
   }
}
