package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class XoroshiroRandomSource implements RandomSource {
   private static final float FLOAT_UNIT = 5.9604645E-8F;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16;
   public static final Codec<XoroshiroRandomSource> CODEC = Xoroshiro128PlusPlus.CODEC
      .xmap(var0 -> new XoroshiroRandomSource(var0), var0 -> var0.randomNumberGenerator);
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

   @Override
   public RandomSource fork() {
      return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   @Override
   public PositionalRandomFactory forkPositional() {
      return new XoroshiroRandomSource.XoroshiroPositionalRandomFactory(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
   }

   @Override
   public void setSeed(long var1) {
      this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(var1));
      this.gaussianSource.reset();
   }

   @Override
   public int nextInt() {
      return (int)this.randomNumberGenerator.nextLong();
   }

   @Override
   public int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else {
         long var2 = Integer.toUnsignedLong(this.nextInt());
         long var4 = var2 * (long)var1;
         long var6 = var4 & 4294967295L;
         if (var6 < (long)var1) {
            for (int var8 = Integer.remainderUnsigned(~var1 + 1, var1); var6 < (long)var8; var6 = var4 & 4294967295L) {
               var2 = Integer.toUnsignedLong(this.nextInt());
               var4 = var2 * (long)var1;
            }
         }

         long var11 = var4 >> 32;
         return (int)var11;
      }
   }

   @Override
   public long nextLong() {
      return this.randomNumberGenerator.nextLong();
   }

   @Override
   public boolean nextBoolean() {
      return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
   }

   @Override
   public float nextFloat() {
      return (float)this.nextBits(24) * 5.9604645E-8F;
   }

   @Override
   public double nextDouble() {
      return (double)this.nextBits(53) * 1.1102230246251565E-16;
   }

   @Override
   public double nextGaussian() {
      return this.gaussianSource.nextGaussian();
   }

   @Override
   public void consumeCount(int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         this.randomNumberGenerator.nextLong();
      }
   }

   private long nextBits(int var1) {
      return this.randomNumberGenerator.nextLong() >>> 64 - var1;
   }

   public static class XoroshiroPositionalRandomFactory implements PositionalRandomFactory {
      private final long seedLo;
      private final long seedHi;

      public XoroshiroPositionalRandomFactory(long var1, long var3) {
         super();
         this.seedLo = var1;
         this.seedHi = var3;
      }

      @Override
      public RandomSource at(int var1, int var2, int var3) {
         long var4 = Mth.getSeed(var1, var2, var3);
         long var6 = var4 ^ this.seedLo;
         return new XoroshiroRandomSource(var6, this.seedHi);
      }

      @Override
      public RandomSource fromHashOf(String var1) {
         RandomSupport.Seed128bit var2 = RandomSupport.seedFromHashOf(var1);
         return new XoroshiroRandomSource(var2.xor(this.seedLo, this.seedHi));
      }

      @VisibleForTesting
      @Override
      public void parityConfigString(StringBuilder var1) {
         var1.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
      }
   }
}