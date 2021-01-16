package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;

@Beta
@GwtIncompatible
public abstract class LinearTransformation {
   public LinearTransformation() {
      super();
   }

   public static LinearTransformation.LinearTransformationBuilder mapping(double var0, double var2) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0) && DoubleUtils.isFinite(var2));
      return new LinearTransformation.LinearTransformationBuilder(var0, var2);
   }

   public static LinearTransformation vertical(double var0) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0));
      return new LinearTransformation.VerticalLinearTransformation(var0);
   }

   public static LinearTransformation horizontal(double var0) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0));
      double var2 = 0.0D;
      return new LinearTransformation.RegularLinearTransformation(var2, var0);
   }

   public static LinearTransformation forNaN() {
      return LinearTransformation.NaNLinearTransformation.INSTANCE;
   }

   public abstract boolean isVertical();

   public abstract boolean isHorizontal();

   public abstract double slope();

   public abstract double transform(double var1);

   public abstract LinearTransformation inverse();

   private static final class NaNLinearTransformation extends LinearTransformation {
      static final LinearTransformation.NaNLinearTransformation INSTANCE = new LinearTransformation.NaNLinearTransformation();

      private NaNLinearTransformation() {
         super();
      }

      public boolean isVertical() {
         return false;
      }

      public boolean isHorizontal() {
         return false;
      }

      public double slope() {
         return 0.0D / 0.0;
      }

      public double transform(double var1) {
         return 0.0D / 0.0;
      }

      public LinearTransformation inverse() {
         return this;
      }

      public String toString() {
         return "NaN";
      }
   }

   private static final class VerticalLinearTransformation extends LinearTransformation {
      final double x;
      @LazyInit
      LinearTransformation inverse;

      VerticalLinearTransformation(double var1) {
         super();
         this.x = var1;
         this.inverse = null;
      }

      VerticalLinearTransformation(double var1, LinearTransformation var3) {
         super();
         this.x = var1;
         this.inverse = var3;
      }

      public boolean isVertical() {
         return true;
      }

      public boolean isHorizontal() {
         return false;
      }

      public double slope() {
         throw new IllegalStateException();
      }

      public double transform(double var1) {
         throw new IllegalStateException();
      }

      public LinearTransformation inverse() {
         LinearTransformation var1 = this.inverse;
         return var1 == null ? (this.inverse = this.createInverse()) : var1;
      }

      public String toString() {
         return String.format("x = %g", this.x);
      }

      private LinearTransformation createInverse() {
         return new LinearTransformation.RegularLinearTransformation(0.0D, this.x, this);
      }
   }

   private static final class RegularLinearTransformation extends LinearTransformation {
      final double slope;
      final double yIntercept;
      @LazyInit
      LinearTransformation inverse;

      RegularLinearTransformation(double var1, double var3) {
         super();
         this.slope = var1;
         this.yIntercept = var3;
         this.inverse = null;
      }

      RegularLinearTransformation(double var1, double var3, LinearTransformation var5) {
         super();
         this.slope = var1;
         this.yIntercept = var3;
         this.inverse = var5;
      }

      public boolean isVertical() {
         return false;
      }

      public boolean isHorizontal() {
         return this.slope == 0.0D;
      }

      public double slope() {
         return this.slope;
      }

      public double transform(double var1) {
         return var1 * this.slope + this.yIntercept;
      }

      public LinearTransformation inverse() {
         LinearTransformation var1 = this.inverse;
         return var1 == null ? (this.inverse = this.createInverse()) : var1;
      }

      public String toString() {
         return String.format("y = %g * x + %g", this.slope, this.yIntercept);
      }

      private LinearTransformation createInverse() {
         return (LinearTransformation)(this.slope != 0.0D ? new LinearTransformation.RegularLinearTransformation(1.0D / this.slope, -1.0D * this.yIntercept / this.slope, this) : new LinearTransformation.VerticalLinearTransformation(this.yIntercept, this));
      }
   }

   public static final class LinearTransformationBuilder {
      private final double x1;
      private final double y1;

      private LinearTransformationBuilder(double var1, double var3) {
         super();
         this.x1 = var1;
         this.y1 = var3;
      }

      public LinearTransformation and(double var1, double var3) {
         Preconditions.checkArgument(DoubleUtils.isFinite(var1) && DoubleUtils.isFinite(var3));
         if (var1 == this.x1) {
            Preconditions.checkArgument(var3 != this.y1);
            return new LinearTransformation.VerticalLinearTransformation(this.x1);
         } else {
            return this.withSlope((var3 - this.y1) / (var1 - this.x1));
         }
      }

      public LinearTransformation withSlope(double var1) {
         Preconditions.checkArgument(!Double.isNaN(var1));
         if (DoubleUtils.isFinite(var1)) {
            double var3 = this.y1 - this.x1 * var1;
            return new LinearTransformation.RegularLinearTransformation(var1, var3);
         } else {
            return new LinearTransformation.VerticalLinearTransformation(this.x1);
         }
      }

      // $FF: synthetic method
      LinearTransformationBuilder(double var1, double var3, Object var5) {
         this(var1, var3);
      }
   }
}
