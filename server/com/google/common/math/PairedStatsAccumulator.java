package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;

@Beta
@GwtIncompatible
public final class PairedStatsAccumulator {
   private final StatsAccumulator xStats = new StatsAccumulator();
   private final StatsAccumulator yStats = new StatsAccumulator();
   private double sumOfProductsOfDeltas = 0.0D;

   public PairedStatsAccumulator() {
      super();
   }

   public void add(double var1, double var3) {
      this.xStats.add(var1);
      if (Doubles.isFinite(var1) && Doubles.isFinite(var3)) {
         if (this.xStats.count() > 1L) {
            this.sumOfProductsOfDeltas += (var1 - this.xStats.mean()) * (var3 - this.yStats.mean());
         }
      } else {
         this.sumOfProductsOfDeltas = 0.0D / 0.0;
      }

      this.yStats.add(var3);
   }

   public void addAll(PairedStats var1) {
      if (var1.count() != 0L) {
         this.xStats.addAll(var1.xStats());
         if (this.yStats.count() == 0L) {
            this.sumOfProductsOfDeltas = var1.sumOfProductsOfDeltas();
         } else {
            this.sumOfProductsOfDeltas += var1.sumOfProductsOfDeltas() + (var1.xStats().mean() - this.xStats.mean()) * (var1.yStats().mean() - this.yStats.mean()) * (double)var1.count();
         }

         this.yStats.addAll(var1.yStats());
      }
   }

   public PairedStats snapshot() {
      return new PairedStats(this.xStats.snapshot(), this.yStats.snapshot(), this.sumOfProductsOfDeltas);
   }

   public long count() {
      return this.xStats.count();
   }

   public Stats xStats() {
      return this.xStats.snapshot();
   }

   public Stats yStats() {
      return this.yStats.snapshot();
   }

   public double populationCovariance() {
      Preconditions.checkState(this.count() != 0L);
      return this.sumOfProductsOfDeltas / (double)this.count();
   }

   public final double sampleCovariance() {
      Preconditions.checkState(this.count() > 1L);
      return this.sumOfProductsOfDeltas / (double)(this.count() - 1L);
   }

   public final double pearsonsCorrelationCoefficient() {
      Preconditions.checkState(this.count() > 1L);
      if (Double.isNaN(this.sumOfProductsOfDeltas)) {
         return 0.0D / 0.0;
      } else {
         double var1 = this.xStats.sumOfSquaresOfDeltas();
         double var3 = this.yStats.sumOfSquaresOfDeltas();
         Preconditions.checkState(var1 > 0.0D);
         Preconditions.checkState(var3 > 0.0D);
         double var5 = this.ensurePositive(var1 * var3);
         return ensureInUnitRange(this.sumOfProductsOfDeltas / Math.sqrt(var5));
      }
   }

   public final LinearTransformation leastSquaresFit() {
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

   private double ensurePositive(double var1) {
      return var1 > 0.0D ? var1 : 4.9E-324D;
   }

   private static double ensureInUnitRange(double var0) {
      if (var0 >= 1.0D) {
         return 1.0D;
      } else {
         return var0 <= -1.0D ? -1.0D : var0;
      }
   }
}
