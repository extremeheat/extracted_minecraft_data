package net.minecraft.world.entity.projectile.arrow;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class AbstractArrow extends Projectile {
   private static final double ARROW_BASE_DAMAGE = 2.0;
   private static final int SHAKE_TIME = 7;
   private static final float WATER_INERTIA = 0.6F;
   private static final float INERTIA = 0.99F;
   private static final short DEFAULT_LIFE = 0;
   private static final byte DEFAULT_SHAKE = 0;
   private static final boolean DEFAULT_IN_GROUND = false;
   private static final boolean DEFAULT_CRIT = false;
   private static final byte DEFAULT_PIERCE_LEVEL = 0;
   private static final EntityDataAccessor<Byte> ID_FLAGS;
   private static final EntityDataAccessor<Byte> PIERCE_LEVEL;
   private static final EntityDataAccessor<Boolean> IN_GROUND;
   private static final int FLAG_CRIT = 1;
   private static final int FLAG_NOPHYSICS = 2;
   private @Nullable BlockState lastState;
   protected int inGroundTime;
   public Pickup pickup;
   public int shakeTime;
   private int life;
   private double baseDamage;
   private SoundEvent soundEvent;
   private @Nullable IntOpenHashSet piercingIgnoreEntityIds;
   private @Nullable List<Entity> piercedAndKilledEntities;
   private ItemStack pickupItemStack;
   private @Nullable ItemStack firedFromWeapon;

   protected AbstractArrow(final EntityType<? extends AbstractArrow> type, final Level level) {
      super(type, level);
      this.pickup = AbstractArrow.Pickup.DISALLOWED;
      this.shakeTime = 0;
      this.life = 0;
      this.baseDamage = 2.0;
      this.soundEvent = this.getDefaultHitGroundSoundEvent();
      this.pickupItemStack = this.getDefaultPickupItem();
      this.firedFromWeapon = null;
   }

   protected AbstractArrow(final EntityType<? extends AbstractArrow> type, final double x, final double y, final double z, final Level level, final ItemStack pickupItemStack, final @Nullable ItemStack firedFromWeapon) {
      this(type, level);
      this.pickupItemStack = pickupItemStack.copy();
      this.applyComponentsFromItemStack(pickupItemStack);
      Unit intangible = (Unit)pickupItemStack.remove(DataComponents.INTANGIBLE_PROJECTILE);
      if (intangible != null) {
         this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
      }

      this.setPos(x, y, z);
      if (firedFromWeapon != null && level instanceof ServerLevel serverLevel) {
         if (firedFromWeapon.isEmpty()) {
            throw new IllegalArgumentException("Invalid weapon firing an arrow");
         }

         this.firedFromWeapon = firedFromWeapon.copy();
         int pierceLevel = EnchantmentHelper.getPiercingCount(serverLevel, firedFromWeapon, this.pickupItemStack);
         if (pierceLevel > 0) {
            this.setPierceLevel((byte)pierceLevel);
         }
      }

   }

   protected AbstractArrow(final EntityType<? extends AbstractArrow> type, final LivingEntity mob, final Level level, final ItemStack pickupItemStack, final @Nullable ItemStack firedFromWeapon) {
      this(type, mob.getX(), mob.getEyeY() - 0.10000000149011612, mob.getZ(), level, pickupItemStack, firedFromWeapon);
      this.setOwner(mob);
   }

   public void setSoundEvent(final SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
   }

   public boolean shouldRenderAtSqrDistance(final double distance) {
      double size = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(size)) {
         size = 1.0;
      }

      size *= 64.0 * getViewScale();
      return distance < size * size;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(ID_FLAGS, (byte)0);
      entityData.define(PIERCE_LEVEL, (byte)0);
      entityData.define(IN_GROUND, false);
   }

   public void shoot(final double xd, final double yd, final double zd, final float pow, final float uncertainty) {
      super.shoot(xd, yd, zd, pow, uncertainty);
      this.life = 0;
   }

   public void lerpMotion(final Vec3 movement) {
      super.lerpMotion(movement);
      this.life = 0;
      if (this.isInGround() && movement.lengthSqr() > 0.0) {
         this.setInGround(false);
      }

   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (!this.firstTick && this.shakeTime <= 0 && accessor.equals(IN_GROUND) && this.isInGround()) {
         this.shakeTime = 7;
      }

   }

   public void tick() {
      boolean physicsEnabled = !this.isNoPhysics();
      Vec3 movement = this.getDeltaMovement();
      BlockPos blockPos = this.blockPosition();
      BlockState blockState = this.level().getBlockState(blockPos);
      if (!blockState.isAir() && physicsEnabled) {
         VoxelShape shape = blockState.getCollisionShape(this.level(), blockPos);
         if (!shape.isEmpty()) {
            Vec3 position = this.position();

            for(AABB aabb : shape.toAabbs()) {
               if (aabb.move(blockPos).contains(position)) {
                  this.setDeltaMovement(Vec3.ZERO);
                  this.setInGround(true);
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         --this.shakeTime;
      }

      if (this.isInWaterOrRain()) {
         this.clearFire();
      }

      if (this.isInGround() && physicsEnabled) {
         if (!this.level().isClientSide()) {
            if (this.lastState != blockState && this.shouldFall()) {
               this.startFalling();
            } else {
               this.tickDespawn();
            }
         }

         ++this.inGroundTime;
         if (this.isAlive()) {
            this.applyEffectsFromBlocks();
         }

         if (!this.level().isClientSide()) {
            this.setSharedFlagOnFire(this.getRemainingFireTicks() > 0);
         }

      } else {
         this.inGroundTime = 0;
         Vec3 originalPosition = this.position();
         if (this.isInWater()) {
            this.applyInertia(this.getWaterInertia());
            this.addBubbleParticles(originalPosition);
         }

         if (this.isCritArrow()) {
            for(int i = 0; i < 4; ++i) {
               this.level().addParticle(ParticleTypes.CRIT, originalPosition.x + movement.x * (double)i / 4.0, originalPosition.y + movement.y * (double)i / 4.0, originalPosition.z + movement.z * (double)i / 4.0, -movement.x, -movement.y + 0.2, -movement.z);
            }
         }

         float yRot;
         if (!physicsEnabled) {
            yRot = (float)(Mth.atan2(-movement.x, -movement.z) * 57.2957763671875);
         } else {
            yRot = (float)(Mth.atan2(movement.x, movement.z) * 57.2957763671875);
         }

         float xRot = (float)(Mth.atan2(movement.y, movement.horizontalDistance()) * 57.2957763671875);
         this.setXRot(lerpRotation(this.getXRot(), xRot));
         this.setYRot(lerpRotation(this.getYRot(), yRot));
         this.checkLeftOwner();
         if (physicsEnabled) {
            BlockHitResult blockHitResult = this.level().clipIncludingBorder(new ClipContext(originalPosition, originalPosition.add(movement), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            this.stepMoveAndHit(blockHitResult);
         } else {
            this.setPos(originalPosition.add(movement));
            this.applyEffectsFromBlocks();
         }

         if (!this.isInWater()) {
            this.applyInertia(0.99F);
         }

         if (physicsEnabled && !this.isInGround()) {
            this.applyGravity();
         }

         super.tick();
      }
   }

   private void stepMoveAndHit(final BlockHitResult blockHitResult) {
      while(true) {
         if (this.isAlive()) {
            Vec3 initialPosition = this.position();
            ArrayList<EntityHitResult> entitiesHit = new ArrayList(this.findHitEntities(initialPosition, blockHitResult.getLocation()));
            entitiesHit.sort(Comparator.comparingDouble((c) -> initialPosition.distanceToSqr(c.getEntity().position())));
            EntityHitResult firstEntityHit = entitiesHit.isEmpty() ? null : (EntityHitResult)entitiesHit.getFirst();
            Vec3 nextLocation = ((HitResult)Objects.requireNonNullElse(firstEntityHit, blockHitResult)).getLocation();
            this.setPos(nextLocation);
            this.applyEffectsFromBlocks(initialPosition, nextLocation);
            if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
               this.handlePortal();
            }

            if (entitiesHit.isEmpty()) {
               if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
                  this.hitTargetOrDeflectSelf(blockHitResult);
                  this.needsSync = true;
               }
            } else {
               if (!this.isAlive() || this.noPhysics) {
                  continue;
               }

               ProjectileDeflection deflection = this.hitTargetsOrDeflectSelf(entitiesHit);
               this.needsSync = true;
               if (this.getPierceLevel() > 0 && deflection == ProjectileDeflection.NONE) {
                  continue;
               }
            }
         }

         return;
      }
   }

   private ProjectileDeflection hitTargetsOrDeflectSelf(final Collection<EntityHitResult> entityHitResults) {
      for(EntityHitResult e : entityHitResults) {
         ProjectileDeflection deflection = this.hitTargetOrDeflectSelf(e);
         if (!this.isAlive() || deflection != ProjectileDeflection.NONE) {
            return deflection;
         }
      }

      return ProjectileDeflection.NONE;
   }

   private void applyInertia(final float inertia) {
      Vec3 movement = this.getDeltaMovement();
      this.setDeltaMovement(movement.scale((double)inertia));
   }

   private void addBubbleParticles(final Vec3 position) {
      Vec3 movement = this.getDeltaMovement();

      for(int i = 0; i < 4; ++i) {
         float s = 0.25F;
         this.level().addParticle(ParticleTypes.BUBBLE, position.x - movement.x * 0.25, position.y - movement.y * 0.25, position.z - movement.z * 0.25, movement.x, movement.y, movement.z);
      }

   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   private boolean shouldFall() {
      return this.isInGround() && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06));
   }

   private void startFalling() {
      this.setInGround(false);
      Vec3 deltaMovement = this.getDeltaMovement();
      this.setDeltaMovement(deltaMovement.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   protected boolean isInGround() {
      return (Boolean)this.entityData.get(IN_GROUND);
   }

   protected void setInGround(final boolean inGround) {
      this.entityData.set(IN_GROUND, inGround);
   }

   public boolean isPushedByFluid() {
      return !this.isInGround();
   }

   public void move(final MoverType moverType, final Vec3 delta) {
      super.move(moverType, delta);
      if (moverType != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.discard();
      }

   }

   private void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   public void onItemBreak(final Item item) {
      this.firedFromWeapon = null;
   }

   public void onAboveBubbleColumn(final boolean dragDown, final BlockPos pos) {
      if (!this.isInGround()) {
         super.onAboveBubbleColumn(dragDown, pos);
      }
   }

   public void onInsideBubbleColumn(final boolean dragDown) {
      if (!this.isInGround()) {
         super.onInsideBubbleColumn(dragDown);
      }
   }

   public void push(final double xa, final double ya, final double za) {
      if (!this.isInGround()) {
         super.push(xa, ya, za);
      }
   }

   protected void onHitEntity(final EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      Entity entity = hitResult.getEntity();
      float pow = (float)this.getDeltaMovement().length();
      double arrowDamage = this.baseDamage;
      Entity currentOwner = this.getOwner();
      DamageSource damageSource = this.damageSources().arrow(this, (Entity)(currentOwner != null ? currentOwner : this));
      if (this.getWeaponItem() != null) {
         Level var9 = this.level();
         if (var9 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var9;
            arrowDamage = (double)EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damageSource, (float)arrowDamage);
         }
      }

      int damage = Mth.ceil(Mth.clamp((double)pow * arrowDamage, 0.0, 2.147483647E9));
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.discard();
            return;
         }

         this.piercingIgnoreEntityIds.add(entity.getId());
      }

      if (this.isCritArrow()) {
         long dmgIncrease = (long)this.random.nextInt(damage / 2 + 2);
         damage = (int)Math.min(dmgIncrease + (long)damage, 2147483647L);
      }

      if (currentOwner instanceof LivingEntity livingOwner) {
         livingOwner.setLastHurtMob(entity);
      }

      boolean isEnderman = entity.is(EntityTypes.ENDERMAN);
      int remainingFireTicks = entity.getRemainingFireTicks();
      if (this.isOnFire() && !isEnderman) {
         entity.igniteForSeconds(5.0F);
      }

      if (entity.hurtOrSimulate(damageSource, (float)damage)) {
         if (isEnderman) {
            return;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity mob = (LivingEntity)entity;
            if (!this.level().isClientSide() && this.getPierceLevel() <= 0) {
               mob.setArrowCount(mob.getArrowCount() + 1);
            }

            this.doKnockback(mob, damageSource);
            Level var13 = this.level();
            if (var13 instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)var13;
               EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, mob, damageSource, this.getWeaponItem());
            }

            this.doPostHurtEffects(mob);
            if (mob instanceof Player && currentOwner instanceof ServerPlayer) {
               ServerPlayer ownerPlayer = (ServerPlayer)currentOwner;
               if (!this.isSilent() && mob != ownerPlayer) {
                  ownerPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND, 0.0F));
               }
            }

            if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(mob);
            }

            if (!this.level().isClientSide() && currentOwner instanceof ServerPlayer) {
               ServerPlayer player = (ServerPlayer)currentOwner;
               if (this.piercedAndKilledEntities != null) {
                  CriteriaTriggers.KILLED_BY_ARROW.trigger(player, this.piercedAndKilledEntities, this.firedFromWeapon);
               } else if (!entity.isAlive()) {
                  CriteriaTriggers.KILLED_BY_ARROW.trigger(player, List.of(entity), this.firedFromWeapon);
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         entity.setRemainingFireTicks(remainingFireTicks);
         this.deflect(ProjectileDeflection.REVERSE, entity, this.owner, false);
         this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
         Level var21 = this.level();
         if (var21 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var21;
            if (this.getDeltaMovement().lengthSqr() < 1.0E-7) {
               if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                  this.spawnAtLocation(level, this.getPickupItem(), 0.1F);
               }

               this.discard();
            }
         }
      }

   }

   protected void doKnockback(final LivingEntity mob, final DamageSource damageSource) {
      float var10000;
      label18: {
         if (this.firedFromWeapon != null) {
            Level var6 = this.level();
            if (var6 instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)var6;
               var10000 = EnchantmentHelper.modifyKnockback(serverLevel, this.firedFromWeapon, mob, damageSource, 0.0F);
               break label18;
            }
         }

         var10000 = 0.0F;
      }

      double knockback = (double)var10000;
      if (knockback > 0.0) {
         double knockbackResistance = Math.max(0.0, 1.0 - mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
         Vec3 movement = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * knockbackResistance);
         if (movement.lengthSqr() > 0.0) {
            mob.push(movement.x, 0.1, movement.z);
         }
      }

   }

   protected void onHitBlock(final BlockHitResult hitResult) {
      this.lastState = this.level().getBlockState(hitResult.getBlockPos());
      super.onHitBlock(hitResult);
      ItemStack weaponItem = this.getWeaponItem();
      Level var4 = this.level();
      if (var4 instanceof ServerLevel serverLevel) {
         if (weaponItem != null) {
            this.hitBlockEnchantmentEffects(serverLevel, hitResult, weaponItem);
         }
      }

      Vec3 movement = this.getDeltaMovement();
      Vec3 offsetDirection = new Vec3(Math.signum(movement.x), Math.signum(movement.y), Math.signum(movement.z));
      Vec3 scaledMovement = offsetDirection.scale(0.05000000074505806);
      this.setPos(this.position().subtract(scaledMovement));
      this.setDeltaMovement(Vec3.ZERO);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.setInGround(true);
      this.shakeTime = 7;
      this.setCritArrow(false);
      this.setPierceLevel((byte)0);
      this.setSoundEvent(SoundEvents.ARROW_HIT);
      this.resetPiercedEntities();
   }

   protected void hitBlockEnchantmentEffects(final ServerLevel serverLevel, final BlockHitResult hitResult, final ItemStack weapon) {
      Vec3 compensatedHitPosition = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
      Entity var6 = this.getOwner();
      LivingEntity var10002;
      if (var6 instanceof LivingEntity livingOwner) {
         var10002 = livingOwner;
      } else {
         var10002 = null;
      }

      EnchantmentHelper.onHitBlock(serverLevel, weapon, var10002, this, (EquipmentSlot)null, compensatedHitPosition, serverLevel.getBlockState(hitResult.getBlockPos()), (item) -> this.firedFromWeapon = null);
   }

   public @Nullable ItemStack getWeaponItem() {
      return this.firedFromWeapon;
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(final LivingEntity mob) {
   }

   protected @Nullable EntityHitResult findHitEntity(final Vec3 from, final Vec3 to) {
      return ProjectileUtil.getEntityHitResult(this.level(), this, from, to, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
   }

   protected Collection<EntityHitResult> findHitEntities(final Vec3 from, final Vec3 to) {
      return ProjectileUtil.getManyEntityHitResult(this.level(), this, from, to, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity, false);
   }

   protected boolean canHitEntity(final Entity entity) {
      if (entity instanceof Player playerEntity) {
         Entity var4 = this.getOwner();
         if (var4 instanceof Player player) {
            if (!player.canHarmPlayer(playerEntity)) {
               return false;
            }
         }
      }

      return super.canHitEntity(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putShort("life", (short)this.life);
      output.storeNullable("inBlockState", BlockState.CODEC, this.lastState);
      output.putByte("shake", (byte)this.shakeTime);
      output.putBoolean("inGround", this.isInGround());
      output.store("pickup", AbstractArrow.Pickup.LEGACY_CODEC, this.pickup);
      output.putDouble("damage", this.baseDamage);
      output.putBoolean("crit", this.isCritArrow());
      output.putByte("PierceLevel", this.getPierceLevel());
      output.store("SoundEvent", BuiltInRegistries.SOUND_EVENT.byNameCodec(), this.soundEvent);
      output.store("item", ItemStack.CODEC, this.pickupItemStack);
      output.storeNullable("weapon", ItemStack.CODEC, this.firedFromWeapon);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.life = input.getShortOr("life", (short)0);
      this.lastState = (BlockState)input.read("inBlockState", BlockState.CODEC).orElse((Object)null);
      this.shakeTime = input.getByteOr("shake", (byte)0) & 255;
      this.setInGround(input.getBooleanOr("inGround", false));
      this.baseDamage = input.getDoubleOr("damage", 2.0);
      this.pickup = (Pickup)input.read("pickup", AbstractArrow.Pickup.LEGACY_CODEC).orElse(AbstractArrow.Pickup.DISALLOWED);
      this.setCritArrow(input.getBooleanOr("crit", false));
      this.setPierceLevel(input.getByteOr("PierceLevel", (byte)0));
      this.soundEvent = (SoundEvent)input.read("SoundEvent", BuiltInRegistries.SOUND_EVENT.byNameCodec()).orElse(this.getDefaultHitGroundSoundEvent());
      this.setPickupItemStack((ItemStack)input.read("item", ItemStack.CODEC).orElse(this.getDefaultPickupItem()));
      this.firedFromWeapon = (ItemStack)input.read("weapon", ItemStack.CODEC).orElse((Object)null);
   }

   public void setOwner(final @Nullable Entity owner) {
      super.setOwner(owner);
      Entity var2 = owner;
      byte var3 = 0;

      Pickup var10001;
      label16:
      while(true) {
         //$FF: var3->value
         //0->net/minecraft/world/entity/player/Player
         //1->net/minecraft/world/entity/OminousItemSpawner
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case -1:
            default:
               var10001 = this.pickup;
               break label16;
            case 0:
               Player ignored = (Player)var2;
               if (this.pickup != AbstractArrow.Pickup.DISALLOWED) {
                  var3 = 1;
                  break;
               }

               var10001 = AbstractArrow.Pickup.ALLOWED;
               break label16;
            case 1:
               OminousItemSpawner ignored = (OminousItemSpawner)var2;
               var10001 = AbstractArrow.Pickup.DISALLOWED;
               break label16;
         }
      }

      this.pickup = var10001;
   }

   public void playerTouch(final Player player) {
      if (!this.level().isClientSide() && (this.isInGround() || this.isNoPhysics()) && this.shakeTime <= 0) {
         if (this.tryPickup(player)) {
            player.take(this, 1);
            this.discard();
         }

      }
   }

   protected boolean tryPickup(final Player player) {
      boolean var10000;
      switch (this.pickup.ordinal()) {
         case 0 -> var10000 = false;
         case 1 -> var10000 = player.getInventory().add(this.getPickupItem());
         case 2 -> var10000 = player.hasInfiniteMaterials();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   protected ItemStack getPickupItem() {
      return this.pickupItemStack.copy();
   }

   protected abstract ItemStack getDefaultPickupItem();

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public ItemStack getPickupItemStackOrigin() {
      return this.pickupItemStack;
   }

   public void setBaseDamage(final double baseDamage) {
      this.baseDamage = baseDamage;
   }

   public boolean isAttackable() {
      return this.is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
   }

   public void setCritArrow(final boolean critArrow) {
      this.setFlag(1, critArrow);
   }

   private void setPierceLevel(final byte pieceLevel) {
      this.entityData.set(PIERCE_LEVEL, pieceLevel);
   }

   private void setFlag(final int flag, final boolean value) {
      byte flags = (Byte)this.entityData.get(ID_FLAGS);
      if (value) {
         this.entityData.set(ID_FLAGS, (byte)(flags | flag));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(flags & ~flag));
      }

   }

   protected void setPickupItemStack(final ItemStack itemStack) {
      if (!itemStack.isEmpty()) {
         this.pickupItemStack = itemStack;
      } else {
         this.pickupItemStack = this.getDefaultPickupItem();
      }

   }

   public boolean isCritArrow() {
      byte flags = (Byte)this.entityData.get(ID_FLAGS);
      return (flags & 1) != 0;
   }

   public byte getPierceLevel() {
      return (Byte)this.entityData.get(PIERCE_LEVEL);
   }

   public void setBaseDamageFromMob(final float power) {
      this.setBaseDamage((double)(power * 2.0F) + this.random.triangle((double)this.level().getDifficulty().getId() * 0.11, 0.57425));
   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(final boolean noPhysics) {
      this.noPhysics = noPhysics;
      this.setFlag(2, noPhysics);
   }

   public boolean isNoPhysics() {
      if (!this.level().isClientSide()) {
         return this.noPhysics;
      } else {
         return ((Byte)this.entityData.get(ID_FLAGS) & 2) != 0;
      }
   }

   public boolean isPickable() {
      return super.isPickable() && !this.isInGround();
   }

   public @Nullable SlotAccess getSlot(final int slot) {
      return slot == 0 ? SlotAccess.of(this::getPickupItemStackOrigin, this::setPickupItemStack) : super.getSlot(slot);
   }

   protected boolean shouldBounceOnWorldBorder() {
      return true;
   }

   static {
      ID_FLAGS = SynchedEntityData.<Byte>defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
      PIERCE_LEVEL = SynchedEntityData.<Byte>defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
      IN_GROUND = SynchedEntityData.<Boolean>defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);
   }

   public static enum Pickup {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static final Codec<Pickup> LEGACY_CODEC = Codec.BYTE.xmap(Pickup::byOrdinal, (p) -> (byte)p.ordinal());

      private Pickup() {
      }

      public static Pickup byOrdinal(int ordinal) {
         if (ordinal < 0 || ordinal > values().length) {
            ordinal = 0;
         }

         return values()[ordinal];
      }

      // $FF: synthetic method
      private static Pickup[] $values() {
         return new Pickup[]{DISALLOWED, ALLOWED, CREATIVE_ONLY};
      }
   }
}
