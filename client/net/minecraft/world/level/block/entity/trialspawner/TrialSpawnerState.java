package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

public enum TrialSpawnerState implements StringRepresentable {
   INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmission.NONE, -1.0, false),
   WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, 200.0, true),
   ACTIVE("active", 8, TrialSpawnerState.ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true),
   WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
   EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
   COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);

   private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
   private static final int TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0F);
   private final String name;
   private final int lightLevel;
   private final double spinningMobSpeed;
   private final ParticleEmission particleEmission;
   private final boolean isCapableOfSpawning;

   private TrialSpawnerState(final String name, final int lightLevel, final ParticleEmission particleEmission, final double spinningMobSpeed, final boolean isCapableOfSpawning) {
      this.name = name;
      this.lightLevel = lightLevel;
      this.particleEmission = particleEmission;
      this.spinningMobSpeed = spinningMobSpeed;
      this.isCapableOfSpawning = isCapableOfSpawning;
   }

   TrialSpawnerState tickAndGetNext(final BlockPos spawnerPos, final TrialSpawner trialSpawner, final ServerLevel serverLevel) {
      TrialSpawnerStateData data = trialSpawner.getStateData();
      TrialSpawnerConfig config = trialSpawner.activeConfig();
      RandomSource random = serverLevel.getRandom();
      TrialSpawnerState var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = data.getOrCreateDisplayEntity(trialSpawner, serverLevel, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
            break;
         case 1:
            if (!trialSpawner.canSpawnInLevel(serverLevel)) {
               data.resetStatistics();
               var10000 = this;
            } else if (!data.hasMobToSpawn(trialSpawner, random)) {
               var10000 = INACTIVE;
            } else {
               data.tryDetectPlayers(serverLevel, spawnerPos, trialSpawner);
               var10000 = data.detectedPlayers.isEmpty() ? this : ACTIVE;
            }
            break;
         case 2:
            if (!trialSpawner.canSpawnInLevel(serverLevel)) {
               data.resetStatistics();
               var10000 = WAITING_FOR_PLAYERS;
            } else if (!data.hasMobToSpawn(trialSpawner, random)) {
               var10000 = INACTIVE;
            } else {
               int additionalPlayers = data.countAdditionalPlayers(spawnerPos);
               data.tryDetectPlayers(serverLevel, spawnerPos, trialSpawner);
               if (trialSpawner.isOminous()) {
                  this.spawnOminousOminousItemSpawner(serverLevel, spawnerPos, trialSpawner);
               }

               if (data.hasFinishedSpawningAllMobs(config, additionalPlayers)) {
                  if (data.haveAllCurrentMobsDied()) {
                     data.cooldownEndsAt = serverLevel.getGameTime() + (long)trialSpawner.getTargetCooldownLength();
                     data.totalMobsSpawned = 0;
                     data.nextMobSpawnsAt = 0L;
                     var10000 = WAITING_FOR_REWARD_EJECTION;
                     break;
                  }
               } else if (data.isReadyToSpawnNextMob(serverLevel, config, additionalPlayers)) {
                  trialSpawner.spawnMob(serverLevel, spawnerPos).ifPresent((entityId) -> {
                     data.currentMobs.add(entityId);
                     ++data.totalMobsSpawned;
                     data.nextMobSpawnsAt = serverLevel.getGameTime() + (long)config.ticksBetweenSpawn();
                     config.spawnPotentialsDefinition().getRandom(random).ifPresent((entry) -> {
                        data.nextSpawnData = Optional.of(entry);
                        trialSpawner.markUpdated();
                     });
                  });
               }

               var10000 = this;
            }
            break;
         case 3:
            if (data.isReadyToOpenShutter(serverLevel, 40.0F, trialSpawner.getTargetCooldownLength())) {
               serverLevel.playSound((Entity)null, spawnerPos, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
               var10000 = EJECTING_REWARD;
            } else {
               var10000 = this;
            }
            break;
         case 4:
            if (!data.isReadyToEjectItems(serverLevel, (float)TIME_BETWEEN_EACH_EJECTION, trialSpawner.getTargetCooldownLength())) {
               var10000 = this;
            } else if (data.detectedPlayers.isEmpty()) {
               serverLevel.playSound((Entity)null, spawnerPos, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS);
               data.ejectingLootTable = Optional.empty();
               var10000 = COOLDOWN;
            } else {
               if (data.ejectingLootTable.isEmpty()) {
                  data.ejectingLootTable = config.lootTablesToEject().getRandom(random);
               }

               data.ejectingLootTable.ifPresent((lootTable) -> trialSpawner.ejectReward(serverLevel, spawnerPos, lootTable));
               data.detectedPlayers.remove(data.detectedPlayers.iterator().next());
               var10000 = this;
            }
            break;
         case 5:
            data.tryDetectPlayers(serverLevel, spawnerPos, trialSpawner);
            if (!data.detectedPlayers.isEmpty()) {
               data.totalMobsSpawned = 0;
               data.nextMobSpawnsAt = 0L;
               var10000 = ACTIVE;
            } else if (data.isCooldownFinished(serverLevel)) {
               trialSpawner.removeOminous(serverLevel, spawnerPos);
               data.reset();
               var10000 = WAITING_FOR_PLAYERS;
            } else {
               var10000 = this;
            }
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void spawnOminousOminousItemSpawner(final ServerLevel level, final BlockPos trialSpawnerPos, final TrialSpawner trialSpawner) {
      TrialSpawnerStateData data = trialSpawner.getStateData();
      TrialSpawnerConfig config = trialSpawner.activeConfig();
      ItemStack itemToDispense = (ItemStack)data.getDispensingItems(level, config, trialSpawnerPos).getRandom(level.getRandom()).orElse(ItemStack.EMPTY);
      if (!itemToDispense.isEmpty()) {
         if (this.timeToSpawnItemSpawner(level, data)) {
            calculatePositionToSpawnSpawner(level, trialSpawnerPos, trialSpawner, data).ifPresent((pos) -> {
               OminousItemSpawner itemSpawner = OminousItemSpawner.create(level, itemToDispense);
               itemSpawner.snapTo(pos);
               level.addFreshEntity(itemSpawner);
               float pitch = (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F;
               level.playSound((Entity)null, BlockPos.containing(pos), SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundSource.BLOCKS, 1.0F, pitch);
               data.cooldownEndsAt = level.getGameTime() + trialSpawner.ominousConfig().ticksBetweenItemSpawners();
            });
         }

      }
   }

   private static Optional<Vec3> calculatePositionToSpawnSpawner(final ServerLevel level, final BlockPos trialSpawnerPos, final TrialSpawner trialSpawner, final TrialSpawnerStateData data) {
      Stream var10000 = data.detectedPlayers.stream();
      Objects.requireNonNull(level);
      List<Player> nearbyPlayers = var10000.map(level::getPlayerByUUID).filter(Objects::nonNull).filter((player) -> !player.isCreative() && !player.isSpectator() && player.isAlive() && player.distanceToSqr(Vec3.atCenterOf(trialSpawnerPos)) <= (double)Mth.square(trialSpawner.getRequiredPlayerRange())).toList();
      if (nearbyPlayers.isEmpty()) {
         return Optional.empty();
      } else {
         Entity entity = selectEntityToSpawnItemAbove(nearbyPlayers, data.currentMobs, trialSpawner, trialSpawnerPos, level);
         return entity == null ? Optional.empty() : calculatePositionAbove(entity, level);
      }
   }

   private static Optional<Vec3> calculatePositionAbove(final Entity entityToSpawnItemAbove, final ServerLevel level) {
      Vec3 entityPos = entityToSpawnItemAbove.position();
      Vec3 trySpawnPos = entityPos.relative(Direction.UP, (double)(entityToSpawnItemAbove.getBbHeight() + 2.0F + (float)level.getRandom().nextInt(4)));
      BlockHitResult hitResult = level.clip(new ClipContext(entityPos, trySpawnPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      Vec3 down = Vec3.atCenterOf(hitResult.getBlockPos()).relative(Direction.DOWN, 1.0);
      BlockPos blockPosDown = BlockPos.containing(down);
      return !level.getBlockState(blockPosDown).getCollisionShape(level, blockPosDown).isEmpty() ? Optional.empty() : Optional.of(down);
   }

   private static @Nullable Entity selectEntityToSpawnItemAbove(final List<Player> nearbyPlayers, final Set<UUID> mobIds, final TrialSpawner trialSpawner, final BlockPos spawnerPos, final ServerLevel level) {
      Stream var10000 = mobIds.stream();
      Objects.requireNonNull(level);
      Stream<Entity> nearbyMobs = var10000.map(level::getEntity).filter(Objects::nonNull).filter((target) -> target.isAlive() && target.distanceToSqr(Vec3.atCenterOf(spawnerPos)) <= (double)Mth.square(trialSpawner.getRequiredPlayerRange()));
      RandomSource random = level.getRandom();
      List<? extends Entity> eligibleEntities = random.nextBoolean() ? nearbyMobs.toList() : nearbyPlayers;
      if (eligibleEntities.isEmpty()) {
         return null;
      } else {
         return eligibleEntities.size() == 1 ? (Entity)eligibleEntities.getFirst() : (Entity)Util.getRandom(eligibleEntities, random);
      }
   }

   private boolean timeToSpawnItemSpawner(final ServerLevel serverLevel, final TrialSpawnerStateData data) {
      return serverLevel.getGameTime() >= data.cooldownEndsAt;
   }

   public int lightLevel() {
      return this.lightLevel;
   }

   public double spinningMobSpeed() {
      return this.spinningMobSpeed;
   }

   public boolean hasSpinningMob() {
      return this.spinningMobSpeed >= 0.0;
   }

   public boolean isCapableOfSpawning() {
      return this.isCapableOfSpawning;
   }

   public void emitParticles(final Level level, final BlockPos blockPos, final boolean isOminous) {
      this.particleEmission.emit(level, level.getRandom(), blockPos, isOminous);
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static TrialSpawnerState[] $values() {
      return new TrialSpawnerState[]{INACTIVE, WAITING_FOR_PLAYERS, ACTIVE, WAITING_FOR_REWARD_EJECTION, EJECTING_REWARD, COOLDOWN};
   }

   private static class LightLevel {
      private static final int UNLIT = 0;
      private static final int HALF_LIT = 4;
      private static final int LIT = 8;

      private LightLevel() {
         super();
      }
   }

   private static class SpinningMob {
      private static final double NONE = -1.0;
      private static final double SLOW = 200.0;
      private static final double FAST = 1000.0;

      private SpinningMob() {
         super();
      }
   }

   private interface ParticleEmission {
      ParticleEmission NONE = (level, random, pos, isOminous) -> {
      };
      ParticleEmission SMALL_FLAMES = (level, random, pos, isOminous) -> {
         if (random.nextInt(2) == 0) {
            Vec3 vec = Vec3.atCenterOf(pos).offsetRandom(random, 0.9F);
            addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec, level);
         }

      };
      ParticleEmission FLAMES_AND_SMOKE = (level, random, pos, isOminous) -> {
         Vec3 vec = Vec3.atCenterOf(pos).offsetRandom(random, 1.0F);
         addParticle(ParticleTypes.SMOKE, vec, level);
         addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec, level);
      };
      ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (level, random, pos, isOminous) -> {
         Vec3 vec = Vec3.atCenterOf(pos).offsetRandom(random, 0.9F);
         if (random.nextInt(3) == 0) {
            addParticle(ParticleTypes.SMOKE, vec, level);
         }

         if (level.getGameTime() % 20L == 0L) {
            Vec3 topFaceVec = Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0);
            int smokeCount = level.getRandom().nextInt(4) + 20;

            for(int i = 0; i < smokeCount; ++i) {
               addParticle(ParticleTypes.SMOKE, topFaceVec, level);
            }
         }

      };

      private static void addParticle(final SimpleParticleType smoke, final Vec3 vec, final Level level) {
         level.addParticle(smoke, vec.x(), vec.y(), vec.z(), 0.0, 0.0, 0.0);
      }

      void emit(final Level level, final RandomSource random, final BlockPos blockPos, final boolean isOminous);
   }
}
