package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public final class EndIslandFunction implements DensityFunction {
   public static final MapCodec<EndIslandFunction> CODEC = MapCodec.unit(new EndIslandFunction(0L));
   private static final float ISLAND_THRESHOLD = -0.9F;
   private final SimplexNoise islandNoise;

   public EndIslandFunction(final long seed) {
      super();
      RandomSource islandRandom = new LegacyRandomSource(seed);
      islandRandom.consumeCount(17292);
      this.islandNoise = new SimplexNoise(islandRandom, true);
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

   public float compute(final DensityFunction.FunctionContext context) {
      return (getHeightValue(this.islandNoise, context.blockX() / 8, context.blockZ() / 8) - 8.0F) / 128.0F;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
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

   public boolean equals(final Object obj) {
      return obj instanceof EndIslandFunction;
   }

   public int hashCode() {
      return 0;
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }
}
