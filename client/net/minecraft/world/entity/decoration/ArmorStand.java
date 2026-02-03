package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ArmorStand extends LivingEntity {
   public static final int WOBBLE_TIME = 5;
   private static final boolean ENABLE_ARMS = true;
   public static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   public static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   public static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   public static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   public static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   public static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   private static final EntityDimensions MARKER_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final double FEET_OFFSET = 0.1;
   private static final double CHEST_OFFSET = 0.9;
   private static final double LEGS_OFFSET = 0.4;
   private static final double HEAD_OFFSET = 1.6;
   public static final int DISABLE_TAKING_OFFSET = 8;
   public static final int DISABLE_PUTTING_OFFSET = 16;
   public static final int CLIENT_FLAG_SMALL = 1;
   public static final int CLIENT_FLAG_SHOW_ARMS = 4;
   public static final int CLIENT_FLAG_NO_BASEPLATE = 8;
   public static final int CLIENT_FLAG_MARKER = 16;
   public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS;
   public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE;
   public static final EntityDataAccessor<Rotations> DATA_BODY_POSE;
   public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE;
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE;
   public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE;
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE;
   private static final Predicate<Entity> RIDABLE_MINECARTS;
   private static final boolean DEFAULT_INVISIBLE = false;
   private static final int DEFAULT_DISABLED_SLOTS = 0;
   private static final boolean DEFAULT_SMALL = false;
   private static final boolean DEFAULT_SHOW_ARMS = false;
   private static final boolean DEFAULT_NO_BASE_PLATE = false;
   private static final boolean DEFAULT_MARKER = false;
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;

   public ArmorStand(final EntityType<? extends ArmorStand> type, final Level level) {
      super(type, level);
      this.invisible = false;
      this.disabledSlots = 0;
   }

   public ArmorStand(final Level level, final double x, final double y, final double z) {
      this(EntityType.ARMOR_STAND, level);
      this.setPos(x, y, z);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createLivingAttributes().add(Attributes.STEP_HEIGHT, 0.0);
   }

   public void refreshDimensions() {
      double oldX = this.getX();
      double oldY = this.getY();
      double oldZ = this.getZ();
      super.refreshDimensions();
      this.setPos(oldX, oldY, oldZ);
   }

   private boolean hasPhysics() {
      return !this.isMarker() && !this.isNoGravity();
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_CLIENT_FLAGS, (byte)0);
      entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
   }

   public boolean canUseSlot(final EquipmentSlot slot) {
      return slot != EquipmentSlot.BODY && slot != EquipmentSlot.SADDLE && !this.isDisabled(slot);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("Invisible", this.isInvisible());
      output.putBoolean("Small", this.isSmall());
      output.putBoolean("ShowArms", this.showArms());
      output.putInt("DisabledSlots", this.disabledSlots);
      output.putBoolean("NoBasePlate", !this.showBasePlate());
      if (this.isMarker()) {
         output.putBoolean("Marker", this.isMarker());
      }

      output.store("Pose", ArmorStand.ArmorStandPose.CODEC, this.getArmorStandPose());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setInvisible(input.getBooleanOr("Invisible", false));
      this.setSmall(input.getBooleanOr("Small", false));
      this.setShowArms(input.getBooleanOr("ShowArms", false));
      this.disabledSlots = input.getIntOr("DisabledSlots", 0);
      this.setNoBasePlate(input.getBooleanOr("NoBasePlate", false));
      this.setMarker(input.getBooleanOr("Marker", false));
      this.noPhysics = !this.hasPhysics();
      input.read("Pose", ArmorStand.ArmorStandPose.CODEC).ifPresent(this::setArmorStandPose);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(final Entity entity) {
   }

   protected void pushEntities() {
      for(Entity entity : this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS)) {
         if (this.distanceToSqr(entity) <= 0.2) {
            entity.push((Entity)this);
         }
      }

   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (!this.isMarker() && !itemStack.is(Items.NAME_TAG)) {
         if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
         } else if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS_SERVER;
         } else {
            EquipmentSlot itemInHandSlot = this.getEquipmentSlotForItem(itemStack);
            if (itemStack.isEmpty()) {
               EquipmentSlot clickedSlot = this.getClickedSlot(location);
               EquipmentSlot targetSlot = this.isDisabled(clickedSlot) ? itemInHandSlot : clickedSlot;
               if (this.hasItemInSlot(targetSlot) && this.swapItem(player, targetSlot, itemStack, hand)) {
                  return InteractionResult.SUCCESS_SERVER;
               }
            } else {
               if (this.isDisabled(itemInHandSlot)) {
                  return InteractionResult.FAIL;
               }

               if (itemInHandSlot.getType() == EquipmentSlot.Type.HAND && !this.showArms()) {
                  return InteractionResult.FAIL;
               }

               if (this.swapItem(player, itemInHandSlot, itemStack, hand)) {
                  return InteractionResult.SUCCESS_SERVER;
               }
            }

            return super.interact(player, hand, location);
         }
      } else {
         return super.interact(player, hand, location);
      }
   }

   private EquipmentSlot getClickedSlot(final Vec3 location) {
      EquipmentSlot slotClicked = EquipmentSlot.MAINHAND;
      boolean small = this.isSmall();
      double clickYPosition = location.y / (double)(this.getScale() * this.getAgeScale());
      EquipmentSlot feet = EquipmentSlot.FEET;
      if (clickYPosition >= 0.1 && clickYPosition < 0.1 + (small ? 0.8 : 0.45) && this.hasItemInSlot(feet)) {
         slotClicked = EquipmentSlot.FEET;
      } else if (clickYPosition >= 0.9 + (small ? 0.3 : 0.0) && clickYPosition < 0.9 + (small ? 1.0 : 0.7) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
         slotClicked = EquipmentSlot.CHEST;
      } else if (clickYPosition >= 0.4 && clickYPosition < 0.4 + (small ? 1.0 : 0.8) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
         slotClicked = EquipmentSlot.LEGS;
      } else if (clickYPosition >= 1.6 && this.hasItemInSlot(EquipmentSlot.HEAD)) {
         slotClicked = EquipmentSlot.HEAD;
      } else if (!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
         slotClicked = EquipmentSlot.OFFHAND;
      }

      return slotClicked;
   }

   private boolean isDisabled(final EquipmentSlot slot) {
      return (this.disabledSlots & 1 << slot.getFilterBit(0)) != 0 || slot.getType() == EquipmentSlot.Type.HAND && !this.showArms();
   }

   private boolean swapItem(final Player player, final EquipmentSlot slot, final ItemStack playerItemStack, final InteractionHand hand) {
      ItemStack itemStack = this.getItemBySlot(slot);
      if (!itemStack.isEmpty() && (this.disabledSlots & 1 << slot.getFilterBit(8)) != 0) {
         return false;
      } else if (itemStack.isEmpty() && (this.disabledSlots & 1 << slot.getFilterBit(16)) != 0) {
         return false;
      } else if (player.hasInfiniteMaterials() && itemStack.isEmpty() && !playerItemStack.isEmpty()) {
         this.setItemSlot(slot, playerItemStack.copyWithCount(1));
         return true;
      } else if (!playerItemStack.isEmpty() && playerItemStack.getCount() > 1) {
         if (!itemStack.isEmpty()) {
            return false;
         } else {
            this.setItemSlot(slot, playerItemStack.split(1));
            return true;
         }
      } else {
         this.setItemSlot(slot, playerItemStack);
         player.setItemInHand(hand, itemStack);
         return true;
      }
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (this.isRemoved()) {
         return false;
      } else if (!(Boolean)level.getGameRules().get(GameRules.MOB_GRIEFING) && source.getEntity() instanceof Mob) {
         return false;
      } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         this.kill(level);
         return false;
      } else if (!this.isInvulnerableTo(level, source) && !this.invisible && !this.isMarker()) {
         if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(level, source);
            this.kill(level);
            return false;
         } else if (source.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            if (this.isOnFire()) {
               this.causeDamage(level, source, 0.15F);
            } else {
               this.igniteForSeconds(5.0F);
            }

            return false;
         } else if (source.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
            this.causeDamage(level, source, 4.0F);
            return false;
         } else {
            boolean allowIncrementalBreaking = source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
            boolean shouldKill = source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
            if (!allowIncrementalBreaking && !shouldKill) {
               return false;
            } else {
               Entity var7 = source.getEntity();
               if (var7 instanceof Player) {
                  Player player = (Player)var7;
                  if (!player.getAbilities().mayBuild) {
                     return false;
                  }
               }

               if (source.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.kill(level);
                  return true;
               } else {
                  long time = level.getGameTime();
                  if (time - this.lastHit > 5L && !shouldKill) {
                     level.broadcastEntityEvent(this, (byte)32);
                     this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                     this.lastHit = time;
                  } else {
                     this.brokenByPlayer(level, source);
                     this.showBreakingParticles();
                     this.kill(level);
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public void handleEntityEvent(final byte id) {
      if (id == 32) {
         if (this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level().getGameTime();
         }
      } else {
         super.handleEntityEvent(id);
      }

   }

   public boolean shouldRenderAtSqrDistance(final double distance) {
      double size = this.getBoundingBox().getSize() * 4.0;
      if (Double.isNaN(size) || size == 0.0) {
         size = 4.0;
      }

      size *= 64.0;
      return distance < size * size;
   }

   private void showBreakingParticles() {
      if (this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05);
      }

   }

   private void causeDamage(final ServerLevel level, final DamageSource source, final float dmg) {
      float health = this.getHealth();
      health -= dmg;
      if (health <= 0.5F) {
         this.brokenByAnything(level, source);
         this.kill(level);
      } else {
         this.setHealth(health);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
      }

   }

   private void brokenByPlayer(final ServerLevel level, final DamageSource source) {
      ItemStack result = new ItemStack(Items.ARMOR_STAND);
      result.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      Block.popResource(this.level(), this.blockPosition(), result);
      this.brokenByAnything(level, source);
   }

   private void brokenByAnything(final ServerLevel level, final DamageSource source) {
      this.playBrokenSound();
      this.dropAllDeathLoot(level, source);

      for(EquipmentSlot slot : EquipmentSlot.VALUES) {
         ItemStack itemStack = this.equipment.set(slot, ItemStack.EMPTY);
         if (!itemStack.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), itemStack);
         }
      }

   }

   private void playBrokenSound() {
      this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected void tickHeadTurn(final float yBodyRotT) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.getYRot();
   }

   public void travel(final Vec3 input) {
      if (this.hasPhysics()) {
         super.travel(input);
      }
   }

   public void setYBodyRot(final float yBodyRot) {
      this.yBodyRotO = this.yRotO = yBodyRot;
      this.yHeadRotO = this.yHeadRot = yBodyRot;
   }

   public void setYHeadRot(final float yHeadRot) {
      this.yBodyRotO = this.yRotO = yHeadRot;
      this.yHeadRotO = this.yHeadRot = yHeadRot;
   }

   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   public void setInvisible(final boolean invisible) {
      this.invisible = invisible;
      super.setInvisible(invisible);
   }

   public boolean isBaby() {
      return this.isSmall();
   }

   public void kill(final ServerLevel level) {
      this.remove(Entity.RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public boolean ignoreExplosion(final Explosion explosion) {
      return explosion.shouldAffectBlocklikeEntities() ? this.isInvisible() : true;
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   public boolean isIgnoringBlockTriggers() {
      return this.isMarker();
   }

   private void setSmall(final boolean value) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 1, value));
   }

   public boolean isSmall() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   public void setShowArms(final boolean value) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 4, value));
   }

   public boolean showArms() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   public void setNoBasePlate(final boolean value) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 8, value));
   }

   public boolean showBasePlate() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 8) == 0;
   }

   private void setMarker(final boolean value) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 16, value));
   }

   public boolean isMarker() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
   }

   private byte setBit(byte data, final int bit, final boolean value) {
      if (value) {
         data = (byte)(data | bit);
      } else {
         data = (byte)(data & ~bit);
      }

      return data;
   }

   public void setHeadPose(final Rotations headPose) {
      this.entityData.set(DATA_HEAD_POSE, headPose);
   }

   public void setBodyPose(final Rotations bodyPose) {
      this.entityData.set(DATA_BODY_POSE, bodyPose);
   }

   public void setLeftArmPose(final Rotations leftArmPose) {
      this.entityData.set(DATA_LEFT_ARM_POSE, leftArmPose);
   }

   public void setRightArmPose(final Rotations rightArmPose) {
      this.entityData.set(DATA_RIGHT_ARM_POSE, rightArmPose);
   }

   public void setLeftLegPose(final Rotations leftLegPose) {
      this.entityData.set(DATA_LEFT_LEG_POSE, leftLegPose);
   }

   public void setRightLegPose(final Rotations rightLegPose) {
      this.entityData.set(DATA_RIGHT_LEG_POSE, rightLegPose);
   }

   public Rotations getHeadPose() {
      return (Rotations)this.entityData.get(DATA_HEAD_POSE);
   }

   public Rotations getBodyPose() {
      return (Rotations)this.entityData.get(DATA_BODY_POSE);
   }

   public Rotations getLeftArmPose() {
      return (Rotations)this.entityData.get(DATA_LEFT_ARM_POSE);
   }

   public Rotations getRightArmPose() {
      return (Rotations)this.entityData.get(DATA_RIGHT_ARM_POSE);
   }

   public Rotations getLeftLegPose() {
      return (Rotations)this.entityData.get(DATA_LEFT_LEG_POSE);
   }

   public Rotations getRightLegPose() {
      return (Rotations)this.entityData.get(DATA_RIGHT_LEG_POSE);
   }

   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   public boolean skipAttackInteraction(final Entity source) {
      boolean var10000;
      if (source instanceof Player playerSource) {
         if (!this.level().mayInteract(playerSource, this.blockPosition())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
   }

   protected @Nullable SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
   }

   public boolean isAffectedByPotions() {
      return false;
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (DATA_CLIENT_FLAGS.equals(accessor)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(accessor);
   }

   public boolean attackable() {
      return false;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.getDimensionsMarker(this.isMarker());
   }

   private EntityDimensions getDimensionsMarker(final boolean isMarker) {
      if (isMarker) {
         return MARKER_DIMENSIONS;
      } else {
         return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
      }
   }

   public Vec3 getLightProbePosition(final float partialTickTime) {
      if (this.isMarker()) {
         AABB box = this.getDimensionsMarker(false).makeBoundingBox(this.position());
         BlockPos probePos = this.blockPosition();
         int brightestLight = -2147483648;

         for(BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
            int blockBrightness = Math.max(this.level().getBrightness(LightLayer.BLOCK, pos), this.level().getBrightness(LightLayer.SKY, pos));
            if (blockBrightness == 15) {
               return Vec3.atCenterOf(pos);
            }

            if (blockBrightness > brightestLight) {
               brightestLight = blockBrightness;
               probePos = pos.immutable();
            }
         }

         return Vec3.atCenterOf(probePos);
      } else {
         return super.getLightProbePosition(partialTickTime);
      }
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.ARMOR_STAND);
   }

   public boolean canBeSeenByAnyone() {
      return !this.isInvisible() && !this.isMarker();
   }

   public void setArmorStandPose(final ArmorStandPose pose) {
      this.setHeadPose(pose.head());
      this.setBodyPose(pose.body());
      this.setLeftArmPose(pose.leftArm());
      this.setRightArmPose(pose.rightArm());
      this.setLeftLegPose(pose.leftLeg());
      this.setRightLegPose(pose.rightLeg());
   }

   public ArmorStandPose getArmorStandPose() {
      return new ArmorStandPose(this.getHeadPose(), this.getBodyPose(), this.getLeftArmPose(), this.getRightArmPose(), this.getLeftLegPose(), this.getRightLegPose());
   }

   static {
      BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F).withEyeHeight(0.9875F);
      DATA_CLIENT_FLAGS = SynchedEntityData.<Byte>defineId(ArmorStand.class, EntityDataSerializers.BYTE);
      DATA_HEAD_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_BODY_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_ARM_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_ARM_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_LEG_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_LEG_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      RIDABLE_MINECARTS = (entity) -> {
         boolean var10000;
         if (entity instanceof AbstractMinecart minecart) {
            if (minecart.isRideable()) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      };
   }

   public static record ArmorStandPose(Rotations head, Rotations body, Rotations leftArm, Rotations rightArm, Rotations leftLeg, Rotations rightLeg) {
      public static final ArmorStandPose DEFAULT;
      public static final Codec<ArmorStandPose> CODEC;

      public ArmorStandPose {
         super();
      }

      static {
         DEFAULT = new ArmorStandPose(ArmorStand.DEFAULT_HEAD_POSE, ArmorStand.DEFAULT_BODY_POSE, ArmorStand.DEFAULT_LEFT_ARM_POSE, ArmorStand.DEFAULT_RIGHT_ARM_POSE, ArmorStand.DEFAULT_LEFT_LEG_POSE, ArmorStand.DEFAULT_RIGHT_LEG_POSE);
         CODEC = RecordCodecBuilder.create((i) -> i.group(Rotations.CODEC.optionalFieldOf("Head", ArmorStand.DEFAULT_HEAD_POSE).forGetter(ArmorStandPose::head), Rotations.CODEC.optionalFieldOf("Body", ArmorStand.DEFAULT_BODY_POSE).forGetter(ArmorStandPose::body), Rotations.CODEC.optionalFieldOf("LeftArm", ArmorStand.DEFAULT_LEFT_ARM_POSE).forGetter(ArmorStandPose::leftArm), Rotations.CODEC.optionalFieldOf("RightArm", ArmorStand.DEFAULT_RIGHT_ARM_POSE).forGetter(ArmorStandPose::rightArm), Rotations.CODEC.optionalFieldOf("LeftLeg", ArmorStand.DEFAULT_LEFT_LEG_POSE).forGetter(ArmorStandPose::leftLeg), Rotations.CODEC.optionalFieldOf("RightLeg", ArmorStand.DEFAULT_RIGHT_LEG_POSE).forGetter(ArmorStandPose::rightLeg)).apply(i, ArmorStandPose::new));
      }
   }
}
