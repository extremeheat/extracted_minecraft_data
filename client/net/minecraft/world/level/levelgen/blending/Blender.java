package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.CompositeDirection;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

public class Blender {
   private static final Blender EMPTY = new Blender(new Long2ObjectOpenHashMap(), new Long2ObjectOpenHashMap()) {
      public BlendingOutput blendOffsetAndFactor(final int blockX, final int blockZ) {
         return new BlendingOutput(1.0, 0.0);
      }

      public double blendDensity(final DensityFunction.FunctionContext context, final double noiseValue) {
         return noiseValue;
      }

      public BiomeResolver getBiomeResolver(final BiomeResolver biomeResolver) {
         return biomeResolver;
      }
   };
   private static final NormalNoise SHIFT_NOISE;
   private static final int HEIGHT_BLENDING_RANGE_CELLS;
   private static final int HEIGHT_BLENDING_RANGE_CHUNKS;
   private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
   private static final int DENSITY_BLENDING_RANGE_CHUNKS;
   private static final double OLD_CHUNK_XZ_RADIUS = 8.0;
   private final Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData;
   private final Long2ObjectOpenHashMap<BlendingData> densityBlendingData;

   public static Blender empty() {
      return EMPTY;
   }

   public static Blender of(final @Nullable WorldGenRegion region) {
      if (!SharedConstants.DEBUG_DISABLE_BLENDING && region != null) {
         ChunkPos centerPos = region.getCenter();
         if (!region.isOldChunkAround(centerPos, HEIGHT_BLENDING_RANGE_CHUNKS)) {
            return EMPTY;
         } else {
            Long2ObjectOpenHashMap<BlendingData> heightAndBiomeData = new Long2ObjectOpenHashMap();
            Long2ObjectOpenHashMap<BlendingData> densityData = new Long2ObjectOpenHashMap();
            int maxDistSq = Mth.square(HEIGHT_BLENDING_RANGE_CHUNKS + 1);

            for(int dx = -HEIGHT_BLENDING_RANGE_CHUNKS; dx <= HEIGHT_BLENDING_RANGE_CHUNKS; ++dx) {
               for(int dz = -HEIGHT_BLENDING_RANGE_CHUNKS; dz <= HEIGHT_BLENDING_RANGE_CHUNKS; ++dz) {
                  if (dx * dx + dz * dz <= maxDistSq) {
                     int chunkX = centerPos.x() + dx;
                     int chunkZ = centerPos.z() + dz;
                     BlendingData blendingData = BlendingData.getOrUpdateBlendingData(region, chunkX, chunkZ);
                     if (blendingData != null) {
                        heightAndBiomeData.put(ChunkPos.pack(chunkX, chunkZ), blendingData);
                        if (dx >= -DENSITY_BLENDING_RANGE_CHUNKS && dx <= DENSITY_BLENDING_RANGE_CHUNKS && dz >= -DENSITY_BLENDING_RANGE_CHUNKS && dz <= DENSITY_BLENDING_RANGE_CHUNKS) {
                           densityData.put(ChunkPos.pack(chunkX, chunkZ), blendingData);
                        }
                     }
                  }
               }
            }

            if (heightAndBiomeData.isEmpty() && densityData.isEmpty()) {
               return EMPTY;
            } else {
               return new Blender(heightAndBiomeData, densityData);
            }
         }
      } else {
         return EMPTY;
      }
   }

   private Blender(final Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData, final Long2ObjectOpenHashMap<BlendingData> densityBlendingData) {
      super();
      this.heightAndBiomeBlendingData = heightAndBiomeBlendingData;
      this.densityBlendingData = densityBlendingData;
   }

   public boolean isEmpty() {
      return this.heightAndBiomeBlendingData.isEmpty() && this.densityBlendingData.isEmpty();
   }

   public BlendingOutput blendOffsetAndFactor(final int blockX, final int blockZ) {
      int cellX = QuartPos.fromBlock(blockX);
      int cellZ = QuartPos.fromBlock(blockZ);
      double fixedHeight = this.getBlendingDataValue(cellX, 0, cellZ, BlendingData::getHeight);
      if (fixedHeight != 1.7976931348623157E308) {
         return new BlendingOutput(0.0, heightToOffset(fixedHeight));
      } else {
         MutableDouble totalWeight = new MutableDouble(0.0);
         MutableDouble weightedHeights = new MutableDouble(0.0);
         MutableDouble closestDistance = new MutableDouble(1.0 / 0.0);
         this.heightAndBiomeBlendingData.forEach((chunkPos, blendingData) -> blendingData.iterateHeights(QuartPos.fromSection(ChunkPos.getX(chunkPos)), QuartPos.fromSection(ChunkPos.getZ(chunkPos)), (testCellX, testCellZ, height) -> {
               double distance = (double)Mth.length((float)(cellX - testCellX), (float)(cellZ - testCellZ));
               if (!(distance > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
                  if (distance < closestDistance.doubleValue()) {
                     closestDistance.setValue(distance);
                  }

                  double weight = 1.0 / (distance * distance * distance * distance);
                  weightedHeights.add(height * weight);
                  totalWeight.add(weight);
               }
            }));
         if (closestDistance.doubleValue() == 1.0 / 0.0) {
            return new BlendingOutput(1.0, 0.0);
         } else {
            double averageHeight = weightedHeights.doubleValue() / totalWeight.doubleValue();
            double alpha = Mth.clamp(closestDistance.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
            alpha = 3.0 * alpha * alpha - 2.0 * alpha * alpha * alpha;
            return new BlendingOutput(alpha, heightToOffset(averageHeight));
         }
      }
   }

   private static double heightToOffset(final double height) {
      double dimensionFactor = 1.0;
      double targetY = height + 0.5;
      double targetYMod = Mth.positiveModulo(targetY, 8.0);
      return 1.0 * (32.0 * (targetY - 128.0) - 3.0 * (targetY - 120.0) * targetYMod + 3.0 * targetYMod * targetYMod) / (128.0 * (32.0 - 3.0 * targetYMod));
   }

   public double blendDensity(final DensityFunction.FunctionContext context, final double noiseValue) {
      int cellX = QuartPos.fromBlock(context.blockX());
      int cellY = context.blockY() / 8;
      int cellZ = QuartPos.fromBlock(context.blockZ());
      double fixedDensity = this.getBlendingDataValue(cellX, cellY, cellZ, BlendingData::getDensity);
      if (fixedDensity != 1.7976931348623157E308) {
         return fixedDensity;
      } else {
         MutableDouble totalWeight = new MutableDouble(0.0);
         MutableDouble weightedHeights = new MutableDouble(0.0);
         MutableDouble closestDistance = new MutableDouble(1.0 / 0.0);
         this.densityBlendingData.forEach((chunkPos, blendingData) -> blendingData.iterateDensities(QuartPos.fromSection(ChunkPos.getX(chunkPos)), QuartPos.fromSection(ChunkPos.getZ(chunkPos)), cellY - 1, cellY + 1, (testCellX, testCellY, testCellZ, density) -> {
               double distance = Mth.length((double)(cellX - testCellX), (double)((cellY - testCellY) * 2), (double)(cellZ - testCellZ));
               if (!(distance > 2.0)) {
                  if (distance < closestDistance.doubleValue()) {
                     closestDistance.setValue(distance);
                  }

                  double weight = 1.0 / (distance * distance * distance * distance);
                  weightedHeights.add(density * weight);
                  totalWeight.add(weight);
               }
            }));
         if (closestDistance.doubleValue() == 1.0 / 0.0) {
            return noiseValue;
         } else {
            double averageDensity = weightedHeights.doubleValue() / totalWeight.doubleValue();
            double alpha = Mth.clamp(closestDistance.doubleValue() / 3.0, 0.0, 1.0);
            return Mth.lerp(alpha, averageDensity, noiseValue);
         }
      }
   }

   private double getBlendingDataValue(final int cellX, final int cellY, final int cellZ, final CellValueGetter cellValueGetter) {
      int chunkX = QuartPos.toSection(cellX);
      int chunkZ = QuartPos.toSection(cellZ);
      boolean minX = (cellX & 3) == 0;
      boolean minZ = (cellZ & 3) == 0;
      double value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ, cellX, cellY, cellZ);
      if (value == 1.7976931348623157E308) {
         if (minX && minZ) {
            value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ - 1, cellX, cellY, cellZ);
         }

         if (value == 1.7976931348623157E308) {
            if (minX) {
               value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ, cellX, cellY, cellZ);
            }

            if (value == 1.7976931348623157E308 && minZ) {
               value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ - 1, cellX, cellY, cellZ);
            }
         }
      }

      return value;
   }

   private double getBlendingDataValue(final CellValueGetter cellValueGetter, final int chunkX, final int chunkZ, final int cellX, final int cellY, final int cellZ) {
      BlendingData blendingData = (BlendingData)this.heightAndBiomeBlendingData.get(ChunkPos.pack(chunkX, chunkZ));
      return blendingData != null ? cellValueGetter.get(blendingData, cellX - QuartPos.fromSection(chunkX), cellY, cellZ - QuartPos.fromSection(chunkZ)) : 1.7976931348623157E308;
   }

   public BiomeResolver getBiomeResolver(final BiomeResolver biomeResolver) {
      return (quartX, quartY, quartZ, sampler) -> {
         Holder<Biome> biome = this.blendBiome(quartX, quartY, quartZ);
         return biome == null ? biomeResolver.getNoiseBiome(quartX, quartY, quartZ, sampler) : biome;
      };
   }

   private Holder<Biome> blendBiome(final int quartX, final int quartY, final int quartZ) {
      MutableDouble closestDistance = new MutableDouble(1.0 / 0.0);
      MutableObject<Holder<Biome>> closestBiome = new MutableObject();
      this.heightAndBiomeBlendingData.forEach((chunkPos, blendingData) -> blendingData.iterateBiomes(QuartPos.fromSection(ChunkPos.getX(chunkPos)), quartY, QuartPos.fromSection(ChunkPos.getZ(chunkPos)), (testCellX, testCellZ, biome) -> {
            double distance = (double)Mth.length((float)(quartX - testCellX), (float)(quartZ - testCellZ));
            if (!(distance > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
               if (distance < closestDistance.doubleValue()) {
                  closestBiome.setValue(biome);
                  closestDistance.setValue(distance);
               }

            }
         }));
      if (closestDistance.doubleValue() == 1.0 / 0.0) {
         return null;
      } else {
         double shiftNoise = SHIFT_NOISE.getValue((double)quartX, 0.0, (double)quartZ) * 12.0;
         double alpha = Mth.clamp((closestDistance.doubleValue() + shiftNoise) / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0, 1.0);
         return alpha > 0.5 ? null : (Holder)closestBiome.get();
      }
   }

   public static void generateBorderTicks(final WorldGenRegion region, final ChunkAccess chunk) {
      if (!SharedConstants.DEBUG_DISABLE_BLENDING) {
         ChunkPos chunkPos = chunk.getPos();
         boolean oldNoiseGeneration = chunk.isOldNoiseGeneration();
         BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
         BlockPos chunkOrigin = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
         BlendingData blendingData = chunk.getBlendingData();
         if (blendingData != null) {
            int oldMinY = blendingData.getAreaWithOldGeneration().getMinY();
            int oldMaxY = blendingData.getAreaWithOldGeneration().getMaxY();
            if (oldNoiseGeneration) {
               for(int x = 0; x < 16; ++x) {
                  for(int z = 0; z < 16; ++z) {
                     generateBorderTick(chunk, pos.setWithOffset(chunkOrigin, x, oldMinY - 1, z));
                     generateBorderTick(chunk, pos.setWithOffset(chunkOrigin, x, oldMinY, z));
                     generateBorderTick(chunk, pos.setWithOffset(chunkOrigin, x, oldMaxY, z));
                     generateBorderTick(chunk, pos.setWithOffset(chunkOrigin, x, oldMaxY + 1, z));
                  }
               }
            }

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (region.getChunk(chunkPos.x() + direction.getStepX(), chunkPos.z() + direction.getStepZ()).isOldNoiseGeneration() != oldNoiseGeneration) {
                  int minX = direction == Direction.EAST ? 15 : 0;
                  int maxX = direction == Direction.WEST ? 0 : 15;
                  int minZ = direction == Direction.SOUTH ? 15 : 0;
                  int maxZ = direction == Direction.NORTH ? 0 : 15;

                  for(int x = minX; x <= maxX; ++x) {
                     for(int z = minZ; z <= maxZ; ++z) {
                        int maxY = Math.min(oldMaxY, chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)) + 1;

                        for(int y = oldMinY; y < maxY; ++y) {
                           generateBorderTick(chunk, pos.setWithOffset(chunkOrigin, x, y, z));
                        }
                     }
                  }
               }
            }

         }
      }
   }

   private static void generateBorderTick(final ChunkAccess chunk, final BlockPos pos) {
      BlockState blockState = chunk.getBlockState(pos);
      if (blockState.is(BlockTags.LEAVES)) {
         chunk.markPosForPostProcessing(pos);
      }

      FluidState fluidState = chunk.getFluidState(pos);
      if (!fluidState.isEmpty()) {
         chunk.markPosForPostProcessing(pos);
      }

   }

   public static void addAroundOldChunksCarvingMaskFilter(final WorldGenLevel region, final ProtoChunk chunk) {
      if (!SharedConstants.DEBUG_DISABLE_BLENDING) {
         ChunkPos chunkPos = chunk.getPos();
         ImmutableMap.Builder<CompositeDirection.Direction8, BlendingData> builder = ImmutableMap.builder();

         for(CompositeDirection.Direction8 direction8 : CompositeDirection.Direction8.values()) {
            int testChunkX = chunkPos.x() + direction8.getStepX();
            int testChunkZ = chunkPos.z() + direction8.getStepZ();
            BlendingData blendingData = region.getChunk(testChunkX, testChunkZ).getBlendingData();
            if (blendingData != null) {
               builder.put(direction8, blendingData);
            }
         }

         ImmutableMap<CompositeDirection.Direction8, BlendingData> oldSidesBlendingData = builder.build();
         if (chunk.isOldNoiseGeneration() || !oldSidesBlendingData.isEmpty()) {
            DistanceGetter distanceGetter = makeOldChunkDistanceGetter(chunk.getBlendingData(), oldSidesBlendingData);
            CarvingMask.Mask filter = (x, y, z) -> {
               double shiftedX = (double)x + 0.5 + SHIFT_NOISE.getValue((double)x, (double)y, (double)z) * 4.0;
               double shiftedY = (double)y + 0.5 + SHIFT_NOISE.getValue((double)y, (double)z, (double)x) * 4.0;
               double shiftedZ = (double)z + 0.5 + SHIFT_NOISE.getValue((double)z, (double)x, (double)y) * 4.0;
               return distanceGetter.getDistance(shiftedX, shiftedY, shiftedZ) < 4.0;
            };
            chunk.getOrCreateCarvingMask().setAdditionalMask(filter);
         }
      }
   }

   public static DistanceGetter makeOldChunkDistanceGetter(final @Nullable BlendingData centerBlendingData, final Map<CompositeDirection.Direction8, BlendingData> oldSidesBlendingData) {
      List<DistanceGetter> distanceGetters = Lists.newArrayList();
      if (centerBlendingData != null) {
         distanceGetters.add(makeOffsetOldChunkDistanceGetter((CompositeDirection.Direction8)null, centerBlendingData));
      }

      oldSidesBlendingData.forEach((side, blendingData) -> distanceGetters.add(makeOffsetOldChunkDistanceGetter(side, blendingData)));
      return (x, y, z) -> {
         double closest = 1.0 / 0.0;

         for(DistanceGetter getter : distanceGetters) {
            double distance = getter.getDistance(x, y, z);
            if (distance < closest) {
               closest = distance;
            }
         }

         return closest;
      };
   }

   private static DistanceGetter makeOffsetOldChunkDistanceGetter(final CompositeDirection.@Nullable Direction8 offset, final BlendingData blendingData) {
      double offsetX = 0.0;
      double offsetZ = 0.0;
      if (offset != null) {
         for(Direction direction : offset.getDirections()) {
            offsetX += (double)(direction.getStepX() * 16);
            offsetZ += (double)(direction.getStepZ() * 16);
         }
      }

      double oldChunkYRadius = (double)blendingData.getAreaWithOldGeneration().getHeight() / 2.0;
      double oldChunkCenterY = (double)blendingData.getAreaWithOldGeneration().getMinY() + oldChunkYRadius;
      return (x, y, z) -> distanceToCube(x - 8.0 - offsetX, y - oldChunkCenterY, z - 8.0 - offsetZ, 8.0, oldChunkYRadius, 8.0);
   }

   private static double distanceToCube(final double x, final double y, final double z, final double radiusX, final double radiusY, final double radiusZ) {
      double deltaX = Math.abs(x) - radiusX;
      double deltaY = Math.abs(y) - radiusY;
      double deltaZ = Math.abs(z) - radiusZ;
      return Mth.length(Math.max(0.0, deltaX), Math.max(0.0, deltaY), Math.max(0.0, deltaZ));
   }

   static {
      SHIFT_NOISE = NormalNoise.create(new XoroshiroRandomSource(42L), NoiseData.DEFAULT_SHIFT);
      HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
      HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(HEIGHT_BLENDING_RANGE_CELLS + 3);
      DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
   }

   public static record BlendingOutput(double alpha, double blendingOffset) {
      public BlendingOutput {
         super();
      }
   }

   private interface CellValueGetter {
      double get(BlendingData data, int cellX, int cellY, int cellZ);
   }

   public interface DistanceGetter {
      double getDistance(double x, double y, double z);
   }
}
