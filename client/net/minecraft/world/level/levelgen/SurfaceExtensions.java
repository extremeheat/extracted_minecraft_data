package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.synth.Noise;

/** @deprecated */
@Deprecated
public class SurfaceExtensions {
   private static final BlockState PACKED_ICE;
   private static final BlockState SNOW_BLOCK;
   private final int seaLevel;
   private final PositionalRandomFactory noiseRandom;
   private final Noise badlandsPillarNoise;
   private final Noise badlandsPillarRoofNoise;
   private final Noise badlandsSurfaceNoise;
   private final Noise icebergPillarNoise;
   private final Noise icebergPillarRoofNoise;
   private final Noise icebergSurfaceNoise;

   public SurfaceExtensions(final RandomState randomState, final int seaLevel, final PositionalRandomFactory noiseRandom) {
      super();
      this.seaLevel = seaLevel;
      this.noiseRandom = noiseRandom;
      this.badlandsPillarNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR);
      this.badlandsPillarRoofNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR_ROOF);
      this.badlandsSurfaceNoise = randomState.getOrCreateNoise(Noises.BADLANDS_SURFACE);
      this.icebergPillarNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR);
      this.icebergPillarRoofNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
      this.icebergSurfaceNoise = randomState.getOrCreateNoise(Noises.ICEBERG_SURFACE);
   }

   public void extendErodedBadlands(final NoiseColumn column, final int blockX, final int blockZ) {
      double pillarNoiseScale = 0.2;
      double pillarBuffer = Math.min(Math.abs((double)this.badlandsSurfaceNoise.get((double)blockX, 0.0, (double)blockZ) * 8.25), (double)(this.badlandsPillarNoise.get((double)blockX * 0.2, 0.0, (double)blockZ * 0.2) * 15.0F));
      if (!(pillarBuffer <= 0.0)) {
         double floorNoiseSampleResolution = 0.75;
         double floorAmplitude = 1.5;
         double pillarFloor = Math.abs((double)this.badlandsPillarRoofNoise.get((double)blockX * 0.75, 0.0, (double)blockZ * 0.75) * 1.5);
         double extensionTop = 64.0 + Math.min(pillarBuffer * pillarBuffer * 2.5, Math.ceil(pillarFloor * 50.0) + 24.0);
         int startBlockY = Mth.floor(extensionTop);
         if (column.topBlockY() + 1 <= startBlockY) {
            for(int y = startBlockY; y >= column.minBlockY(); --y) {
               BlockState block = column.getBlock(y);
               if (block == NoiseColumn.SOLID) {
                  break;
               }

               if (block.is(Blocks.WATER)) {
                  return;
               }
            }

            for(int y = startBlockY; y >= column.minBlockY() && column.isEmpty(y); --y) {
               column.setSolid(y);
            }

         }
      }
   }

   public void extendFrozenOcean(final int minSurfaceLevel, final Biome surfaceBiome, final BlockColumn column, final int blockX, final int blockZ, final int height) {
      double pillarScale = 1.28;
      double iceberg = Math.min(Math.abs((double)this.icebergSurfaceNoise.get((double)blockX, 0.0, (double)blockZ) * 8.25), (double)(this.icebergPillarNoise.get((double)blockX * 1.28, 0.0, (double)blockZ * 1.28) * 15.0F));
      if (!(iceberg <= 1.8)) {
         double roofScale = 1.17;
         double roofAmplitude = 1.5;
         double icebergRoof = Math.abs((double)this.icebergPillarRoofNoise.get((double)blockX * 1.17, 0.0, (double)blockZ * 1.17) * 1.5);
         double top = Math.min(iceberg * iceberg * 1.2, Math.ceil(icebergRoof * 40.0) + 14.0);
         if (surfaceBiome.shouldMeltFrozenOceanIcebergSlightly(new BlockPos(blockX, this.seaLevel, blockZ), this.seaLevel)) {
            top -= 2.0;
         }

         if (!(top <= 2.0)) {
            double extensionBottom = (double)this.seaLevel - top - 7.0;
            top += (double)this.seaLevel;
            double extensionTop = top;
            RandomSource random = this.noiseRandom.at(blockX, 0, blockZ);
            int maxSnowDepth = 2 + random.nextInt(4);
            int minSnowHeight = this.seaLevel + 18 + random.nextInt(10);
            int snowDepth = 0;

            for(int y = Math.max(height, (int)top + 1); y >= minSurfaceLevel; --y) {
               BlockState block = column.getBlock(y);
               if (block.isAir() && y < (int)extensionTop && random.nextDouble() > 0.01 || block.is(Blocks.WATER) && y > (int)extensionBottom && y < this.seaLevel && random.nextDouble() > 0.15) {
                  if (snowDepth <= maxSnowDepth && y > minSnowHeight) {
                     column.setBlock(y, SNOW_BLOCK);
                     ++snowDepth;
                  } else {
                     column.setBlock(y, PACKED_ICE);
                  }
               }
            }

         }
      }
   }

   static {
      PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
      SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   }
}
