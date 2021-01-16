package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.Comparator;

public final class IntComparators {
   public static final IntComparator NATURAL_COMPARATOR = new IntComparators.NaturalImplicitComparator();
   public static final IntComparator OPPOSITE_COMPARATOR = new IntComparators.OppositeImplicitComparator();

   private IntComparators() {
      super();
   }

   public static IntComparator oppositeComparator(IntComparator var0) {
      return new IntComparators.OppositeComparator(var0);
   }

   public static IntComparator asIntComparator(final Comparator<? super Integer> var0) {
      return var0 != null && !(var0 instanceof IntComparator) ? new IntComparator() {
         public int compare(int var1, int var2) {
            return var0.compare(var1, var2);
         }

         public int compare(Integer var1, Integer var2) {
            return var0.compare(var1, var2);
         }
      } : (IntComparator)var0;
   }

   protected static class OppositeComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final IntComparator comparator;

      protected OppositeComparator(IntComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(int var1, int var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(int var1, int var2) {
         return -Integer.compare(var1, var2);
      }

      private Object readResolve() {
         return IntComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(int var1, int var2) {
         return Integer.compare(var1, var2);
      }

      private Object readResolve() {
         return IntComparators.NATURAL_COMPARATOR;
      }
   }
}
