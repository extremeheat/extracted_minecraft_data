package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.Arrays;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jspecify.annotations.Nullable;

public interface Aquifer {
   static Aquifer createDisabled(final FluidPicker fluidRule) {
      return new Aquifer() {
         public @Nullable BlockState computeSubstance(final int blockX, final int blockY, final int blockZ, final double density) {
            return density > 0.0 ? null : fluidRule.computeFluid(blockX, blockY, blockZ).at(blockY);
         }

         public boolean shouldScheduleFluidUpdate() {
            return false;
         }
      };
   }

   @Nullable BlockState computeSubstance(int blockX, int blockY, int blockZ, double density);

   boolean shouldScheduleFluidUpdate();

   public static record Config(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction exclusion, DensityFunction surfaceLevel) {
      public static final Codec<Config> CODEC = RecordCodecBuilder.create((i) -> i.group(DensityFunction.CODEC.fieldOf("barrier").forGetter(Config::barrierNoise), DensityFunction.CODEC.fieldOf("fluid_level_floodedness").forGetter(Config::fluidLevelFloodednessNoise), DensityFunction.CODEC.fieldOf("fluid_level_spread").forGetter(Config::fluidLevelSpreadNoise), DensityFunction.CODEC.fieldOf("lava").forGetter(Config::lavaNoise), DensityFunction.CODEC.fieldOf("exclusion").forGetter(Config::exclusion), DensityFunction.CODEC.fieldOf("surface_level").forGetter(Config::surfaceLevel)).apply(i, Config::new));

      public Config {
         super();
      }

      public Aquifer create(final DensitySamplerSet cachingSamplers, final PositionalRandomFactory positionalRandomFactory, final DensityVolume volume, final FluidPicker fluidRule) {
         return new NoiseBasedAquifer(cachingSamplers, this, positionalRandomFactory, volume, fluidRule);
      }
   }

   public static class NoiseBasedAquifer implements Aquifer {
      private static final int X_RANGE = 10;
      private static final int Y_RANGE = 9;
      private static final int Z_RANGE = 10;
      private static final int X_SEPARATION = 6;
      private static final int Y_SEPARATION = 3;
      private static final int Z_SEPARATION = 6;
      private static final int X_SPACING = 16;
      private static final int Y_SPACING = 12;
      private static final int Z_SPACING = 16;
      private static final int X_SPACING_SHIFT = 4;
      private static final int Z_SPACING_SHIFT = 4;
      private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
      private static final double FLOWING_UPDATE_SIMULARITY = similarity(Mth.square(10), Mth.square(12));
      private static final int SAMPLE_OFFSET_X = -5;
      private static final int SAMPLE_OFFSET_Y = 1;
      private static final int SAMPLE_OFFSET_Z = -5;
      private static final int MIN_CELL_SAMPLE_X = 0;
      private static final int MIN_CELL_SAMPLE_Y = -1;
      private static final int MIN_CELL_SAMPLE_Z = 0;
      private static final int MAX_CELL_SAMPLE_X = 1;
      private static final int MAX_CELL_SAMPLE_Y = 1;
      private static final int MAX_CELL_SAMPLE_Z = 1;
      private final DensitySampler.Bound barrierNoise;
      private final DensitySampler.Bound fluidLevelFloodednessNoise;
      private final DensitySampler.Bound fluidLevelSpreadNoise;
      private final DensitySampler.Bound lavaNoise;
      private final PositionalRandomFactory positionalRandomFactory;
      private final @Nullable Aquifer.FluidStatus[] aquiferCache;
      private final long[] aquiferLocationCache;
      private final FluidPicker globalFluidPicker;
      private final DensitySampler.Bound exclusion;
      private boolean shouldScheduleFluidUpdate;
      private final int skipSamplingAboveY;
      private final int minGridX;
      private final int minGridY;
      private final int minGridZ;
      private final int gridSizeX;
      private final int gridSizeZ;
      private final DensitySampler.Bound surfaceLevel;
      private final Long2IntMap surfaceLevelCache = new Long2IntOpenHashMap();
      private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

      private NoiseBasedAquifer(final DensitySamplerSet cachingSamplers, final Config config, final PositionalRandomFactory positionalRandomFactory, final DensityVolume volume, final FluidPicker globalFluidPicker) {
         super();
         this.barrierNoise = cachingSamplers.get(config.barrierNoise());
         this.fluidLevelFloodednessNoise = cachingSamplers.get(config.fluidLevelFloodednessNoise());
         this.fluidLevelSpreadNoise = cachingSamplers.get(config.fluidLevelSpreadNoise());
         this.lavaNoise = cachingSamplers.get(config.lavaNoise());
         this.exclusion = cachingSamplers.get(config.exclusion());
         this.surfaceLevel = cachingSamplers.get(config.surfaceLevel());
         this.positionalRandomFactory = positionalRandomFactory;
         this.minGridX = gridX(volume.minBlockX() + -5) + 0;
         this.globalFluidPicker = globalFluidPicker;
         int maxGridX = gridX(volume.maxBlockX() + -5) + 1;
         this.gridSizeX = maxGridX - this.minGridX + 1;
         this.minGridY = gridY(volume.minBlockY() + 1) + -1;
         int maxGridY = gridY(volume.maxBlockY() + 1) + 1;
         int gridSizeY = maxGridY - this.minGridY + 1;
         this.minGridZ = gridZ(volume.minBlockZ() + -5) + 0;
         int maxGridZ = gridZ(volume.maxBlockZ() + -5) + 1;
         this.gridSizeZ = maxGridZ - this.minGridZ + 1;
         int totalGridSize = this.gridSizeX * gridSizeY * this.gridSizeZ;
         this.aquiferCache = new FluidStatus[totalGridSize];
         this.aquiferLocationCache = new long[totalGridSize];
         Arrays.fill(this.aquiferLocationCache, 9223372036854775807L);
         int maxAdjustedSurfaceLevel = this.adjustSurfaceLevel(this.maxSurfaceLevel(fromGridX(this.minGridX, 0), fromGridZ(this.minGridZ, 0), fromGridX(maxGridX, 9), fromGridZ(maxGridZ, 9)));
         int skipSamplingAboveGridY = gridY(maxAdjustedSurfaceLevel + 12) - -1;
         this.skipSamplingAboveY = fromGridY(skipSamplingAboveGridY, 11) - 1;
      }

      private int surfaceLevel(final int blockX, final int blockZ) {
         int quantizedX = QuartPos.toBlock(QuartPos.fromBlock(blockX));
         int quantizedZ = QuartPos.toBlock(QuartPos.fromBlock(blockZ));
         return this.surfaceLevelCache.computeIfAbsent(ChunkPos.pack(quantizedX, quantizedZ), (k) -> Mth.floor(this.surfaceLevel.sampleValue(quantizedX, 0, quantizedZ)));
      }

      private int maxSurfaceLevel(final int minBlockX, final int minBlockZ, final int maxBlockX, final int maxBlockZ) {
         int minQuartX = QuartPos.fromBlock(minBlockX);
         int maxQuartX = QuartPos.fromBlock(maxBlockX);
         int minQuartZ = QuartPos.fromBlock(minBlockZ);
         int maxQuartZ = QuartPos.fromBlock(maxBlockZ);
         DensityVolume volume = new DensityVolume(maxQuartX - minQuartX + 1, 1, maxQuartZ - minQuartZ + 1, QuartPos.toBlock(minQuartX), 0, QuartPos.toBlock(minQuartZ), 4, 1, 4);

         try (ScopedDensityBuffer buffer = this.surfaceLevel.sampleVolume(volume)) {
            int maxY = -2147483648;

            for(int z = 0; z < volume.sizeZ(); ++z) {
               for(int x = 0; x < volume.sizeX(); ++x) {
                  int surfaceLevel = Mth.floor(buffer.get(volume.indexUnchecked(x, 0, z)));
                  this.surfaceLevelCache.put(ChunkPos.pack(volume.blockX(x), volume.blockZ(z)), surfaceLevel);
                  if (surfaceLevel > maxY) {
                     maxY = surfaceLevel;
                  }
               }
            }

            return maxY;
         }
      }

      private int getIndex(final int gridX, final int gridY, final int gridZ) {
         int x = gridX - this.minGridX;
         int y = gridY - this.minGridY;
         int z = gridZ - this.minGridZ;
         return (y * this.gridSizeZ + z) * this.gridSizeX + x;
      }

      public @Nullable BlockState computeSubstance(final int blockX, final int blockY, final int blockZ, final double density) {
         if (density > 0.0) {
            this.shouldScheduleFluidUpdate = false;
            return null;
         } else {
            FluidStatus globalFluid = this.globalFluidPicker.computeFluid(blockX, blockY, blockZ);
            if (blockY > this.skipSamplingAboveY) {
               this.shouldScheduleFluidUpdate = false;
               return globalFluid.at(blockY);
            } else if (globalFluid.at(blockY).is(Blocks.LAVA)) {
               this.shouldScheduleFluidUpdate = false;
               return SharedConstants.DEBUG_DISABLE_FLUID_GENERATION ? Blocks.AIR.defaultBlockState() : Blocks.LAVA.defaultBlockState();
            } else {
               int xAnchor = gridX(blockX + -5);
               int yAnchor = gridY(blockY + 1);
               int zAnchor = gridZ(blockZ + -5);
               int distanceSqr1 = 2147483647;
               int distanceSqr2 = 2147483647;
               int distanceSqr3 = 2147483647;
               int distanceSqr4 = 2147483647;
               int closestIndex1 = 0;
               int closestIndex2 = 0;
               int closestIndex3 = 0;
               int closestIndex4 = 0;

               for(int x1 = 0; x1 <= 1; ++x1) {
                  for(int y1 = -1; y1 <= 1; ++y1) {
                     for(int z1 = 0; z1 <= 1; ++z1) {
                        int spacedGridX = xAnchor + x1;
                        int spacedGridY = yAnchor + y1;
                        int spacedGridZ = zAnchor + z1;
                        int index = this.getIndex(spacedGridX, spacedGridY, spacedGridZ);
                        long existingLocation = this.aquiferLocationCache[index];
                        long location;
                        if (existingLocation != 9223372036854775807L) {
                           location = existingLocation;
                        } else {
                           RandomSource random = this.positionalRandomFactory.at(spacedGridX, spacedGridY, spacedGridZ);
                           location = BlockPos.asLong(fromGridX(spacedGridX, random.nextInt(10)), fromGridY(spacedGridY, random.nextInt(9)), fromGridZ(spacedGridZ, random.nextInt(10)));
                           this.aquiferLocationCache[index] = location;
                        }

                        int dx = BlockPos.getX(location) - blockX;
                        int dy = BlockPos.getY(location) - blockY;
                        int dz = BlockPos.getZ(location) - blockZ;
                        int newDistance = dx * dx + dy * dy + dz * dz;
                        if (distanceSqr1 >= newDistance) {
                           closestIndex4 = closestIndex3;
                           closestIndex3 = closestIndex2;
                           closestIndex2 = closestIndex1;
                           closestIndex1 = index;
                           distanceSqr4 = distanceSqr3;
                           distanceSqr3 = distanceSqr2;
                           distanceSqr2 = distanceSqr1;
                           distanceSqr1 = newDistance;
                        } else if (distanceSqr2 >= newDistance) {
                           closestIndex4 = closestIndex3;
                           closestIndex3 = closestIndex2;
                           closestIndex2 = index;
                           distanceSqr4 = distanceSqr3;
                           distanceSqr3 = distanceSqr2;
                           distanceSqr2 = newDistance;
                        } else if (distanceSqr3 >= newDistance) {
                           closestIndex4 = closestIndex3;
                           closestIndex3 = index;
                           distanceSqr4 = distanceSqr3;
                           distanceSqr3 = newDistance;
                        } else if (distanceSqr4 >= newDistance) {
                           closestIndex4 = index;
                           distanceSqr4 = newDistance;
                        }
                     }
                  }
               }

               FluidStatus closestStatus1 = this.getAquiferStatus(closestIndex1);
               double similarity12 = similarity(distanceSqr1, distanceSqr2);
               BlockState fluidState = closestStatus1.at(blockY);
               BlockState actualFluidState = SharedConstants.DEBUG_DISABLE_FLUID_GENERATION ? Blocks.AIR.defaultBlockState() : fluidState;
               if (similarity12 <= 0.0) {
                  if (similarity12 >= FLOWING_UPDATE_SIMULARITY) {
                     FluidStatus closestStatus2 = this.getAquiferStatus(closestIndex2);
                     this.shouldScheduleFluidUpdate = !closestStatus1.equals(closestStatus2);
                  } else {
                     this.shouldScheduleFluidUpdate = false;
                  }

                  return actualFluidState;
               } else if (fluidState.is(Blocks.WATER) && this.globalFluidPicker.computeFluid(blockX, blockY - 1, blockZ).at(blockY - 1).is(Blocks.LAVA)) {
                  this.shouldScheduleFluidUpdate = true;
                  return actualFluidState;
               } else {
                  MutableDouble barrierNoiseValue = new MutableDouble(0.0 / 0.0);
                  FluidStatus closestStatus2 = this.getAquiferStatus(closestIndex2);
                  double barrier12 = similarity12 * this.calculatePressure(blockX, blockY, blockZ, barrierNoiseValue, closestStatus1, closestStatus2);
                  if (density + barrier12 > 0.0) {
                     this.shouldScheduleFluidUpdate = false;
                     return null;
                  } else {
                     FluidStatus closestStatus3 = this.getAquiferStatus(closestIndex3);
                     double similarity13 = similarity(distanceSqr1, distanceSqr3);
                     if (similarity13 > 0.0) {
                        double barrier13 = similarity12 * similarity13 * this.calculatePressure(blockX, blockY, blockZ, barrierNoiseValue, closestStatus1, closestStatus3);
                        if (density + barrier13 > 0.0) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     double similarity23 = similarity(distanceSqr2, distanceSqr3);
                     if (similarity23 > 0.0) {
                        double barrier23 = similarity12 * similarity23 * this.calculatePressure(blockX, blockY, blockZ, barrierNoiseValue, closestStatus2, closestStatus3);
                        if (density + barrier23 > 0.0) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     boolean mayFlow12 = !closestStatus1.equals(closestStatus2);
                     boolean mayFlow23 = similarity23 >= FLOWING_UPDATE_SIMULARITY && !closestStatus2.equals(closestStatus3);
                     boolean mayFlow13 = similarity13 >= FLOWING_UPDATE_SIMULARITY && !closestStatus1.equals(closestStatus3);
                     if (!mayFlow12 && !mayFlow23 && !mayFlow13) {
                        this.shouldScheduleFluidUpdate = similarity13 >= FLOWING_UPDATE_SIMULARITY && similarity(distanceSqr1, distanceSqr4) >= FLOWING_UPDATE_SIMULARITY && !closestStatus1.equals(this.getAquiferStatus(closestIndex4));
                     } else {
                        this.shouldScheduleFluidUpdate = true;
                     }

                     return actualFluidState;
                  }
               }
            }
         }
      }

      public boolean shouldScheduleFluidUpdate() {
         return this.shouldScheduleFluidUpdate;
      }

      private static double similarity(final int distanceSqr1, final int distanceSqr2) {
         double threshold = 25.0;
         return 1.0 - (double)(distanceSqr2 - distanceSqr1) / 25.0;
      }

      private double calculatePressure(final int blockX, final int blockY, final int blockZ, final MutableDouble barrierNoiseValue, final FluidStatus statusClosest1, final FluidStatus statusClosest2) {
         BlockState type1 = statusClosest1.at(blockY);
         BlockState type2 = statusClosest2.at(blockY);
         if ((!type1.is(Blocks.LAVA) || !type2.is(Blocks.WATER)) && (!type1.is(Blocks.WATER) || !type2.is(Blocks.LAVA))) {
            int fluidYDiff = Math.abs(statusClosest1.fluidLevel - statusClosest2.fluidLevel);
            if (fluidYDiff == 0) {
               return 0.0;
            } else {
               double averageFluidY = 0.5 * (double)(statusClosest1.fluidLevel + statusClosest2.fluidLevel);
               double howFarAboveAverageFluidPoint = (double)blockY + 0.5 - averageFluidY;
               double baseValue = (double)fluidYDiff / 2.0;
               double topBias = 0.0;
               double furthestRocksFromTopBias = 2.5;
               double furthestHolesFromTopBias = 1.5;
               double bottomBias = 3.0;
               double furthestRocksFromBottomBias = 10.0;
               double furthestHolesFromBottomBias = 3.0;
               double distanceFromBarrierEdgeTowardsMiddle = baseValue - Math.abs(howFarAboveAverageFluidPoint);
               double gradient;
               if (howFarAboveAverageFluidPoint > 0.0) {
                  double centerPoint = 0.0 + distanceFromBarrierEdgeTowardsMiddle;
                  if (centerPoint > 0.0) {
                     gradient = centerPoint / 1.5;
                  } else {
                     gradient = centerPoint / 2.5;
                  }
               } else {
                  double centerPoint = 3.0 + distanceFromBarrierEdgeTowardsMiddle;
                  if (centerPoint > 0.0) {
                     gradient = centerPoint / 3.0;
                  } else {
                     gradient = centerPoint / 10.0;
                  }
               }

               double amplitude = 2.0;
               double noiseValue;
               if (!(gradient < -2.0) && !(gradient > 2.0)) {
                  double currentNoiseValue = barrierNoiseValue.doubleValue();
                  if (Double.isNaN(currentNoiseValue)) {
                     double barrierNoise = (double)this.barrierNoise.sampleValue(blockX, blockY, blockZ);
                     barrierNoiseValue.setValue(barrierNoise);
                     noiseValue = barrierNoise;
                  } else {
                     noiseValue = currentNoiseValue;
                  }
               } else {
                  noiseValue = 0.0;
               }

               return 2.0 * (noiseValue + gradient);
            }
         } else {
            return 2.0;
         }
      }

      private static int gridX(final int blockCoord) {
         return blockCoord >> 4;
      }

      private static int fromGridX(final int gridCoord, final int blockOffset) {
         return (gridCoord << 4) + blockOffset;
      }

      private static int gridY(final int blockCoord) {
         return Math.floorDiv(blockCoord, 12);
      }

      private static int fromGridY(final int gridCoord, final int blockOffset) {
         return gridCoord * 12 + blockOffset;
      }

      private static int gridZ(final int blockCoord) {
         return blockCoord >> 4;
      }

      private static int fromGridZ(final int gridCoord, final int blockOffset) {
         return (gridCoord << 4) + blockOffset;
      }

      private FluidStatus getAquiferStatus(final int index) {
         FluidStatus oldStatus = this.aquiferCache[index];
         if (oldStatus != null) {
            return oldStatus;
         } else {
            long location = this.aquiferLocationCache[index];
            FluidStatus status = this.computeFluid(BlockPos.getX(location), BlockPos.getY(location), BlockPos.getZ(location));
            this.aquiferCache[index] = status;
            return status;
         }
      }

      private FluidStatus computeFluid(final int x, final int y, final int z) {
         FluidStatus globalFluid = this.globalFluidPicker.computeFluid(x, y, z);
         int lowestPreliminarySurface = 2147483647;
         int topOfAquiferCell = y + 12;
         int bottomOfAquiferCell = y - 12;
         boolean surfaceAtCenterIsUnderGlobalFluidLevel = false;

         for(int[] offset : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
            int sampleX = x + SectionPos.sectionToBlockCoord(offset[0]);
            int sampleZ = z + SectionPos.sectionToBlockCoord(offset[1]);
            int surfaceLevel = this.surfaceLevel(sampleX, sampleZ);
            int adjustedSurfaceLevel = this.adjustSurfaceLevel(surfaceLevel);
            boolean start = offset[0] == 0 && offset[1] == 0;
            if (start && bottomOfAquiferCell > adjustedSurfaceLevel) {
               return globalFluid;
            }

            boolean topOfAquiferCellPokesAboveSurface = topOfAquiferCell > adjustedSurfaceLevel;
            if (topOfAquiferCellPokesAboveSurface || start) {
               FluidStatus globalFluidAtSurface = this.globalFluidPicker.computeFluid(sampleX, adjustedSurfaceLevel, sampleZ);
               if (!globalFluidAtSurface.at(adjustedSurfaceLevel).isAir()) {
                  if (start) {
                     surfaceAtCenterIsUnderGlobalFluidLevel = true;
                  }

                  if (topOfAquiferCellPokesAboveSurface) {
                     return globalFluidAtSurface;
                  }
               }
            }

            lowestPreliminarySurface = Math.min(lowestPreliminarySurface, surfaceLevel);
         }

         int fluidSurfaceLevel = this.computeSurfaceLevel(x, y, z, globalFluid, lowestPreliminarySurface, surfaceAtCenterIsUnderGlobalFluidLevel);
         return new FluidStatus(fluidSurfaceLevel, this.computeFluidType(x, y, z, globalFluid, fluidSurfaceLevel));
      }

      private int adjustSurfaceLevel(final int preliminarySurfaceLevel) {
         return preliminarySurfaceLevel + 8;
      }

      private int computeSurfaceLevel(final int x, final int y, final int z, final FluidStatus globalFluid, final int lowestSurfaceLevel, final boolean surfaceAtCenterIsUnderGlobalFluidLevel) {
         double partiallyFloodedness;
         double fullyFloodidness;
         if ((double)this.exclusion.sampleValue(x, y, z) > 0.0) {
            partiallyFloodedness = -1.0;
            fullyFloodidness = -1.0;
         } else {
            int distanceBelowSurface = this.adjustSurfaceLevel(lowestSurfaceLevel) - y;
            int floodednessMaxDepth = 64;
            double floodednessFactor = surfaceAtCenterIsUnderGlobalFluidLevel ? Mth.clampedMap((double)distanceBelowSurface, 0.0, 64.0, 1.0, 0.0) : 0.0;
            double floodednessNoiseValue = Mth.clamp((double)this.fluidLevelFloodednessNoise.sampleValue(x, y, z), -1.0, 1.0);
            double fullyFloodedThreshold = Mth.map(floodednessFactor, 1.0, 0.0, -0.3, 0.8);
            double partiallyFloodedThreshold = Mth.map(floodednessFactor, 1.0, 0.0, -0.8, 0.4);
            partiallyFloodedness = floodednessNoiseValue - partiallyFloodedThreshold;
            fullyFloodidness = floodednessNoiseValue - fullyFloodedThreshold;
         }

         int fluidSurfaceLevel;
         if (fullyFloodidness > 0.0) {
            fluidSurfaceLevel = globalFluid.fluidLevel;
         } else if (partiallyFloodedness > 0.0) {
            fluidSurfaceLevel = this.computeRandomizedFluidSurfaceLevel(x, y, z, lowestSurfaceLevel);
         } else {
            fluidSurfaceLevel = DimensionType.WAY_BELOW_MIN_Y;
         }

         return fluidSurfaceLevel;
      }

      private int computeRandomizedFluidSurfaceLevel(final int x, final int y, final int z, final int lowestSurfaceLevel) {
         int fluidCellWidth = 16;
         int fluidCellHeight = 40;
         int fluidLevelCellX = Math.floorDiv(x, 16);
         int fluidLevelCellY = Math.floorDiv(y, 40);
         int fluidLevelCellZ = Math.floorDiv(z, 16);
         int fluidCellMiddleY = fluidLevelCellY * 40 + 20;
         int maxSpread = 10;
         double fluidLevelSpread = (double)(this.fluidLevelSpreadNoise.sampleValue(fluidLevelCellX, fluidLevelCellY, fluidLevelCellZ) * 10.0F);
         int fluidLevelSpreadQuantized = Mth.quantize(fluidLevelSpread, 3);
         int targetFluidSurfaceLevel = fluidCellMiddleY + fluidLevelSpreadQuantized;
         return Math.min(lowestSurfaceLevel, targetFluidSurfaceLevel);
      }

      private BlockState computeFluidType(final int x, final int y, final int z, final FluidStatus globalFluid, final int fluidSurfaceLevel) {
         BlockState fluidType = globalFluid.fluidType;
         if (fluidSurfaceLevel <= -10 && fluidSurfaceLevel != DimensionType.WAY_BELOW_MIN_Y && globalFluid.fluidType != Blocks.LAVA.defaultBlockState()) {
            int fluidTypeCellWidth = 64;
            int fluidTypeCellHeight = 40;
            int fluidTypeCellX = Math.floorDiv(x, 64);
            int fluidTypeCellY = Math.floorDiv(y, 40);
            int fluidTypeCellZ = Math.floorDiv(z, 64);
            double lavaNoiseValue = (double)this.lavaNoise.sampleValue(fluidTypeCellX, fluidTypeCellY, fluidTypeCellZ);
            if (Math.abs(lavaNoiseValue) > 0.3) {
               fluidType = Blocks.LAVA.defaultBlockState();
            }
         }

         return fluidType;
      }
   }

   public static record FluidStatus(int fluidLevel, BlockState fluidType) {
      public FluidStatus {
         super();
      }

      public BlockState at(final int blockY) {
         return blockY < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
      }
   }

   public interface FluidPicker {
      FluidStatus computeFluid(final int blockX, final int blockY, final int blockZ);
   }
}
