package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet extends Projectile {
   private static final double SPEED = 0.15;
   @Nullable
   private EntityReference<Entity> finalTarget;
   @Nullable
   private Direction currentMoveDirection;
   private int flightSteps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;

   public ShulkerBullet(EntityType<? extends ShulkerBullet> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public ShulkerBullet(Level var1, LivingEntity var2, Entity var3, Direction.Axis var4) {
      this(EntityType.SHULKER_BULLET, var1);
      this.setOwner(var2);
      Vec3 var5 = var2.getBoundingBox().getCenter();
      this.snapTo(var5.x, var5.y, var5.z, this.getYRot(), this.getXRot());
      this.finalTarget = new EntityReference<Entity>(var3);
      this.currentMoveDirection = Direction.UP;
      this.selectNextMoveDirection(var4, var3);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      if (this.finalTarget != null) {
         var1.store("Target", UUIDUtil.CODEC, this.finalTarget.getUUID());
      }

      var1.storeNullable("Dir", Direction.LEGACY_ID_CODEC, this.currentMoveDirection);
      var1.putInt("Steps", this.flightSteps);
      var1.putDouble("TXD", this.targetDeltaX);
      var1.putDouble("TYD", this.targetDeltaY);
      var1.putDouble("TZD", this.targetDeltaZ);
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.flightSteps = var1.getIntOr("Steps", 0);
      this.targetDeltaX = var1.getDoubleOr("TXD", 0.0);
      this.targetDeltaY = var1.getDoubleOr("TYD", 0.0);
      this.targetDeltaZ = var1.getDoubleOr("TZD", 0.0);
      this.currentMoveDirection = (Direction)var1.read("Dir", Direction.LEGACY_ID_CODEC).orElse((Object)null);
      this.finalTarget = EntityReference.<Entity>read(var1, "Target");
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   @Nullable
   private Direction getMoveDirection() {
      return this.currentMoveDirection;
   }

   private void setMoveDirection(@Nullable Direction var1) {
      this.currentMoveDirection = var1;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis var1, @Nullable Entity var2) {
      double var4 = 0.5;
      BlockPos var3;
      if (var2 == null) {
         var3 = this.blockPosition().below();
      } else {
         var4 = (double)var2.getBbHeight() * 0.5;
         var3 = BlockPos.containing(var2.getX(), var2.getY() + var4, var2.getZ());
      }

      double var6 = (double)var3.getX() + 0.5;
      double var8 = (double)var3.getY() + var4;
      double var10 = (double)var3.getZ() + 0.5;
      Direction var12 = null;
      if (!var3.closerToCenterThan(this.position(), 2.0)) {
         BlockPos var13 = this.blockPosition();
         ArrayList var14 = Lists.newArrayList();
         if (var1 != Direction.Axis.X) {
            if (var13.getX() < var3.getX() && this.level().isEmptyBlock(var13.east())) {
               var14.add(Direction.EAST);
            } else if (var13.getX() > var3.getX() && this.level().isEmptyBlock(var13.west())) {
               var14.add(Direction.WEST);
            }
         }

         if (var1 != Direction.Axis.Y) {
            if (var13.getY() < var3.getY() && this.level().isEmptyBlock(var13.above())) {
               var14.add(Direction.UP);
            } else if (var13.getY() > var3.getY() && this.level().isEmptyBlock(var13.below())) {
               var14.add(Direction.DOWN);
            }
         }

         if (var1 != Direction.Axis.Z) {
            if (var13.getZ() < var3.getZ() && this.level().isEmptyBlock(var13.south())) {
               var14.add(Direction.SOUTH);
            } else if (var13.getZ() > var3.getZ() && this.level().isEmptyBlock(var13.north())) {
               var14.add(Direction.NORTH);
            }
         }

         var12 = Direction.getRandom(this.random);
         if (var14.isEmpty()) {
            for(int var15 = 5; !this.level().isEmptyBlock(var13.relative(var12)) && var15 > 0; --var15) {
               var12 = Direction.getRandom(this.random);
            }
         } else {
            var12 = (Direction)var14.get(this.random.nextInt(var14.size()));
         }

         var6 = this.getX() + (double)var12.getStepX();
         var8 = this.getY() + (double)var12.getStepY();
         var10 = this.getZ() + (double)var12.getStepZ();
      }

      this.setMoveDirection(var12);
      double var21 = var6 - this.getX();
      double var22 = var8 - this.getY();
      double var17 = var10 - this.getZ();
      double var19 = Math.sqrt(var21 * var21 + var22 * var22 + var17 * var17);
      if (var19 == 0.0) {
         this.targetDeltaX = 0.0;
         this.targetDeltaY = 0.0;
         this.targetDeltaZ = 0.0;
      } else {
         this.targetDeltaX = var21 / var19 * 0.15;
         this.targetDeltaY = var22 / var19 * 0.15;
         this.targetDeltaZ = var17 / var19 * 0.15;
      }

      this.hasImpulse = true;
      this.flightSteps = 10 + this.random.nextInt(5) * 10;
   }

   public void checkDespawn() {
      if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
         this.discard();
      }

   }

   protected double getDefaultGravity() {
      return 0.04;
   }

   public void tick() {
      super.tick();
      Entity var1 = !this.level().isClientSide() ? (Entity)EntityReference.get(this.finalTarget, this.level(), Entity.class) : null;
      HitResult var2 = null;
      if (!this.level().isClientSide()) {
         if (var1 == null) {
            this.finalTarget = null;
         }

         if (var1 == null || !var1.isAlive() || var1 instanceof Player && var1.isSpectator()) {
            this.applyGravity();
         } else {
            this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
            this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
            this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
            Vec3 var3 = this.getDeltaMovement();
            this.setDeltaMovement(var3.add((this.targetDeltaX - var3.x) * 0.2, (this.targetDeltaY - var3.y) * 0.2, (this.targetDeltaZ - var3.z) * 0.2));
         }

         var2 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      }

      Vec3 var7 = this.getDeltaMovement();
      this.setPos(this.position().add(var7));
      this.applyEffectsFromBlocks();
      if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
         this.handlePortal();
      }

      if (var2 != null && this.isAlive() && var2.getType() != HitResult.Type.MISS) {
         this.hitTargetOrDeflectSelf(var2);
      }

      ProjectileUtil.rotateTowardsMovement(this, 0.5F);
      if (this.level().isClientSide()) {
         this.level().addParticle(ParticleTypes.END_ROD, this.getX() - var7.x, this.getY() - var7.y + 0.15, this.getZ() - var7.z, 0.0, 0.0, 0.0);
      } else if (var1 != null) {
         if (this.flightSteps > 0) {
            --this.flightSteps;
            if (this.flightSteps == 0) {
               this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis(), var1);
            }
         }

         if (this.currentMoveDirection != null) {
            BlockPos var4 = this.blockPosition();
            Direction.Axis var5 = this.currentMoveDirection.getAxis();
            if (this.level().loadedAndEntityCanStandOn(var4.relative(this.currentMoveDirection), this)) {
               this.selectNextMoveDirection(var5, var1);
            } else {
               BlockPos var6 = var1.blockPosition();
               if (var5 == Direction.Axis.X && var4.getX() == var6.getX() || var5 == Direction.Axis.Z && var4.getZ() == var6.getZ() || var5 == Direction.Axis.Y && var4.getY() == var6.getY()) {
                  this.selectNextMoveDirection(var5, var1);
               }
            }
         }
      }

   }

   protected boolean isAffectedByBlocks() {
      return !this.isRemoved();
   }

   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) && !var1.noPhysics;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 16384.0;
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = var1.getEntity();
      Entity var3 = this.getOwner();
      LivingEntity var4 = var3 instanceof LivingEntity ? (LivingEntity)var3 : null;
      DamageSource var5 = this.damageSources().mobProjectile(this, var4);
      boolean var6 = var2.hurtOrSimulate(var5, 4.0F);
      if (var6) {
         Level var8 = this.level();
         if (var8 instanceof ServerLevel) {
            ServerLevel var7 = (ServerLevel)var8;
            EnchantmentHelper.doPostAttackEffects(var7, var2, var5);
         }

         if (var2 instanceof LivingEntity) {
            LivingEntity var9 = (LivingEntity)var2;
            var9.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200), (Entity)MoreObjects.firstNonNull(var3, this));
         }
      }

   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
      this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
   }

   private void destroy() {
      this.discard();
      this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of((Entity)this));
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      this.destroy();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurtClient(DamageSource var1) {
      return true;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
      var1.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
      this.destroy();
      return true;
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.setDeltaMovement(var1.getMovement());
   }
}
