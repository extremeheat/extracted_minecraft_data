package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ThreadingDetector;

public class LegacyRandomSource implements BitRandomSource {
   private static final int MODULUS_BITS = 48;
   private static final long MODULUS_MASK = 281474976710655L;
   private static final long MULTIPLIER = 25214903917L;
   private static final long INCREMENT = 11L;
   private final AtomicLong seed = new AtomicLong();
   private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

   public LegacyRandomSource(final long seed) {
      super();
      this.setSeed(seed);
   }

   public RandomSource fork() {
      return new LegacyRandomSource(this.nextLong());
   }

   public PositionalRandomFactory forkPositional() {
      return new LegacyPositionalRandomFactory(this.nextLong());
   }

   public void setSeed(final long seed) {
      if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 25214903917L) & 281474976710655L)) {
         throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread)null);
      } else {
         this.gaussianSource.reset();
      }
   }

   public int next(final int bits) {
      long oldSeed = this.seed.get();
      long newSeed = oldSeed * 25214903917L + 11L & 281474976710655L;
      if (!this.seed.compareAndSet(oldSeed, newSeed)) {
         throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread)null);
      } else {
         return (int)(newSeed >> 48 - bits);
      }
   }

   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }

   public static class LegacyPositionalRandomFactory implements PositionalRandomFactory {
      private final long seed;

      public LegacyPositionalRandomFactory(final long seed) {
         super();
         this.seed = seed;
      }

      public RandomSource at(final int x, final int y, final int z) {
         long positionalSeed = Mth.getSeed(x, y, z);
         long randomSeed = positionalSeed ^ this.seed;
         return new LegacyRandomSource(randomSeed);
      }

      public RandomSource fromHashOf(final String name) {
         int positionalSeed = name.hashCode();
         return new LegacyRandomSource((long)positionalSeed ^ this.seed);
      }

      public RandomSource fromSeed(final long seed) {
         return new LegacyRandomSource(seed);
      }

      @VisibleForTesting
      public void parityConfigString(final StringBuilder sb) {
         sb.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
      }
   }
}
