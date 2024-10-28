package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
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
import net.minecraft.world.level.GameRules;
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
   private final NonNullList<ItemStack> handItems;
   private final NonNullList<ItemStack> armorItems;
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;
   private Rotations headPose;
   private Rotations bodyPose;
   private Rotations leftArmPose;
   private Rotations rightArmPose;
   private Rotations leftLegPose;
   private Rotations rightLegPose;

   public ArmorStand(EntityType<? extends ArmorStand> var1, Level var2) {
      super(var1, var2);
      this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
      this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
      this.headPose = DEFAULT_HEAD_POSE;
      this.bodyPose = DEFAULT_BODY_POSE;
      this.leftArmPose = DEFAULT_LEFT_ARM_POSE;
      this.rightArmPose = DEFAULT_RIGHT_ARM_POSE;
      this.leftLegPose = DEFAULT_LEFT_LEG_POSE;
      this.rightLegPose = DEFAULT_RIGHT_LEG_POSE;
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

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlot var1) {
      switch (var1.getType()) {
         case HAND -> {
            return (ItemStack)this.handItems.get(var1.getIndex());
         }
         case HUMANOID_ARMOR -> {
            return (ItemStack)this.armorItems.get(var1.getIndex());
         }
         default -> {
            return ItemStack.EMPTY;
         }
      }
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      return var1 != EquipmentSlot.BODY && !this.isDisabled(var1);
   }

   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      this.verifyEquippedItem(var2);
      switch (var1.getType()) {
         case HAND -> this.onEquipItem(var1, (ItemStack)this.handItems.set(var1.getIndex(), var2), var2);
         case HUMANOID_ARMOR -> this.onEquipItem(var1, (ItemStack)this.armorItems.set(var1.getIndex(), var2), var2);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ListTag var2 = new ListTag();
      Iterator var3 = this.armorItems.iterator();

      while(var3.hasNext()) {
         ItemStack var4 = (ItemStack)var3.next();
         var2.add(var4.saveOptional(this.registryAccess()));
      }

      var1.put("ArmorItems", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = this.handItems.iterator();

      while(var7.hasNext()) {
         ItemStack var5 = (ItemStack)var7.next();
         var6.add(var5.saveOptional(this.registryAccess()));
      }

      var1.put("HandItems", var6);
      var1.putBoolean("Invisible", this.isInvisible());
      var1.putBoolean("Small", this.isSmall());
      var1.putBoolean("ShowArms", this.showArms());
      var1.putInt("DisabledSlots", this.disabledSlots);
      var1.putBoolean("NoBasePlate", !this.showBasePlate());
      if (this.isMarker()) {
         var1.putBoolean("Marker", this.isMarker());
      }

      var1.put("Pose", this.writePose());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ListTag var2;
      int var3;
      CompoundTag var4;
      if (var1.contains("ArmorItems", 9)) {
         var2 = var1.getList("ArmorItems", 10);

         for(var3 = 0; var3 < this.armorItems.size(); ++var3) {
            var4 = var2.getCompound(var3);
            this.armorItems.set(var3, ItemStack.parseOptional(this.registryAccess(), var4));
         }
      }

      if (var1.contains("HandItems", 9)) {
         var2 = var1.getList("HandItems", 10);

         for(var3 = 0; var3 < this.handItems.size(); ++var3) {
            var4 = var2.getCompound(var3);
            this.handItems.set(var3, ItemStack.parseOptional(this.registryAccess(), var4));
         }
      }

      this.setInvisible(var1.getBoolean("Invisible"));
      this.setSmall(var1.getBoolean("Small"));
      this.setShowArms(var1.getBoolean("ShowArms"));
      this.disabledSlots = var1.getInt("DisabledSlots");
      this.setNoBasePlate(var1.getBoolean("NoBasePlate"));
      this.setMarker(var1.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundTag var5 = var1.getCompound("Pose");
      this.readPose(var5);
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

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity var1) {
   }

   protected void pushEntities() {
      List var1 = this.level().getEntities((Entity)this, this.getBoundingBox(), RIDABLE_MINECARTS);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
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

      int var3;
      ItemStack var4;
      for(var3 = 0; var3 < this.handItems.size(); ++var3) {
         var4 = (ItemStack)this.handItems.get(var3);
         if (!var4.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), var4);
            this.handItems.set(var3, ItemStack.EMPTY);
         }
      }

      for(var3 = 0; var3 < this.armorItems.size(); ++var3) {
         var4 = (ItemStack)this.armorItems.get(var3);
         if (!var4.isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), var4);
            this.armorItems.set(var3, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected float tickHeadTurn(float var1, float var2) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.getYRot();
      return 0.0F;
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

   public void tick() {
      super.tick();
      Rotations var1 = (Rotations)this.entityData.get(DATA_HEAD_POSE);
      if (!this.headPose.equals(var1)) {
         this.setHeadPose(var1);
      }

      Rotations var2 = (Rotations)this.entityData.get(DATA_BODY_POSE);
      if (!this.bodyPose.equals(var2)) {
         this.setBodyPose(var2);
      }

      Rotations var3 = (Rotations)this.entityData.get(DATA_LEFT_ARM_POSE);
      if (!this.leftArmPose.equals(var3)) {
         this.setLeftArmPose(var3);
      }

      Rotations var4 = (Rotations)this.entityData.get(DATA_RIGHT_ARM_POSE);
      if (!this.rightArmPose.equals(var4)) {
         this.setRightArmPose(var4);
      }

      Rotations var5 = (Rotations)this.entityData.get(DATA_LEFT_LEG_POSE);
      if (!this.leftLegPose.equals(var5)) {
         this.setLeftLegPose(var5);
      }

      Rotations var6 = (Rotations)this.entityData.get(DATA_RIGHT_LEG_POSE);
      if (!this.rightLegPose.equals(var6)) {
         this.setRightLegPose(var6);
      }

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

   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   public boolean skipAttackInteraction(Entity var1) {
      return var1 instanceof Player && !this.level().mayInteract((Player)var1, this.blockPosition());
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
         Iterator var5 = BlockPos.betweenClosed(BlockPos.containing(var2.minX, var2.minY, var2.minZ), BlockPos.containing(var2.maxX, var2.maxY, var2.maxZ)).iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
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

   static {
      BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F).withEyeHeight(0.9875F);
      DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
      DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
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
}
