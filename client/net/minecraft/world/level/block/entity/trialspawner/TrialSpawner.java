package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class TrialSpawner {
   public static final String NORMAL_CONFIG_TAG_NAME = "normal_config";
   public static final String OMINOUS_CONFIG_TAG_NAME = "ominous_config";
   public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
   private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
   private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
   private static final int MAX_MOB_TRACKING_DISTANCE = 47;
   private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
   private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
   private final TrialSpawnerConfig normalConfig;
   private final TrialSpawnerConfig ominousConfig;
   private final TrialSpawnerData data;
   private final int requiredPlayerRange;
   private final int targetCooldownLength;
   private final TrialSpawner.StateAccessor stateAccessor;
   private PlayerDetector playerDetector;
   private final PlayerDetector.EntitySelector entitySelector;
   private boolean overridePeacefulAndMobSpawnRule;
   private boolean isOminous;

   public Codec<TrialSpawner> codec() {
      return RecordCodecBuilder.create(
         var1 -> var1.group(
                  TrialSpawnerConfig.CODEC.optionalFieldOf("normal_config", TrialSpawnerConfig.DEFAULT).forGetter(TrialSpawner::getNormalConfig),
                  TrialSpawnerConfig.CODEC
                     .optionalFieldOf("ominous_config", TrialSpawnerConfig.DEFAULT)
                     .forGetter(TrialSpawner::getOminousConfigForSerialization),
                  TrialSpawnerData.MAP_CODEC.forGetter(TrialSpawner::getData),
                  Codec.intRange(0, 2147483647).optionalFieldOf("target_cooldown_length", 36000).forGetter(TrialSpawner::getTargetCooldownLength),
                  Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(TrialSpawner::getRequiredPlayerRange)
               )
               .apply(
                  var1,
                  (var1x, var2, var3, var4, var5) -> new TrialSpawner(
                        var1x, var2, var3, var4, var5, this.stateAccessor, this.playerDetector, this.entitySelector
                     )
               )
      );
   }

   public TrialSpawner(TrialSpawner.StateAccessor var1, PlayerDetector var2, PlayerDetector.EntitySelector var3) {
      this(TrialSpawnerConfig.DEFAULT, TrialSpawnerConfig.DEFAULT, new TrialSpawnerData(), 36000, 14, var1, var2, var3);
   }

   public TrialSpawner(
      TrialSpawnerConfig var1,
      TrialSpawnerConfig var2,
      TrialSpawnerData var3,
      int var4,
      int var5,
      TrialSpawner.StateAccessor var6,
      PlayerDetector var7,
      PlayerDetector.EntitySelector var8
   ) {
      super();
      this.normalConfig = var1;
      this.ominousConfig = var2;
      this.data = var3;
      this.targetCooldownLength = var4;
      this.requiredPlayerRange = var5;
      this.stateAccessor = var6;
      this.playerDetector = var7;
      this.entitySelector = var8;
   }

   public TrialSpawnerConfig getConfig() {
      return this.isOminous ? this.ominousConfig : this.normalConfig;
   }

   @VisibleForTesting
   public TrialSpawnerConfig getNormalConfig() {
      return this.normalConfig;
   }

   @VisibleForTesting
   public TrialSpawnerConfig getOminousConfig() {
      return this.ominousConfig;
   }

   private TrialSpawnerConfig getOminousConfigForSerialization() {
      return !this.ominousConfig.equals(this.normalConfig) ? this.ominousConfig : TrialSpawnerConfig.DEFAULT;
   }

   public void applyOminous(ServerLevel var1, BlockPos var2) {
      var1.setBlock(var2, var1.getBlockState(var2).setValue(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(true)), 3);
      var1.levelEvent(3020, var2, 1);
      this.isOminous = true;
      this.data.resetAfterBecomingOminous(this, var1);
   }

   public void removeOminous(ServerLevel var1, BlockPos var2) {
      var1.setBlock(var2, var1.getBlockState(var2).setValue(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(false)), 3);
      this.isOminous = false;
   }

   public boolean isOminous() {
      return this.isOminous;
   }

   public TrialSpawnerData getData() {
      return this.data;
   }

   public int getTargetCooldownLength() {
      return this.targetCooldownLength;
   }

   public int getRequiredPlayerRange() {
      return this.requiredPlayerRange;
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

   public PlayerDetector.EntitySelector getEntitySelector() {
      return this.entitySelector;
   }

   public boolean canSpawnInLevel(Level var1) {
      if (this.overridePeacefulAndMobSpawnRule) {
         return true;
      } else {
         return var1.getDifficulty() == Difficulty.PEACEFUL ? false : var1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      }
   }

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
         double var9 = var8 >= 1
            ? var6.getDouble(0)
            : (double)var2.getX() + (var3.nextDouble() - var3.nextDouble()) * (double)this.getConfig().spawnRange() + 0.5;
         double var11 = var8 >= 2 ? var6.getDouble(1) : (double)(var2.getY() + var3.nextInt(3) - 1);
         double var13 = var8 >= 3
            ? var6.getDouble(2)
            : (double)var2.getZ() + (var3.nextDouble() - var3.nextDouble()) * (double)this.getConfig().spawnRange() + 0.5;
         if (!var1.noCollision(((EntityType)var7.get()).getSpawnAABB(var9, var11, var13))) {
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
                  if (var4.getCustomSpawnRules().isPresent()) {
                     SpawnData.CustomSpawnRules var17 = (SpawnData.CustomSpawnRules)var4.getCustomSpawnRules().get();
                     if (!var17.isValidPosition(var16, var1)) {
                        return Optional.empty();
                     }
                  }

                  Entity var20 = EntityType.loadEntityRecursive(var5, var1, var7x -> {
                     var7x.moveTo(var9, var11, var13, var3.nextFloat() * 360.0F, 0.0F);
                     return var7x;
                  });
                  if (var20 == null) {
                     return Optional.empty();
                  } else {
                     if (var20 instanceof Mob var18) {
                        if (!((Mob)var18).checkSpawnObstruction(var1)) {
                           return Optional.empty();
                        }

                        boolean var19 = var4.getEntityToSpawn().size() == 1 && var4.getEntityToSpawn().contains("id", 8);
                        if (var19) {
                           ((Mob)var18).finalizeSpawn(var1, var1.getCurrentDifficultyAt(((Mob)var18).blockPosition()), MobSpawnType.TRIAL_SPAWNER, null);
                        }

                        ((Mob)var18).setPersistenceRequired();
                        var4.getEquipmentLootTable().ifPresent(var18::equip);
                     }

                     if (!var1.tryAddFreshEntityWithPassengers(var20)) {
                        return Optional.empty();
                     } else {
                        TrialSpawner.FlameParticle var21 = this.isOminous ? TrialSpawner.FlameParticle.OMINOUS : TrialSpawner.FlameParticle.NORMAL;
                        var1.levelEvent(3011, var2, var21.encode());
                        var1.levelEvent(3012, var16, var21.encode());
                        var1.gameEvent(var20, GameEvent.ENTITY_PLACE, var16);
                        return Optional.of(var20.getUUID());
                     }
                  }
               }
            }
         }
      }
   }

   public void ejectReward(ServerLevel var1, BlockPos var2, ResourceKey<LootTable> var3) {
      LootTable var4 = var1.getServer().reloadableRegistries().getLootTable(var3);
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

   public void tickClient(Level var1, BlockPos var2, boolean var3) {
      if (!this.canSpawnInLevel(var1)) {
         this.data.oSpin = this.data.spin;
      } else {
         TrialSpawnerState var4 = this.getState();
         var4.emitParticles(var1, var2, var3);
         if (var4.hasSpinningMob()) {
            double var5 = (double)Math.max(0L, this.data.nextMobSpawnsAt - var1.getGameTime());
            this.data.oSpin = this.data.spin;
            this.data.spin = (this.data.spin + var4.spinningMobSpeed() / (var5 + 200.0)) % 360.0;
         }

         if (var4.isCapableOfSpawning()) {
            RandomSource var7 = var1.getRandom();
            if (var7.nextFloat() <= 0.02F) {
               SoundEvent var6 = var3 ? SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.TRIAL_SPAWNER_AMBIENT;
               var1.playLocalSound(var2, var6, SoundSource.BLOCKS, var7.nextFloat() * 0.25F + 0.75F, var7.nextFloat() + 0.5F, false);
            }
         }
      }
   }

   public void tickServer(ServerLevel var1, BlockPos var2, boolean var3) {
      this.isOminous = var3;
      TrialSpawnerState var4 = this.getState();
      if (!this.canSpawnInLevel(var1)) {
         if (var4.isCapableOfSpawning()) {
            this.data.reset();
            this.setState(var1, TrialSpawnerState.INACTIVE);
         }
      } else {
         if (this.data.currentMobs.removeIf(var2x -> shouldMobBeUntracked(var1, var2, var2x))) {
            this.data.nextMobSpawnsAt = var1.getGameTime() + (long)this.getConfig().ticksBetweenSpawn();
         }

         TrialSpawnerState var5 = var4.tickAndGetNext(var2, this, var1);
         if (var5 != var4) {
            this.setState(var1, var5);
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

   public static void addSpawnParticles(Level var0, BlockPos var1, RandomSource var2, SimpleParticleType var3) {
      for(int var4 = 0; var4 < 20; ++var4) {
         double var5 = (double)var1.getX() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var7 = (double)var1.getY() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var9 = (double)var1.getZ() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         var0.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0, 0.0, 0.0);
         var0.addParticle(var3, var5, var7, var9, 0.0, 0.0, 0.0);
      }
   }

   public static void addBecomeOminousParticles(Level var0, BlockPos var1, RandomSource var2) {
      for(int var3 = 0; var3 < 20; ++var3) {
         double var4 = (double)var1.getX() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var6 = (double)var1.getY() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var8 = (double)var1.getZ() + 0.5 + (var2.nextDouble() - 0.5) * 2.0;
         double var10 = var2.nextGaussian() * 0.02;
         double var12 = var2.nextGaussian() * 0.02;
         double var14 = var2.nextGaussian() * 0.02;
         var0.addParticle(ParticleTypes.TRIAL_OMEN, var4, var6, var8, var10, var12, var14);
         var0.addParticle(ParticleTypes.SOUL_FIRE_FLAME, var4, var6, var8, var10, var12, var14);
      }
   }

   public static void addDetectPlayerParticles(Level var0, BlockPos var1, RandomSource var2, int var3, ParticleOptions var4) {
      for(int var5 = 0; var5 < 30 + Math.min(var3, 10) * 5; ++var5) {
         double var6 = (double)(2.0F * var2.nextFloat() - 1.0F) * 0.65;
         double var8 = (double)(2.0F * var2.nextFloat() - 1.0F) * 0.65;
         double var10 = (double)var1.getX() + 0.5 + var6;
         double var12 = (double)var1.getY() + 0.1 + (double)var2.nextFloat() * 0.8;
         double var14 = (double)var1.getZ() + 0.5 + var8;
         var0.addParticle(var4, var10, var12, var14, 0.0, 0.0, 0.0);
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

   public static enum FlameParticle {
      NORMAL(ParticleTypes.FLAME),
      OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

      public final SimpleParticleType particleType;

      private FlameParticle(SimpleParticleType var3) {
         this.particleType = var3;
      }

      public static TrialSpawner.FlameParticle decode(int var0) {
         TrialSpawner.FlameParticle[] var1 = values();
         return var0 <= var1.length && var0 >= 0 ? var1[var0] : NORMAL;
      }

      public int encode() {
         return this.ordinal();
      }
   }

   public interface StateAccessor {
      void setState(Level var1, TrialSpawnerState var2);

      TrialSpawnerState getState();

      void markUpdated();
   }
}
