package net.minecraft.world.entity.monster.cubemob;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.SulfurCubeContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SulfurCube extends AbstractCubeMob implements Bucketable, Shearable {
   public static final int SPLIT_COUNT = 2;
   public static final int MAX_SIZE = 2;
   private static final int MIN_SIZE = 1;
   public static final int PICKUP_TIMER_DURATION = 100;
   public static final double PUSH_DISTANCE_THRESHOLD = 1.2999999523162842;
   private int pickupTimer = 0;
   private boolean floatsInLiquids = false;
   private static final double MAX_PLAYER_PUSH_SPEED = 0.5;
   private static final float PLAYER_PUSH_SPEED_SCALE_MULTIPLIER = 0.3F;
   private static final float VERTICAL_PUSH_MULTIPLIER = 0.3F;
   private static final float DAMAGE_MULTIPLIER_SCALE = 0.6F;
   private static final float PUSH_SOUND_THRESHOLD = 0.5F;
   private OptionalInt maxFuseFromArchetype = OptionalInt.empty();
   private int fuse = -1;
   private static final EntityDataAccessor<Integer> MAX_FUSE;
   private static final EntityDataAccessor<Boolean> FROM_BUCKET;
   private static final boolean DEFAULT_FROM_BUCKET = false;
   private static final Predicate<ItemEntity> ALLOWED_ITEMS;

   public SulfurCube(final EntityType<? extends SulfurCube> type, final Level level) {
      super(type, level);
      this.lookControl = new SulfurCubeLookControl();
      this.moveControl = new SulfurCubeMobMoveControl(this);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(FROM_BUCKET, false);
      entityData.define(MAX_FUSE, -1);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new SulfurCubeTemptGoal(this, 1.0, (itemStack) -> this.isBaby() ? itemStack.is(ItemTags.SULFUR_CUBE_FOOD) : isSwallowableItem(itemStack), false, 1.0));
      this.goalSelector.addGoal(3, new SulfurCubeSearchForItemsGoal(this));
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public int getFuse() {
      return this.fuse;
   }

   public boolean isPrimed() {
      return this.getFuse() >= 0;
   }

   private void setFuse(final int fuse) {
      this.fuse = fuse;
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (MAX_FUSE.equals(accessor)) {
         this.setFuse((Integer)this.entityData.get(MAX_FUSE));
      }

      super.onSyncedDataUpdated(accessor);
   }

   public void setFromBucket(final boolean fromBucket) {
      this.entityData.set(FROM_BUCKET, fromBucket);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_SULFUR_CUBE;
   }

   public void saveToBucketTag(final ItemStack bucket) {
      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      bucket.copyFrom(DataComponents.SULFUR_CUBE_CONTENT, this);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
         tag.putInt("age", this.getAge());
         tag.putBoolean("age_locked", this.isAgeLocked());
      });
   }

   public boolean canBreatheUnderwater() {
      return this.hasBodyItem() || super.canBreatheUnderwater();
   }

   protected void travelInFluid(final Vec3 input) {
      super.travelInFluid(input);
      if (this.hasBodyItem() && this.floatsInLiquids) {
         float vibeAmount = 0.2F * Mth.sin((double)((float)this.tickCount * 0.4F));
         double immersion = this.getFluidHeight(this.isInWater() ? FluidTags.WATER : FluidTags.LAVA) - this.getFluidJumpThreshold() + (double)vibeAmount;
         if (immersion > 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, Math.min(1.0, immersion) * 0.03999999910593033, 0.0));
         }

      }
   }

   public double getFluidJumpThreshold() {
      return (double)this.getBbHeight() * 0.2;
   }

   public void loadFromBucketTag(final CompoundTag tag) {
      Bucketable.loadDefaultDataFromBucketTag(this, tag);
      this.setAge(tag.getIntOr("age", 0));
      this.setAgeLocked(tag.getBooleanOr("age_locked", false));
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.SULFUR_CUBE_BUCKET);
   }

   protected void addTargetingGoals() {
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   protected boolean isDealsDamage() {
      return false;
   }

   public static boolean checkSulfurCubeSpawnRules(final EntityType<SulfurCube> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return true;
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.hasBodyItem();
   }

   public boolean canBeLeashed() {
      return this.hasBodyItem();
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (this.hasBodyItem()) {
         if (this.canExplode() && !this.isPrimed()) {
            label46: {
               Entity sourceEntity = source.getDirectEntity();
               if (!source.is(DamageTypeTags.IS_FIRE)) {
                  label44: {
                     if (sourceEntity instanceof AbstractArrow) {
                        AbstractArrow projectile = (AbstractArrow)sourceEntity;
                        if (projectile.isOnFire()) {
                           break label44;
                        }
                     }

                     if (source.is(DamageTypeTags.IS_EXPLOSION)) {
                        this.primeTime(true);
                     }
                     break label46;
                  }
               }

               this.primeTime(false);
            }
         }

         if (source.is(DamageTypeTags.SULFUR_CUBE_WITH_BLOCK_IMMUNE_TO)) {
            Entity var7 = source.getEntity();
            if (var7 instanceof LivingEntity) {
               LivingEntity player = (LivingEntity)var7;
               if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                  this.entityHit(player, damage);
                  return false;
               }
            }

            return true;
         }
      }

      return super.hurtServer(level, source, damage);
   }

   public boolean hasBodyItem() {
      return !this.getItemBySlot(EquipmentSlot.BODY).isEmpty();
   }

   public boolean canExplode() {
      return this.maxFuseFromArchetype.isPresent() && this.isAlive() && !this.isPrimed();
   }

   @VisibleForTesting
   public List<SulfurCubeArchetype> matchingArchetypes(final ItemStack stack) {
      return (List)this.level().registryAccess().lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE).stream().filter((arch) -> stack.is(arch.items())).collect(Collectors.toCollection(ArrayList::new));
   }

   public void tick() {
      this.tickFuse();
      this.primeWhenOnPoweredPosition();
      super.tick();
   }

   private void tickFuse() {
      if (this.fuse > 0) {
         --this.fuse;
         if (this.fuse == 0) {
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
               ServerLevel level = (ServerLevel)var2;
               this.dropLeash();
               this.remove(Entity.RemovalReason.DISCARDED);
               level.explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, Level.ExplosionInteraction.TNT);
            }
         }
      }

   }

   private void primeWhenOnPoweredPosition() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel level) {
         if (this.canExplode()) {
            BlockPos here = BlockPos.containing(this.position());
            if (level.getBestOwnOrNeighbourSignal(here) != 0) {
               this.primeTime(false);
            }
         }
      }

   }

   public boolean primeTime(final boolean imminent) {
      if (!this.maxFuseFromArchetype.isEmpty() && this.isAlive()) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var3;
            if ((Boolean)serverLevel.getGameRules().get(GameRules.TNT_EXPLODES) && !this.isPrimed()) {
               int fuseTime = imminent ? PrimedTnt.getRandomShortFuse(this.maxFuseFromArchetype.getAsInt(), this.getRandom()) : this.maxFuseFromArchetype.getAsInt();
               this.setInvulnerable(true);
               this.setFuse(fuseTime);
               this.entityData.set(MAX_FUSE, fuseTime);
               this.makeSound(SoundEvents.TNT_PRIMED);
               this.gameEvent(GameEvent.PRIME_FUSE);
               return true;
            }
         }
      }

      return false;
   }

   protected void customServerAiStep(final ServerLevel level) {
      super.customServerAiStep(level);
      if (this.pickupTimer > 0) {
         --this.pickupTimer;
      }

   }

   protected @Nullable Map<EquipmentSlot, ItemStack> collectEquipmentChanges(final Map<EquipmentSlot, ItemStack> lastEquipmentItems) {
      ItemStack previous = (ItemStack)lastEquipmentItems.get(EquipmentSlot.BODY);
      ItemStack current = this.getItemBySlot(EquipmentSlot.BODY);
      if (this.equipmentHasChanged(previous, current)) {
         if (!current.isEmpty()) {
            this.removeAllGoals((g) -> true);
            this.setSpeed(0.0F);
         } else {
            this.registerGoals();
         }

         for(SulfurCubeArchetype archetype : this.matchingArchetypes(previous)) {
            for(SulfurCubeArchetype.AttributeEntry mod : archetype.attributeModifiers()) {
               AttributeInstance attr = this.getAttribute(mod.attribute());
               if (attr != null) {
                  attr.removeModifier(mod.modifier());
               }
            }
         }

         this.floatsInLiquids = false;
         this.maxFuseFromArchetype = OptionalInt.empty();

         for(SulfurCubeArchetype archetype : this.matchingArchetypes(current)) {
            if (archetype.buoyant()) {
               this.floatsInLiquids = true;
            }

            if (archetype.explosionFuse().isPresent()) {
               this.maxFuseFromArchetype = OptionalInt.of((Integer)archetype.explosionFuse().get());
            }

            for(SulfurCubeArchetype.AttributeEntry mod : archetype.attributeModifiers()) {
               AttributeInstance attr = this.getAttribute(mod.attribute());
               if (attr != null) {
                  attr.addOrUpdateTransientModifier(mod.modifier());
               }
            }
         }
      }

      return super.collectEquipmentChanges(lastEquipmentItems);
   }

   public float maxUpStep() {
      return this.hasBodyItem() ? 0.0F : super.maxUpStep();
   }

   protected boolean omnidirectionalAirMover() {
      return this.hasBodyItem();
   }

   protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      ItemStack heldItem = player.getItemInHand(hand);
      if (this.isBaby()) {
         if (this.isFood(heldItem) && this.canAgeUp()) {
            this.usePlayerItem(player, hand, heldItem);
            this.ageUp(getSpeedUpSecondsWhenFeeding(-this.age), true);
            return InteractionResult.SUCCESS;
         } else {
            return super.mobInteract(player, hand);
         }
      } else if (this.isPrimed()) {
         return InteractionResult.SUCCESS;
      } else if (!this.canExplode() || !heldItem.is(Items.FLINT_AND_STEEL) && !heldItem.is(Items.FIRE_CHARGE)) {
         if (heldItem.is(Items.SHEARS) && this.readyForShearing()) {
            Level var5 = this.level();
            if (var5 instanceof ServerLevel) {
               ServerLevel level = (ServerLevel)var5;
               ItemStack itemStackToShear = this.getItemBySlot(EquipmentSlot.BODY);
               this.shear(level, SoundSource.PLAYERS, heldItem);
               this.gameEvent(GameEvent.SHEAR, player);
               heldItem.hurtAndBreak(1, player, (EquipmentSlot)hand.asEquipmentSlot());
               CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.trigger((ServerPlayer)player, itemStackToShear, this);
            }

            return InteractionResult.SUCCESS;
         } else if (isSwallowableItem(heldItem)) {
            boolean itWorked = this.equipItem(heldItem);
            if (itWorked) {
               heldItem.consume(1, player);
               this.gameEvent(GameEvent.ENTITY_INTERACT);
            }

            return (InteractionResult)(itWorked ? InteractionResult.SUCCESS : InteractionResult.PASS);
         } else {
            return (InteractionResult)Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
         }
      } else {
         this.primeTime(false);
         if (heldItem.is(Items.FLINT_AND_STEEL)) {
            heldItem.hurtAndBreak(1, player, (EquipmentSlot)hand.asEquipmentSlot());
         } else {
            heldItem.consume(1, player);
         }

         player.awardStat(Stats.ITEM_USED.get(heldItem.getItem()));
         return InteractionResult.SUCCESS;
      }
   }

   public boolean equipItem(final ItemStack heldItem) {
      if (this.hasBodyItem()) {
         Item swallowedItem = this.getItemBySlot(EquipmentSlot.BODY).getItem();
         if (heldItem.is(swallowedItem)) {
            return false;
         }

         Vec3 equipmentSpawnOffset = this.getAttachments().getAverage(EntityAttachment.PASSENGER);
         Level var5 = this.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var5;
            this.spawnAtLocation(serverLevel, this.getItemBySlot(EquipmentSlot.BODY), equipmentSpawnOffset);
         }
      }

      this.setItemSlotAndDropWhenKilled(EquipmentSlot.BODY, heldItem.copyWithCount(1));
      this.playSound(this.getAbsorbSound());
      return true;
   }

   public boolean canBePickedUpWithBucket(final ItemStack itemStack) {
      return itemStack.getItem() == Items.BUCKET;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return this.isTiny() ? SoundEvents.SULFUR_CUBE_SMALL_HURT : SoundEvents.SULFUR_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.SULFUR_CUBE_SMALL_DEATH : SoundEvents.SULFUR_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      if (this.isTiny()) {
         return SoundEvents.SULFUR_CUBE_SMALL_SQUISH;
      } else {
         return this.hasBodyItem() ? SoundEvents.SULFUR_CUBE_BOUNCE : SoundEvents.SULFUR_CUBE_SQUISH;
      }
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.SULFUR_CUBE_SMALL_JUMP : SoundEvents.SULFUR_CUBE_JUMP;
   }

   private SoundEvent getHitSound() {
      return SoundEvents.SULFUR_CUBE_HIT;
   }

   private SoundEvent getPushSound() {
      return SoundEvents.SULFUR_CUBE_PUSH;
   }

   private SoundEvent getAbsorbSound() {
      return SoundEvents.SULFUR_CUBE_ABSORB;
   }

   private SoundEvent getEjectSound() {
      return SoundEvents.SULFUR_CUBE_EJECT;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      if (!this.hasBodyItem()) {
         super.playStepSound(pos, blockState);
      }

   }

   protected @Nullable ParticleOptions getParticleType() {
      return ParticleTypes.SULFUR_CUBE_GOO;
   }

   public static AttributeSupplier.Builder createSulfurCubeAttributes() {
      return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 8.0);
   }

   public void shear(final ServerLevel level, final SoundSource soundSource, final ItemStack tool) {
      Vec3 equipmentSpawnOffset = this.getAttachments().getAverage(EntityAttachment.PASSENGER);
      ItemStack itemStackToShear = this.getItemBySlot(EquipmentSlot.BODY);
      this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
      this.spawnAtLocation(level, itemStackToShear, equipmentSpawnOffset);
      this.playSound(this.getEjectSound());
      this.pickupTimer = 100;
   }

   public boolean readyForShearing() {
      return this.hasBodyItem();
   }

   public boolean canPickUpLoot() {
      return !this.hasBodyItem();
   }

   private static boolean isSwallowableItem(final ItemStack itemStack) {
      return itemStack.is(ItemTags.SULFUR_CUBE_SWALLOWABLE);
   }

   public boolean canHoldItem(final ItemStack itemStack) {
      ItemStack heldItemStack = this.getItemBySlot(EquipmentSlot.BODY);
      return heldItemStack.isEmpty() && isSwallowableItem(itemStack) && !this.isBaby();
   }

   protected void pickUpItem(final ServerLevel level, final ItemEntity entity) {
      ItemStack itemStack = entity.getItem();
      if (this.canHoldItem(itemStack) && this.pickupTimer <= 0) {
         this.onItemPickup(entity);
         this.setItemSlot(EquipmentSlot.BODY, itemStack.split(1));
         this.playSound(this.getAbsorbSound());
         this.setGuaranteedDrop(EquipmentSlot.BODY);
         this.take(entity, 1);
      }

   }

   protected int getBaseExperienceReward(final ServerLevel level) {
      return this.isBaby() ? 0 : 1 + this.random.nextInt(2);
   }

   protected int getSplitCount() {
      return 2;
   }

   protected void setSpawnSize(final ServerLevelAccessor level, final DifficultyInstance difficulty) {
      if (this.isBaby()) {
         this.setSize(1, true);
      } else {
         this.setSize(2, true);
      }

   }

   protected void setUpSplitCube(final AbstractCubeMob cubeMob, final int halfSize, final float xd, final float zd) {
      super.setUpSplitCube(cubeMob, halfSize, xd, zd);
      cubeMob.setBaby(true);
   }

   public @Nullable AbstractCubeMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      SulfurCube sulfurCube = EntityTypes.SULFUR_CUBE.create(level, EntitySpawnReason.BREEDING);
      if (sulfurCube != null) {
         sulfurCube.setSize(1, true);
      }

      return sulfurCube;
   }

   private boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.SULFUR_CUBE_FOOD);
   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (!this.isBaby()) {
         this.setSize(2, true);
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("pickup_timer", this.pickupTimer);
      output.putBoolean("from_bucket", this.fromBucket());
      output.putInt("fuse", this.getFuse());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.pickupTimer = input.getIntOr("pickup_timer", 0);
      this.setFromBucket(input.getBooleanOr("from_bucket", false));
      this.setFuse(input.getIntOr("fuse", -1));
      this.entityData.set(MAX_FUSE, this.getFuse());
      super.readAdditionalSaveData(input);
   }

   public void playerTouch(final Player player) {
      super.playerTouch(player);
      this.playerPush(player);
   }

   private void playerPush(final Player player) {
      if (this.hasBodyItem()) {
         Vec3 cubeToPlayer = this.position().subtract(player.position());
         if (cubeToPlayer.horizontalDistance() < 1.2999999523162842 && player.getY() <= this.getY() + (double)this.getBbHeight()) {
            double knockback = Math.max(0.0, 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 pushDirection = cubeToPlayer.horizontal().normalize().scale(knockback);
            double playerSpeed = player.getKnownSpeed().length() * 2.0 * 0.30000001192092896;
            playerSpeed = Mth.clamp(playerSpeed, 0.0, 0.5);
            Vec3 pushVelocity = (new Vec3(pushDirection.x, this.onGround() ? knockback * 0.30000001192092896 : 0.0, pushDirection.z)).scale(playerSpeed);
            this.needsSync = true;
            if (pushVelocity.lengthSqr() > 0.25) {
               this.playSound(this.getPushSound());
            }

            this.addDeltaMovement(pushVelocity);
         }

      }
   }

   private void entityHit(final LivingEntity player, final float damage) {
      Vec3 playerEyePosition = player.getEyePosition();
      Vec3 cubePosition = this.getBoundingBox().getCenter();
      Vec3 playerToCubeDirectionEye = cubePosition.subtract(playerEyePosition);
      Vec3 playerAimDirection = player.getHeadLookAngle().scale(playerToCubeDirectionEye.length());
      double hitScale = 1.0 / (double)((float)this.getSize() * this.getScale());
      Vec3 hitVector = playerToCubeDirectionEye.subtract(playerAimDirection).scale(hitScale);
      hitVector = hitVector.add(cubePosition.subtract(player.position()).normalize().scale(hitScale)).scale(0.5);
      this.playSound(this.getHitSound());
      this.applyKnockback(damage, hitVector);
   }

   private void applyKnockback(final float damage, final Vec3 hitVector) {
      double damageMultiplier = (double)Mth.sqrt(damage);
      damageMultiplier *= Math.max(0.0, 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
      this.needsSync = true;
      Vec3 deltaVector = hitVector.scale(damageMultiplier * 0.6000000238418579);
      this.addDeltaMovement(deltaVector);
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.SULFUR_CUBE_CONTENT ? castComponentValue(type, getSulfurCubeContent(this.getBodyArmorItem())) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.SULFUR_CUBE_CONTENT);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.SULFUR_CUBE_CONTENT) {
         this.setSulfurCubeContent((SulfurCubeContent)castComponentValue(DataComponents.SULFUR_CUBE_CONTENT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   private static @Nullable SulfurCubeContent getSulfurCubeContent(final ItemStack itemStack) {
      return itemStack.isEmpty() ? null : SulfurCubeContent.ofNonEmpty(itemStack);
   }

   private void setSulfurCubeContent(final SulfurCubeContent sulfurCubeContent) {
      this.setItemSlotAndDropWhenKilled(EquipmentSlot.BODY, sulfurCubeContent.absorbedBlockItemStack().create());
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(this.getBbHeight() / 2.0F), 0.0);
   }

   protected void setcubeMobHealth(final int actualSize) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(4 * actualSize));
   }

   static {
      MAX_FUSE = SynchedEntityData.<Integer>defineId(SulfurCube.class, EntityDataSerializers.INT);
      FROM_BUCKET = SynchedEntityData.<Boolean>defineId(SulfurCube.class, EntityDataSerializers.BOOLEAN);
      ALLOWED_ITEMS = (e) -> !e.hasPickUpDelay() && e.isAlive() && isSwallowableItem(e.getItem());
   }

   private static class SulfurCubeTemptGoal extends TemptGoal.ForNonPathfinders {
      public SulfurCubeTemptGoal(final Mob mob, final double speedModifier, final Predicate<ItemStack> items, final boolean canScare, final double stopDistance) {
         super(mob, speedModifier, items, canScare, stopDistance);
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      protected void stopNavigation() {
         MoveControl var2 = this.mob.getMoveControl();
         if (var2 instanceof AbstractCubeMob.CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setWantedMovement(0.0);
         }

      }

      protected void navigateTowards(final Player player) {
         this.mob.lookAt(player, 10.0F, 10.0F);
         MoveControl var3 = this.mob.getMoveControl();
         if (var3 instanceof AbstractCubeMob.CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setDirection(this.mob.getYRot(), true);
         }

      }
   }

   private class SulfurCubeSearchForItemsGoal extends Goal {
      SulfurCube sulfurCube;
      @Nullable ItemEntity targetItem;

      public SulfurCubeSearchForItemsGoal(final SulfurCube sulfurCube) {
         Objects.requireNonNull(SulfurCube.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
         this.sulfurCube = sulfurCube;
      }

      public boolean canUse() {
         if (!this.sulfurCube.isBaby() && this.sulfurCube.pickupTimer <= 0) {
            this.targetItem = (ItemEntity)getServerLevel(this.sulfurCube).getNearestEntity(this.sulfurCube.level().getEntitiesOfClass(ItemEntity.class, this.sulfurCube.getBoundingBox().inflate(8.0, 8.0, 8.0), SulfurCube.ALLOWED_ITEMS), this.sulfurCube.getX(), this.sulfurCube.getY(), this.sulfurCube.getZ());
            return this.targetItem != null;
         } else {
            return false;
         }
      }

      public void tick() {
         SulfurCube.this.lookAt(this.targetItem, 10.0F, 10.0F);
         MoveControl var2 = SulfurCube.this.getMoveControl();
         if (var2 instanceof AbstractCubeMob.CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setDirection(SulfurCube.this.getYRot(), true);
         }

      }
   }

   private class SulfurCubeLookControl extends LookControl {
      private SulfurCubeLookControl() {
         Objects.requireNonNull(SulfurCube.this);
         super(SulfurCube.this);
      }

      public void tick() {
         if (!SulfurCube.this.hasBodyItem()) {
            super.tick();
         } else {
            float closeAngle = Mth.wrapDegrees90(SulfurCube.this.getYRot());
            SulfurCube.this.setYRot(SulfurCube.this.getYRot() - closeAngle);
            SulfurCube.this.setYHeadRot(SulfurCube.this.getYRot());
         }
      }
   }

   protected static class SulfurCubeMobMoveControl<T extends SulfurCube> extends AbstractCubeMob.CubeMobMoveControl<T> {
      public SulfurCubeMobMoveControl(final T cubeMob) {
         super(cubeMob);
      }

      public void tick() {
         if (!((SulfurCube)this.mob).hasBodyItem()) {
            super.tick();
         }

      }
   }
}
