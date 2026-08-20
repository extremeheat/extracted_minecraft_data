package net.minecraft.world.entity.monster.warden;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
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
import net.minecraft.world.entity.ai.sensing.SensorType;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public class Warden extends Monster implements VibrationSystem {
   private static final int VIBRATION_COOLDOWN_TICKS = 40;
   private static final int TIME_TO_USE_MELEE_UNTIL_SONIC_BOOM = 200;
   private static final int MAX_HEALTH = 500;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   private static final float KNOCKBACK_RESISTANCE = 1.0F;
   private static final float ATTACK_KNOCKBACK = 1.5F;
   private static final int ATTACK_DAMAGE = 30;
   private static final int FOLLOW_RANGE = 24;
   private static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL;
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
   private static final Brain.Provider<Warden> BRAIN_PROVIDER;
   private int tendrilAnimation;
   private int tendrilAnimationO;
   private int heartAnimation;
   private int heartAnimationO;
   public final AnimationState roarAnimationState = new AnimationState();
   public final AnimationState sniffAnimationState = new AnimationState();
   public final AnimationState emergeAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState attackAnimationState = new AnimationState();
   public final AnimationState sonicBoomAnimationState = new AnimationState();
   private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener = new DynamicGameEventListener<VibrationSystem.Listener>(new VibrationSystem.Listener(this));
   private final VibrationSystem.User vibrationUser = new VibrationUser();
   private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
   private AngerManagement angerManagement = new AngerManagement(this::canTargetEntity, Collections.emptyList());

   public Warden(final EntityType<? extends Monster> type, final Level level) {
      super(type, level);
      this.xpReward = 5;
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGING, 8.0F);
      this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.FIRE, 0.0F);
      this.setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, 0.0F);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(final ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity, this.hasPose(Pose.EMERGING) ? 1 : 0);
   }

   public void recreateFromPacket(final ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      if (packet.getData() == 1) {
         this.setPose(Pose.EMERGING);
      }

   }

   public boolean checkSpawnObstruction(final LevelReader level) {
      return super.checkSpawnObstruction(level) && level.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
   }

   public float getWalkTargetValue(final BlockPos pos, final LevelReader level) {
      return 0.0F;
   }

   public boolean isInvulnerableTo(final ServerLevel level, final DamageSource source) {
      return this.isDiggingOrEmerging() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? true : super.isInvulnerableTo(level, source);
   }

   private boolean isDiggingOrEmerging() {
      return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING);
   }

   protected boolean canRide(final Entity vehicle) {
      return false;
   }

   public float getSecondsToDisableBlocking() {
      return 5.0F;
   }

   protected float nextStep() {
      return this.moveDist + 0.55F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.ATTACK_DAMAGE, 30.0).add(Attributes.FOLLOW_RANGE, 24.0);
   }

   public boolean dampensVibrations() {
      return true;
   }

   protected float getSoundVolume() {
      return 4.0F;
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return !this.hasPose(Pose.ROARING) && !this.isDiggingOrEmerging() ? this.getAngerLevel().getAmbientSound() : null;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.WARDEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WARDEN_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.WARDEN_STEP, 10.0F, 1.0F);
   }

   public boolean doHurtTarget(final ServerLevel level, final Entity target) {
      level.broadcastEntityEvent(this, (byte)4);
      this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
      SonicBoom.setCooldown(this, 40);
      return super.doHurtTarget(level, target);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(CLIENT_ANGER_LEVEL, 0);
   }

   public int getClientAngerLevel() {
      return (Integer)this.entityData.get(CLIENT_ANGER_LEVEL);
   }

   private void syncClientAngerLevel() {
      this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
   }

   public void tick() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel serverLevel) {
         VibrationSystem.Ticker.tick(serverLevel, this.vibrationData, this.vibrationUser);
         if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
            WardenAi.setDigCooldown(this);
         }
      }

      super.tick();
      if (this.level().isClientSide()) {
         if (this.tickCount % this.getHeartBeatDelay() == 0) {
            this.heartAnimation = 10;
            if (!this.isSilent()) {
               this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0F, this.getVoicePitch(), false);
            }
         }

         this.tendrilAnimationO = this.tendrilAnimation;
         if (this.tendrilAnimation > 0) {
            --this.tendrilAnimation;
         }

         this.heartAnimationO = this.heartAnimation;
         if (this.heartAnimation > 0) {
            --this.heartAnimation;
         }

         switch (this.getPose()) {
            case EMERGING -> this.clientDiggingParticles(this.emergeAnimationState);
            case DIGGING -> this.clientDiggingParticles(this.diggingAnimationState);
         }
      }

   }

   protected void customServerAiStep(final ServerLevel level) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("wardenBrain");
      this.getBrain().tick(level, this);
      profiler.pop();
      super.customServerAiStep(level);
      if ((this.tickCount + this.getId()) % 120 == 0) {
         applyDarknessAround(level, this.position(), this, 20);
      }

      if (this.tickCount % 20 == 0) {
         this.angerManagement.tick(level, this::canTargetEntity);
         this.syncClientAngerLevel();
      }

      WardenAi.updateActivity(this.getBrain());
   }

   public void handleEntityEvent(final @EntityEvent.Value byte id) {
      if (id == 4) {
         this.roarAnimationState.stop();
         this.attackAnimationState.start(this.tickCount);
      } else if (id == 61) {
         this.tendrilAnimation = 10;
      } else if (id == 62) {
         this.sonicBoomAnimationState.start(this.tickCount);
      } else {
         super.handleEntityEvent(id);
      }

   }

   private int getHeartBeatDelay() {
      float anger = (float)this.getClientAngerLevel() / (float)AngerLevel.ANGRY.getMinimumAnger();
      return 40 - Mth.floor(Mth.clamp(anger, 0.0F, 1.0F) * 30.0F);
   }

   public float getTendrilAnimation(final float a) {
      return Mth.lerp(a, (float)this.tendrilAnimationO, (float)this.tendrilAnimation) / 10.0F;
   }

   public float getHeartAnimation(final float a) {
      return Mth.lerp(a, (float)this.heartAnimationO, (float)this.heartAnimation) / 10.0F;
   }

   private void clientDiggingParticles(final AnimationState state) {
      if ((float)state.getTimeInMillis((float)this.tickCount) < 4500.0F) {
         RandomSource random = this.getRandom();
         BlockState stateBelow = this.getBlockStateOn();
         if (stateBelow.getRenderShape() != RenderShape.INVISIBLE) {
            for(int i = 0; i < 30; ++i) {
               double xx = this.getX() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
               double yy = this.getY();
               double zz = this.getZ() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
               this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateBelow), xx, yy, zz, 0.0, 0.0, 0.0);
            }
         }
      }

   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (DATA_POSE.equals(accessor)) {
         switch (this.getPose()) {
            case EMERGING -> this.emergeAnimationState.start(this.tickCount);
            case DIGGING -> this.diggingAnimationState.start(this.tickCount);
            case ROARING -> this.roarAnimationState.start(this.tickCount);
            case SNIFFING -> this.sniffAnimationState.start(this.tickCount);
         }
      }

      super.onSyncedDataUpdated(accessor);
   }

   public boolean ignoreExplosion(final Explosion explosion) {
      return this.isDiggingOrEmerging();
   }

   protected Brain<Warden> makeBrain(final Brain.Packed packedBrain) {
      return BRAIN_PROVIDER.makeBrain(this, packedBrain);
   }

   public Brain<Warden> getBrain() {
      return super.getBrain();
   }

   public void updateDynamicGameEventListener(final BiConsumer<DynamicGameEventListener<?>, ServerLevel> action) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         action.accept(this.dynamicGameEventListener, serverLevel);
      }

   }

   public boolean canAttack(final LivingEntity target) {
      return this.canTargetEntity(target);
   }

   @Contract("null->false")
   public boolean canTargetEntity(final @Nullable Entity entity) {
      boolean var10000;
      if (entity instanceof LivingEntity livingEntity) {
         if (this.level() == entity.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && !livingEntity.is(EntityTypes.ARMOR_STAND) && !livingEntity.is(EntityTypes.WARDEN) && !livingEntity.isInvulnerable() && !livingEntity.isDeadOrDying() && this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public static void applyDarknessAround(final ServerLevel level, final Vec3 position, final @Nullable Entity source, final int darknessRadius) {
      MobEffectInstance darkness = new MobEffectInstance(MobEffects.DARKNESS, 260, 0, false, false);
      MobEffectUtil.addEffectToPlayersAround(level, source, position, (double)darknessRadius, darkness, 200);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("anger", AngerManagement.codec(this::canTargetEntity), this.angerManagement);
      output.store("listener", VibrationSystem.Data.CODEC, this.vibrationData);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.angerManagement = (AngerManagement)input.read("anger", AngerManagement.codec(this::canTargetEntity)).orElseGet(() -> new AngerManagement(this::canTargetEntity, Collections.emptyList()));
      this.syncClientAngerLevel();
      this.vibrationData = (VibrationSystem.Data)input.read("listener", VibrationSystem.Data.CODEC).orElseGet(VibrationSystem.Data::new);
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

   public void clearAnger(final Entity entity) {
      this.angerManagement.clearAnger(entity);
   }

   public void increaseAngerAt(final @Nullable Entity entity) {
      this.increaseAngerAt(entity, 35, true);
   }

   @VisibleForTesting
   public void increaseAngerAt(final @Nullable Entity entity, final int amount, final boolean playSound) {
      if (!this.isNoAi() && this.canTargetEntity(entity)) {
         WardenAi.setDigCooldown(this);
         boolean maybeSwitchTarget = !(this.getTarget() instanceof Player);
         int newAnger = this.angerManagement.increaseAnger(entity, amount);
         if (entity instanceof Player && maybeSwitchTarget && AngerLevel.byAnger(newAnger).isAngry()) {
            this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         }

         if (playSound) {
            this.playListeningSound();
         }
      }

   }

   public Optional<LivingEntity> getEntityAngryAt() {
      return this.getAngerLevel().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
   }

   public @Nullable LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   public boolean removeWhenFarAway(final double distSqr) {
      return false;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
      if (spawnReason == EntitySpawnReason.TRIGGERED) {
         this.setPose(Pose.EMERGING);
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long)WardenAi.EMERGE_DURATION);
         this.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
      }

      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      boolean wasHurt = super.hurtServer(level, source, damage);
      if (!this.isNoAi() && !this.isDiggingOrEmerging()) {
         Entity attacker = source.getEntity();
         this.increaseAngerAt(attacker, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
         if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && attacker instanceof LivingEntity) {
            LivingEntity livingAttacker = (LivingEntity)attacker;
            if (source.isDirect() || this.closerThan(livingAttacker, 5.0)) {
               this.setAttackTarget(livingAttacker);
            }
         }
      }

      return wasHurt;
   }

   public void setAttackTarget(final LivingEntity target) {
      this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
      this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
      this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      SonicBoom.setCooldown(this, 200);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pose);
      return this.isDiggingOrEmerging() ? EntityDimensions.fixed(dimensions.width(), 1.0F) : dimensions;
   }

   public boolean isPushable() {
      return !this.isDiggingOrEmerging() && super.isPushable();
   }

   protected void doPush(final Entity entity) {
      if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
         this.increaseAngerAt(entity);
         WardenAi.setDisturbanceLocation(this, entity.blockPosition());
      }

      super.doPush(entity);
   }

   @VisibleForTesting
   public AngerManagement getAngerManagement() {
      return this.angerManagement;
   }

   protected PathNavigation createNavigation(final Level level) {
      return new GroundPathNavigation(this, level) {
         {
            Objects.requireNonNull(Warden.this);
         }

         protected PathFinder createPathFinder(final int maxVisitedNodes) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes) {
               {
                  Objects.requireNonNull(<VAR_NAMELESS_ENCLOSURE>);
               }

               protected float distance(final Node from, final Node to) {
                  return from.distanceToXZ(to);
               }
            };
         }
      };
   }

   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   static {
      CLIENT_ANGER_LEVEL = SynchedEntityData.<Integer>defineId(Warden.class, EntityDataSerializers.INT);
      BRAIN_PROVIDER = Brain.<Warden>provider(List.of(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.RECENT_PROJECTILE, MemoryModuleType.TOUCH_COOLDOWN, MemoryModuleType.VIBRATION_COOLDOWN), List.of(SensorType.NEAREST_PLAYERS, SensorType.WARDEN_ENTITY_SENSOR), WardenAi::getActivities);
   }

   private class VibrationUser implements VibrationSystem.User {
      private static final int GAME_EVENT_LISTENER_RANGE = 16;
      private final PositionSource positionSource;

      private VibrationUser() {
         Objects.requireNonNull(Warden.this);
         super();
         this.positionSource = new EntityPositionSource(Warden.this, Warden.this.getEyeHeight());
      }

      public int getListenerRadius() {
         return 16;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.WARDEN_CAN_LISTEN;
      }

      public boolean canTriggerAvoidVibration() {
         return true;
      }

      public boolean canReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final GameEvent.Context context) {
         if (!Warden.this.isNoAi() && !Warden.this.isDeadOrDying() && !Warden.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && !Warden.this.isDiggingOrEmerging() && level.getWorldBorder().isWithinBounds(pos)) {
            Entity var6 = context.sourceEntity();
            boolean var10000;
            if (var6 instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)var6;
               if (!Warden.this.canTargetEntity(livingEntity)) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         } else {
            return false;
         }
      }

      public void onReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final @Nullable Entity sourceEntity, final @Nullable Entity projectileOwner, final float receivingDistance) {
         if (!Warden.this.isDeadOrDying()) {
            Warden.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
            level.broadcastEntityEvent(Warden.this, (byte)61);
            Warden.this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, Warden.this.getVoicePitch());
            BlockPos suspiciousPos = pos;
            if (projectileOwner != null) {
               if (Warden.this.closerThan(projectileOwner, 30.0)) {
                  if (Warden.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                     if (Warden.this.canTargetEntity(projectileOwner)) {
                        suspiciousPos = projectileOwner.blockPosition();
                     }

                     Warden.this.increaseAngerAt(projectileOwner);
                  } else {
                     Warden.this.increaseAngerAt(projectileOwner, 10, true);
                  }
               }

               Warden.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
            } else {
               Warden.this.increaseAngerAt(sourceEntity);
            }

            if (!Warden.this.getAngerLevel().isAngry()) {
               Optional<LivingEntity> activeEntity = Warden.this.angerManagement.getActiveEntity();
               if (projectileOwner != null || activeEntity.isEmpty() || activeEntity.get() == sourceEntity) {
                  WardenAi.setDisturbanceLocation(Warden.this, suspiciousPos);
               }
            }

         }
      }
   }
}
