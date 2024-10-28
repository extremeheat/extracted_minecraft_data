package net.minecraft.world.level.levelgen;

import java.util.function.LongFunction;
import net.minecraft.util.RandomSource;

public class WorldgenRandom extends LegacyRandomSource {
   private final RandomSource randomSource;
   private int count;

   public WorldgenRandom(RandomSource var1) {
      super(0L);
      this.randomSource = var1;
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

   public int next(int var1) {
      ++this.count;
      RandomSource var3 = this.randomSource;
      if (var3 instanceof LegacyRandomSource var2) {
         return var2.next(var1);
      } else {
         return (int)(this.randomSource.nextLong() >>> 64 - var1);
      }
   }

   public synchronized void setSeed(long var1) {
      if (this.randomSource != null) {
         this.randomSource.setSeed(var1);
      }
   }

   public long setDecorationSeed(long var1, int var3, int var4) {
      this.setSeed(var1);
      long var5 = this.nextLong() | 1L;
      long var7 = this.nextLong() | 1L;
      long var9 = (long)var3 * var5 + (long)var4 * var7 ^ var1;
      this.setSeed(var9);
      return var9;
   }

   public void setFeatureSeed(long var1, int var3, int var4) {
      long var5 = var1 + (long)var3 + (long)(10000 * var4);
      this.setSeed(var5);
   }

   public void setLargeFeatureSeed(long var1, int var3, int var4) {
      this.setSeed(var1);
      long var5 = this.nextLong();
      long var7 = this.nextLong();
      long var9 = (long)var3 * var5 ^ (long)var4 * var7 ^ var1;
      this.setSeed(var9);
   }

   public void setLargeFeatureWithSalt(long var1, int var3, int var4, int var5) {
      long var6 = (long)var3 * 341873128712L + (long)var4 * 132897987541L + var1 + (long)var5;
      this.setSeed(var6);
   }

   public static RandomSource seedSlimeChunk(int var0, int var1, long var2, long var4) {
      return RandomSource.create(var2 + (long)(var0 * var0 * 4987142) + (long)(var0 * 5947611) + (long)(var1 * var1) * 4392871L + (long)(var1 * 389711) ^ var4);
   }

   public static enum Algorithm {
      LEGACY(LegacyRandomSource::new),
      XOROSHIRO(XoroshiroRandomSource::new);

      private final LongFunction<RandomSource> constructor;

      private Algorithm(final LongFunction var3) {
         this.constructor = var3;
      }

      public RandomSource newInstance(long var1) {
         return (RandomSource)this.constructor.apply(var1);
      }

      // $FF: synthetic method
      private static Algorithm[] $values() {
         return new Algorithm[]{LEGACY, XOROSHIRO};
      }
   }
}
