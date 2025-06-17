package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

   public ArmorStand(EntityType<? extends ArmorStand> var1, Level var2) {
      super(var1, var2);
      this.invisible = false;
      this.disabledSlots = 0;
   }

   public ArmorStand(Level var1, double var2, double var4, double var6) {
      this(EntityType.ARMOR_STAND, var1);
      this.setPos(var2, var4, var6);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createLivingAttributes().add(Attributes.STEP_HEIGHT, 0.0);
   }

   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   private boolean hasPhysics() {
      return !this.isMarker() && !this.isNoGravity();
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_CLIENT_FLAGS, (byte)0);
      var1.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      var1.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      var1.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      var1.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      var1.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      var1.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      return var1 != EquipmentSlot.BODY && var1 != EquipmentSlot.SADDLE && !this.isDisabled(var1);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Invisible", this.isInvisible());
      var1.putBoolean("Small", this.isSmall());
      var1.putBoolean("ShowArms", this.showArms());
      var1.putInt("DisabledSlots", this.disabledSlots);
      var1.putBoolean("NoBasePlate", !this.showBasePlate());
      if (this.isMarker()) {
         var1.putBoolean("Marker", this.isMarker());
      }

      var1.store("Pose", ArmorStand.ArmorStandPose.CODEC, this.getArmorStandPose());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setInvisible(var1.getBooleanOr("Invisible", false));
      this.setSmall(var1.getBooleanOr("Small", false));
      this.setShowArms(var1.getBooleanOr("ShowArms", false));
      this.disabledSlots = var1.getIntOr("DisabledSlots", 0);
      this.setNoBasePlate(var1.getBooleanOr("NoBasePlate", false));
      this.setMarker(var1.getBooleanOr("Marker", false));
      this.noPhysics = !this.hasPhysics();
      var1.read("Pose", ArmorStand.ArmorStandPose.CODEC).ifPresent(this::setArmorStandPose);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity var1) {
   }

   protected void pushEntities() {
      for(Entity var3 : this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS)) {
         if (this.distanceToSqr(var3) <= 0.2) {
            var3.push((Entity)this);
         }
      }

   }

   public InteractionResult interactAt(Player var1, Vec3 var2, InteractionHand var3) {
      ItemStack var4 = var1.getItemInHand(var3);
      if (!this.isMarker() && !var4.is(Items.NAME_TAG)) {
         if (var1.isSpectator()) {
            return InteractionResult.SUCCESS;
         } else if (var1.level().isClientSide) {
            return InteractionResult.SUCCESS_SERVER;
         } else {
            EquipmentSlot var5 = this.getEquipmentSlotForItem(var4);
            if (var4.isEmpty()) {
               EquipmentSlot var6 = this.getClickedSlot(var2);
               EquipmentSlot var7 = this.isDisabled(var6) ? var5 : var6;
               if (this.hasItemInSlot(var7) && this.swapItem(var1, var7, var4, var3)) {
                  return InteractionResult.SUCCESS_SERVER;
               }
            } else {
               if (this.isDisabled(var5)) {
                  return InteractionResult.FAIL;
               }

               if (var5.getType() == EquipmentSlot.Type.HAND && !this.showArms()) {
                  return InteractionResult.FAIL;
               }

               if (this.swapItem(var1, var5, var4, var3)) {
                  return InteractionResult.SUCCESS_SERVER;
               }
            }

            return InteractionResult.PASS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private EquipmentSlot getClickedSlot(Vec3 var1) {
      EquipmentSlot var2 = EquipmentSlot.MAINHAND;
      boolean var3 = this.isSmall();
      double var4 = var1.y / (double)(this.getScale() * this.getAgeScale());
      EquipmentSlot var6 = EquipmentSlot.FEET;
      if (var4 >= 0.1 && var4 < 0.1 + (var3 ? 0.8 : 0.45) && this.hasItemInSlot(var6)) {
         var2 = EquipmentSlot.FEET;
      } else if (var4 >= 0.9 + (var3 ? 0.3 : 0.0) && var4 < 0.9 + (var3 ? 1.0 : 0.7) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
         var2 = EquipmentSlot.CHEST;
      } else if (var4 >= 0.4 && var4 < 0.4 + (var3 ? 1.0 : 0.8) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
         var2 = EquipmentSlot.LEGS;
      } else if (var4 >= 1.6 && this.hasItemInSlot(EquipmentSlot.HEAD)) {
         var2 = EquipmentSlot.HEAD;
      } else if (!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
         var2 = EquipmentSlot.OFFHAND;
      }

      return var2;
   }

   private boolean isDisabled(EquipmentSlot var1) {
      return (this.disabledSlots & 1 << var1.getFilterBit(0)) != 0 || var1.getType() == EquipmentSlot.Type.HAND && !this.showArms();
   }

   private boolean swapItem(Player var1, EquipmentSlot var2, ItemStack var3, InteractionHand var4) {
      ItemStack var5 = this.getItemBySlot(var2);
      if (!var5.isEmpty() && (this.disabledSlots & 1 << var2.getFilterBit(8)) != 0) {
         return false;
      } else if (var5.isEmpty() && (this.disabledSlots & 1 << var2.getFilterBit(16)) != 0) {
         return false;
      } else if (var1.hasInfiniteMaterials() && var5.isEmpty() && !var3.isEmpty()) {
         this.setItemSlot(var2, var3.copyWithCount(1));
         return true;
      } else if (!var3.isEmpty() && var3.getCount() > 1) {
         if (!var5.isEmpty()) {
            return false;
         } else {
            this.setItemSlot(var2, var3.split(1));
            return true;
         }
      } else {
         this.setItemSlot(var2, var3);
         var1.setItemInHand(var4, var5);
         return true;
      }
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isRemoved()) {
         return false;
      } else if (!var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && var2.getEntity() instanceof Mob) {
         return false;
      } else if (var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         this.kill(var1);
         return false;
      } else if (!this.isInvulnerableTo(var1, var2) && !this.invisible && !this.isMarker()) {
         if (var2.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(var1, var2);
            this.kill(var1);
            return false;
         } else if (var2.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            if (this.isOnFire()) {
               this.causeDamage(var1, var2, 0.15F);
            } else {
               this.igniteForSeconds(5.0F);
            }

            return false;
         } else if (var2.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
            this.causeDamage(var1, var2, 4.0F);
            return false;
         } else {
            boolean var4 = var2.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
            boolean var5 = var2.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
            if (!var4 && !var5) {
               return false;
            } else {
               Entity var7 = var2.getEntity();
               if (var7 instanceof Player) {
                  Player var6 = (Player)var7;
                  if (!var6.getAbilities().mayBuild) {
                     return false;
                  }
               }

               if (var2.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.kill(var1);
                  return true;
               } else {
                  long var8 = var1.getGameTime();
                  if (var8 - this.lastHit > 5L && !var5) {
                     var1.broadcastEntityEvent(this, (byte)32);
                     this.gameEvent(GameEvent.ENTITY_DAMAGE, var2.getEntity());
                     this.lastHit = var8;
                  } else {
                     this.brokenByPlayer(var1, var2);
                     this.showBreakingParticles();
                     this.kill(var1);
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 32) {
         if (this.level().isClientSide) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level().getGameTime();
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0;
      if (Double.isNaN(var3) || var3 == 0.0) {
         var3 = 4.0;
      }

      var3 *= 64.0;
      return var1 < var3 * var3;
   }

   private void showBreakingParticles() {
      if (this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05);
      }

   }

   private void causeDamage(ServerLevel var1, DamageSource var2, float var3) {
      float var4 = this.getHealth();
      var4 -= var3;
      if (var4 <= 0.5F) {
         this.brokenByAnything(var1, var2);
         this.kill(var1);
      } else {
         this.setHealth(var4);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, var2.getEntity());
      }

   }

   private void brokenByPlayer(ServerLevel var1, DamageSource var2) {
      ItemStack var3 = new ItemStack(Items.ARMOR_STAND);
      var3.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      Block.popResource(this.level(), this.blockPosition(), var3);
      this.brokenByAnything(var1, var2);
   }

   private void brokenByAnything(ServerLevel var1, DamageSource var2) {
      this.playBrokenSound();
      this.dropAllDeathLoot(var1, var2);

      for(EquipmentSlot var4 : EquipmentSlot.VALUES) {
         ItemStack var5 = this.equipment.set(var4, ItemStack.EMPTY);
         if (!var5.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), var5);
         }
      }

   }

   private void playBrokenSound() {
      this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected void tickHeadTurn(float var1) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.getYRot();
   }

   public void travel(Vec3 var1) {
      if (this.hasPhysics()) {
         super.travel(var1);
      }
   }

   public void setYBodyRot(float var1) {
      this.yBodyRotO = this.yRotO = var1;
      this.yHeadRotO = this.yHeadRot = var1;
   }

   public void setYHeadRot(float var1) {
      this.yBodyRotO = this.yRotO = var1;
      this.yHeadRotO = this.yHeadRot = var1;
   }

   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   public void setInvisible(boolean var1) {
      this.invisible = var1;
      super.setInvisible(var1);
   }

   public boolean isBaby() {
      return this.isSmall();
   }

   public void kill(ServerLevel var1) {
      this.remove(Entity.RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public boolean ignoreExplosion(Explosion var1) {
      return var1.shouldAffectBlocklikeEntities() ? this.isInvisible() : true;
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   public boolean isIgnoringBlockTriggers() {
      return this.isMarker();
   }

   private void setSmall(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 1, var1));
   }

   public boolean isSmall() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   public void setShowArms(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 4, var1));
   }

   public boolean showArms() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   public void setNoBasePlate(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 8, var1));
   }

   public boolean showBasePlate() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 8) == 0;
   }

   private void setMarker(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 16, var1));
   }

   public boolean isMarker() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
   }

   private byte setBit(byte var1, int var2, boolean var3) {
      if (var3) {
         var1 = (byte)(var1 | var2);
      } else {
         var1 = (byte)(var1 & ~var2);
      }

      return var1;
   }

   public void setHeadPose(Rotations var1) {
      this.entityData.set(DATA_HEAD_POSE, var1);
   }

   public void setBodyPose(Rotations var1) {
      this.entityData.set(DATA_BODY_POSE, var1);
   }

   public void setLeftArmPose(Rotations var1) {
      this.entityData.set(DATA_LEFT_ARM_POSE, var1);
   }

   public void setRightArmPose(Rotations var1) {
      this.entityData.set(DATA_RIGHT_ARM_POSE, var1);
   }

   public void setLeftLegPose(Rotations var1) {
      this.entityData.set(DATA_LEFT_LEG_POSE, var1);
   }

   public void setRightLegPose(Rotations var1) {
      this.entityData.set(DATA_RIGHT_LEG_POSE, var1);
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

   public boolean skipAttackInteraction(Entity var1) {
      boolean var10000;
      if (var1 instanceof Player var2) {
         if (!this.level().mayInteract(var2, this.blockPosition())) {
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

   @Nullable
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
   }

   public boolean isAffectedByPotions() {
      return false;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_CLIENT_FLAGS.equals(var1)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(var1);
   }

   public boolean attackable() {
      return false;
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.getDimensionsMarker(this.isMarker());
   }

   private EntityDimensions getDimensionsMarker(boolean var1) {
      if (var1) {
         return MARKER_DIMENSIONS;
      } else {
         return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
      }
   }

   public Vec3 getLightProbePosition(float var1) {
      if (this.isMarker()) {
         AABB var2 = this.getDimensionsMarker(false).makeBoundingBox(this.position());
         BlockPos var3 = this.blockPosition();
         int var4 = -2147483648;

         for(BlockPos var6 : BlockPos.betweenClosed(BlockPos.containing(var2.minX, var2.minY, var2.minZ), BlockPos.containing(var2.maxX, var2.maxY, var2.maxZ))) {
            int var7 = Math.max(this.level().getBrightness(LightLayer.BLOCK, var6), this.level().getBrightness(LightLayer.SKY, var6));
            if (var7 == 15) {
               return Vec3.atCenterOf(var6);
            }

            if (var7 > var4) {
               var4 = var7;
               var3 = var6.immutable();
            }
         }

         return Vec3.atCenterOf(var3);
      } else {
         return super.getLightProbePosition(var1);
      }
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.ARMOR_STAND);
   }

   public boolean canBeSeenByAnyone() {
      return !this.isInvisible() && !this.isMarker();
   }

   public void setArmorStandPose(ArmorStandPose var1) {
      this.setHeadPose(var1.head());
      this.setBodyPose(var1.body());
      this.setLeftArmPose(var1.leftArm());
      this.setRightArmPose(var1.rightArm());
      this.setLeftLegPose(var1.leftLeg());
      this.setRightLegPose(var1.rightLeg());
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
      RIDABLE_MINECARTS = (var0) -> {
         boolean var10000;
         if (var0 instanceof AbstractMinecart var1) {
            if (var1.isRideable()) {
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

      public ArmorStandPose(Rotations var1, Rotations var2, Rotations var3, Rotations var4, Rotations var5, Rotations var6) {
         super();
         this.head = var1;
         this.body = var2;
         this.leftArm = var3;
         this.rightArm = var4;
         this.leftLeg = var5;
         this.rightLeg = var6;
      }

      static {
         DEFAULT = new ArmorStandPose(ArmorStand.DEFAULT_HEAD_POSE, ArmorStand.DEFAULT_BODY_POSE, ArmorStand.DEFAULT_LEFT_ARM_POSE, ArmorStand.DEFAULT_RIGHT_ARM_POSE, ArmorStand.DEFAULT_LEFT_LEG_POSE, ArmorStand.DEFAULT_RIGHT_LEG_POSE);
         CODEC = RecordCodecBuilder.create((var0) -> var0.group(Rotations.CODEC.optionalFieldOf("Head", ArmorStand.DEFAULT_HEAD_POSE).forGetter(ArmorStandPose::head), Rotations.CODEC.optionalFieldOf("Body", ArmorStand.DEFAULT_BODY_POSE).forGetter(ArmorStandPose::body), Rotations.CODEC.optionalFieldOf("LeftArm", ArmorStand.DEFAULT_LEFT_ARM_POSE).forGetter(ArmorStandPose::leftArm), Rotations.CODEC.optionalFieldOf("RightArm", ArmorStand.DEFAULT_RIGHT_ARM_POSE).forGetter(ArmorStandPose::rightArm), Rotations.CODEC.optionalFieldOf("LeftLeg", ArmorStand.DEFAULT_LEFT_LEG_POSE).forGetter(ArmorStandPose::leftLeg), Rotations.CODEC.optionalFieldOf("RightLeg", ArmorStand.DEFAULT_RIGHT_LEG_POSE).forGetter(ArmorStandPose::rightLeg)).apply(var0, ArmorStandPose::new));
      }
   }
}
