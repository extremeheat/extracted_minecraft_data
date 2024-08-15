package net.minecraft.world.entity.monster.warden;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

public class Warden extends Monster implements VibrationSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int VIBRATION_COOLDOWN_TICKS = 40;
   private static final int TIME_TO_USE_MELEE_UNTIL_SONIC_BOOM = 200;
   private static final int MAX_HEALTH = 500;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   private static final float KNOCKBACK_RESISTANCE = 1.0F;
   private static final float ATTACK_KNOCKBACK = 1.5F;
   private static final int ATTACK_DAMAGE = 30;
   private static final int FOLLOW_RANGE = 24;
   private static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData.defineId(Warden.class, EntityDataSerializers.INT);
   private static final int DARKNESS_DISPLAY_LIMIT = 200;
   private static final int DARKNESS_DURATION = 260;
   private static final int DARKNESS_RADIUS = 20;
   private static final int DARKNESS_INTERVAL = 120;
   private static final int ANGERMANAGEMENT_TICK_DELAY = 20;
   private static final int DEFAULT_ANGER = 35;
   private static final int PROJECTILE_ANGER = 10;
   private static final int ON_HURT_ANGER_BOOST = 20;
   private static final int RECENT_PROJECTILE_TICK_THRESHOLD = 100;
   private static final int TOUCH_COOLDOWN_TICKS = 20;
   private static final int DIGGING_PARTICLES_AMOUNT = 30;
   private static final float DIGGING_PARTICLES_DURATION = 4.5F;
   private static final float DIGGING_PARTICLES_OFFSET = 0.7F;
   private static final int PROJECTILE_ANGER_DISTANCE = 30;
   private int tendrilAnimation;
   private int tendrilAnimationO;
   private int heartAnimation;
   private int heartAnimationO;
   public AnimationState roarAnimationState = new AnimationState();
   public AnimationState sniffAnimationState = new AnimationState();
   public AnimationState emergeAnimationState = new AnimationState();
   public AnimationState diggingAnimationState = new AnimationState();
   public AnimationState attackAnimationState = new AnimationState();
   public AnimationState sonicBoomAnimationState = new AnimationState();
   private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
   private final VibrationSystem.User vibrationUser;
   private VibrationSystem.Data vibrationData;
   AngerManagement angerManagement = new AngerManagement(this::canTargetEntity, Collections.emptyList());

   public Warden(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.vibrationUser = new Warden.VibrationUser();
      this.vibrationData = new VibrationSystem.Data();
      this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
      this.xpReward = 5;
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
      this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, var1, this.hasPose(Pose.EMERGING) ? 1 : 0);
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      if (var1.getData() == 1) {
         this.setPose(Pose.EMERGING);
      }
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      return super.checkSpawnObstruction(var1) && var1.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   @Override
   public boolean isInvulnerableTo(DamageSource var1) {
      return this.isDiggingOrEmerging() && !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? true : super.isInvulnerableTo(var1);
   }

   boolean isDiggingOrEmerging() {
      return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING);
   }

   @Override
   protected boolean canRide(Entity var1) {
      return false;
   }

   @Override
   public boolean canDisableShield() {
      return true;
   }

   @Override
   protected float nextStep() {
      return this.moveDist + 0.55F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 500.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.5)
         .add(Attributes.ATTACK_DAMAGE, 30.0)
         .add(Attributes.FOLLOW_RANGE, 24.0);
   }

   @Override
   public boolean dampensVibrations() {
      return true;
   }

   @Override
   protected float getSoundVolume() {
      return 4.0F;
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return !this.hasPose(Pose.ROARING) && !this.isDiggingOrEmerging() ? this.getAngerLevel().getAmbientSound() : null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WARDEN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WARDEN_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.WARDEN_STEP, 10.0F, 1.0F);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      this.level().broadcastEntityEvent(this, (byte)4);
      this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
      SonicBoom.setCooldown(this, 40);
      return super.doHurtTarget(var1);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(CLIENT_ANGER_LEVEL, 0);
   }

   public int getClientAngerLevel() {
      return this.entityData.get(CLIENT_ANGER_LEVEL);
   }

   private void syncClientAngerLevel() {
      this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
   }

   @Override
   public void tick() {
      if (this.level() instanceof ServerLevel var1) {
         VibrationSystem.Ticker.tick(var1, this.vibrationData, this.vibrationUser);
         if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
            WardenAi.setDigCooldown(this);
         }
      }

      super.tick();
      if (this.level().isClientSide()) {
         if (this.tickCount % this.getHeartBeatDelay() == 0) {
            this.heartAnimation = 10;
            if (!this.isSilent()) {
               this.level()
                  .playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0F, this.getVoicePitch(), false);
            }
         }

         this.tendrilAnimationO = this.tendrilAnimation;
         if (this.tendrilAnimation > 0) {
            this.tendrilAnimation--;
         }

         this.heartAnimationO = this.heartAnimation;
         if (this.heartAnimation > 0) {
            this.heartAnimation--;
         }

         switch (this.getPose()) {
            case EMERGING:
               this.clientDiggingParticles(this.emergeAnimationState);
               break;
            case DIGGING:
               this.clientDiggingParticles(this.diggingAnimationState);
         }
      }
   }

   @Override
   protected void customServerAiStep() {
      ServerLevel var1 = (ServerLevel)this.level();
      var1.getProfiler().push("wardenBrain");
      this.getBrain().tick(var1, this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
      if ((this.tickCount + this.getId()) % 120 == 0) {
         applyDarknessAround(var1, this.position(), this, 20);
      }

      if (this.tickCount % 20 == 0) {
         this.angerManagement.tick(var1, this::canTargetEntity);
         this.syncClientAngerLevel();
      }

      WardenAi.updateActivity(this);
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.roarAnimationState.stop();
         this.attackAnimationState.start(this.tickCount);
      } else if (var1 == 61) {
         this.tendrilAnimation = 10;
      } else if (var1 == 62) {
         this.sonicBoomAnimationState.start(this.tickCount);
      } else {
         super.handleEntityEvent(var1);
      }
   }

   private int getHeartBeatDelay() {
      float var1 = (float)this.getClientAngerLevel() / (float)AngerLevel.ANGRY.getMinimumAnger();
      return 40 - Mth.floor(Mth.clamp(var1, 0.0F, 1.0F) * 30.0F);
   }

   public float getTendrilAnimation(float var1) {
      return Mth.lerp(var1, (float)this.tendrilAnimationO, (float)this.tendrilAnimation) / 10.0F;
   }

   public float getHeartAnimation(float var1) {
      return Mth.lerp(var1, (float)this.heartAnimationO, (float)this.heartAnimation) / 10.0F;
   }

   private void clientDiggingParticles(AnimationState var1) {
      if ((float)var1.getTimeInMillis((float)this.tickCount) < 4500.0F) {
         RandomSource var2 = this.getRandom();
         BlockState var3 = this.getBlockStateOn();
         if (var3.getRenderShape() != RenderShape.INVISIBLE) {
            for (int var4 = 0; var4 < 30; var4++) {
               double var5 = this.getX() + (double)Mth.randomBetween(var2, -0.7F, 0.7F);
               double var7 = this.getY();
               double var9 = this.getZ() + (double)Mth.randomBetween(var2, -0.7F, 0.7F);
               this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var3), var5, var7, var9, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_POSE.equals(var1)) {
         switch (this.getPose()) {
            case EMERGING:
               this.emergeAnimationState.start(this.tickCount);
               break;
            case DIGGING:
               this.diggingAnimationState.start(this.tickCount);
               break;
            case ROARING:
               this.roarAnimationState.start(this.tickCount);
               break;
            case SNIFFING:
               this.sniffAnimationState.start(this.tickCount);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public boolean ignoreExplosion(Explosion var1) {
      return this.isDiggingOrEmerging();
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return WardenAi.makeBrain(this, var1);
   }

   @Override
   public Brain<Warden> getBrain() {
      return (Brain<Warden>)super.getBrain();
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var1) {
      if (this.level() instanceof ServerLevel var2) {
         var1.accept(this.dynamicGameEventListener, var2);
      }
   }

   @Contract("null->false")
   public boolean canTargetEntity(@Nullable Entity var1) {
      if (var1 instanceof LivingEntity var2
         && this.level() == var1.level()
         && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(var1)
         && !this.isAlliedTo(var1)
         && var2.getType() != EntityType.ARMOR_STAND
         && var2.getType() != EntityType.WARDEN
         && !var2.isInvulnerable()
         && !var2.isDeadOrDying()
         && this.level().getWorldBorder().isWithinBounds(var2.getBoundingBox())) {
         return true;
      }

      return false;
   }

   public static void applyDarknessAround(ServerLevel var0, Vec3 var1, @Nullable Entity var2, int var3) {
      MobEffectInstance var4 = new MobEffectInstance(MobEffects.DARKNESS, 260, 0, false, false);
      MobEffectUtil.addEffectToPlayersAround(var0, var2, var1, (double)var3, var4, 200);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      AngerManagement.codec(this::canTargetEntity)
         .encodeStart(var2, this.angerManagement)
         .resultOrPartial(var0 -> LOGGER.error("Failed to encode anger state for Warden: '{}'", var0))
         .ifPresent(var1x -> var1.put("anger", var1x));
      VibrationSystem.Data.CODEC
         .encodeStart(var2, this.vibrationData)
         .resultOrPartial(var0 -> LOGGER.error("Failed to encode vibration listener for Warden: '{}'", var0))
         .ifPresent(var1x -> var1.put("listener", var1x));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      if (var1.contains("anger")) {
         AngerManagement.codec(this::canTargetEntity)
            .parse(var2, var1.get("anger"))
            .resultOrPartial(var0 -> LOGGER.error("Failed to parse anger state for Warden: '{}'", var0))
            .ifPresent(var1x -> this.angerManagement = var1x);
         this.syncClientAngerLevel();
      }

      if (var1.contains("listener", 10)) {
         VibrationSystem.Data.CODEC
            .parse(var2, var1.getCompound("listener"))
            .resultOrPartial(var0 -> LOGGER.error("Failed to parse vibration listener for Warden: '{}'", var0))
            .ifPresent(var1x -> this.vibrationData = var1x);
      }
   }

   private void playListeningSound() {
      if (!this.hasPose(Pose.ROARING)) {
         this.playSound(this.getAngerLevel().getListeningSound(), 10.0F, this.getVoicePitch());
      }
   }

   public AngerLevel getAngerLevel() {
      return AngerLevel.byAnger(this.getActiveAnger());
   }

   private int getActiveAnger() {
      return this.angerManagement.getActiveAnger(this.getTarget());
   }

   public void clearAnger(Entity var1) {
      this.angerManagement.clearAnger(var1);
   }

   public void increaseAngerAt(@Nullable Entity var1) {
      this.increaseAngerAt(var1, 35, true);
   }

   @VisibleForTesting
   public void increaseAngerAt(@Nullable Entity var1, int var2, boolean var3) {
      if (!this.isNoAi() && this.canTargetEntity(var1)) {
         WardenAi.setDigCooldown(this);
         boolean var4 = !(this.getTarget() instanceof Player);
         int var5 = this.angerManagement.increaseAnger(var1, var2);
         if (var1 instanceof Player && var4 && AngerLevel.byAnger(var5).isAngry()) {
            this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         }

         if (var3) {
            this.playListeningSound();
         }
      }
   }

   public Optional<LivingEntity> getEntityAngryAt() {
      return this.getAngerLevel().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
   }

   @Nullable
   @Override
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
      if (var3 == EntitySpawnReason.TRIGGERED) {
         this.setPose(Pose.EMERGING);
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long)WardenAi.EMERGE_DURATION);
         this.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
      }

      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (!this.level().isClientSide && !this.isNoAi() && !this.isDiggingOrEmerging()) {
         Entity var4 = var1.getEntity();
         this.increaseAngerAt(var4, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
         if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
            && var4 instanceof LivingEntity var5
            && (var1.isDirect() || this.closerThan(var5, 5.0))) {
            this.setAttackTarget(var5);
         }
      }

      return var3;
   }

   public void setAttackTarget(LivingEntity var1) {
      this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
      this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, var1);
      this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      SonicBoom.setCooldown(this, 200);
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      EntityDimensions var2 = super.getDefaultDimensions(var1);
      return this.isDiggingOrEmerging() ? EntityDimensions.fixed(var2.width(), 1.0F) : var2;
   }

   @Override
   public boolean isPushable() {
      return !this.isDiggingOrEmerging() && super.isPushable();
   }

   @Override
   protected void doPush(Entity var1) {
      if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
         this.increaseAngerAt(var1);
         WardenAi.setDisturbanceLocation(this, var1.blockPosition());
      }

      super.doPush(var1);
   }

   @VisibleForTesting
   public AngerManagement getAngerManagement() {
      return this.angerManagement;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new GroundPathNavigation(this, var1) {
         @Override
         protected PathFinder createPathFinder(int var1) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, var1) {
               @Override
               protected float distance(Node var1, Node var2) {
                  return var1.distanceToXZ(var2);
               }
            };
         }
      };
   }

   @Override
   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   @Override
   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   class VibrationUser implements VibrationSystem.User {
      private static final int GAME_EVENT_LISTENER_RANGE = 16;
      private final PositionSource positionSource = new EntityPositionSource(Warden.this, Warden.this.getEyeHeight());

      VibrationUser() {
         super();
      }

      @Override
      public int getListenerRadius() {
         return 16;
      }

      @Override
      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      @Override
      public TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.WARDEN_CAN_LISTEN;
      }

      @Override
      public boolean canTriggerAvoidVibration() {
         return true;
      }

      @Override
      public boolean canReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, GameEvent.Context var4) {
         if (!Warden.this.isNoAi()
            && !Warden.this.isDeadOrDying()
            && !Warden.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN)
            && !Warden.this.isDiggingOrEmerging()
            && var1.getWorldBorder().isWithinBounds(var2)) {
            if (var4.sourceEntity() instanceof LivingEntity var5 && !Warden.this.canTargetEntity(var5)) {
               return false;
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void onReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable Entity var4, @Nullable Entity var5, float var6) {
         if (!Warden.this.isDeadOrDying()) {
            Warden.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
            var1.broadcastEntityEvent(Warden.this, (byte)61);
            Warden.this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, Warden.this.getVoicePitch());
            BlockPos var7 = var2;
            if (var5 != null) {
               if (Warden.this.closerThan(var5, 30.0)) {
                  if (Warden.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                     if (Warden.this.canTargetEntity(var5)) {
                        var7 = var5.blockPosition();
                     }

                     Warden.this.increaseAngerAt(var5);
                  } else {
                     Warden.this.increaseAngerAt(var5, 10, true);
                  }
               }

               Warden.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
            } else {
               Warden.this.increaseAngerAt(var4);
            }

            if (!Warden.this.getAngerLevel().isAngry()) {
               Optional var8 = Warden.this.angerManagement.getActiveEntity();
               if (var5 != null || var8.isEmpty() || var8.get() == var4) {
                  WardenAi.setDisturbanceLocation(Warden.this, var7);
               }
            }
         }
      }
   }
}
