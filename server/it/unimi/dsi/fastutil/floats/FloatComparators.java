package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.Comparator;

public final class FloatComparators {
   public static final FloatComparator NATURAL_COMPARATOR = new FloatComparators.NaturalImplicitComparator();
   public static final FloatComparator OPPOSITE_COMPARATOR = new FloatComparators.OppositeImplicitComparator();

   private FloatComparators() {
      super();
   }

   public static FloatComparator oppositeComparator(FloatComparator var0) {
      return new FloatComparators.OppositeComparator(var0);
   }

   public static FloatComparator asFloatComparator(final Comparator<? super Float> var0) {
      return var0 != null && !(var0 instanceof FloatComparator) ? new FloatComparator() {
         public int compare(float var1, float var2) {
            return var0.compare(var1, var2);
         }

         public int compare(Float var1, Float var2) {
            return var0.compare(var1, var2);
         }
      } : (FloatComparator)var0;
   }

   protected static class OppositeComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final FloatComparator comparator;

      protected OppositeComparator(FloatComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(float var1, float var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(float var1, float var2) {
         return -Float.compare(var1, var2);
      }

      private Object readResolve() {
         return FloatComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements FloatComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(float var1, float var2) {
         return Float.compare(var1, var2);
      }

      private Object readResolve() {
         return FloatComparators.NATURAL_COMPARATOR;
      }
   }
}
