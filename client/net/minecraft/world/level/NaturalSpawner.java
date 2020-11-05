package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.NearestNeighborBiomeZoomer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NaturalSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final MobCategory[] SPAWNING_CATEGORIES = (MobCategory[])Stream.of(MobCategory.values()).filter((var0) -> {
      return var0 != MobCategory.MISC;
   }).toArray((var0) -> {
      return new MobCategory[var0];
   });

   public static NaturalSpawner.SpawnState createState(int var0, Iterable<Entity> var1, NaturalSpawner.ChunkGetter var2) {
      PotentialCalculator var3 = new PotentialCalculator();
      Object2IntOpenHashMap var4 = new Object2IntOpenHashMap();
      Iterator var5 = var1.iterator();

      while(true) {
         Entity var6;
         Mob var7;
         do {
            if (!var5.hasNext()) {
               return new NaturalSpawner.SpawnState(var0, var4, var3);
            }

            var6 = (Entity)var5.next();
            if (!(var6 instanceof Mob)) {
               break;
            }

            var7 = (Mob)var6;
         } while(var7.isPersistenceRequired() || var7.requiresCustomPersistence());

         MobCategory var11 = var6.getType().getCategory();
         if (var11 != MobCategory.MISC) {
            BlockPos var8 = var6.blockPosition();
            long var9 = ChunkPos.asLong(var8.getX() >> 4, var8.getZ() >> 4);
            var2.query(var9, (var5x) -> {
               MobSpawnSettings.MobSpawnCost var6x = getRoughBiome(var8, var5x).getMobSettings().getMobSpawnCost(var6.getType());
               if (var6x != null) {
                  var3.addCharge(var6.blockPosition(), var6x.getCharge());
               }

               var4.addTo(var11, 1);
            });
         }
      }
   }

   private static Biome getRoughBiome(BlockPos var0, ChunkAccess var1) {
      return NearestNeighborBiomeZoomer.INSTANCE.getBiome(0L, var0.getX(), var0.getY(), var0.getZ(), var1.getBiomes());
   }

   public static void spawnForChunk(ServerLevel var0, LevelChunk var1, NaturalSpawner.SpawnState var2, boolean var3, boolean var4, boolean var5) {
      var0.getProfiler().push("spawner");
      MobCategory[] var6 = SPAWNING_CATEGORIES;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         MobCategory var9 = var6[var8];
         if ((var3 || !var9.isFriendly()) && (var4 || var9.isFriendly()) && (var5 || !var9.isPersistent()) && var2.canSpawnForCategory(var9)) {
            spawnCategoryForChunk(var9, var0, var1, (var1x, var2x, var3x) -> {
               return var2.canSpawn(var1x, var2x, var3x);
            }, (var1x, var2x) -> {
               var2.afterSpawn(var1x, var2x);
            });
         }
      }

      var0.getProfiler().pop();
   }

   public static void spawnCategoryForChunk(MobCategory var0, ServerLevel var1, LevelChunk var2, NaturalSpawner.SpawnPredicate var3, NaturalSpawner.AfterSpawnCallback var4) {
      BlockPos var5 = getRandomPosWithin(var1, var2);
      if (var5.getY() >= 1) {
         spawnCategoryForPosition(var0, var1, var2, var5, var3, var4);
      }
   }

   public static void spawnCategoryForPosition(MobCategory var0, ServerLevel var1, ChunkAccess var2, BlockPos var3, NaturalSpawner.SpawnPredicate var4, NaturalSpawner.AfterSpawnCallback var5) {
      StructureFeatureManager var6 = var1.structureFeatureManager();
      ChunkGenerator var7 = var1.getChunkSource().getGenerator();
      int var8 = var3.getY();
      BlockState var9 = var2.getBlockState(var3);
      if (!var9.isRedstoneConductor(var2, var3)) {
         BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
         int var11 = 0;

         for(int var12 = 0; var12 < 3; ++var12) {
            int var13 = var3.getX();
            int var14 = var3.getZ();
            boolean var15 = true;
            MobSpawnSettings.SpawnerData var16 = null;
            SpawnGroupData var17 = null;
            int var18 = Mth.ceil(var1.random.nextFloat() * 4.0F);
            int var19 = 0;

            for(int var20 = 0; var20 < var18; ++var20) {
               var13 += var1.random.nextInt(6) - var1.random.nextInt(6);
               var14 += var1.random.nextInt(6) - var1.random.nextInt(6);
               var10.set(var13, var8, var14);
               double var21 = (double)var13 + 0.5D;
               double var23 = (double)var14 + 0.5D;
               Player var25 = var1.getNearestPlayer(var21, (double)var8, var23, -1.0D, false);
               if (var25 != null) {
                  double var26 = var25.distanceToSqr(var21, (double)var8, var23);
                  if (isRightDistanceToPlayerAndSpawnPoint(var1, var2, var10, var26)) {
                     if (var16 == null) {
                        var16 = getRandomSpawnMobAt(var1, var6, var7, var0, var1.random, var10);
                        if (var16 == null) {
                           break;
                        }

                        var18 = var16.minCount + var1.random.nextInt(1 + var16.maxCount - var16.minCount);
                     }

                     if (isValidSpawnPostitionForType(var1, var0, var6, var7, var16, var10, var26) && var4.test(var16.type, var10, var2)) {
                        Mob var28 = getMobForSpawn(var1, var16.type);
                        if (var28 == null) {
                           return;
                        }

                        var28.moveTo(var21, (double)var8, var23, var1.random.nextFloat() * 360.0F, 0.0F);
                        if (isValidPositionForMob(var1, var28, var26)) {
                           var17 = var28.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var28.blockPosition()), MobSpawnType.NATURAL, var17, (CompoundTag)null);
                           ++var11;
                           ++var19;
                           var1.addFreshEntityWithPassengers(var28);
                           var5.run(var28, var2);
                           if (var11 >= var28.getMaxSpawnClusterSize()) {
                              return;
                           }

                           if (var28.isMaxGroupSizeReached(var19)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel var0, ChunkAccess var1, BlockPos.MutableBlockPos var2, double var3) {
      if (var3 <= 576.0D) {
         return false;
      } else if (var0.getSharedSpawnPos().closerThan(new Vec3((double)var2.getX() + 0.5D, (double)var2.getY(), (double)var2.getZ() + 0.5D), 24.0D)) {
         return false;
      } else {
         ChunkPos var5 = new ChunkPos(var2);
         return Objects.equals(var5, var1.getPos()) || var0.getChunkSource().isEntityTickingChunk(var5);
      }
   }

   private static boolean isValidSpawnPostitionForType(ServerLevel var0, MobCategory var1, StructureFeatureManager var2, ChunkGenerator var3, MobSpawnSettings.SpawnerData var4, BlockPos.MutableBlockPos var5, double var6) {
      EntityType var8 = var4.type;
      if (var8.getCategory() == MobCategory.MISC) {
         return false;
      } else if (!var8.canSpawnFarFromPlayer() && var6 > (double)(var8.getCategory().getDespawnDistance() * var8.getCategory().getDespawnDistance())) {
         return false;
      } else if (var8.canSummon() && canSpawnMobAt(var0, var2, var3, var1, var4, var5)) {
         SpawnPlacements.Type var9 = SpawnPlacements.getPlacementType(var8);
         if (!isSpawnPositionOk(var9, var0, var5, var8)) {
            return false;
         } else if (!SpawnPlacements.checkSpawnRules(var8, var0, MobSpawnType.NATURAL, var5, var0.random)) {
            return false;
         } else {
            return var0.noCollision(var8.getAABB((double)var5.getX() + 0.5D, (double)var5.getY(), (double)var5.getZ() + 0.5D));
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static Mob getMobForSpawn(ServerLevel var0, EntityType<?> var1) {
      try {
         Entity var3 = var1.create(var0);
         if (!(var3 instanceof Mob)) {
            throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(var1));
         } else {
            Mob var2 = (Mob)var3;
            return var2;
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to create mob", var4);
         return null;
      }
   }

   private static boolean isValidPositionForMob(ServerLevel var0, Mob var1, double var2) {
      if (var2 > (double)(var1.getType().getCategory().getDespawnDistance() * var1.getType().getCategory().getDespawnDistance()) && var1.removeWhenFarAway(var2)) {
         return false;
      } else {
         return var1.checkSpawnRules(var0, MobSpawnType.NATURAL) && var1.checkSpawnObstruction(var0);
      }
   }

   @Nullable
   private static MobSpawnSettings.SpawnerData getRandomSpawnMobAt(ServerLevel var0, StructureFeatureManager var1, ChunkGenerator var2, MobCategory var3, Random var4, BlockPos var5) {
      Biome var6 = var0.getBiome(var5);
      if (var3 == MobCategory.WATER_AMBIENT && var6.getBiomeCategory() == Biome.BiomeCategory.RIVER && var4.nextFloat() < 0.98F) {
         return null;
      } else {
         List var7 = mobsAt(var0, var1, var2, var3, var5, var6);
         return var7.isEmpty() ? null : (MobSpawnSettings.SpawnerData)WeighedRandom.getRandomItem(var4, var7);
      }
   }

   private static boolean canSpawnMobAt(ServerLevel var0, StructureFeatureManager var1, ChunkGenerator var2, MobCategory var3, MobSpawnSettings.SpawnerData var4, BlockPos var5) {
      return mobsAt(var0, var1, var2, var3, var5, (Biome)null).contains(var4);
   }

   private static List<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel var0, StructureFeatureManager var1, ChunkGenerator var2, MobCategory var3, BlockPos var4, @Nullable Biome var5) {
      return var3 == MobCategory.MONSTER && var0.getBlockState(var4.below()).getBlock() == Blocks.NETHER_BRICKS && var1.getStructureAt(var4, false, StructureFeature.NETHER_BRIDGE).isValid() ? StructureFeature.NETHER_BRIDGE.getSpecialEnemies() : var2.getMobsAt(var5 != null ? var5 : var0.getBiome(var4), var1, var3, var4);
   }

   private static BlockPos getRandomPosWithin(Level var0, LevelChunk var1) {
      ChunkPos var2 = var1.getPos();
      int var3 = var2.getMinBlockX() + var0.random.nextInt(16);
      int var4 = var2.getMinBlockZ() + var0.random.nextInt(16);
      int var5 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var3, var4) + 1;
      int var6 = var0.random.nextInt(var5 + 1);
      return new BlockPos(var3, var6, var4);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter var0, BlockPos var1, BlockState var2, FluidState var3, EntityType<?> var4) {
      if (var2.isCollisionShapeFullBlock(var0, var1)) {
         return false;
      } else if (var2.isSignalSource()) {
         return false;
      } else if (!var3.isEmpty()) {
         return false;
      } else if (var2.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !var4.isBlockDangerous(var2);
      }
   }

   public static boolean isSpawnPositionOk(SpawnPlacements.Type var0, LevelReader var1, BlockPos var2, @Nullable EntityType<?> var3) {
      if (var0 == SpawnPlacements.Type.NO_RESTRICTIONS) {
         return true;
      } else if (var3 != null && var1.getWorldBorder().isWithinBounds(var2)) {
         BlockState var4 = var1.getBlockState(var2);
         FluidState var5 = var1.getFluidState(var2);
         BlockPos var6 = var2.above();
         BlockPos var7 = var2.below();
         switch(var0) {
         case IN_WATER:
            return var5.is(FluidTags.WATER) && var1.getFluidState(var7).is(FluidTags.WATER) && !var1.getBlockState(var6).isRedstoneConductor(var1, var6);
         case IN_LAVA:
            return var5.is(FluidTags.LAVA);
         case ON_GROUND:
         default:
            BlockState var8 = var1.getBlockState(var7);
            if (!var8.isValidSpawn(var1, var7, var3)) {
               return false;
            } else {
               return isValidEmptySpawnBlock(var1, var2, var4, var5, var3) && isValidEmptySpawnBlock(var1, var6, var1.getBlockState(var6), var1.getFluidState(var6), var3);
            }
         }
      } else {
         return false;
      }
   }

   public static void spawnMobsForChunkGeneration(ServerLevelAccessor var0, Biome var1, int var2, int var3, Random var4) {
      MobSpawnSettings var5 = var1.getMobSettings();
      List var6 = var5.getMobs(MobCategory.CREATURE);
      if (!var6.isEmpty()) {
         int var7 = var2 << 4;
         int var8 = var3 << 4;

         while(var4.nextFloat() < var5.getCreatureProbability()) {
            MobSpawnSettings.SpawnerData var9 = (MobSpawnSettings.SpawnerData)WeighedRandom.getRandomItem(var4, var6);
            int var10 = var9.minCount + var4.nextInt(1 + var9.maxCount - var9.minCount);
            SpawnGroupData var11 = null;
            int var12 = var7 + var4.nextInt(16);
            int var13 = var8 + var4.nextInt(16);
            int var14 = var12;
            int var15 = var13;

            for(int var16 = 0; var16 < var10; ++var16) {
               boolean var17 = false;

               for(int var18 = 0; !var17 && var18 < 4; ++var18) {
                  BlockPos var19 = getTopNonCollidingPos(var0, var9.type, var12, var13);
                  if (var9.type.canSummon() && isSpawnPositionOk(SpawnPlacements.getPlacementType(var9.type), var0, var19, var9.type)) {
                     float var20 = var9.type.getWidth();
                     double var21 = Mth.clamp((double)var12, (double)var7 + (double)var20, (double)var7 + 16.0D - (double)var20);
                     double var23 = Mth.clamp((double)var13, (double)var8 + (double)var20, (double)var8 + 16.0D - (double)var20);
                     if (!var0.noCollision(var9.type.getAABB(var21, (double)var19.getY(), var23)) || !SpawnPlacements.checkSpawnRules(var9.type, var0, MobSpawnType.CHUNK_GENERATION, new BlockPos(var21, (double)var19.getY(), var23), var0.getRandom())) {
                        continue;
                     }

                     Entity var25;
                     try {
                        var25 = var9.type.create(var0.getLevel());
                     } catch (Exception var27) {
                        LOGGER.warn("Failed to create mob", var27);
                        continue;
                     }

                     var25.moveTo(var21, (double)var19.getY(), var23, var4.nextFloat() * 360.0F, 0.0F);
                     if (var25 instanceof Mob) {
                        Mob var26 = (Mob)var25;
                        if (var26.checkSpawnRules(var0, MobSpawnType.CHUNK_GENERATION) && var26.checkSpawnObstruction(var0)) {
                           var11 = var26.finalizeSpawn(var0, var0.getCurrentDifficultyAt(var26.blockPosition()), MobSpawnType.CHUNK_GENERATION, var11, (CompoundTag)null);
                           var0.addFreshEntityWithPassengers(var26);
                           var17 = true;
                        }
                     }
                  }

                  var12 += var4.nextInt(5) - var4.nextInt(5);

                  for(var13 += var4.nextInt(5) - var4.nextInt(5); var12 < var7 || var12 >= var7 + 16 || var13 < var8 || var13 >= var8 + 16; var13 = var15 + var4.nextInt(5) - var4.nextInt(5)) {
                     var12 = var14 + var4.nextInt(5) - var4.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader var0, EntityType<?> var1, int var2, int var3) {
      int var4 = var0.getHeight(SpawnPlacements.getHeightmapType(var1), var2, var3);
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var2, var4, var3);
      if (var0.dimensionType().hasCeiling()) {
         do {
            var5.move(Direction.DOWN);
         } while(!var0.getBlockState(var5).isAir());

         do {
            var5.move(Direction.DOWN);
         } while(var0.getBlockState(var5).isAir() && var5.getY() > 0);
      }

      if (SpawnPlacements.getPlacementType(var1) == SpawnPlacements.Type.ON_GROUND) {
         BlockPos var6 = var5.below();
         if (var0.getBlockState(var6).isPathfindable(var0, var6, PathComputationType.LAND)) {
            return var6;
         }
      }

      return var5.immutable();
   }

   @FunctionalInterface
   public interface ChunkGetter {
      void query(long var1, Consumer<LevelChunk> var3);
   }

   @FunctionalInterface
   public interface AfterSpawnCallback {
      void run(Mob var1, ChunkAccess var2);
   }

   @FunctionalInterface
   public interface SpawnPredicate {
      boolean test(EntityType<?> var1, BlockPos var2, ChunkAccess var3);
   }

   public static class SpawnState {
      private final int spawnableChunkCount;
      private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
      private final PotentialCalculator spawnPotential;
      private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
      @Nullable
      private BlockPos lastCheckedPos;
      @Nullable
      private EntityType<?> lastCheckedType;
      private double lastCharge;

      private SpawnState(int var1, Object2IntOpenHashMap<MobCategory> var2, PotentialCalculator var3) {
         super();
         this.spawnableChunkCount = var1;
         this.mobCategoryCounts = var2;
         this.spawnPotential = var3;
         this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(var2);
      }

      private boolean canSpawn(EntityType<?> var1, BlockPos var2, ChunkAccess var3) {
         this.lastCheckedPos = var2;
         this.lastCheckedType = var1;
         MobSpawnSettings.MobSpawnCost var4 = NaturalSpawner.getRoughBiome(var2, var3).getMobSettings().getMobSpawnCost(var1);
         if (var4 == null) {
            this.lastCharge = 0.0D;
            return true;
         } else {
            double var5 = var4.getCharge();
            this.lastCharge = var5;
            double var7 = this.spawnPotential.getPotentialEnergyChange(var2, var5);
            return var7 <= var4.getEnergyBudget();
         }
      }

      private void afterSpawn(Mob var1, ChunkAccess var2) {
         EntityType var3 = var1.getType();
         BlockPos var6 = var1.blockPosition();
         double var4;
         if (var6.equals(this.lastCheckedPos) && var3 == this.lastCheckedType) {
            var4 = this.lastCharge;
         } else {
            MobSpawnSettings.MobSpawnCost var7 = NaturalSpawner.getRoughBiome(var6, var2).getMobSettings().getMobSpawnCost(var3);
            if (var7 != null) {
               var4 = var7.getCharge();
            } else {
               var4 = 0.0D;
            }
         }

         this.spawnPotential.addCharge(var6, var4);
         this.mobCategoryCounts.addTo(var3.getCategory(), 1);
      }

      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      public Object2IntMap<MobCategory> getMobCategoryCounts() {
         return this.unmodifiableMobCategoryCounts;
      }

      private boolean canSpawnForCategory(MobCategory var1) {
         int var2 = var1.getMaxInstancesPerChunk() * this.spawnableChunkCount / NaturalSpawner.MAGIC_NUMBER;
         return this.mobCategoryCounts.getInt(var1) < var2;
      }

      // $FF: synthetic method
      SpawnState(int var1, Object2IntOpenHashMap var2, PotentialCalculator var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
