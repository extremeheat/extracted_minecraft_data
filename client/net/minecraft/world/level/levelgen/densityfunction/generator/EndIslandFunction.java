package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public record EndIslandFunction() implements DensityFunction {
   public static final MapCodec<EndIslandFunction> CODEC = MapCodec.unit(new EndIslandFunction());
   private static final float ISLAND_THRESHOLD = -0.9F;

   public EndIslandFunction() {
      super();
   }

   private static float getHeightValue(final SimplexNoise islandNoise, final int sectionX, final int sectionZ) {
      int chunkX = sectionX / 2;
      int chunkZ = sectionZ / 2;
      int subSectionX = sectionX % 2;
      int subSectionZ = sectionZ % 2;
      float doffs = -100.0F;

      for(int xo = -12; xo <= 12; ++xo) {
         for(int zo = -12; zo <= 12; ++zo) {
            long totalChunkX = (long)(chunkX + xo);
            long totalChunkZ = (long)(chunkZ + zo);
            if (totalChunkX * totalChunkX + totalChunkZ * totalChunkZ > 4096L && islandNoise.get((double)totalChunkX, (double)totalChunkZ) < -0.9F) {
               float islandSize = (Mth.abs((float)totalChunkX) * 3439.0F + Mth.abs((float)totalChunkZ) * 147.0F) % 13.0F + 9.0F;
               float xd = (float)(subSectionX - xo * 2);
               float zd = (float)(subSectionZ - zo * 2);
               float newDoffs = 100.0F - Mth.sqrt(xd * xd + zd * zd) * islandSize;
               newDoffs = Mth.clamp(newDoffs, -100.0F, 80.0F);
               doffs = Math.max(doffs, newDoffs);
            }
         }
      }

      return doffs;
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      RandomSource islandRandom = context.createEndIslandRandom();
      islandRandom.consumeCount(17292);
      SimplexNoise islandNoise = new SimplexNoise(islandRandom, true);
      return new Sampler(islandNoise);
   }

   public Interval range() {
      return Interval.of(-0.84375F, 0.5625F);
   }

   public @DensityFunction.Axes int domainAxes() {
      return 5;
   }

   public MapCodec<EndIslandFunction> codec() {
      return CODEC;
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      return this;
   }

   private static record Sampler(SimplexNoise islandNoise) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         for(int z = 0; z < volume.sizeZ(); ++z) {
            int blockZ = volume.blockZ(z);

            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);
               float value = this.sampleValue(context, blockX, 0, blockZ);
               int index = volume.indexUnchecked(x, 0, z);
               outputBuffer.setRange(index, volume.sizeY(), value);
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return (EndIslandFunction.getHeightValue(this.islandNoise, blockX / 8, blockZ / 8) - 8.0F) / 128.0F;
      }
   }
}
