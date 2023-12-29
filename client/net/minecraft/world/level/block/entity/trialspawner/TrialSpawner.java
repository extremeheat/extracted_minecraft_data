package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class TrialSpawner {
   public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
   private static final int MAX_MOB_TRACKING_DISTANCE = 47;
   private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
   private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
   private final TrialSpawnerConfig config;
   private final TrialSpawnerData data;
   private final TrialSpawner.StateAccessor stateAccessor;
   private PlayerDetector playerDetector;
   private boolean overridePeacefulAndMobSpawnRule;

   public Codec<TrialSpawner> codec() {
      return RecordCodecBuilder.create(
         var1 -> var1.group(TrialSpawnerConfig.MAP_CODEC.forGetter(TrialSpawner::getConfig), TrialSpawnerData.MAP_CODEC.forGetter(TrialSpawner::getData))
               .apply(var1, (var1x, var2) -> new TrialSpawner(var1x, var2, this.stateAccessor, this.playerDetector))
      );
   }

   public TrialSpawner(TrialSpawner.StateAccessor var1, PlayerDetector var2) {
      this(TrialSpawnerConfig.DEFAULT, new TrialSpawnerData(), var1, var2);
   }

   public TrialSpawner(TrialSpawnerConfig var1, TrialSpawnerData var2, TrialSpawner.StateAccessor var3, PlayerDetector var4) {
      super();
      this.config = var1;
      this.data = var2;
      this.data.setSpawnPotentialsFromConfig(var1);
      this.stateAccessor = var3;
      this.playerDetector = var4;
   }

   public TrialSpawnerConfig getConfig() {
      return this.config;
   }

   public TrialSpawnerData getData() {
      return this.data;
   }

   public TrialSpawnerState getState() {
      return this.stateAccessor.getState();
   }

   public void setState(Level var1, TrialSpawnerState var2) {
      this.stateAccessor.setState(var1, var2);
   }

   public void markUpdated() {
      this.stateAccessor.markUpdated();
   }

   public PlayerDetector getPlayerDetector() {
      return this.playerDetector;
   }

   public boolean canSpawnInLevel(Level var1) {
      if (this.overridePeacefulAndMobSpawnRule) {
         return true;
      } else {
         return var1.getDifficulty() == Difficulty.PEACEFUL ? false : var1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Optional<UUID> spawnMob(ServerLevel var1, BlockPos var2) {
      RandomSource var3 = var1.getRandom();
      SpawnData var4 = this.data.getOrCreateNextSpawnData(this, var1.getRandom());
      CompoundTag var5 = var4.entityToSpawn();
      ListTag var6 = var5.getList("Pos", 6);
      Optional var7 = EntityType.by(var5);
      if (var7.isEmpty()) {
         return Optional.empty();
      } else {
         int var8 = var6.size();
         double var9 = var8 >= 1 ? var6.getDouble(0) : (double)var2.getX() + (var3.nextDouble() - var3.nextDouble()) * (double)this.config.spawnRange() + 0.5;
         double var11 = var8 >= 2 ? var6.getDouble(1) : (double)(var2.getY() + var3.nextInt(3) - 1);
         double var13 = var8 >= 3 ? var6.getDouble(2) : (double)var2.getZ() + (var3.nextDouble() - var3.nextDouble()) * (double)this.config.spawnRange() + 0.5;
         if (!var1.noCollision(((EntityType)var7.get()).getAABB(var9, var11, var13))) {
            return Optional.empty();
         } else {
            Vec3 var15 = new Vec3(var9, var11, var13);
            if (!inLineOfSight(var1, var2.getCenter(), var15)) {
               return Optional.empty();
            } else {
               BlockPos var16 = BlockPos.containing(var15);
               if (!SpawnPlacements.checkSpawnRules((EntityType)var7.get(), var1, MobSpawnType.TRIAL_SPAWNER, var16, var1.getRandom())) {
                  return Optional.empty();
               } else {
                  Entity var17 = EntityType.loadEntityRecursive(var5, var1, var7x -> {
                     var7x.moveTo(var9, var11, var13, var3.nextFloat() * 360.0F, 0.0F);
                     return var7x;
                  });
                  if (var17 == null) {
                     return Optional.empty();
                  } else {
                     if (var17 instanceof Mob var18) {
                        if (!var18.checkSpawnObstruction(var1)) {
                           return Optional.empty();
                        }

                        if (var4.getEntityToSpawn().size() == 1 && var4.getEntityToSpawn().contains("id", 8)) {
                           var18.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var18.blockPosition()), MobSpawnType.TRIAL_SPAWNER, null, null);
                           var18.setPersistenceRequired();
                        }
                     }

                     if (!var1.tryAddFreshEntityWithPassengers(var17)) {
                        return Optional.empty();
                     } else {
                        var1.levelEvent(3011, var2, 0);
                        var1.levelEvent(3012, var16, 0);
                        var1.gameEvent(var17, GameEvent.ENTITY_PLACE, var16);
                        return Optional.of(var17.getUUID());
                     }
                  }
               }
            }
         }
      }
   }

   public void ejectReward(ServerLevel var1, BlockPos var2, ResourceLocation var3) {
      LootTable var4 = var1.getServer().getLootData().getLootTable(var3);
      LootParams var5 = new LootParams.Builder(var1).create(LootContextParamSets.EMPTY);
      ObjectArrayList var6 = var4.getRandomItems(var5);
      if (!var6.isEmpty()) {
         ObjectListIterator var7 = var6.iterator();

         while(var7.hasNext()) {
            ItemStack var8 = (ItemStack)var7.next();
            DefaultDispenseItemBehavior.spawnItem(var1, var8, 2, Direction.UP, Vec3.atBottomCenterOf(var2).relative(Direction.UP, 1.2));
         }

         var1.levelEvent(3014, var2, 0);
      }
   }

   public void tickClient(Level var1, BlockPos var2) {
      if (!this.canSpawnInLevel(var1)) {
         this.data.oSpin = this.data.spin;
      } else {
         TrialSpawnerState var3 = this.getState();
         var3.emitParticles(var1, var2);
         if (var3.hasSpinningMob()) {
            double var4 = (double)Math.max(0L, this.data.nextMobSpawnsAt - var1.getGameTime());
            this.data.oSpin = this.data.spin;
            this.data.spin = (this.data.spin + var3.spinningMobSpeed() / (var4 + 200.0)) % 360.0;
         }

         if (var3.isCapableOfSpawning()) {
            RandomSource var6 = var1.getRandom();
            if (var6.nextFloat() <= 0.02F) {
               var1.playLocalSound(
                  var2, SoundEvents.TRIAL_SPAWNER_AMBIENT, SoundSource.BLOCKS, var6.nextFloat() * 0.25F + 0.75F, var6.nextFloat() + 0.5F, false
               );
            }
         }
      }
   }

   public void tickServer(ServerLevel var1, BlockPos var2) {
      TrialSpawnerState var3 = this.getState();
      if (!this.canSpawnInLevel(var1)) {
         if (var3.isCapableOfSpawning()) {
            this.data.reset();
            this.setState(var1, TrialSpawnerState.INACTIVE);
         }
      } else {
         if (this.data.currentMobs.removeIf(var2x -> shouldMobBeUntracked(var1, var2, var2x))) {
            this.data.nextMobSpawnsAt = var1.getGameTime() + (long)this.config.ticksBetweenSpawn();
         }

         TrialSpawnerState var4 = var3.tickAndGetNext(var2, this, var1);
         if (var4 != var3) {
            this.setState(var1, var4);
         }
      }
   }

   private static boolean shouldMobBeUntracked(ServerLevel var0, BlockPos var1, UUID var2) {
      Entity var3 = var0.getEntity(var2);
      return var3 == null
         || !var3.isAlive()
         || !var3.level().dimension().equals(var0.dimension())
         || var3.blockPosition().distSqr(var1) > (double)MAX_MOB_TRACKING_DISTANCE_SQR;
   }

   private static boolean inLineOfSight(Level var0, Vec3 var1, Vec3 var2) {
      BlockHitResult var3 = var0.clip(new ClipContext(var2, var1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      return var3.getBlockPos().equals(BlockPos.containing(var1)) || var3.getType() == HitResult.Type.MISS;
   }

   public static void addSpawnParticles(Level var0, BlockPos var1, RandomSource var2) {
      for(int var3 = 0; var3 < 20; ++var3) {
         double var4 = (double)var1.getX() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var6 = (double)var1.getY() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var8 = (double)var1.getZ() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         var0.addParticle(ParticleTypes.SMOKE, var4, var6, var8, 0.0, 0.0, 0.0);
         var0.addParticle(ParticleTypes.FLAME, var4, var6, var8, 0.0, 0.0, 0.0);
      }
   }

   public static void addDetectPlayerParticles(Level var0, BlockPos var1, RandomSource var2, int var3) {
      for(int var4 = 0; var4 < 30 + Math.min(var3, 10) * 5; ++var4) {
         double var5 = (double)(2.0F * var2.nextFloat() - 1.0F) * 0.65;
         double var7 = (double)(2.0F * var2.nextFloat() - 1.0F) * 0.65;
         double var9 = (double)var1.getX() + 0.5 + var5;
         double var11 = (double)var1.getY() + 0.1 + (double)var2.nextFloat() * 0.8;
         double var13 = (double)var1.getZ() + 0.5 + var7;
         var0.addParticle(ParticleTypes.TRIAL_SPAWNER_DETECTION, var9, var11, var13, 0.0, 0.0, 0.0);
      }
   }

   public static void addEjectItemParticles(Level var0, BlockPos var1, RandomSource var2) {
      for(int var3 = 0; var3 < 20; ++var3) {
         double var4 = (double)var1.getX() + 0.4 + var2.nextDouble() * 0.2;
         double var6 = (double)var1.getY() + 0.4 + var2.nextDouble() * 0.2;
         double var8 = (double)var1.getZ() + 0.4 + var2.nextDouble() * 0.2;
         double var10 = var2.nextGaussian() * 0.02;
         double var12 = var2.nextGaussian() * 0.02;
         double var14 = var2.nextGaussian() * 0.02;
         var0.addParticle(ParticleTypes.SMALL_FLAME, var4, var6, var8, var10, var12, var14 * 0.25);
         var0.addParticle(ParticleTypes.SMOKE, var4, var6, var8, var10, var12, var14);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void setPlayerDetector(PlayerDetector var1) {
      this.playerDetector = var1;
   }

   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void overridePeacefulAndMobSpawnRule() {
      this.overridePeacefulAndMobSpawnRule = true;
   }

   public interface StateAccessor {
      void setState(Level var1, TrialSpawnerState var2);

      TrialSpawnerState getState();

      void markUpdated();
   }
}
