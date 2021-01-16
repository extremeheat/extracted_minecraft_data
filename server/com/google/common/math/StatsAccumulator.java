package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import java.util.Iterator;

@Beta
@GwtIncompatible
public final class StatsAccumulator {
   private long count = 0L;
   private double mean = 0.0D;
   private double sumOfSquaresOfDeltas = 0.0D;
   private double min = 0.0D / 0.0;
   private double max = 0.0D / 0.0;

   public StatsAccumulator() {
      super();
   }

   public void add(double var1) {
      if (this.count == 0L) {
         this.count = 1L;
         this.mean = var1;
         this.min = var1;
         this.max = var1;
         if (!Doubles.isFinite(var1)) {
            this.sumOfSquaresOfDeltas = 0.0D / 0.0;
         }
      } else {
         ++this.count;
         if (Doubles.isFinite(var1) && Doubles.isFinite(this.mean)) {
            double var3 = var1 - this.mean;
            this.mean += var3 / (double)this.count;
            this.sumOfSquaresOfDeltas += var3 * (var1 - this.mean);
         } else {
            this.mean = calculateNewMeanNonFinite(this.mean, var1);
            this.sumOfSquaresOfDeltas = 0.0D / 0.0;
         }

         this.min = Math.min(this.min, var1);
         this.max = Math.max(this.max, var1);
      }

   }

   public void addAll(Iterable<? extends Number> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Number var3 = (Number)var2.next();
         this.add(var3.doubleValue());
      }

   }

   public void addAll(Iterator<? extends Number> var1) {
      while(var1.hasNext()) {
         this.add(((Number)var1.next()).doubleValue());
      }

   }

   public void addAll(double... var1) {
      double[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         double var5 = var2[var4];
         this.add(var5);
      }

   }

   public void addAll(int... var1) {
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.add((double)var5);
      }

   }

   public void addAll(long... var1) {
      long[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         this.add((double)var5);
      }

   }

   public void addAll(Stats var1) {
      if (var1.count() != 0L) {
         if (this.count == 0L) {
            this.count = var1.count();
            this.mean = var1.mean();
            this.sumOfSquaresOfDeltas = var1.sumOfSquaresOfDeltas();
            this.min = var1.min();
            this.max = var1.max();
         } else {
            this.count += var1.count();
            if (Doubles.isFinite(this.mean) && Doubles.isFinite(var1.mean())) {
               double var2 = var1.mean() - this.mean;
               this.mean += var2 * (double)var1.count() / (double)this.count;
               this.sumOfSquaresOfDeltas += var1.sumOfSquaresOfDeltas() + var2 * (var1.mean() - this.mean) * (double)var1.count();
            } else {
               this.mean = calculateNewMeanNonFinite(this.mean, var1.mean());
               this.sumOfSquaresOfDeltas = 0.0D / 0.0;
            }

            this.min = Math.min(this.min, var1.min());
            this.max = Math.max(this.max, var1.max());
         }

      }
   }

   public Stats snapshot() {
      return new Stats(this.count, this.mean, this.sumOfSquaresOfDeltas, this.min, this.max);
   }

   public long count() {
      return this.count;
   }

   public double mean() {
      Preconditions.checkState(this.count != 0L);
      return this.mean;
   }

   public final double sum() {
      return this.mean * (double)this.count;
   }

   public final double populationVariance() {
      Preconditions.checkState(this.count != 0L);
      if (Double.isNaN(this.sumOfSquaresOfDeltas)) {
         return 0.0D / 0.0;
      } else {
         return this.count == 1L ? 0.0D : DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / (double)this.count;
      }
   }

   public final double populationStandardDeviation() {
      return Math.sqrt(this.populationVariance());
   }

   public final double sampleVariance() {
      Preconditions.checkState(this.count > 1L);
      return Double.isNaN(this.sumOfSquaresOfDeltas) ? 0.0D / 0.0 : DoubleUtils.ensureNonNegative(this.sumOfSquaresOfDeltas) / (double)(this.count - 1L);
   }

   public final double sampleStandardDeviation() {
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

   double sumOfSquaresOfDeltas() {
      return this.sumOfSquaresOfDeltas;
   }

   static double calculateNewMeanNonFinite(double var0, double var2) {
      if (Doubles.isFinite(var0)) {
         return var2;
      } else {
         return !Doubles.isFinite(var2) && var0 != var2 ? 0.0D / 0.0 : var0;
      }
   }
}
