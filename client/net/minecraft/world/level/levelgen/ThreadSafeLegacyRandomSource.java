package net.minecraft.world.level.levelgen;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.RandomSource;

/** @deprecated */
@Deprecated
public class ThreadSafeLegacyRandomSource implements BitRandomSource {
   private static final int MODULUS_BITS = 48;
   private static final long MODULUS_MASK = 281474976710655L;
   private static final long MULTIPLIER = 25214903917L;
   private static final long INCREMENT = 11L;
   private final AtomicLong seed = new AtomicLong();
   private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

   public ThreadSafeLegacyRandomSource(long var1) {
      super();
      this.setSeed(var1);
   }

   public RandomSource fork() {
      return new ThreadSafeLegacyRandomSource(this.nextLong());
   }

   public PositionalRandomFactory forkPositional() {
      return new LegacyRandomSource.LegacyPositionalRandomFactory(this.nextLong());
   }

   public void setSeed(long var1) {
      this.seed.set((var1 ^ 25214903917L) & 281474976710655L);
   }

   public int next(int var1) {
      long var2;
      long var4;
      do {
         var2 = this.seed.get();
         var4 = var2 * 25214903917L + 11L & 281474976710655L;
      } while(!this.seed.compareAndSet(var2, var4));

      return (int)(var4 >>> 48 - var1);
   }

   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }
}
