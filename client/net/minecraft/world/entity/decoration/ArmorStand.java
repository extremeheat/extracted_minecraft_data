package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStand extends LivingEntity {
   private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   private static final EntityDimensions MARKER_DIMENSIONS = new EntityDimensions(0.0F, 0.0F, true);
   private static final EntityDimensions BABY_DIMENSIONS;
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
      this.maxUpStep = 0.0F;
   }

   public ArmorStand(Level var1, double var2, double var4, double var6) {
      this(EntityType.ARMOR_STAND, var1);
      this.setPos(var2, var4, var6);
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

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
      this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
   }

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlot var1) {
      switch(var1.getType()) {
      case HAND:
         return (ItemStack)this.handItems.get(var1.getIndex());
      case ARMOR:
         return (ItemStack)this.armorItems.get(var1.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      switch(var1.getType()) {
      case HAND:
         this.playEquipSound(var2);
         this.handItems.set(var1.getIndex(), var2);
         break;
      case ARMOR:
         this.playEquipSound(var2);
         this.armorItems.set(var1.getIndex(), var2);
      }

   }

   public boolean setSlot(int var1, ItemStack var2) {
      EquipmentSlot var3;
      if (var1 == 98) {
         var3 = EquipmentSlot.MAINHAND;
      } else if (var1 == 99) {
         var3 = EquipmentSlot.OFFHAND;
      } else if (var1 == 100 + EquipmentSlot.HEAD.getIndex()) {
         var3 = EquipmentSlot.HEAD;
      } else if (var1 == 100 + EquipmentSlot.CHEST.getIndex()) {
         var3 = EquipmentSlot.CHEST;
      } else if (var1 == 100 + EquipmentSlot.LEGS.getIndex()) {
         var3 = EquipmentSlot.LEGS;
      } else {
         if (var1 != 100 + EquipmentSlot.FEET.getIndex()) {
            return false;
         }

         var3 = EquipmentSlot.FEET;
      }

      if (!var2.isEmpty() && !Mob.isValidSlotForItem(var3, var2) && var3 != EquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemSlot(var3, var2);
         return true;
      }
   }

   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      return this.getItemBySlot(var2).isEmpty() && !this.isDisabled(var2);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ListTag var2 = new ListTag();

      CompoundTag var5;
      for(Iterator var3 = this.armorItems.iterator(); var3.hasNext(); var2.add(var5)) {
         ItemStack var4 = (ItemStack)var3.next();
         var5 = new CompoundTag();
         if (!var4.isEmpty()) {
            var4.save(var5);
         }
      }

      var1.put("ArmorItems", var2);
      ListTag var7 = new ListTag();

      CompoundTag var6;
      for(Iterator var8 = this.handItems.iterator(); var8.hasNext(); var7.add(var6)) {
         ItemStack var9 = (ItemStack)var8.next();
         var6 = new CompoundTag();
         if (!var9.isEmpty()) {
            var9.save(var6);
         }
      }

      var1.put("HandItems", var7);
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

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ListTag var2;
      int var3;
      if (var1.contains("ArmorItems", 9)) {
         var2 = var1.getList("ArmorItems", 10);

         for(var3 = 0; var3 < this.armorItems.size(); ++var3) {
            this.armorItems.set(var3, ItemStack.of(var2.getCompound(var3)));
         }
      }

      if (var1.contains("HandItems", 9)) {
         var2 = var1.getList("HandItems", 10);

         for(var3 = 0; var3 < this.handItems.size(); ++var3) {
            this.handItems.set(var3, ItemStack.of(var2.getCompound(var3)));
         }
      }

      this.setInvisible(var1.getBoolean("Invisible"));
      this.setSmall(var1.getBoolean("Small"));
      this.setShowArms(var1.getBoolean("ShowArms"));
      this.disabledSlots = var1.getInt("DisabledSlots");
      this.setNoBasePlate(var1.getBoolean("NoBasePlate"));
      this.setMarker(var1.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundTag var4 = var1.getCompound("Pose");
      this.readPose(var4);
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
      List var1 = this.level.getEntities((Entity)this, this.getBoundingBox(), RIDABLE_MINECARTS);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (this.distanceToSqr(var3) <= 0.2D) {
            var3.push(this);
         }
      }

   }

   public InteractionResult interactAt(Player var1, Vec3 var2, InteractionHand var3) {
      ItemStack var4 = var1.getItemInHand(var3);
      if (!this.isMarker() && !var4.is(Items.NAME_TAG)) {
         if (var1.isSpectator()) {
            return InteractionResult.SUCCESS;
         } else if (var1.level.isClientSide) {
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
      } else {
         return InteractionResult.PASS;
      }
   }

   private EquipmentSlot getClickedSlot(Vec3 var1) {
      EquipmentSlot var2 = EquipmentSlot.MAINHAND;
      boolean var3 = this.isSmall();
      double var4 = var3 ? var1.y * 2.0D : var1.y;
      EquipmentSlot var6 = EquipmentSlot.FEET;
      if (var4 >= 0.1D && var4 < 0.1D + (var3 ? 0.8D : 0.45D) && this.hasItemInSlot(var6)) {
         var2 = EquipmentSlot.FEET;
      } else if (var4 >= 0.9D + (var3 ? 0.3D : 0.0D) && var4 < 0.9D + (var3 ? 1.0D : 0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
         var2 = EquipmentSlot.CHEST;
      } else if (var4 >= 0.4D && var4 < 0.4D + (var3 ? 1.0D : 0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
         var2 = EquipmentSlot.LEGS;
      } else if (var4 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
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
      } else {
         ItemStack var6;
         if (var1.getAbilities().instabuild && var5.isEmpty() && !var3.isEmpty()) {
            var6 = var3.copy();
            var6.setCount(1);
            this.setItemSlot(var2, var6);
            return true;
         } else if (!var3.isEmpty() && var3.getCount() > 1) {
            if (!var5.isEmpty()) {
               return false;
            } else {
               var6 = var3.copy();
               var6.setCount(1);
               this.setItemSlot(var2, var6);
               var3.shrink(1);
               return true;
            }
         } else {
            this.setItemSlot(var2, var3);
            var1.setItemInHand(var4, var5);
            return true;
         }
      }
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (!this.level.isClientSide && !this.isRemoved()) {
         if (DamageSource.OUT_OF_WORLD.equals(var1)) {
            this.kill();
            return false;
         } else if (!this.isInvulnerableTo(var1) && !this.invisible && !this.isMarker()) {
            if (var1.isExplosion()) {
               this.brokenByAnything(var1);
               this.kill();
               return false;
            } else if (DamageSource.IN_FIRE.equals(var1)) {
               if (this.isOnFire()) {
                  this.causeDamage(var1, 0.15F);
               } else {
                  this.setSecondsOnFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(var1) && this.getHealth() > 0.5F) {
               this.causeDamage(var1, 4.0F);
               return false;
            } else {
               boolean var3 = var1.getDirectEntity() instanceof AbstractArrow;
               boolean var4 = var3 && ((AbstractArrow)var1.getDirectEntity()).getPierceLevel() > 0;
               boolean var5 = "player".equals(var1.getMsgId());
               if (!var5 && !var3) {
                  return false;
               } else if (var1.getEntity() instanceof Player && !((Player)var1.getEntity()).getAbilities().mayBuild) {
                  return false;
               } else if (var1.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.kill();
                  return var4;
               } else {
                  long var6 = this.level.getGameTime();
                  if (var6 - this.lastHit > 5L && !var3) {
                     this.level.broadcastEntityEvent(this, (byte)32);
                     this.lastHit = var6;
                  } else {
                     this.brokenByPlayer(var1);
                     this.showBreakingParticles();
                     this.kill();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 32) {
         if (this.level.isClientSide) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level.getGameTime();
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(var3) || var3 == 0.0D) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   private void showBreakingParticles() {
      if (this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
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
      }

   }

   private void brokenByPlayer(DamageSource var1) {
      Block.popResource(this.level, this.blockPosition(), new ItemStack(Items.ARMOR_STAND));
      this.brokenByAnything(var1);
   }

   private void brokenByAnything(DamageSource var1) {
      this.playBrokenSound();
      this.dropAllDeathLoot(var1);

      int var2;
      ItemStack var3;
      for(var2 = 0; var2 < this.handItems.size(); ++var2) {
         var3 = (ItemStack)this.handItems.get(var2);
         if (!var3.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), var3);
            this.handItems.set(var2, ItemStack.EMPTY);
         }
      }

      for(var2 = 0; var2 < this.armorItems.size(); ++var2) {
         var3 = (ItemStack)this.armorItems.get(var2);
         if (!var3.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), var3);
            this.armorItems.set(var2, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected float tickHeadTurn(float var1, float var2) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.yRot;
      return 0.0F;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * (this.isBaby() ? 0.5F : 0.9F);
   }

   public double getMyRidingOffset() {
      return this.isMarker() ? 0.0D : 0.10000000149011612D;
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

   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
   }

   public boolean ignoreExplosion() {
      return this.isInvisible();
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   private void setSmall(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 1, var1));
   }

   public boolean isSmall() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   private void setShowArms(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 4, var1));
   }

   public boolean isShowArms() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   private void setNoBasePlate(boolean var1) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit((Byte)this.entityData.get(DATA_CLIENT_FLAGS), 8, var1));
   }

   public boolean isNoBasePlate() {
      return ((Byte)this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
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
      return var1 instanceof Player && !this.level.mayInteract((Player)var1, this.blockPosition());
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   protected SoundEvent getFallDamageSound(int var1) {
      return SoundEvents.ARMOR_STAND_FALL;
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

   public EntityDimensions getDimensions(Pose var1) {
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
         Iterator var5 = BlockPos.betweenClosed(new BlockPos(var2.minX, var2.minY, var2.minZ), new BlockPos(var2.maxX, var2.maxY, var2.maxZ)).iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            int var7 = Math.max(this.level.getBrightness(LightLayer.BLOCK, var6), this.level.getBrightness(LightLayer.SKY, var6));
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

   static {
      BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F);
      DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
      DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
      RIDABLE_MINECARTS = (var0) -> {
         return var0 instanceof AbstractMinecart && ((AbstractMinecart)var0).getMinecartType() == AbstractMinecart.Type.RIDEABLE;
      };
   }
}
