package net.minecraft.world.entity.decoration;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStand extends LivingEntity {
   public static final int WOBBLE_TIME = 5;
   private static final boolean ENABLE_ARMS = true;
   private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   private static final EntityDimensions MARKER_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F).withEyeHeight(0.9875F);
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
   public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
   public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   private static final Predicate<Entity> RIDABLE_MINECARTS = var0 -> var0 instanceof AbstractMinecart
         && ((AbstractMinecart)var0).getMinecartType() == AbstractMinecart.Type.RIDEABLE;
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;
   private Rotations headPose = DEFAULT_HEAD_POSE;
   private Rotations bodyPose = DEFAULT_BODY_POSE;
   private Rotations leftArmPose = DEFAULT_LEFT_ARM_POSE;
   private Rotations rightArmPose = DEFAULT_RIGHT_ARM_POSE;
   private Rotations leftLegPose = DEFAULT_LEFT_LEG_POSE;
   private Rotations rightLegPose = DEFAULT_RIGHT_LEG_POSE;

   public ArmorStand(EntityType<? extends ArmorStand> var1, Level var2) {
      super(var1, var2);
   }

   public ArmorStand(Level var1, double var2, double var4, double var6) {
      this(EntityType.ARMOR_STAND, var1);
      this.setPos(var2, var4, var6);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createLivingAttributes().add(Attributes.STEP_HEIGHT, 0.0);
   }

   @Override
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

   @Override
   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   @Override
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

   @Override
   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   @Override
   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   @Override
   public ItemStack getItemBySlot(EquipmentSlot var1) {
      switch (var1.getType()) {
         case HAND:
            return this.handItems.get(var1.getIndex());
         case ARMOR:
            return this.armorItems.get(var1.getIndex());
         default:
            return ItemStack.EMPTY;
      }
   }

   @Override
   public boolean canUseSlot(EquipmentSlot var1) {
      return var1 != EquipmentSlot.BODY;
   }

   @Override
   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      this.verifyEquippedItem(var2);
      switch (var1.getType()) {
         case HAND:
            this.onEquipItem(var1, this.handItems.set(var1.getIndex(), var2), var2);
            break;
         case ARMOR:
            this.onEquipItem(var1, this.armorItems.set(var1.getIndex(), var2), var2);
      }
   }

   @Override
   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      return this.getItemBySlot(var2).isEmpty() && !this.isDisabled(var2);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ListTag var2 = new ListTag();

      for (ItemStack var4 : this.armorItems) {
         var2.add(var4.saveOptional(this.registryAccess()));
      }

      var1.put("ArmorItems", var2);
      ListTag var6 = new ListTag();

      for (ItemStack var5 : this.handItems) {
         var6.add(var5.saveOptional(this.registryAccess()));
      }

      var1.put("HandItems", var6);
      var1.putBoolean("Invisible", this.isInvisible());
      var1.putBoolean("Small", this.isSmall());
      var1.putBoolean("ShowArms", this.isShowArms());
      var1.putInt("DisabledSlots", this.disabledSlots);
      var1.putBoolean("NoBasePlate", this.isNoBasePlate());
      if (this.isMarker()) {
         var1.putBoolean("Marker", this.isMarker());
      }

      var1.put("Pose", this.writePose());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("ArmorItems", 9)) {
         ListTag var2 = var1.getList("ArmorItems", 10);

         for (int var3 = 0; var3 < this.armorItems.size(); var3++) {
            CompoundTag var4 = var2.getCompound(var3);
            this.armorItems.set(var3, ItemStack.parseOptional(this.registryAccess(), var4));
         }
      }

      if (var1.contains("HandItems", 9)) {
         ListTag var5 = var1.getList("HandItems", 10);

         for (int var7 = 0; var7 < this.handItems.size(); var7++) {
            CompoundTag var8 = var5.getCompound(var7);
            this.handItems.set(var7, ItemStack.parseOptional(this.registryAccess(), var8));
         }
      }

      this.setInvisible(var1.getBoolean("Invisible"));
      this.setSmall(var1.getBoolean("Small"));
      this.setShowArms(var1.getBoolean("ShowArms"));
      this.disabledSlots = var1.getInt("DisabledSlots");
      this.setNoBasePlate(var1.getBoolean("NoBasePlate"));
      this.setMarker(var1.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundTag var6 = var1.getCompound("Pose");
      this.readPose(var6);
   }

   private void readPose(CompoundTag var1) {
      ListTag var2 = var1.getList("Head", 5);
      this.setHeadPose(var2.isEmpty() ? DEFAULT_HEAD_POSE : new Rotations(var2));
      ListTag var3 = var1.getList("Body", 5);
      this.setBodyPose(var3.isEmpty() ? DEFAULT_BODY_POSE : new Rotations(var3));
      ListTag var4 = var1.getList("LeftArm", 5);
      this.setLeftArmPose(var4.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new Rotations(var4));
      ListTag var5 = var1.getList("RightArm", 5);
      this.setRightArmPose(var5.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new Rotations(var5));
      ListTag var6 = var1.getList("LeftLeg", 5);
      this.setLeftLegPose(var6.isEmpty() ? DEFAULT_LEFT_LEG_POSE : new Rotations(var6));
      ListTag var7 = var1.getList("RightLeg", 5);
      this.setRightLegPose(var7.isEmpty() ? DEFAULT_RIGHT_LEG_POSE : new Rotations(var7));
   }

   private CompoundTag writePose() {
      CompoundTag var1 = new CompoundTag();
      if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
         var1.put("Head", this.headPose.save());
      }

      if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
         var1.put("Body", this.bodyPose.save());
      }

      if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
         var1.put("LeftArm", this.leftArmPose.save());
      }

      if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
         var1.put("RightArm", this.rightArmPose.save());
      }

      if (!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
         var1.put("LeftLeg", this.leftLegPose.save());
      }

      if (!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
         var1.put("RightLeg", this.rightLegPose.save());
      }

      return var1;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   protected void doPush(Entity var1) {
   }

   @Override
   protected void pushEntities() {
      for (Entity var3 : this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS)) {
         if (this.distanceToSqr(var3) <= 0.2) {
            var3.push(this);
         }
      }
   }

   @Override
   public InteractionResult interactAt(Player var1, Vec3 var2, InteractionHand var3) {
      ItemStack var4 = var1.getItemInHand(var3);
      if (this.isMarker() || var4.is(Items.NAME_TAG)) {
         return InteractionResult.PASS;
      } else if (var1.isSpectator()) {
         return InteractionResult.SUCCESS;
      } else if (var1.level().isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
         if (var4.isEmpty()) {
            EquipmentSlot var6 = this.getClickedSlot(var2);
            EquipmentSlot var7 = this.isDisabled(var6) ? var5 : var6;
            if (this.hasItemInSlot(var7) && this.swapItem(var1, var7, var4, var3)) {
               return InteractionResult.SUCCESS;
            }
         } else {
            if (this.isDisabled(var5)) {
               return InteractionResult.FAIL;
            }

            if (var5.getType() == EquipmentSlot.Type.HAND && !this.isShowArms()) {
               return InteractionResult.FAIL;
            }

            if (this.swapItem(var1, var5, var4, var3)) {
               return InteractionResult.SUCCESS;
            }
         }

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
      return (this.disabledSlots & 1 << var1.getFilterFlag()) != 0 || var1.getType() == EquipmentSlot.Type.HAND && !this.isShowArms();
   }

   private boolean swapItem(Player var1, EquipmentSlot var2, ItemStack var3, InteractionHand var4) {
      ItemStack var5 = this.getItemBySlot(var2);
      if (!var5.isEmpty() && (this.disabledSlots & 1 << var2.getFilterFlag() + 8) != 0) {
         return false;
      } else if (var5.isEmpty() && (this.disabledSlots & 1 << var2.getFilterFlag() + 16) != 0) {
         return false;
      } else if (var1.hasInfiniteMaterials() && var5.isEmpty() && !var3.isEmpty()) {
         this.setItemSlot(var2, var3.copyWithCount(1));
         return true;
      } else if (var3.isEmpty() || var3.getCount() <= 1) {
         this.setItemSlot(var2, var3);
         var1.setItemInHand(var4, var5);
         return true;
      } else if (!var5.isEmpty()) {
         return false;
      } else {
         this.setItemSlot(var2, var3.split(1));
         return true;
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.level().isClientSide || this.isRemoved()) {
         return false;
      } else if (var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         this.kill();
         return false;
      } else if (this.isInvulnerableTo(var1) || this.invisible || this.isMarker()) {
         return false;
      } else if (var1.is(DamageTypeTags.IS_EXPLOSION)) {
         this.brokenByAnything(var1);
         this.kill();
         return false;
      } else if (var1.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
         if (this.isOnFire()) {
            this.causeDamage(var1, 0.15F);
         } else {
            this.igniteForSeconds(5.0F);
         }

         return false;
      } else if (var1.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
         this.causeDamage(var1, 4.0F);
         return false;
      } else {
         boolean var3 = var1.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
         boolean var4 = var1.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
         if (!var3 && !var4) {
            return false;
         } else {
            if (var1.getEntity() instanceof Player var5 && !var5.getAbilities().mayBuild) {
               return false;
            }

            if (var1.isCreativePlayer()) {
               this.playBrokenSound();
               this.showBreakingParticles();
               this.kill();
               return true;
            } else {
               long var7 = this.level().getGameTime();
               if (var7 - this.lastHit > 5L && !var4) {
                  this.level().broadcastEntityEvent(this, (byte)32);
                  this.gameEvent(GameEvent.ENTITY_DAMAGE, var1.getEntity());
                  this.lastHit = var7;
               } else {
                  this.brokenByPlayer(var1);
                  this.showBreakingParticles();
                  this.kill();
               }

               return true;
            }
         }
      }
   }

   @Override
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

   @Override
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
         ((ServerLevel)this.level())
            .sendParticles(
               new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
               this.getX(),
               this.getY(0.6666666666666666),
               this.getZ(),
               10,
               (double)(this.getBbWidth() / 4.0F),
               (double)(this.getBbHeight() / 4.0F),
               (double)(this.getBbWidth() / 4.0F),
               0.05
            );
      }
   }

   private void causeDamage(DamageSource var1, float var2) {
      float var3 = this.getHealth();
      var3 -= var2;
      if (var3 <= 0.5F) {
         this.brokenByAnything(var1);
         this.kill();
      } else {
         this.setHealth(var3);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, var1.getEntity());
      }
   }

   private void brokenByPlayer(DamageSource var1) {
      ItemStack var2 = new ItemStack(Items.ARMOR_STAND);
      var2.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      Block.popResource(this.level(), this.blockPosition(), var2);
      this.brokenByAnything(var1);
   }

   private void brokenByAnything(DamageSource var1) {
      this.playBrokenSound();
      this.dropAllDeathLoot(var1);

      for (int var2 = 0; var2 < this.handItems.size(); var2++) {
         ItemStack var3 = this.handItems.get(var2);
         if (!var3.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), var3);
            this.handItems.set(var2, ItemStack.EMPTY);
         }
      }

      for (int var4 = 0; var4 < this.armorItems.size(); var4++) {
         ItemStack var5 = this.armorItems.get(var4);
         if (!var5.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), var5);
            this.armorItems.set(var4, ItemStack.EMPTY);
         }
      }
   }

   private void playBrokenSound() {
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   @Override
   protected float tickHeadTurn(float var1, float var2) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.getYRot();
      return 0.0F;
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.hasPhysics()) {
         super.travel(var1);
      }
   }

   @Override
   public void setYBodyRot(float var1) {
      this.yBodyRotO = this.yRotO = var1;
      this.yHeadRotO = this.yHeadRot = var1;
   }

   @Override
   public void setYHeadRot(float var1) {
      this.yBodyRotO = this.yRotO = var1;
      this.yHeadRotO = this.yHeadRot = var1;
   }

   @Override
   public void tick() {
      super.tick();
      Rotations var1 = this.entityData.get(DATA_HEAD_POSE);
      if (!this.headPose.equals(var1)) {
         this.setHeadPose(var1);
      }

      Rotations var2 = this.entityData.get(DATA_BODY_POSE);
      if (!this.bodyPose.equals(var2)) {
         this.setBodyPose(var2);
      }

      Rotations var3 = this.entityData.get(DATA_LEFT_ARM_POSE);
      if (!this.leftArmPose.equals(var3)) {
         this.setLeftArmPose(var3);
      }

      Rotations var4 = this.entityData.get(DATA_RIGHT_ARM_POSE);
      if (!this.rightArmPose.equals(var4)) {
         this.setRightArmPose(var4);
      }

      Rotations var5 = this.entityData.get(DATA_LEFT_LEG_POSE);
      if (!this.leftLegPose.equals(var5)) {
         this.setLeftLegPose(var5);
      }

      Rotations var6 = this.entityData.get(DATA_RIGHT_LEG_POSE);
      if (!this.rightLegPose.equals(var6)) {
         this.setRightLegPose(var6);
      }
   }

   @Override
   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   @Override
   public void setInvisible(boolean var1) {
      this.invisible = var1;
      super.setInvisible(var1);
   }

   @Override
   public boolean isBaby() {
      return this.isSmall();
   }

   @Override
   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   @Override
   public boolean ignoreExplosion(Explosion var1) {
      return this.isInvisible();
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return this.isMarker();
   }

   private void setSmall(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, var1));
   }

   public boolean isSmall() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   public void setShowArms(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, var1));
   }

   public boolean isShowArms() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   public void setNoBasePlate(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, var1));
   }

   public boolean isNoBasePlate() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
   }

   private void setMarker(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, var1));
   }

   public boolean isMarker() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
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
      this.headPose = var1;
      this.entityData.set(DATA_HEAD_POSE, var1);
   }

   public void setBodyPose(Rotations var1) {
      this.bodyPose = var1;
      this.entityData.set(DATA_BODY_POSE, var1);
   }

   public void setLeftArmPose(Rotations var1) {
      this.leftArmPose = var1;
      this.entityData.set(DATA_LEFT_ARM_POSE, var1);
   }

   public void setRightArmPose(Rotations var1) {
      this.rightArmPose = var1;
      this.entityData.set(DATA_RIGHT_ARM_POSE, var1);
   }

   public void setLeftLegPose(Rotations var1) {
      this.leftLegPose = var1;
      this.entityData.set(DATA_LEFT_LEG_POSE, var1);
   }

   public void setRightLegPose(Rotations var1) {
      this.rightLegPose = var1;
      this.entityData.set(DATA_RIGHT_LEG_POSE, var1);
   }

   public Rotations getHeadPose() {
      return this.headPose;
   }

   public Rotations getBodyPose() {
      return this.bodyPose;
   }

   public Rotations getLeftArmPose() {
      return this.leftArmPose;
   }

   public Rotations getRightArmPose() {
      return this.rightArmPose;
   }

   public Rotations getLeftLegPose() {
      return this.leftLegPose;
   }

   public Rotations getRightLegPose() {
      return this.rightLegPose;
   }

   @Override
   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   @Override
   public boolean skipAttackInteraction(Entity var1) {
      return var1 instanceof Player && !this.level().mayInteract((Player)var1, this.blockPosition());
   }

   @Override
   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   @Override
   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
   }

   @Nullable
   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   @Override
   public void thunderHit(ServerLevel var1, LightningBolt var2) {
   }

   @Override
   public boolean isAffectedByPotions() {
      return false;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_CLIENT_FLAGS.equals(var1)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public boolean attackable() {
      return false;
   }

   @Override
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

   @Override
   public Vec3 getLightProbePosition(float var1) {
      if (this.isMarker()) {
         AABB var2 = this.getDimensionsMarker(false).makeBoundingBox(this.position());
         BlockPos var3 = this.blockPosition();
         int var4 = -2147483648;

         for (BlockPos var6 : BlockPos.betweenClosed(BlockPos.containing(var2.minX, var2.minY, var2.minZ), BlockPos.containing(var2.maxX, var2.maxY, var2.maxZ))) {
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

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.ARMOR_STAND);
   }

   @Override
   public boolean canBeSeenByAnyone() {
      return !this.isInvisible() && !this.isMarker();
   }
}
