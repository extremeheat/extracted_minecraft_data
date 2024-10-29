package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Allay extends PathfinderMob implements InventoryCarrier, VibrationSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
   private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
   private static final float DANCING_LOOP_DURATION = 55.0F;
   private static final float SPINNING_ANIMATION_DURATION = 15.0F;
   private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
   private static final int NUM_OF_DUPLICATION_HEARTS = 3;
   public static final int MAX_NOTEBLOCK_DISTANCE = 1024;
   private static final EntityDataAccessor<Boolean> DATA_DANCING;
   private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
   public static final ImmutableList<Float> THROW_SOUND_PITCHES;
   private final DynamicGameEventListener<VibrationSystem.Listener> dynamicVibrationListener;
   private VibrationSystem.Data vibrationData;
   private final VibrationSystem.User vibrationUser;
   private final DynamicGameEventListener<JukeboxListener> dynamicJukeboxListener;
   private final SimpleContainer inventory = new SimpleContainer(1);
   @Nullable
   private BlockPos jukeboxPos;
   private long duplicationCooldown;
   private float holdingItemAnimationTicks;
   private float holdingItemAnimationTicks0;
   private float dancingAnimationTicks;
   private float spinningAnimationTicks;
   private float spinningAnimationTicks0;

   public Allay(EntityType<? extends Allay> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setCanPickUpLoot(this.canPickUpLoot());
      this.vibrationUser = new VibrationUser();
      this.vibrationData = new VibrationSystem.Data();
      this.dynamicVibrationListener = new DynamicGameEventListener(new VibrationSystem.Listener(this));
      this.dynamicJukeboxListener = new DynamicGameEventListener(new JukeboxListener(this.vibrationUser.getPositionSource(), ((GameEvent)GameEvent.JUKEBOX_PLAY.value()).notificationRadius()));
   }

   protected Brain.Provider<Allay> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return AllayAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Allay> getBrain() {
      return super.getBrain();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.FLYING_SPEED, 0.10000000149011612).add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      var2.setCanPassDoors(true);
      var2.setRequiredPathLength(48.0F);
      return var2;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_DANCING, false);
      var1.define(DATA_CAN_DUPLICATE, true);
   }

   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance()) {
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

   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      Entity var5 = var2.getEntity();
      if (var5 instanceof Player var4) {
         Optional var6 = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
         if (var6.isPresent() && var4.getUUID().equals(var6.get())) {
            return false;
         }
      }

      return super.hurtServer(var1, var2, var3);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   protected SoundEvent getAmbientSound() {
      return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ALLAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ALLAY_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("allayBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("allayActivityUpdate");
      AllayAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
         this.heal(1.0F);
      }

      if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
         this.setDancing(false);
         this.jukeboxPos = null;
      }

      this.updateDuplicationCooldown();
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
         if (this.hasItemInHand()) {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
         } else {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
         }

         if (this.isDancing()) {
            ++this.dancingAnimationTicks;
            this.spinningAnimationTicks0 = this.spinningAnimationTicks;
            if (this.isSpinning()) {
               ++this.spinningAnimationTicks;
            } else {
               --this.spinningAnimationTicks;
            }

            this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
         } else {
            this.dancingAnimationTicks = 0.0F;
            this.spinningAnimationTicks = 0.0F;
            this.spinningAnimationTicks0 = 0.0F;
         }
      } else {
         VibrationSystem.Ticker.tick(this.level(), this.vibrationData, this.vibrationUser);
         if (this.isPanicking()) {
            this.setDancing(false);
         }
      }

   }

   public boolean canPickUpLoot() {
      return !this.isOnPickupCooldown() && this.hasItemInHand();
   }

   public boolean hasItemInHand() {
      return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
   }

   protected boolean canDispenserEquipIntoSlot(EquipmentSlot var1) {
      return false;
   }

   private boolean isOnPickupCooldown() {
      return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
   }

   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      ItemStack var4 = this.getItemInHand(InteractionHand.MAIN_HAND);
      if (this.isDancing() && var3.is(ItemTags.DUPLICATES_ALLAYS) && this.canDuplicate()) {
         this.duplicateAllay();
         this.level().broadcastEntityEvent(this, (byte)18);
         this.level().playSound((Player)var1, (Entity)this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.removeInteractionItem(var1, var3);
         return InteractionResult.SUCCESS;
      } else if (var4.isEmpty() && !var3.isEmpty()) {
         ItemStack var7 = var3.copyWithCount(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, var7);
         this.removeInteractionItem(var1, var3);
         this.level().playSound((Player)var1, (Entity)this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, (Object)var1.getUUID());
         return InteractionResult.SUCCESS;
      } else if (!var4.isEmpty() && var2 == InteractionHand.MAIN_HAND && var3.isEmpty()) {
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         this.level().playSound((Player)var1, (Entity)this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.swing(InteractionHand.MAIN_HAND);
         Iterator var5 = this.getInventory().removeAllItems().iterator();

         while(var5.hasNext()) {
            ItemStack var6 = (ItemStack)var5.next();
            BehaviorUtils.throwItem(this, var6, this.position());
         }

         this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
         var1.addItem(var4);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public void setJukeboxPlaying(BlockPos var1, boolean var2) {
      if (var2) {
         if (!this.isDancing()) {
            this.jukeboxPos = var1;
            this.setDancing(true);
         }
      } else if (var1.equals(this.jukeboxPos) || this.jukeboxPos == null) {
         this.jukeboxPos = null;
         this.setDancing(false);
      }

   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   protected Vec3i getPickupReach() {
      return ITEM_PICKUP_REACH;
   }

   public boolean wantsToPickUp(ServerLevel var1, ItemStack var2) {
      ItemStack var3 = this.getItemInHand(InteractionHand.MAIN_HAND);
      return !var3.isEmpty() && var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.inventory.canAddItem(var2) && this.allayConsidersItemEqual(var3, var2);
   }

   private boolean allayConsidersItemEqual(ItemStack var1, ItemStack var2) {
      return ItemStack.isSameItem(var1, var2) && !this.hasNonMatchingPotion(var1, var2);
   }

   private boolean hasNonMatchingPotion(ItemStack var1, ItemStack var2) {
      PotionContents var3 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      PotionContents var4 = (PotionContents)var2.get(DataComponents.POTION_CONTENTS);
      return !Objects.equals(var3, var4);
   }

   protected void pickUpItem(ServerLevel var1, ItemEntity var2) {
      InventoryCarrier.pickUpItem(var1, this, this, var2);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public boolean isFlapping() {
      return !this.onGround();
   }

   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var1) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         var1.accept(this.dynamicVibrationListener, var2);
         var1.accept(this.dynamicJukeboxListener, var2);
      }

   }

   public boolean isDancing() {
      return (Boolean)this.entityData.get(DATA_DANCING);
   }

   public void setDancing(boolean var1) {
      if (!this.level().isClientSide && this.isEffectiveAi() && (!var1 || !this.isPanicking())) {
         this.entityData.set(DATA_DANCING, var1);
      }
   }

   private boolean shouldStopDancing() {
      return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), (double)((GameEvent)GameEvent.JUKEBOX_PLAY.value()).notificationRadius()) || !this.level().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
   }

   public float getHoldingItemAnimationProgress(float var1) {
      return Mth.lerp(var1, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
   }

   public boolean isSpinning() {
      float var1 = this.dancingAnimationTicks % 55.0F;
      return var1 < 15.0F;
   }

   public float getSpinningProgress(float var1) {
      return Mth.lerp(var1, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
   }

   public boolean equipmentHasChanged(ItemStack var1, ItemStack var2) {
      return !this.allayConsidersItemEqual(var1, var2);
   }

   protected void dropEquipment(ServerLevel var1) {
      super.dropEquipment(var1);
      this.inventory.removeAllItems().forEach((var2x) -> {
         this.spawnAtLocation(var1, var2x);
      });
      ItemStack var2 = this.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!var2.isEmpty() && !EnchantmentHelper.has(var2, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
         this.spawnAtLocation(var1, var2);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.writeInventoryToTag(var1, this.registryAccess());
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      VibrationSystem.Data.CODEC.encodeStart(var2, this.vibrationData).resultOrPartial((var0) -> {
         LOGGER.error("Failed to encode vibration listener for Allay: '{}'", var0);
      }).ifPresent((var1x) -> {
         var1.put("listener", var1x);
      });
      var1.putLong("DuplicationCooldown", this.duplicationCooldown);
      var1.putBoolean("CanDuplicate", this.canDuplicate());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readInventoryFromTag(var1, this.registryAccess());
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      if (var1.contains("listener", 10)) {
         VibrationSystem.Data.CODEC.parse(var2, var1.getCompound("listener")).resultOrPartial((var0) -> {
            LOGGER.error("Failed to parse vibration listener for Allay: '{}'", var0);
         }).ifPresent((var1x) -> {
            this.vibrationData = var1x;
         });
      }

      this.duplicationCooldown = (long)var1.getInt("DuplicationCooldown");
      this.entityData.set(DATA_CAN_DUPLICATE, var1.getBoolean("CanDuplicate"));
   }

   protected boolean shouldStayCloseToLeashHolder() {
      return false;
   }

   private void updateDuplicationCooldown() {
      if (this.duplicationCooldown > 0L) {
         --this.duplicationCooldown;
      }

      if (!this.level().isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
         this.entityData.set(DATA_CAN_DUPLICATE, true);
      }

   }

   private void duplicateAllay() {
      Allay var1 = (Allay)EntityType.ALLAY.create(this.level(), EntitySpawnReason.BREEDING);
      if (var1 != null) {
         var1.moveTo(this.position());
         var1.setPersistenceRequired();
         var1.resetDuplicationCooldown();
         this.resetDuplicationCooldown();
         this.level().addFreshEntity(var1);
      }

   }

   private void resetDuplicationCooldown() {
      this.duplicationCooldown = 6000L;
      this.entityData.set(DATA_CAN_DUPLICATE, false);
   }

   private boolean canDuplicate() {
      return (Boolean)this.entityData.get(DATA_CAN_DUPLICATE);
   }

   private void removeInteractionItem(Player var1, ItemStack var2) {
      var2.consume(1, var1);
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 3; ++var2) {
            this.spawnHeartParticle();
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   private void spawnHeartParticle() {
      double var1 = this.random.nextGaussian() * 0.02;
      double var3 = this.random.nextGaussian() * 0.02;
      double var5 = this.random.nextGaussian() * 0.02;
      this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var1, var3, var5);
   }

   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   static {
      DATA_DANCING = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
      DATA_CAN_DUPLICATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING, new MemoryModuleType[0]);
      THROW_SOUND_PITCHES = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, new Float[]{2.5F, 3.0F, 3.75F, 4.0F});
   }

   private class VibrationUser implements VibrationSystem.User {
      private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
      private final PositionSource positionSource = new EntityPositionSource(Allay.this, Allay.this.getEyeHeight());

      VibrationUser() {
         super();
      }

      public int getListenerRadius() {
         return 16;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public boolean canReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, GameEvent.Context var4) {
         if (Allay.this.isNoAi()) {
            return false;
         } else {
            Optional var5 = Allay.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
            if (var5.isEmpty()) {
               return true;
            } else {
               GlobalPos var6 = (GlobalPos)var5.get();
               return var6.isCloseEnough(var1.dimension(), Allay.this.blockPosition(), 1024) && var6.pos().equals(var2);
            }
         }
      }

      public void onReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable Entity var4, @Nullable Entity var5, float var6) {
         if (var3.is((Holder)GameEvent.NOTE_BLOCK_PLAY)) {
            AllayAi.hearNoteblock(Allay.this, new BlockPos(var2));
         }

      }

      public TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.ALLAY_CAN_LISTEN;
      }
   }

   class JukeboxListener implements GameEventListener {
      private final PositionSource listenerSource;
      private final int listenerRadius;

      public JukeboxListener(final PositionSource var2, final int var3) {
         super();
         this.listenerSource = var2;
         this.listenerRadius = var3;
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRadius;
      }

      public boolean handleGameEvent(ServerLevel var1, Holder<GameEvent> var2, GameEvent.Context var3, Vec3 var4) {
         if (var2.is((Holder)GameEvent.JUKEBOX_PLAY)) {
            Allay.this.setJukeboxPlaying(BlockPos.containing(var4), true);
            return true;
         } else if (var2.is((Holder)GameEvent.JUKEBOX_STOP_PLAY)) {
            Allay.this.setJukeboxPlaying(BlockPos.containing(var4), false);
            return true;
         } else {
            return false;
         }
      }
   }
}
