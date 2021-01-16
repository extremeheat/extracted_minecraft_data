package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Comparator;

public final class ShortComparators {
   public static final ShortComparator NATURAL_COMPARATOR = new ShortComparators.NaturalImplicitComparator();
   public static final ShortComparator OPPOSITE_COMPARATOR = new ShortComparators.OppositeImplicitComparator();

   private ShortComparators() {
      super();
   }

   public static ShortComparator oppositeComparator(ShortComparator var0) {
      return new ShortComparators.OppositeComparator(var0);
   }

   public static ShortComparator asShortComparator(final Comparator<? super Short> var0) {
      return var0 != null && !(var0 instanceof ShortComparator) ? new ShortComparator() {
         public int compare(short var1, short var2) {
            return var0.compare(var1, var2);
         }

         public int compare(Short var1, Short var2) {
            return var0.compare(var1, var2);
         }
      } : (ShortComparator)var0;
   }

   protected static class OppositeComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final ShortComparator comparator;

      protected OppositeComparator(ShortComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(short var1, short var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(short var1, short var2) {
         return -Short.compare(var1, var2);
      }

      private Object readResolve() {
         return ShortComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements ShortComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(short var1, short var2) {
         return Short.compare(var1, var2);
      }

      private Object readResolve() {
         return ShortComparators.NATURAL_COMPARATOR;
      }
   }
}
