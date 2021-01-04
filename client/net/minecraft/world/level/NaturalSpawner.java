package net.minecraft.world.level;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NaturalSpawner {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void spawnCategoryForChunk(MobCategory var0, Level var1, LevelChunk var2, BlockPos var3) {
      ChunkGenerator var4 = var1.getChunkSource().getGenerator();
      int var5 = 0;
      BlockPos var6 = getRandomPosWithin(var1, var2);
      int var7 = var6.getX();
      int var8 = var6.getY();
      int var9 = var6.getZ();
      if (var8 >= 1) {
         BlockState var10 = var2.getBlockState(var6);
         if (!var10.isRedstoneConductor(var2, var6)) {
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
            int var12 = 0;

            while(var12 < 3) {
               int var13 = var7;
               int var14 = var9;
               boolean var15 = true;
               Biome.SpawnerData var16 = null;
               SpawnGroupData var17 = null;
               int var18 = Mth.ceil(Math.random() * 4.0D);
               int var19 = 0;
               int var20 = 0;

               while(true) {
                  label115: {
                     if (var20 < var18) {
                        label123: {
                           var13 += var1.random.nextInt(6) - var1.random.nextInt(6);
                           var14 += var1.random.nextInt(6) - var1.random.nextInt(6);
                           var11.set(var13, var8, var14);
                           float var21 = (float)var13 + 0.5F;
                           float var22 = (float)var14 + 0.5F;
                           Player var23 = var1.getNearestPlayerIgnoreY((double)var21, (double)var22, -1.0D);
                           if (var23 == null) {
                              break label115;
                           }

                           double var24 = var23.distanceToSqr((double)var21, (double)var8, (double)var22);
                           if (var24 <= 576.0D || var3.closerThan(new Vec3((double)var21, (double)var8, (double)var22), 24.0D)) {
                              break label115;
                           }

                           ChunkPos var26 = new ChunkPos(var11);
                           if (!Objects.equals(var26, var2.getPos()) && !var1.getChunkSource().isEntityTickingChunk(var26)) {
                              break label115;
                           }

                           if (var16 == null) {
                              var16 = getRandomSpawnMobAt(var4, var0, var1.random, var11);
                              if (var16 == null) {
                                 break label123;
                              }

                              var18 = var16.minCount + var1.random.nextInt(1 + var16.maxCount - var16.minCount);
                           }

                           if (var16.type.getCategory() == MobCategory.MISC || !var16.type.canSpawnFarFromPlayer() && var24 > 16384.0D) {
                              break label115;
                           }

                           EntityType var27 = var16.type;
                           if (!var27.canSummon() || !canSpawnMobAt(var4, var0, var16, var11)) {
                              break label115;
                           }

                           SpawnPlacements.Type var28 = SpawnPlacements.getPlacementType(var27);
                           if (!isSpawnPositionOk(var28, var1, var11, var27) || !SpawnPlacements.checkSpawnRules(var27, var1, MobSpawnType.NATURAL, var11, var1.random) || !var1.noCollision(var27.getAABB((double)var21, (double)var8, (double)var22))) {
                              break label115;
                           }

                           Mob var29;
                           try {
                              Entity var30 = var27.create(var1);
                              if (!(var30 instanceof Mob)) {
                                 throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(var27));
                              }

                              var29 = (Mob)var30;
                           } catch (Exception var31) {
                              LOGGER.warn("Failed to create mob", var31);
                              return;
                           }

                           var29.moveTo((double)var21, (double)var8, (double)var22, var1.random.nextFloat() * 360.0F, 0.0F);
                           if (var24 > 16384.0D && var29.removeWhenFarAway(var24) || !var29.checkSpawnRules(var1, MobSpawnType.NATURAL) || !var29.checkSpawnObstruction(var1)) {
                              break label115;
                           }

                           var17 = var29.finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var29)), MobSpawnType.NATURAL, var17, (CompoundTag)null);
                           ++var5;
                           ++var19;
                           var1.addFreshEntity(var29);
                           if (var5 >= var29.getMaxSpawnClusterSize()) {
                              return;
                           }

                           if (!var29.isMaxGroupSizeReached(var19)) {
                              break label115;
                           }
                        }
                     }

                     ++var12;
                     break;
                  }

                  ++var20;
               }
            }

         }
      }
   }

   @Nullable
   private static Biome.SpawnerData getRandomSpawnMobAt(ChunkGenerator<?> var0, MobCategory var1, Random var2, BlockPos var3) {
      List var4 = var0.getMobsAt(var1, var3);
      return var4.isEmpty() ? null : (Biome.SpawnerData)WeighedRandom.getRandomItem(var2, var4);
   }

   private static boolean canSpawnMobAt(ChunkGenerator<?> var0, MobCategory var1, Biome.SpawnerData var2, BlockPos var3) {
      List var4 = var0.getMobsAt(var1, var3);
      return var4.isEmpty() ? false : var4.contains(var2);
   }

   private static BlockPos getRandomPosWithin(Level var0, LevelChunk var1) {
      ChunkPos var2 = var1.getPos();
      int var3 = var2.getMinBlockX() + var0.random.nextInt(16);
      int var4 = var2.getMinBlockZ() + var0.random.nextInt(16);
      int var5 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var3, var4) + 1;
      int var6 = var0.random.nextInt(var5 + 1);
      return new BlockPos(var3, var6, var4);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter var0, BlockPos var1, BlockState var2, FluidState var3) {
      if (var2.isCollisionShapeFullBlock(var0, var1)) {
         return false;
      } else if (var2.isSignalSource()) {
         return false;
      } else if (!var3.isEmpty()) {
         return false;
      } else {
         return !var2.is(BlockTags.RAILS);
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
         case ON_GROUND:
         default:
            BlockState var8 = var1.getBlockState(var7);
            if (!var8.isValidSpawn(var1, var7, var3)) {
               return false;
            } else {
               return isValidEmptySpawnBlock(var1, var2, var4, var5) && isValidEmptySpawnBlock(var1, var6, var1.getBlockState(var6), var1.getFluidState(var6));
            }
         }
      } else {
         return false;
      }
   }

   public static void spawnMobsForChunkGeneration(LevelAccessor var0, Biome var1, int var2, int var3, Random var4) {
      List var5 = var1.getMobs(MobCategory.CREATURE);
      if (!var5.isEmpty()) {
         int var6 = var2 << 4;
         int var7 = var3 << 4;

         while(var4.nextFloat() < var1.getCreatureProbability()) {
            Biome.SpawnerData var8 = (Biome.SpawnerData)WeighedRandom.getRandomItem(var4, var5);
            int var9 = var8.minCount + var4.nextInt(1 + var8.maxCount - var8.minCount);
            SpawnGroupData var10 = null;
            int var11 = var6 + var4.nextInt(16);
            int var12 = var7 + var4.nextInt(16);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var9; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  BlockPos var18 = getTopNonCollidingPos(var0, var8.type, var11, var12);
                  if (var8.type.canSummon() && isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, var0, var18, var8.type)) {
                     float var19 = var8.type.getWidth();
                     double var20 = Mth.clamp((double)var11, (double)var6 + (double)var19, (double)var6 + 16.0D - (double)var19);
                     double var22 = Mth.clamp((double)var12, (double)var7 + (double)var19, (double)var7 + 16.0D - (double)var19);
                     if (!var0.noCollision(var8.type.getAABB(var20, (double)var18.getY(), var22)) || !SpawnPlacements.checkSpawnRules(var8.type, var0, MobSpawnType.CHUNK_GENERATION, new BlockPos(var20, (double)var18.getY(), var22), var0.getRandom())) {
                        continue;
                     }

                     Entity var24;
                     try {
                        var24 = var8.type.create(var0.getLevel());
                     } catch (Exception var26) {
                        LOGGER.warn("Failed to create mob", var26);
                        continue;
                     }

                     var24.moveTo(var20, (double)var18.getY(), var22, var4.nextFloat() * 360.0F, 0.0F);
                     if (var24 instanceof Mob) {
                        Mob var25 = (Mob)var24;
                        if (var25.checkSpawnRules(var0, MobSpawnType.CHUNK_GENERATION) && var25.checkSpawnObstruction(var0)) {
                           var10 = var25.finalizeSpawn(var0, var0.getCurrentDifficultyAt(new BlockPos(var25)), MobSpawnType.CHUNK_GENERATION, var10, (CompoundTag)null);
                           var0.addFreshEntity(var25);
                           var16 = true;
                        }
                     }
                  }

                  var11 += var4.nextInt(5) - var4.nextInt(5);

                  for(var12 += var4.nextInt(5) - var4.nextInt(5); var11 < var6 || var11 >= var6 + 16 || var12 < var7 || var12 >= var7 + 16; var12 = var14 + var4.nextInt(5) - var4.nextInt(5)) {
                     var11 = var13 + var4.nextInt(5) - var4.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader var0, @Nullable EntityType<?> var1, int var2, int var3) {
      BlockPos var4 = new BlockPos(var2, var0.getHeight(SpawnPlacements.getHeightmapType(var1), var2, var3), var3);
      BlockPos var5 = var4.below();
      return var0.getBlockState(var5).isPathfindable(var0, var5, PathComputationType.LAND) ? var5 : var4;
   }
}
