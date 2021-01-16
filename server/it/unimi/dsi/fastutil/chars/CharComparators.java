package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Comparator;

public final class CharComparators {
   public static final CharComparator NATURAL_COMPARATOR = new CharComparators.NaturalImplicitComparator();
   public static final CharComparator OPPOSITE_COMPARATOR = new CharComparators.OppositeImplicitComparator();

   private CharComparators() {
      super();
   }

   public static CharComparator oppositeComparator(CharComparator var0) {
      return new CharComparators.OppositeComparator(var0);
   }

   public static CharComparator asCharComparator(final Comparator<? super Character> var0) {
      return var0 != null && !(var0 instanceof CharComparator) ? new CharComparator() {
         public int compare(char var1, char var2) {
            return var0.compare(var1, var2);
         }

         public int compare(Character var1, Character var2) {
            return var0.compare(var1, var2);
         }
      } : (CharComparator)var0;
   }

   protected static class OppositeComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final CharComparator comparator;

      protected OppositeComparator(CharComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(char var1, char var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(char var1, char var2) {
         return -Character.compare(var1, var2);
      }

      private Object readResolve() {
         return CharComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements CharComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(char var1, char var2) {
         return Character.compare(var1, var2);
      }

      private Object readResolve() {
         return CharComparators.NATURAL_COMPARATOR;
      }
   }
}
