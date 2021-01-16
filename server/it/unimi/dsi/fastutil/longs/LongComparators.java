package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.Comparator;

public final class LongComparators {
   public static final LongComparator NATURAL_COMPARATOR = new LongComparators.NaturalImplicitComparator();
   public static final LongComparator OPPOSITE_COMPARATOR = new LongComparators.OppositeImplicitComparator();

   private LongComparators() {
      super();
   }

   public static LongComparator oppositeComparator(LongComparator var0) {
      return new LongComparators.OppositeComparator(var0);
   }

   public static LongComparator asLongComparator(final Comparator<? super Long> var0) {
      return var0 != null && !(var0 instanceof LongComparator) ? new LongComparator() {
         public int compare(long var1, long var3) {
            return var0.compare(var1, var3);
         }

         public int compare(Long var1, Long var2) {
            return var0.compare(var1, var2);
         }
      } : (LongComparator)var0;
   }

   protected static class OppositeComparator implements LongComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final LongComparator comparator;

      protected OppositeComparator(LongComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(long var1, long var3) {
         return this.comparator.compare(var3, var1);
      }
   }

   protected static class OppositeImplicitComparator implements LongComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(long var1, long var3) {
         return -Long.compare(var1, var3);
      }

      private Object readResolve() {
         return LongComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements LongComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(long var1, long var3) {
         return Long.compare(var1, var3);
      }

      private Object readResolve() {
         return LongComparators.NATURAL_COMPARATOR;
      }
   }
}
