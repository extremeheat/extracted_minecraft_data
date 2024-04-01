package net.minecraft.util;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;

public interface RandomSource {
   @Deprecated
   double GAUSSIAN_SPREAD_FACTOR = 2.297;

   static RandomSource create() {
      return create(RandomSupport.generateUniqueSeed());
   }

   @Deprecated
   static RandomSource createThreadSafe() {
      return new ThreadSafeLegacyRandomSource(RandomSupport.generateUniqueSeed());
   }

   static RandomSource create(long var0) {
      return new LegacyRandomSource(var0);
   }

   static RandomSource createNewThreadLocalInstance() {
      return new SingleThreadedRandomSource(ThreadLocalRandom.current().nextLong());
   }

   RandomSource fork();

   PositionalRandomFactory forkPositional();

   void setSeed(long var1);

   int nextInt();

   int nextInt(int var1);

   default int nextIntBetweenInclusive(int var1, int var2) {
      return this.nextInt(var2 - var1 + 1) + var1;
   }

   long nextLong();

   boolean nextBoolean();

   float nextFloat();

   double nextDouble();

   double nextGaussian();

   default double triangle(double var1, double var3) {
      return var1 + var3 * (this.nextDouble() - this.nextDouble());
   }

   default void consumeCount(int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         this.nextInt();
      }
   }

   default int nextInt(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException("bound - origin is non positive");
      } else {
         return var1 + this.nextInt(var2 - var1);
      }
   }

   default float nextFloat(float var1, float var2) {
      return var1 + this.nextFloat() * (var2 - var1);
   }
}
