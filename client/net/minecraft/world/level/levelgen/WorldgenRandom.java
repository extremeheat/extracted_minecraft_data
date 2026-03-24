package net.minecraft.world.level.levelgen;

import java.util.function.LongFunction;
import net.minecraft.util.RandomSource;

public class WorldgenRandom extends LegacyRandomSource {
   private final RandomSource randomSource;
   private int count;

   public WorldgenRandom(final RandomSource randomSource) {
      super(0L);
      this.randomSource = randomSource;
   }

   public int getCount() {
      return this.count;
   }

   public RandomSource fork() {
      return this.randomSource.fork();
   }

   public PositionalRandomFactory forkPositional() {
      return this.randomSource.forkPositional();
   }

   public int next(final int bits) {
      ++this.count;
      RandomSource var3 = this.randomSource;
      if (var3 instanceof LegacyRandomSource legacyRandomSource) {
         return legacyRandomSource.next(bits);
      } else {
         return (int)(this.randomSource.nextLong() >>> 64 - bits);
      }
   }

   public synchronized void setSeed(final long seed) {
      if (this.randomSource != null) {
         this.randomSource.setSeed(seed);
      }
   }

   public long setDecorationSeed(final long seed, final int chunkX, final int chunkZ) {
      this.setSeed(seed);
      long xScale = this.nextLong() | 1L;
      long zScale = this.nextLong() | 1L;
      long result = (long)chunkX * xScale + (long)chunkZ * zScale ^ seed;
      this.setSeed(result);
      return result;
   }

   public void setFeatureSeed(final long seed, final int index, final int step) {
      long result = seed + (long)index + (long)(10000 * step);
      this.setSeed(result);
   }

   public void setLargeFeatureSeed(final long seed, final int chunkX, final int chunkZ) {
      this.setSeed(seed);
      long xScale = this.nextLong();
      long zScale = this.nextLong();
      long result = (long)chunkX * xScale ^ (long)chunkZ * zScale ^ seed;
      this.setSeed(result);
   }

   public void setLargeFeatureWithSalt(final long seed, final int x, final int z, final int blend) {
      long result = (long)x * 341873128712L + (long)z * 132897987541L + seed + (long)blend;
      this.setSeed(result);
   }

   public static RandomSource seedSlimeChunk(final int x, final int z, final long seed, final long salt) {
      return RandomSource.createThreadLocalInstance(seed + (long)(x * x * 4987142) + (long)(x * 5947611) + (long)(z * z) * 4392871L + (long)(z * 389711) ^ salt);
   }

   public static enum Algorithm {
      LEGACY(LegacyRandomSource::new),
      XOROSHIRO(XoroshiroRandomSource::new);

      private final LongFunction<RandomSource> constructor;

      private Algorithm(final LongFunction<RandomSource> constructor) {
         this.constructor = constructor;
      }

      public RandomSource newInstance(final long seed) {
         return (RandomSource)this.constructor.apply(seed);
      }

      // $FF: synthetic method
      private static Algorithm[] $values() {
         return new Algorithm[]{LEGACY, XOROSHIRO};
      }
   }
}
