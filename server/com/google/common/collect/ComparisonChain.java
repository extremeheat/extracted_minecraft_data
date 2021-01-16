package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ComparisonChain {
   private static final ComparisonChain ACTIVE = new ComparisonChain() {
      public ComparisonChain compare(Comparable var1, Comparable var2) {
         return this.classify(var1.compareTo(var2));
      }

      public <T> ComparisonChain compare(@Nullable T var1, @Nullable T var2, Comparator<T> var3) {
         return this.classify(var3.compare(var1, var2));
      }

      public ComparisonChain compare(int var1, int var2) {
         return this.classify(Ints.compare(var1, var2));
      }

      public ComparisonChain compare(long var1, long var3) {
         return this.classify(Longs.compare(var1, var3));
      }

      public ComparisonChain compare(float var1, float var2) {
         return this.classify(Float.compare(var1, var2));
      }

      public ComparisonChain compare(double var1, double var3) {
         return this.classify(Double.compare(var1, var3));
      }

      public ComparisonChain compareTrueFirst(boolean var1, boolean var2) {
         return this.classify(Booleans.compare(var2, var1));
      }

      public ComparisonChain compareFalseFirst(boolean var1, boolean var2) {
         return this.classify(Booleans.compare(var1, var2));
      }

      ComparisonChain classify(int var1) {
         return var1 < 0 ? ComparisonChain.LESS : (var1 > 0 ? ComparisonChain.GREATER : ComparisonChain.ACTIVE);
      }

      public int result() {
         return 0;
      }
   };
   private static final ComparisonChain LESS = new ComparisonChain.InactiveComparisonChain(-1);
   private static final ComparisonChain GREATER = new ComparisonChain.InactiveComparisonChain(1);

   private ComparisonChain() {
      super();
   }

   public static ComparisonChain start() {
      return ACTIVE;
   }

   public abstract ComparisonChain compare(Comparable<?> var1, Comparable<?> var2);

   public abstract <T> ComparisonChain compare(@Nullable T var1, @Nullable T var2, Comparator<T> var3);

   public abstract ComparisonChain compare(int var1, int var2);

   public abstract ComparisonChain compare(long var1, long var3);

   public abstract ComparisonChain compare(float var1, float var2);

   public abstract ComparisonChain compare(double var1, double var3);

   /** @deprecated */
   @Deprecated
   public final ComparisonChain compare(Boolean var1, Boolean var2) {
      return this.compareFalseFirst(var1, var2);
   }

   public abstract ComparisonChain compareTrueFirst(boolean var1, boolean var2);

   public abstract ComparisonChain compareFalseFirst(boolean var1, boolean var2);

   public abstract int result();

   // $FF: synthetic method
   ComparisonChain(Object var1) {
      this();
   }

   private static final class InactiveComparisonChain extends ComparisonChain {
      final int result;

      InactiveComparisonChain(int var1) {
         super(null);
         this.result = var1;
      }

      public ComparisonChain compare(@Nullable Comparable var1, @Nullable Comparable var2) {
         return this;
      }

      public <T> ComparisonChain compare(@Nullable T var1, @Nullable T var2, @Nullable Comparator<T> var3) {
         return this;
      }

      public ComparisonChain compare(int var1, int var2) {
         return this;
      }

      public ComparisonChain compare(long var1, long var3) {
         return this;
      }

      public ComparisonChain compare(float var1, float var2) {
         return this;
      }

      public ComparisonChain compare(double var1, double var3) {
         return this;
      }

      public ComparisonChain compareTrueFirst(boolean var1, boolean var2) {
         return this;
      }

      public ComparisonChain compareFalseFirst(boolean var1, boolean var2) {
         return this;
      }

      public int result() {
         return this.result;
      }
   }
}
