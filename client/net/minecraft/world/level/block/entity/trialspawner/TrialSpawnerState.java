package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

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
   private final TrialSpawnerState.ParticleEmission particleEmission;
   private final boolean isCapableOfSpawning;

   private TrialSpawnerState(
      final String nullxx, final int nullxxx, final TrialSpawnerState.ParticleEmission nullxxxx, final double nullxxxxx, final boolean nullxxxxxx
   ) {
      this.name = nullxx;
      this.lightLevel = nullxxx;
      this.particleEmission = nullxxxx;
      this.spinningMobSpeed = nullxxxxx;
      this.isCapableOfSpawning = nullxxxxxx;
   }

   TrialSpawnerState tickAndGetNext(BlockPos var1, TrialSpawner var2, ServerLevel var3) {
      TrialSpawnerData var4 = var2.getData();
      TrialSpawnerConfig var5 = var2.getConfig();

      return switch (this) {
         case INACTIVE -> var4.getOrCreateDisplayEntity(var2, var3, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
         case WAITING_FOR_PLAYERS -> {
            if (!var2.canSpawnInLevel(var3)) {
               var4.resetStatistics();
               yield this;
            } else if (!var4.hasMobToSpawn(var2, var3.random)) {
               yield INACTIVE;
            } else {
               var4.tryDetectPlayers(var3, var1, var2);
               yield var4.detectedPlayers.isEmpty() ? this : ACTIVE;
            }
         }
         case ACTIVE -> {
            if (!var2.canSpawnInLevel(var3)) {
               var4.resetStatistics();
               yield WAITING_FOR_PLAYERS;
            } else if (!var4.hasMobToSpawn(var2, var3.random)) {
               yield INACTIVE;
            } else {
               int var6 = var4.countAdditionalPlayers(var1);
               var4.tryDetectPlayers(var3, var1, var2);
               if (var2.isOminous()) {
                  this.spawnOminousOminousItemSpawner(var3, var1, var2);
               }

               if (var4.hasFinishedSpawningAllMobs(var5, var6)) {
                  if (var4.haveAllCurrentMobsDied()) {
                     var4.cooldownEndsAt = var3.getGameTime() + (long)var2.getTargetCooldownLength();
                     var4.totalMobsSpawned = 0;
                     var4.nextMobSpawnsAt = 0L;
                     yield WAITING_FOR_REWARD_EJECTION;
                  }
               } else if (var4.isReadyToSpawnNextMob(var3, var5, var6)) {
                  var2.spawnMob(var3, var1).ifPresent(var4x -> {
                     var4.currentMobs.add(var4x);
                     var4.totalMobsSpawned++;
                     var4.nextMobSpawnsAt = var3.getGameTime() + (long)var5.ticksBetweenSpawn();
                     var5.spawnPotentialsDefinition().getRandom(var3.getRandom()).ifPresent(var2xx -> {
                        var4.nextSpawnData = Optional.of(var2xx.data());
                        var2.markUpdated();
                     });
                  });
               }

               yield this;
            }
         }
         case WAITING_FOR_REWARD_EJECTION -> {
            if (var4.isReadyToOpenShutter(var3, 40.0F, var2.getTargetCooldownLength())) {
               var3.playSound(null, var1, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
               yield EJECTING_REWARD;
            } else {
               yield this;
            }
         }
         case EJECTING_REWARD -> {
            if (!var4.isReadyToEjectItems(var3, (float)TIME_BETWEEN_EACH_EJECTION, var2.getTargetCooldownLength())) {
               yield this;
            } else if (var4.detectedPlayers.isEmpty()) {
               var3.playSound(null, var1, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS);
               var4.ejectingLootTable = Optional.empty();
               yield COOLDOWN;
            } else {
               if (var4.ejectingLootTable.isEmpty()) {
                  var4.ejectingLootTable = var5.lootTablesToEject().getRandomValue(var3.getRandom());
               }

               var4.ejectingLootTable.ifPresent(var3x -> var2.ejectReward(var3, var1, (ResourceKey<LootTable>)var3x));
               var4.detectedPlayers.remove(var4.detectedPlayers.iterator().next());
               yield this;
            }
         }
         case COOLDOWN -> {
            var4.tryDetectPlayers(var3, var1, var2);
            if (!var4.detectedPlayers.isEmpty()) {
               var4.totalMobsSpawned = 0;
               var4.nextMobSpawnsAt = 0L;
               yield ACTIVE;
            } else if (var4.isCooldownFinished(var3)) {
               var2.removeOminous(var3, var1);
               var4.reset();
               yield WAITING_FOR_PLAYERS;
            } else {
               yield this;
            }
         }
      };
   }

   private void spawnOminousOminousItemSpawner(ServerLevel var1, BlockPos var2, TrialSpawner var3) {
      TrialSpawnerData var4 = var3.getData();
      TrialSpawnerConfig var5 = var3.getConfig();
      ItemStack var6 = var4.getDispensingItems(var1, var5, var2).getRandomValue(var1.random).orElse(ItemStack.EMPTY);
      if (!var6.isEmpty()) {
         if (this.timeToSpawnItemSpawner(var1, var4)) {
            calculatePositionToSpawnSpawner(var1, var2, var3, var4).ifPresent(var4x -> {
               OminousItemSpawner var5x = OminousItemSpawner.create(var1, var6);
               var5x.moveTo(var4x);
               var1.addFreshEntity(var5x);
               float var6x = (var1.getRandom().nextFloat() - var1.getRandom().nextFloat()) * 0.2F + 1.0F;
               var1.playSound(null, BlockPos.containing(var4x), SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundSource.BLOCKS, 1.0F, var6x);
               var4.cooldownEndsAt = var1.getGameTime() + var3.getOminousConfig().ticksBetweenItemSpawners();
            });
         }
      }
   }

   private static Optional<Vec3> calculatePositionToSpawnSpawner(ServerLevel var0, BlockPos var1, TrialSpawner var2, TrialSpawnerData var3) {
      List var4 = var3.detectedPlayers
         .stream()
         .map(var0::getPlayerByUUID)
         .filter(Objects::nonNull)
         .filter(
            var2x -> !var2x.isCreative()
                  && !var2x.isSpectator()
                  && var2x.isAlive()
                  && var2x.distanceToSqr(var1.getCenter()) <= (double)Mth.square(var2.getRequiredPlayerRange())
         )
         .toList();
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         Entity var5 = selectEntityToSpawnItemAbove(var4, var3.currentMobs, var2, var1, var0);
         return var5 == null ? Optional.empty() : calculatePositionAbove(var5, var0);
      }
   }

   private static Optional<Vec3> calculatePositionAbove(Entity var0, ServerLevel var1) {
      Vec3 var2 = var0.position();
      Vec3 var3 = var2.relative(Direction.UP, (double)(var0.getBbHeight() + 2.0F + (float)var1.random.nextInt(4)));
      BlockHitResult var4 = var1.clip(new ClipContext(var2, var3, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      Vec3 var5 = var4.getBlockPos().getCenter().relative(Direction.DOWN, 1.0);
      BlockPos var6 = BlockPos.containing(var5);
      return !var1.getBlockState(var6).getCollisionShape(var1, var6).isEmpty() ? Optional.empty() : Optional.of(var5);
   }

   @Nullable
   private static Entity selectEntityToSpawnItemAbove(List<Player> var0, Set<UUID> var1, TrialSpawner var2, BlockPos var3, ServerLevel var4) {
      Stream var5 = var1.stream()
         .map(var4::getEntity)
         .filter(Objects::nonNull)
         .filter(var2x -> var2x.isAlive() && var2x.distanceToSqr(var3.getCenter()) <= (double)Mth.square(var2.getRequiredPlayerRange()));
      List var6 = var4.random.nextBoolean() ? var5.toList() : var0;
      if (var6.isEmpty()) {
         return null;
      } else {
         return var6.size() == 1 ? (Entity)var6.getFirst() : Util.getRandom(var6, var4.random);
      }
   }

   private boolean timeToSpawnItemSpawner(ServerLevel var1, TrialSpawnerData var2) {
      return var1.getGameTime() >= var2.cooldownEndsAt;
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

   public void emitParticles(Level var1, BlockPos var2, boolean var3) {
      this.particleEmission.emit(var1, var1.getRandom(), var2, var3);
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   static class LightLevel {
      private static final int UNLIT = 0;
      private static final int HALF_LIT = 4;
      private static final int LIT = 8;

      private LightLevel() {
         super();
      }
   }

   interface ParticleEmission {
      TrialSpawnerState.ParticleEmission NONE = (var0, var1, var2, var3) -> {
      };
      TrialSpawnerState.ParticleEmission SMALL_FLAMES = (var0, var1, var2, var3) -> {
         if (var1.nextInt(2) == 0) {
            Vec3 var4 = var2.getCenter().offsetRandom(var1, 0.9F);
            addParticle(var3 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, var4, var0);
         }
      };
      TrialSpawnerState.ParticleEmission FLAMES_AND_SMOKE = (var0, var1, var2, var3) -> {
         Vec3 var4 = var2.getCenter().offsetRandom(var1, 1.0F);
         addParticle(ParticleTypes.SMOKE, var4, var0);
         addParticle(var3 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, var4, var0);
      };
      TrialSpawnerState.ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (var0, var1, var2, var3) -> {
         Vec3 var4 = var2.getCenter().offsetRandom(var1, 0.9F);
         if (var1.nextInt(3) == 0) {
            addParticle(ParticleTypes.SMOKE, var4, var0);
         }

         if (var0.getGameTime() % 20L == 0L) {
            Vec3 var5 = var2.getCenter().add(0.0, 0.5, 0.0);
            int var6 = var0.getRandom().nextInt(4) + 20;

            for (int var7 = 0; var7 < var6; var7++) {
               addParticle(ParticleTypes.SMOKE, var5, var0);
            }
         }
      };

      private static void addParticle(SimpleParticleType var0, Vec3 var1, Level var2) {
         var2.addParticle(var0, var1.x(), var1.y(), var1.z(), 0.0, 0.0, 0.0);
      }

      void emit(Level var1, RandomSource var2, BlockPos var3, boolean var4);
   }

   static class SpinningMob {
      private static final double NONE = -1.0;
      private static final double SLOW = 200.0;
      private static final double FAST = 1000.0;

      private SpinningMob() {
         super();
      }
   }
}
