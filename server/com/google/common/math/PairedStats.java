package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class PairedStats implements Serializable {
   private final Stats xStats;
   private final Stats yStats;
   private final double sumOfProductsOfDeltas;
   private static final int BYTES = 88;
   private static final long serialVersionUID = 0L;

   PairedStats(Stats var1, Stats var2, double var3) {
      super();
      this.xStats = var1;
      this.yStats = var2;
      this.sumOfProductsOfDeltas = var3;
   }

   public long count() {
      return this.xStats.count();
   }

   public Stats xStats() {
      return this.xStats;
   }

   public Stats yStats() {
      return this.yStats;
   }

   public double populationCovariance() {
      Preconditions.checkState(this.count() != 0L);
      return this.sumOfProductsOfDeltas / (double)this.count();
   }

   public double sampleCovariance() {
      Preconditions.checkState(this.count() > 1L);
      return this.sumOfProductsOfDeltas / (double)(this.count() - 1L);
   }

   public double pearsonsCorrelationCoefficient() {
      Preconditions.checkState(this.count() > 1L);
      if (Double.isNaN(this.sumOfProductsOfDeltas)) {
         return 0.0D / 0.0;
      } else {
         double var1 = this.xStats().sumOfSquaresOfDeltas();
         double var3 = this.yStats().sumOfSquaresOfDeltas();
         Preconditions.checkState(var1 > 0.0D);
         Preconditions.checkState(var3 > 0.0D);
         double var5 = ensurePositive(var1 * var3);
         return ensureInUnitRange(this.sumOfProductsOfDeltas / Math.sqrt(var5));
      }
   }

   public LinearTransformation leastSquaresFit() {
      Preconditions.checkState(this.count() > 1L);
      if (Double.isNaN(this.sumOfProductsOfDeltas)) {
         return LinearTransformation.forNaN();
      } else {
         double var1 = this.xStats.sumOfSquaresOfDeltas();
         if (var1 > 0.0D) {
            return this.yStats.sumOfSquaresOfDeltas() > 0.0D ? LinearTransformation.mapping(this.xStats.mean(), this.yStats.mean()).withSlope(this.sumOfProductsOfDeltas / var1) : LinearTransformation.horizontal(this.yStats.mean());
         } else {
            Preconditions.checkState(this.yStats.sumOfSquaresOfDeltas() > 0.0D);
            return LinearTransformation.vertical(this.xStats.mean());
         }
      }
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         PairedStats var2 = (PairedStats)var1;
         return this.xStats.equals(var2.xStats) && this.yStats.equals(var2.yStats) && Double.doubleToLongBits(this.sumOfProductsOfDeltas) == Double.doubleToLongBits(var2.sumOfProductsOfDeltas);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.xStats, this.yStats, this.sumOfProductsOfDeltas);
   }

   public String toString() {
      return this.count() > 0L ? MoreObjects.toStringHelper((Object)this).add("xStats", this.xStats).add("yStats", this.yStats).add("populationCovariance", this.populationCovariance()).toString() : MoreObjects.toStringHelper((Object)this).add("xStats", this.xStats).add("yStats", this.yStats).toString();
   }

   double sumOfProductsOfDeltas() {
      return this.sumOfProductsOfDeltas;
   }

   private static double ensurePositive(double var0) {
      return var0 > 0.0D ? var0 : 4.9E-324D;
   }

   private static double ensureInUnitRange(double var0) {
      if (var0 >= 1.0D) {
         return 1.0D;
      } else {
         return var0 <= -1.0D ? -1.0D : var0;
      }
   }

   public byte[] toByteArray() {
      ByteBuffer var1 = ByteBuffer.allocate(88).order(ByteOrder.LITTLE_ENDIAN);
      this.xStats.writeTo(var1);
      this.yStats.writeTo(var1);
      var1.putDouble(this.sumOfProductsOfDeltas);
      return var1.array();
   }

   public static PairedStats fromByteArray(byte[] var0) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var0.length == 88, "Expected PairedStats.BYTES = %s, got %s", (int)88, (int)var0.length);
      ByteBuffer var1 = ByteBuffer.wrap(var0).order(ByteOrder.LITTLE_ENDIAN);
      Stats var2 = Stats.readFrom(var1);
      Stats var3 = Stats.readFrom(var1);
      double var4 = var1.getDouble();
      return new PairedStats(var2, var3, var4);
   }
}
