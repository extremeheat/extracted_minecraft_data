package net.minecraft.world.level.block.entity.trialspawner;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.phys.Vec3;

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

   private TrialSpawnerState(String var3, int var4, TrialSpawnerState.ParticleEmission var5, double var6, boolean var8) {
      this.name = var3;
      this.lightLevel = var4;
      this.particleEmission = var5;
      this.spinningMobSpeed = var6;
      this.isCapableOfSpawning = var8;
   }

   TrialSpawnerState tickAndGetNext(BlockPos var1, TrialSpawner var2, ServerLevel var3) {
      TrialSpawnerData var4 = var2.getData();
      TrialSpawnerConfig var5 = var2.getConfig();
      PlayerDetector var6 = var2.getPlayerDetector();
      PlayerDetector.EntitySelector var7 = var2.getEntitySelector();
      TrialSpawnerState var10000;
      switch(this) {
         case INACTIVE:
            var10000 = var4.getOrCreateDisplayEntity(var2, var3, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
            break;
         case WAITING_FOR_PLAYERS:
            if (!var4.hasMobToSpawn(var2, var3.random)) {
               var10000 = INACTIVE;
            } else {
               var4.tryDetectPlayers(var3, var1, var6, var7, var5.requiredPlayerRange());
               var10000 = var4.detectedPlayers.isEmpty() ? this : ACTIVE;
            }
            break;
         case ACTIVE:
            if (!var4.hasMobToSpawn(var2, var3.random)) {
               var10000 = INACTIVE;
            } else {
               int var8 = var4.countAdditionalPlayers(var1);
               var4.tryDetectPlayers(var3, var1, var6, var7, var5.requiredPlayerRange());
               if (var4.hasFinishedSpawningAllMobs(var5, var8)) {
                  if (var4.haveAllCurrentMobsDied()) {
                     var4.cooldownEndsAt = var3.getGameTime() + (long)var5.targetCooldownLength();
                     var4.totalMobsSpawned = 0;
                     var4.nextMobSpawnsAt = 0L;
                     var10000 = WAITING_FOR_REWARD_EJECTION;
                     break;
                  }
               } else if (var4.isReadyToSpawnNextMob(var3, var5, var8)) {
                  var2.spawnMob(var3, var1).ifPresent(var4x -> {
                     var4.currentMobs.add(var4x);
                     ++var4.totalMobsSpawned;
                     var4.nextMobSpawnsAt = var3.getGameTime() + (long)var5.ticksBetweenSpawn();
                     var4.spawnPotentials.getRandom(var3.getRandom()).ifPresent(var2xx -> {
                        var4.nextSpawnData = Optional.of((SpawnData)var2xx.getData());
                        var2.markUpdated();
                     });
                  });
               }

               var10000 = this;
            }
            break;
         case WAITING_FOR_REWARD_EJECTION:
            if (var4.isReadyToOpenShutter(var3, var5, 40.0F)) {
               var3.playSound(null, var1, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
               var10000 = EJECTING_REWARD;
            } else {
               var10000 = this;
            }
            break;
         case EJECTING_REWARD:
            if (!var4.isReadyToEjectItems(var3, var5, (float)TIME_BETWEEN_EACH_EJECTION)) {
               var10000 = this;
            } else if (var4.detectedPlayers.isEmpty()) {
               var3.playSound(null, var1, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS);
               var4.ejectingLootTable = Optional.empty();
               var10000 = COOLDOWN;
            } else {
               if (var4.ejectingLootTable.isEmpty()) {
                  var4.ejectingLootTable = var5.lootTablesToEject().getRandomValue(var3.getRandom());
               }

               var4.ejectingLootTable.ifPresent(var3x -> var2.ejectReward(var3, var1, var3x));
               var4.detectedPlayers.remove(var4.detectedPlayers.iterator().next());
               var10000 = this;
            }
            break;
         case COOLDOWN:
            if (var4.isCooldownFinished(var3)) {
               var4.cooldownEndsAt = 0L;
               var10000 = WAITING_FOR_PLAYERS;
            } else {
               var10000 = this;
            }
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
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

   public void emitParticles(Level var1, BlockPos var2) {
      this.particleEmission.emit(var1, var1.getRandom(), var2);
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
      TrialSpawnerState.ParticleEmission NONE = (var0, var1, var2) -> {
      };
      TrialSpawnerState.ParticleEmission SMALL_FLAMES = (var0, var1, var2) -> {
         if (var1.nextInt(2) == 0) {
            Vec3 var3 = var2.getCenter().offsetRandom(var1, 0.9F);
            addParticle(ParticleTypes.SMALL_FLAME, var3, var0);
         }
      };
      TrialSpawnerState.ParticleEmission FLAMES_AND_SMOKE = (var0, var1, var2) -> {
         Vec3 var3 = var2.getCenter().offsetRandom(var1, 1.0F);
         addParticle(ParticleTypes.SMOKE, var3, var0);
         addParticle(ParticleTypes.FLAME, var3, var0);
      };
      TrialSpawnerState.ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (var0, var1, var2) -> {
         Vec3 var3 = var2.getCenter().offsetRandom(var1, 0.9F);
         if (var1.nextInt(3) == 0) {
            addParticle(ParticleTypes.SMOKE, var3, var0);
         }

         if (var0.getGameTime() % 20L == 0L) {
            Vec3 var4 = var2.getCenter().add(0.0, 0.5, 0.0);
            int var5 = var0.getRandom().nextInt(4) + 20;

            for(int var6 = 0; var6 < var5; ++var6) {
               addParticle(ParticleTypes.SMOKE, var4, var0);
            }
         }
      };

      private static void addParticle(SimpleParticleType var0, Vec3 var1, Level var2) {
         var2.addParticle(var0, var1.x(), var1.y(), var1.z(), 0.0, 0.0, 0.0);
      }

      void emit(Level var1, RandomSource var2, BlockPos var3);
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
