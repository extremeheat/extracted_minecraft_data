package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class Stats implements Serializable {
   private final long count;
   private final double mean;
   private final double sumOfSquaresOfDeltas;
   private final double min;
   private final double max;
   static final int BYTES = 40;
   private static final long serialVersionUID = 0L;

   Stats(long var1, double var3, double var5, double var7, double var9) {
      super();
      this.count = var1;
      this.mean = var3;
      this.sumOfSquaresOfDeltas = var5;
      this.min = var7;
      this.max = var9;
   }

   public static Stats of(Iterable<? extends Number> var0) {
      StatsAccumulator var1 = new StatsAccumulator();
      var1.addAll(var0);
      return var1.snapshot();
   }

   public static Stats of(Iterator<? extends Number> var0) {
      StatsAccumulator var1 = new StatsAccumulator();
      var1.addAll(var0);
      return var1.snapshot();
   }

   public static Stats of(double... var0) {
      StatsAccumulator var1 = new StatsAccumulator();
      var1.addAll(var0);
      return var1.snapshot();
   }

   public static Stats of(int... var0) {
      StatsAccumulator var1 = new StatsAccumulator();
      var1.addAll(var0);
      return var1.snapshot();
   }

   public static Stats of(long... var0) {
      StatsAccumulator var1 = new StatsAccumulator();
      var1.addAll(var0);
      return var1.snapshot();
   }

   public long count() {
      return this.count;
   }

   public double mean() {
      Preconditions.checkState(this.count != 0L);
      return this.mean;
   }

   public double sum() {
      return this.mean * (double)this.count;
   }

   public double populationVariance() {
      Preconditions.checkState(this.count > 0L);
      if (Double.isNaN(this.sumOfSquaresOfDeltas)) {
         return 0.0D / 0.0;
      } else {
         return this.count == 1L ? 0.0D : DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / (double)this.count();
      }
   }

   public double populationStandardDeviation() {
      return Math.sqrt(this.populationVariance());
   }

   public double sampleVariance() {
      Preconditions.checkState(this.count > 1L);
      return Double.isNaN(this.sumOfSquaresOfDeltas) ? 0.0D / 0.0 : DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / (double)(this.count - 1L);
   }

   public double sampleStandardDeviation() {
      return Math.sqrt(this.sampleVariance());
   }

   public double min() {
      Preconditions.checkState(this.count != 0L);
      return this.min;
   }

   public double max() {
      Preconditions.checkState(this.count != 0L);
      return this.max;
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Stats var2 = (Stats)var1;
         return this.count == var2.count && Double.doubleToLongBits(this.mean) == Double.doubleToLongBits(var2.mean) && Double.doubleToLongBits(this.sumOfSquaresOfDeltas) == Double.doubleToLongBits(var2.sumOfSquaresOfDeltas) && Double.doubleToLongBits(this.min) == Double.doubleToLongBits(var2.min) && Double.doubleToLongBits(this.max) == Double.doubleToLongBits(var2.max);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.count, this.mean, this.sumOfSquaresOfDeltas, this.min, this.max);
   }

   public String toString() {
      return this.count() > 0L ? MoreObjects.toStringHelper((Object)this).add("count", this.count).add("mean", this.mean).add("populationStandardDeviation", this.populationStandardDeviation()).add("min", this.min).add("max", this.max).toString() : MoreObjects.toStringHelper((Object)this).add("count", this.count).toString();
   }

   double sumOfSquaresOfDeltas() {
      return this.sumOfSquaresOfDeltas;
   }

   public static double meanOf(Iterable<? extends Number> var0) {
      return meanOf(var0.iterator());
   }

   public static double meanOf(Iterator<? extends Number> var0) {
      Preconditions.checkArgument(var0.hasNext());
      long var1 = 1L;
      double var3 = ((Number)var0.next()).doubleValue();

      while(true) {
         while(var0.hasNext()) {
            double var5 = ((Number)var0.next()).doubleValue();
            ++var1;
            if (Doubles.isFinite(var5) && Doubles.isFinite(var3)) {
               var3 += (var5 - var3) / (double)var1;
            } else {
               var3 = StatsAccumulator.calculateNewMeanNonFinite(var3, var5);
            }
         }

         return var3;
      }
   }

   public static double meanOf(double... var0) {
      Preconditions.checkArgument(var0.length > 0);
      double var1 = var0[0];

      for(int var3 = 1; var3 < var0.length; ++var3) {
         double var4 = var0[var3];
         if (Doubles.isFinite(var4) && Doubles.isFinite(var1)) {
            var1 += (var4 - var1) / (double)(var3 + 1);
         } else {
            var1 = StatsAccumulator.calculateNewMeanNonFinite(var1, var4);
         }
      }

      return var1;
   }

   public static double meanOf(int... var0) {
      Preconditions.checkArgument(var0.length > 0);
      double var1 = (double)var0[0];

      for(int var3 = 1; var3 < var0.length; ++var3) {
         double var4 = (double)var0[var3];
         if (Doubles.isFinite(var4) && Doubles.isFinite(var1)) {
            var1 += (var4 - var1) / (double)(var3 + 1);
         } else {
            var1 = StatsAccumulator.calculateNewMeanNonFinite(var1, var4);
         }
      }

      return var1;
   }

   public static double meanOf(long... var0) {
      Preconditions.checkArgument(var0.length > 0);
      double var1 = (double)var0[0];

      for(int var3 = 1; var3 < var0.length; ++var3) {
         double var4 = (double)var0[var3];
         if (Doubles.isFinite(var4) && Doubles.isFinite(var1)) {
            var1 += (var4 - var1) / (double)(var3 + 1);
         } else {
            var1 = StatsAccumulator.calculateNewMeanNonFinite(var1, var4);
         }
      }

      return var1;
   }

   public byte[] toByteArray() {
      ByteBuffer var1 = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN);
      this.writeTo(var1);
      return var1.array();
   }

   void writeTo(ByteBuffer var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(var1.remaining() >= 40, "Expected at least Stats.BYTES = %s remaining , got %s", (int)40, (int)var1.remaining());
      var1.putLong(this.count).putDouble(this.mean).putDouble(this.sumOfSquaresOfDeltas).putDouble(this.min).putDouble(this.max);
   }

   public static Stats fromByteArray(byte[] var0) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var0.length == 40, "Expected Stats.BYTES = %s remaining , got %s", (int)40, (int)var0.length);
      return readFrom(ByteBuffer.wrap(var0).order(ByteOrder.LITTLE_ENDIAN));
   }

   static Stats readFrom(ByteBuffer var0) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var0.remaining() >= 40, "Expected at least Stats.BYTES = %s remaining , got %s", (int)40, (int)var0.remaining());
      return new Stats(var0.getLong(), var0.getDouble(), var0.getDouble(), var0.getDouble(), var0.getDouble());
   }
}
