package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.Comparator;

public final class DoubleComparators {
   public static final DoubleComparator NATURAL_COMPARATOR = new DoubleComparators.NaturalImplicitComparator();
   public static final DoubleComparator OPPOSITE_COMPARATOR = new DoubleComparators.OppositeImplicitComparator();

   private DoubleComparators() {
      super();
   }

   public static DoubleComparator oppositeComparator(DoubleComparator var0) {
      return new DoubleComparators.OppositeComparator(var0);
   }

   public static DoubleComparator asDoubleComparator(final Comparator<? super Double> var0) {
      return var0 != null && !(var0 instanceof DoubleComparator) ? new DoubleComparator() {
         public int compare(double var1, double var3) {
            return var0.compare(var1, var3);
         }

         public int compare(Double var1, Double var2) {
            return var0.compare(var1, var2);
         }
      } : (DoubleComparator)var0;
   }

   protected static class OppositeComparator implements DoubleComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final DoubleComparator comparator;

      protected OppositeComparator(DoubleComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(double var1, double var3) {
         return this.comparator.compare(var3, var1);
      }
   }

   protected static class OppositeImplicitComparator implements DoubleComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(double var1, double var3) {
         return -Double.compare(var1, var3);
      }

      private Object readResolve() {
         return DoubleComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements DoubleComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(double var1, double var3) {
         return Double.compare(var1, var3);
      }

      private Object readResolve() {
         return DoubleComparators.NATURAL_COMPARATOR;
      }
   }
}
