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

   public LegacyRandomSource(long var1) {
      super();
      this.setSeed(var1);
   }

   @Override
   public RandomSource fork() {
      return new LegacyRandomSource(this.nextLong());
   }

   @Override
   public PositionalRandomFactory forkPositional() {
      return new LegacyRandomSource.LegacyPositionalRandomFactory(this.nextLong());
   }

   @Override
   public void setSeed(long var1) {
      if (!this.seed.compareAndSet(this.seed.get(), (var1 ^ 25214903917L) & 281474976710655L)) {
         throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
      } else {
         this.gaussianSource.reset();
      }
   }

   @Override
   public int next(int var1) {
      long var2 = this.seed.get();
      long var4 = var2 * 25214903917L + 11L & 281474976710655L;
      if (!this.seed.compareAndSet(var2, var4)) {
         throw ThreadingDetector.makeThreadingException("LegacyRandomSource", null);
      } else {
         return (int)(var4 >> 48 - var1);
      }
   }

   @Override
   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }

   public static class LegacyPositionalRandomFactory implements PositionalRandomFactory {
      private final long seed;

      public LegacyPositionalRandomFactory(long var1) {
         super();
         this.seed = var1;
      }

      @Override
      public RandomSource at(int var1, int var2, int var3) {
         long var4 = Mth.getSeed(var1, var2, var3);
         long var6 = var4 ^ this.seed;
         return new LegacyRandomSource(var6);
      }

      @Override
      public RandomSource fromHashOf(String var1) {
         int var2 = var1.hashCode();
         return new LegacyRandomSource((long)var2 ^ this.seed);
      }

      @Override
      public RandomSource fromSeed(long var1) {
         return new LegacyRandomSource(var1);
      }

      @VisibleForTesting
      @Override
      public void parityConfigString(StringBuilder var1) {
         var1.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
      }
   }
}
