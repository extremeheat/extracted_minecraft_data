package net.minecraft.world.level.levelgen;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

public class NoiseSpawnFinder {
   private static final long MAX_RADIUS = 2048L;
   private Result result;

   private NoiseSpawnFinder(final List<SpawnTargetPoint> targetPoints, final DensitySamplerSet samplers) {
      super();
      this.result = getSpawnPositionAndFitness(samplers, targetPoints, 0, 0);
      this.radialSearch(samplers, targetPoints, 2048.0F, 512.0F);
      this.radialSearch(samplers, targetPoints, 512.0F, 32.0F);
   }

   public static BlockPos findSpawnPosition(final List<SpawnTargetPoint> targetPoints, final DensitySamplerSet samplers) {
      return (new NoiseSpawnFinder(targetPoints, samplers)).result.location();
   }

   private void radialSearch(final DensitySamplerSet samplers, final List<SpawnTargetPoint> targetPoints, final float maxRadius, final float radiusIncrement) {
      float angle = 0.0F;
      float radius = radiusIncrement;
      BlockPos searchOrigin = this.result.location();

      while(radius <= maxRadius) {
         int x = searchOrigin.getX() + (int)(Math.sin((double)angle) * (double)radius);
         int z = searchOrigin.getZ() + (int)(Math.cos((double)angle) * (double)radius);
         Result candidate = getSpawnPositionAndFitness(samplers, targetPoints, x, z);
         if (candidate.fitness() < this.result.fitness()) {
            this.result = candidate;
         }

         angle += radiusIncrement / radius;
         if ((double)angle > 6.283185307179586) {
            angle = 0.0F;
            radius += radiusIncrement;
         }
      }

   }

   private static Result getSpawnPositionAndFitness(final DensitySamplerSet samplers, final List<SpawnTargetPoint> targetPoints, final int blockX, final int blockZ) {
      int quartBlockX = QuartPos.toBlock(QuartPos.fromBlock(blockX));
      int quartBlockZ = QuartPos.toBlock(QuartPos.fromBlock(blockZ));
      long minFitness = 9223372036854775807L;

      for(SpawnTargetPoint point : targetPoints) {
         minFitness = Math.min(minFitness, point.sampleFitness(samplers, quartBlockX, 0, quartBlockZ));
      }

      long distanceBiasToWorldOrigin = Mth.square((long)blockX) + Mth.square((long)blockZ);
      long fitnessWithDistance = minFitness * Mth.square(2048L) + distanceBiasToWorldOrigin;
      return new Result(new BlockPos(blockX, 0, blockZ), fitnessWithDistance);
   }

   private static record Result(BlockPos location, long fitness) {
      private Result {
         super();
      }
   }
}
