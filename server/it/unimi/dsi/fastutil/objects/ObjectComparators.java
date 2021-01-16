package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;

public final class ObjectComparators {
   public static final Comparator NATURAL_COMPARATOR = new ObjectComparators.NaturalImplicitComparator();
   public static final Comparator OPPOSITE_COMPARATOR = new ObjectComparators.OppositeImplicitComparator();

   private ObjectComparators() {
      super();
   }

   public static <K> Comparator<K> oppositeComparator(Comparator<K> var0) {
      return new ObjectComparators.OppositeComparator(var0);
   }

   protected static class OppositeComparator<K> implements Comparator<K>, Serializable {
      private static final long serialVersionUID = 1L;
      private final Comparator<K> comparator;

      protected OppositeComparator(Comparator<K> var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(K var1, K var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements Comparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(Object var1, Object var2) {
         return ((Comparable)var2).compareTo(var1);
      }

      private Object readResolve() {
         return ObjectComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements Comparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(Object var1, Object var2) {
         return ((Comparable)var1).compareTo(var2);
      }

      private Object readResolve() {
         return ObjectComparators.NATURAL_COMPARATOR;
      }
   }
}
