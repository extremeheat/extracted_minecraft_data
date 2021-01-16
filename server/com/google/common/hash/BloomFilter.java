package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
public final class BloomFilter<T> implements Predicate<T>, Serializable {
   private final BloomFilterStrategies.BitArray bits;
   private final int numHashFunctions;
   private final Funnel<? super T> funnel;
   private final BloomFilter.Strategy strategy;

   private BloomFilter(BloomFilterStrategies.BitArray var1, int var2, Funnel<? super T> var3, BloomFilter.Strategy var4) {
      super();
      Preconditions.checkArgument(var2 > 0, "numHashFunctions (%s) must be > 0", var2);
      Preconditions.checkArgument(var2 <= 255, "numHashFunctions (%s) must be <= 255", var2);
      this.bits = (BloomFilterStrategies.BitArray)Preconditions.checkNotNull(var1);
      this.numHashFunctions = var2;
      this.funnel = (Funnel)Preconditions.checkNotNull(var3);
      this.strategy = (BloomFilter.Strategy)Preconditions.checkNotNull(var4);
   }

   public BloomFilter<T> copy() {
      return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
   }

   public boolean mightContain(T var1) {
      return this.strategy.mightContain(var1, this.funnel, this.numHashFunctions, this.bits);
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(T var1) {
      return this.mightContain(var1);
   }

   @CanIgnoreReturnValue
   public boolean put(T var1) {
      return this.strategy.put(var1, this.funnel, this.numHashFunctions, this.bits);
   }

   public double expectedFpp() {
      return Math.pow((double)this.bits.bitCount() / (double)this.bitSize(), (double)this.numHashFunctions);
   }

   @VisibleForTesting
   long bitSize() {
      return this.bits.bitSize();
   }

   public boolean isCompatible(BloomFilter<T> var1) {
      Preconditions.checkNotNull(var1);
      return this != var1 && this.numHashFunctions == var1.numHashFunctions && this.bitSize() == var1.bitSize() && this.strategy.equals(var1.strategy) && this.funnel.equals(var1.funnel);
   }

   public void putAll(BloomFilter<T> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this != var1, "Cannot combine a BloomFilter with itself.");
      Preconditions.checkArgument(this.numHashFunctions == var1.numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", this.numHashFunctions, var1.numHashFunctions);
      Preconditions.checkArgument(this.bitSize() == var1.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", this.bitSize(), var1.bitSize());
      Preconditions.checkArgument(this.strategy.equals(var1.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, var1.strategy);
      Preconditions.checkArgument(this.funnel.equals(var1.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, var1.funnel);
      this.bits.putAll(var1.bits);
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BloomFilter)) {
         return false;
      } else {
         BloomFilter var2 = (BloomFilter)var1;
         return this.numHashFunctions == var2.numHashFunctions && this.funnel.equals(var2.funnel) && this.bits.equals(var2.bits) && this.strategy.equals(var2.strategy);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.numHashFunctions, this.funnel, this.strategy, this.bits);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, int var1, double var2) {
      return create(var0, (long)var1, var2);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1, double var3) {
      return create(var0, var1, var3, BloomFilterStrategies.MURMUR128_MITZ_64);
   }

   @VisibleForTesting
   static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1, double var3, BloomFilter.Strategy var5) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0L, "Expected insertions (%s) must be >= 0", var1);
      Preconditions.checkArgument(var3 > 0.0D, "False positive probability (%s) must be > 0.0", (Object)var3);
      Preconditions.checkArgument(var3 < 1.0D, "False positive probability (%s) must be < 1.0", (Object)var3);
      Preconditions.checkNotNull(var5);
      if (var1 == 0L) {
         var1 = 1L;
      }

      long var6 = optimalNumOfBits(var1, var3);
      int var8 = optimalNumOfHashFunctions(var1, var6);

      try {
         return new BloomFilter(new BloomFilterStrategies.BitArray(var6), var8, var0, var5);
      } catch (IllegalArgumentException var10) {
         throw new IllegalArgumentException("Could not create BloomFilter of " + var6 + " bits", var10);
      }
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, int var1) {
      return create(var0, (long)var1);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1) {
      return create(var0, var1, 0.03D);
   }

   @VisibleForTesting
   static int optimalNumOfHashFunctions(long var0, long var2) {
      return Math.max(1, (int)Math.round((double)var2 / (double)var0 * Math.log(2.0D)));
   }

   @VisibleForTesting
   static long optimalNumOfBits(long var0, double var2) {
      if (var2 == 0.0D) {
         var2 = 4.9E-324D;
      }

      return (long)((double)(-var0) * Math.log(var2) / (Math.log(2.0D) * Math.log(2.0D)));
   }

   private Object writeReplace() {
      return new BloomFilter.SerialForm(this);
   }

   public void writeTo(OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(var1);
      var2.writeByte(SignedBytes.checkedCast((long)this.strategy.ordinal()));
      var2.writeByte(UnsignedBytes.checkedCast((long)this.numHashFunctions));
      var2.writeInt(this.bits.data.length);
      long[] var3 = this.bits.data;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long var6 = var3[var5];
         var2.writeLong(var6);
      }

   }

   public static <T> BloomFilter<T> readFrom(InputStream var0, Funnel<T> var1) throws IOException {
      Preconditions.checkNotNull(var0, "InputStream");
      Preconditions.checkNotNull(var1, "Funnel");
      byte var2 = -1;
      byte var3 = -1;
      byte var4 = -1;

      try {
         DataInputStream var5 = new DataInputStream(var0);
         var2 = var5.readByte();
         int var10 = UnsignedBytes.toInt(var5.readByte());
         int var11 = var5.readInt();
         BloomFilterStrategies var12 = BloomFilterStrategies.values()[var2];
         long[] var7 = new long[var11];

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = var5.readLong();
         }

         return new BloomFilter(new BloomFilterStrategies.BitArray(var7), var10, var1, var12);
      } catch (RuntimeException var9) {
         String var6 = "Unable to deserialize BloomFilter from InputStream. strategyOrdinal: " + var2 + " numHashFunctions: " + var3 + " dataLength: " + var4;
         throw new IOException(var6, var9);
      }
   }

   // $FF: synthetic method
   BloomFilter(BloomFilterStrategies.BitArray var1, int var2, Funnel var3, BloomFilter.Strategy var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   private static class SerialForm<T> implements Serializable {
      final long[] data;
      final int numHashFunctions;
      final Funnel<? super T> funnel;
      final BloomFilter.Strategy strategy;
      private static final long serialVersionUID = 1L;

      SerialForm(BloomFilter<T> var1) {
         super();
         this.data = var1.bits.data;
         this.numHashFunctions = var1.numHashFunctions;
         this.funnel = var1.funnel;
         this.strategy = var1.strategy;
      }

      Object readResolve() {
         return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
      }
   }

   interface Strategy extends Serializable {
      <T> boolean put(T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.BitArray var4);

      <T> boolean mightContain(T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.BitArray var4);

      int ordinal();
   }
}
