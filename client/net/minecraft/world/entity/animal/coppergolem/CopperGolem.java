package net.minecraft.world.entity.animal.coppergolem;

import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class CopperGolem extends AbstractGolem implements ContainerUser, Shearable {
   private static final long IGNORE_WEATHERING_TICK = -2L;
   private static final long UNSET_WEATHERING_TICK = -1L;
   private static final int WEATHERING_TICK_FROM = 504000;
   private static final int WEATHERING_TICK_TO = 552000;
   private static final int SPIN_ANIMATION_MIN_COOLDOWN = 200;
   private static final int SPIN_ANIMATION_MAX_COOLDOWN = 240;
   private static final float SPIN_SOUND_TIME_INTERVAL_OFFSET = 10.0F;
   private static final EntityDataAccessor<WeatheringCopper.WeatherState> DATA_WEATHER_STATE;
   private static final EntityDataAccessor<CopperGolemState> COPPER_GOLEM_STATE;
   @Nullable
   private BlockPos openedChestPos;
   private long nextWeatheringTick = -1L;
   private int idleAnimationStartTick = 0;
   private final AnimationState idleAnimationState = new AnimationState();
   private final AnimationState interactionGetItemAnimationState = new AnimationState();
   private final AnimationState interactionGetNoItemAnimationState = new AnimationState();
   private final AnimationState interactionDropItemAnimationState = new AnimationState();
   private final AnimationState interactionDropNoItemAnimationState = new AnimationState();
   public static final EquipmentSlot EQUIPMENT_SLOT_ANTENNA;

   public CopperGolem(EntityType<? extends AbstractGolem> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setRequiredPathLength(48.0F);
      this.setPersistenceRequired();
      this.setState(CopperGolemState.IDLE);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathType.DANGER_OTHER, 16.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.20000000298023224).add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.MAX_HEALTH, 12.0);
   }

   public CopperGolemState getState() {
      return (CopperGolemState)this.entityData.get(COPPER_GOLEM_STATE);
   }

   public void setState(CopperGolemState var1) {
      this.entityData.set(COPPER_GOLEM_STATE, var1);
   }

   public WeatheringCopper.WeatherState getWeatherState() {
      return (WeatheringCopper.WeatherState)this.entityData.get(DATA_WEATHER_STATE);
   }

   public void setWeatherState(WeatheringCopper.WeatherState var1) {
      this.entityData.set(DATA_WEATHER_STATE, var1);
   }

   public void setOpenedChestPos(BlockPos var1) {
      this.openedChestPos = var1;
   }

   public void clearOpenedChestPos() {
      this.openedChestPos = null;
   }

   public AnimationState getIdleAnimationState() {
      return this.idleAnimationState;
   }

   public AnimationState getInteractionGetItemAnimationState() {
      return this.interactionGetItemAnimationState;
   }

   public AnimationState getInteractionGetNoItemAnimationState() {
      return this.interactionGetNoItemAnimationState;
   }

   public AnimationState getInteractionDropItemAnimationState() {
      return this.interactionDropItemAnimationState;
   }

   public AnimationState getInteractionDropNoItemAnimationState() {
      return this.interactionDropNoItemAnimationState;
   }

   protected Brain.Provider<CopperGolem> brainProvider() {
      return CopperGolemAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return CopperGolemAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<CopperGolem> getBrain() {
      return super.getBrain();
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_WEATHER_STATE, WeatheringCopper.WeatherState.UNAFFECTED);
      var1.define(COPPER_GOLEM_STATE, CopperGolemState.IDLE);
   }

   public void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putLong("next_weather_age", this.nextWeatheringTick);
      var1.store("weather_state", WeatheringCopper.WeatherState.CODEC, this.getWeatherState());
   }

   public void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.nextWeatheringTick = var1.getLongOr("next_weather_age", -1L);
      this.setWeatherState((WeatheringCopper.WeatherState)var1.read("weather_state", WeatheringCopper.WeatherState.CODEC).orElse(WeatheringCopper.WeatherState.UNAFFECTED));
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("copperGolemBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("copperGolemActivityUpdate");
      CopperGolemAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (!this.isNoAi()) {
            this.setupAnimationStates();
         }
      } else {
         this.updateWeathering((ServerLevel)this.level(), this.level().getRandom(), this.level().getDayTime());
      }

   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.isEmpty()) {
         ItemStack var4 = this.getMainHandItem();
         if (!var4.isEmpty()) {
            BehaviorUtils.throwItem(this, var4, var1.position());
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
         }
      }

      Level var6 = this.level();
      if (var3.is(Items.SHEARS) && this.readyForShearing()) {
         if (var6 instanceof ServerLevel) {
            ServerLevel var7 = (ServerLevel)var6;
            this.shear(var7, SoundSource.PLAYERS, var3);
            this.gameEvent(GameEvent.SHEAR, var1);
            var3.hurtAndBreak(1, var1, (InteractionHand)var2);
         }

         return InteractionResult.SUCCESS;
      } else if (var6.isClientSide()) {
         return InteractionResult.PASS;
      } else if (var3.is(Items.HONEYCOMB) && this.nextWeatheringTick != -2L) {
         var6.levelEvent(this, 3003, this.blockPosition(), 0);
         this.nextWeatheringTick = -2L;
         this.usePlayerItem(var1, var2, var3);
         return InteractionResult.SUCCESS_SERVER;
      } else if (var3.is(ItemTags.AXES) && this.nextWeatheringTick == -2L) {
         var6.playSound((Entity)null, (Entity)this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
         var6.levelEvent(this, 3004, this.blockPosition(), 0);
         this.nextWeatheringTick = -1L;
         var3.hurtAndBreak(1, var1, (EquipmentSlot)var2.asEquipmentSlot());
         return InteractionResult.SUCCESS_SERVER;
      } else {
         if (var3.is(ItemTags.AXES)) {
            WeatheringCopper.WeatherState var5 = this.getWeatherState();
            if (var5 != WeatheringCopper.WeatherState.UNAFFECTED) {
               var6.playSound((Entity)null, (Entity)this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
               var6.levelEvent(this, 3005, this.blockPosition(), 0);
               this.nextWeatheringTick = -1L;
               this.entityData.set(DATA_WEATHER_STATE, var5.previous(), true);
               var3.hurtAndBreak(1, var1, (EquipmentSlot)var2.asEquipmentSlot());
               return InteractionResult.SUCCESS_SERVER;
            }
         }

         return super.mobInteract(var1, var2);
      }
   }

   private void updateWeathering(ServerLevel var1, RandomSource var2, long var3) {
      if (this.nextWeatheringTick != -2L) {
         if (this.nextWeatheringTick == -1L) {
            this.nextWeatheringTick = var3 + (long)var2.nextIntBetweenInclusive(504000, 552000);
         } else {
            if (var3 >= this.nextWeatheringTick) {
               WeatheringCopper.WeatherState var5 = (WeatheringCopper.WeatherState)this.entityData.get(DATA_WEATHER_STATE);
               if (var5.equals(WeatheringCopper.WeatherState.OXIDIZED)) {
                  if (this.canTurnToStatue(var1)) {
                     this.turnToStatue(var1);
                  }
               } else {
                  this.setWeatherState(var5.next());
                  this.nextWeatheringTick += (long)var2.nextIntBetweenInclusive(504000, 552000);
               }
            }

         }
      }
   }

   private boolean canTurnToStatue(Level var1) {
      BlockPos var2 = this.blockPosition();
      return var1.getBlockState(var2).is(Blocks.AIR) && var2.getCenter().closerThan(this.position().add(0.0, 0.5, 0.0), 0.15, 0.2);
   }

   private void turnToStatue(ServerLevel var1) {
      BlockPos var2 = this.blockPosition();
      var1.setBlock(var2, (BlockState)((BlockState)Blocks.OXIDIZED_COPPER_GOLEM_STATUE.defaultBlockState().setValue(CopperGolemStatueBlock.POSE, CopperGolemStatueBlock.Pose.values()[this.random.nextInt(0, CopperGolemStatueBlock.Pose.values().length)])).setValue(CopperGolemStatueBlock.FACING, Direction.fromYRot((double)this.getYRot())), 3);
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof CopperGolemStatueBlockEntity var3) {
         var3.createStatue(this);
         this.dropPreservedEquipment(var1);
         this.discard();
         this.playSound(SoundEvents.COPPER_GOLEM_BECOME_STATUE);
      }

   }

   private void setupAnimationStates() {
      switch (this.getState()) {
         case IDLE:
            this.interactionGetNoItemAnimationState.stop();
            this.interactionGetItemAnimationState.stop();
            this.interactionDropItemAnimationState.stop();
            this.interactionDropNoItemAnimationState.stop();
            if (this.idleAnimationStartTick == this.tickCount) {
               this.idleAnimationState.start(this.tickCount);
            } else if (this.idleAnimationStartTick == 0) {
               this.idleAnimationStartTick = this.tickCount + this.random.nextInt(200, 240);
            }

            if ((float)this.tickCount == (float)this.idleAnimationStartTick + 10.0F) {
               this.playHeadSpinSound();
               this.idleAnimationStartTick = 0;
            }
            break;
         case GETTING_ITEM:
            this.idleAnimationState.stop();
            this.idleAnimationStartTick = 0;
            this.interactionGetNoItemAnimationState.stop();
            this.interactionDropItemAnimationState.stop();
            this.interactionDropNoItemAnimationState.stop();
            this.interactionGetItemAnimationState.startIfStopped(this.tickCount);
            break;
         case GETTING_NO_ITEM:
            this.idleAnimationState.stop();
            this.idleAnimationStartTick = 0;
            this.interactionGetItemAnimationState.stop();
            this.interactionDropNoItemAnimationState.stop();
            this.interactionDropItemAnimationState.stop();
            this.interactionGetNoItemAnimationState.startIfStopped(this.tickCount);
            break;
         case DROPPING_ITEM:
            this.idleAnimationState.stop();
            this.idleAnimationStartTick = 0;
            this.interactionGetItemAnimationState.stop();
            this.interactionGetNoItemAnimationState.stop();
            this.interactionDropNoItemAnimationState.stop();
            this.interactionDropItemAnimationState.startIfStopped(this.tickCount);
            break;
         case DROPPING_NO_ITEM:
            this.idleAnimationState.stop();
            this.idleAnimationStartTick = 0;
            this.interactionGetItemAnimationState.stop();
            this.interactionGetNoItemAnimationState.stop();
            this.interactionDropItemAnimationState.stop();
            this.interactionDropNoItemAnimationState.startIfStopped(this.tickCount);
      }

   }

   public void spawn(WeatheringCopper.WeatherState var1) {
      this.setWeatherState(var1);
      this.playSpawnSound();
      this.getBrain().setMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, 60);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.playSpawnSound();
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public void playSpawnSound() {
      this.playSound(SoundEvents.COPPER_GOLEM_SPAWN);
   }

   private void playHeadSpinSound() {
      if (!this.isSilent()) {
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSpinHeadSound(), this.getSoundSource(), 1.0F, 1.0F, false);
      }

   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).hurtSound();
   }

   protected SoundEvent getDeathSound() {
      return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).deathSound();
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).stepSound(), 1.0F, 1.0F);
   }

   private SoundEvent getSpinHeadSound() {
      return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).spinHeadSound();
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.75F * this.getEyeHeight()), 0.0);
   }

   public boolean hasContainerOpen(ContainerOpenersCounter var1, BlockPos var2) {
      if (this.openedChestPos == null) {
         return false;
      } else {
         BlockState var3 = this.level().getBlockState(this.openedChestPos);
         return this.openedChestPos.equals(var2) || var3.getBlock() instanceof ChestBlock && var3.getValue(ChestBlock.TYPE) != ChestType.SINGLE && ChestBlock.getConnectedBlockPos(this.openedChestPos, var3).equals(var2);
      }
   }

   public double getContainerInteractionRange() {
      return 3.0;
   }

   public void shear(ServerLevel var1, SoundSource var2, ItemStack var3) {
      var1.playSound((Entity)null, this, SoundEvents.COPPER_GOLEM_SHEAR, var2, 1.0F, 1.0F);
      ItemStack var4 = this.getItemBySlot(EQUIPMENT_SLOT_ANTENNA);
      this.setItemSlot(EQUIPMENT_SLOT_ANTENNA, ItemStack.EMPTY);
      this.spawnAtLocation(var1, var4, 1.5F);
   }

   public boolean readyForShearing() {
      return this.isAlive() && this.getItemBySlot(EQUIPMENT_SLOT_ANTENNA).is(ItemTags.SHEARABLE_FROM_COPPER_GOLEM);
   }

   protected void dropEquipment(ServerLevel var1) {
      super.dropEquipment(var1);
      this.dropPreservedEquipment(var1);
   }

   protected void actuallyHurt(ServerLevel var1, DamageSource var2, float var3) {
      super.actuallyHurt(var1, var2, var3);
      this.setState(CopperGolemState.IDLE);
   }

   static {
      DATA_WEATHER_STATE = SynchedEntityData.<WeatheringCopper.WeatherState>defineId(CopperGolem.class, EntityDataSerializers.WEATHERING_COPPER_STATE);
      COPPER_GOLEM_STATE = SynchedEntityData.<CopperGolemState>defineId(CopperGolem.class, EntityDataSerializers.COPPER_GOLEM_STATE);
      EQUIPMENT_SLOT_ANTENNA = EquipmentSlot.SADDLE;
   }
}
