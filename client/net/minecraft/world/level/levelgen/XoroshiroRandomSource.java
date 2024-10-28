package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class XoroshiroRandomSource implements RandomSource {
   private static final float FLOAT_UNIT = 5.9604645E-8F;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16;
   public static final Codec<XoroshiroRandomSource> CODEC;
   private Xoroshiro128PlusPlus randomNumberGenerator;
   private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

   public XoroshiroRandomSource(long var1) {
      super();
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(var1));
   }

   public XoroshiroRandomSource(RandomSupport.Seed128bit var1) {
      super();
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(var1);
   }

   public XoroshiroRandomSource(long var1, long var3) {
      super();
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(var1, var3);
   }

   private XoroshiroRandomSource(Xoroshiro128PlusPlus var1) {
      super();
      this.randomNumberGenerator = var1;
   }

   public RandomSource fork() {
      return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   public PositionalRandomFactory forkPositional() {
      return new XoroshiroPositionalRandomFactory(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   public void setSeed(long var1) {
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(var1));
      this.gaussianSource.reset();
   }

   public int nextInt() {
      return (int)this.randomNumberGenerator.nextLong();
   }

   public int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else {
         long var2 = Integer.toUnsignedLong(this.nextInt());
         long var4 = var2 * (long)var1;
         long var6 = var4 & 4294967295L;
         if (var6 < (long)var1) {
            for(int var8 = Integer.remainderUnsigned(~var1 + 1, var1); var6 < (long)var8; var6 = var4 & 4294967295L) {
               var2 = Integer.toUnsignedLong(this.nextInt());
               var4 = var2 * (long)var1;
            }
         }

         long var10 = var4 >> 32;
         return (int)var10;
      }
   }

   public long nextLong() {
      return this.randomNumberGenerator.nextLong();
   }

   public boolean nextBoolean() {
      return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
   }

   public float nextFloat() {
      return (float)this.nextBits(24) * 5.9604645E-8F;
   }

   public double nextDouble() {
      return (double)this.nextBits(53) * 1.1102230246251565E-16;
   }

   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }

   public void consumeCount(int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         this.randomNumberGenerator.nextLong();
      }

   }

   private long nextBits(int var1) {
      return this.randomNumberGenerator.nextLong() >>> 64 - var1;
   }

   static {
      CODEC = Xoroshiro128PlusPlus.CODEC.xmap((var0) -> {
         return new XoroshiroRandomSource(var0);
      }, (var0) -> {
         return var0.randomNumberGenerator;
      });
   }

   public static class XoroshiroPositionalRandomFactory implements PositionalRandomFactory {
      private final long seedLo;
      private final long seedHi;

      public XoroshiroPositionalRandomFactory(long var1, long var3) {
         super();
         this.seedLo = var1;
         this.seedHi = var3;
      }

      public RandomSource at(int var1, int var2, int var3) {
         long var4 = Mth.getSeed(var1, var2, var3);
         long var6 = var4 ^ this.seedLo;
         return new XoroshiroRandomSource(var6, this.seedHi);
      }

      public RandomSource fromHashOf(String var1) {
         RandomSupport.Seed128bit var2 = RandomSupport.seedFromHashOf(var1);
         return new XoroshiroRandomSource(var2.xor(this.seedLo, this.seedHi));
      }

      public RandomSource fromSeed(long var1) {
         return new XoroshiroRandomSource(var1 ^ this.seedLo, var1 ^ this.seedHi);
      }

      @VisibleForTesting
      public void parityConfigString(StringBuilder var1) {
         var1.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
      }
   }
}
