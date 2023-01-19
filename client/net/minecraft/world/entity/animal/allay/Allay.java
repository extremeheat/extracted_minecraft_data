package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Allay extends PathfinderMob implements InventoryCarrier, VibrationListener.VibrationListenerConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int GAME_EVENT_LISTENER_RANGE = 16;
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
   private static final int ANIMATION_DURATION = 5;
   private static final float PATHFINDING_BOUNDING_BOX_PADDING = 0.5F;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS
   );
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.PATH,
      MemoryModuleType.LOOK_TARGET,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.WALK_TARGET,
      MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
      MemoryModuleType.HURT_BY,
      MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
      MemoryModuleType.LIKED_PLAYER,
      MemoryModuleType.LIKED_NOTEBLOCK_POSITION,
      MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
      MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
      MemoryModuleType.IS_PANICKING,
      new MemoryModuleType[0]
   );
   public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(
      0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, new Float[]{2.5F, 3.0F, 3.75F, 4.0F}
   );
   private final DynamicGameEventListener<VibrationListener> dynamicGameEventListener;
   private final SimpleContainer inventory = new SimpleContainer(1);
   private float holdingItemAnimationTicks;
   private float holdingItemAnimationTicks0;

   public Allay(EntityType<? extends Allay> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setCanPickUpLoot(this.canPickUpLoot());
      this.dynamicGameEventListener = new DynamicGameEventListener<>(
         new VibrationListener(new EntityPositionSource(this, this.getEyeHeight()), 16, this, null, 0.0F, 0)
      );
   }

   @Override
   protected Brain.Provider<Allay> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return AllayAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Allay> getBrain() {
      return super.getBrain();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.FLYING_SPEED, 0.10000000149011612)
         .add(Attributes.MOVEMENT_SPEED, 0.10000000149011612)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.FOLLOW_RANGE, 48.0);
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      var2.setCanPassDoors(true);
      return var2;
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
         if (this.isInWater()) {
            this.moveRelative(0.02F, var1);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929));
         } else if (this.isInLava()) {
            this.moveRelative(0.02F, var1);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
         } else {
            this.moveRelative(this.getSpeed(), var1);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9100000262260437));
         }
      }

      this.calculateEntityAnimation(this, false);
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * 0.6F;
   }

   @Override
   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      Entity var4 = var1.getEntity();
      if (var4 instanceof Player var3) {
         Optional var5 = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
         if (var5.isPresent() && var3.getUUID().equals(var5.get())) {
            return false;
         }
      }

      return super.hurt(var1, var2);
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   @Override
   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ALLAY_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ALLAY_DEATH;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected void customServerAiStep() {
      this.level.getProfiler().push("allayBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      this.level.getProfiler().push("allayActivityUpdate");
      AllayAi.updateActivity(this);
      this.level.getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
         this.heal(1.0F);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
         if (this.hasItemInHand()) {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
         } else {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
         }
      } else {
         this.dynamicGameEventListener.getListener().tick(this.level);
      }
   }

   @Override
   public boolean canPickUpLoot() {
      return !this.isOnPickupCooldown() && this.hasItemInHand();
   }

   public boolean hasItemInHand() {
      return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
   }

   @Override
   public boolean canTakeItem(ItemStack var1) {
      return false;
   }

   private boolean isOnPickupCooldown() {
      return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
   }

   @Override
   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      ItemStack var4 = this.getItemInHand(InteractionHand.MAIN_HAND);
      if (var4.isEmpty() && !var3.isEmpty()) {
         ItemStack var7 = var3.copy();
         var7.setCount(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, var7);
         if (!var1.getAbilities().instabuild) {
            var3.shrink(1);
         }

         this.level.playSound(var1, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, var1.getUUID());
         return InteractionResult.SUCCESS;
      } else if (!var4.isEmpty() && var2 == InteractionHand.MAIN_HAND && var3.isEmpty()) {
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         this.level.playSound(var1, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.swing(InteractionHand.MAIN_HAND);

         for(ItemStack var6 : this.getInventory().removeAllItems()) {
            BehaviorUtils.throwItem(this, var6, this.position());
         }

         this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
         var1.addItem(var4);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   public SimpleContainer getInventory() {
      return this.inventory;
   }

   @Override
   protected Vec3i getPickupReach() {
      return ITEM_PICKUP_REACH;
   }

   @Override
   public boolean wantsToPickUp(ItemStack var1) {
      ItemStack var2 = this.getItemInHand(InteractionHand.MAIN_HAND);
      return !var2.isEmpty() && var2.sameItemStackIgnoreDurability(var1) && this.inventory.canAddItem(var1);
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      InventoryCarrier.pickUpItem(this, this, var1);
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public boolean isFlapping() {
      return !this.isOnGround();
   }

   @Override
   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var1) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel var2) {
         var1.accept(this.dynamicGameEventListener, var2);
      }
   }

   public boolean isFlying() {
      return this.animationSpeed > 0.3F;
   }

   public float getHoldingItemAnimationProgress(float var1) {
      return Mth.lerp(var1, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
   }

   @Override
   protected void dropEquipment() {
      super.dropEquipment();
      this.inventory.removeAllItems().forEach(this::spawnAtLocation);
      ItemStack var1 = this.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!var1.isEmpty() && !EnchantmentHelper.hasVanishingCurse(var1)) {
         this.spawnAtLocation(var1);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Override
   public boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Context var5) {
      if (this.level != var1 || this.isRemoved() || this.isNoAi()) {
         return false;
      } else if (!this.brain.hasMemoryValue(MemoryModuleType.LIKED_NOTEBLOCK_POSITION)) {
         return true;
      } else {
         Optional var6 = this.brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
         return var6.isPresent() && ((GlobalPos)var6.get()).dimension() == var1.dimension() && ((GlobalPos)var6.get()).pos().equals(var3);
      }
   }

   @Override
   public void onSignalReceive(
      ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7
   ) {
      if (var4 == GameEvent.NOTE_BLOCK_PLAY) {
         AllayAi.hearNoteblock(this, new BlockPos(var3));
      }
   }

   @Override
   public TagKey<GameEvent> getListenableEvents() {
      return GameEventTags.ALLAY_CAN_LISTEN;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("Inventory", this.inventory.createTag());
      VibrationListener.codec(this)
         .encodeStart(NbtOps.INSTANCE, this.dynamicGameEventListener.getListener())
         .resultOrPartial(LOGGER::error)
         .ifPresent(var1x -> var1.put("listener", var1x));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.inventory.fromTag(var1.getList("Inventory", 10));
      if (var1.contains("listener", 10)) {
         VibrationListener.codec(this)
            .parse(new Dynamic(NbtOps.INSTANCE, var1.getCompound("listener")))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.dynamicGameEventListener.updateListener(var1x, this.level));
      }
   }

   @Override
   protected boolean shouldStayCloseToLeashHolder() {
      return false;
   }

   @Override
   public Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions() {
      AABB var1 = this.getBoundingBox();
      int var2 = Mth.floor(var1.minX - 0.5);
      int var3 = Mth.floor(var1.maxX + 0.5);
      int var4 = Mth.floor(var1.minZ - 0.5);
      int var5 = Mth.floor(var1.maxZ + 0.5);
      int var6 = Mth.floor(var1.minY - 0.5);
      int var7 = Mth.floor(var1.maxY + 0.5);
      return BlockPos.betweenClosed(var2, var6, var4, var3, var7, var5);
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
   }
}
