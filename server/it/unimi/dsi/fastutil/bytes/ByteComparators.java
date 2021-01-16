package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.Comparator;

public final class ByteComparators {
   public static final ByteComparator NATURAL_COMPARATOR = new ByteComparators.NaturalImplicitComparator();
   public static final ByteComparator OPPOSITE_COMPARATOR = new ByteComparators.OppositeImplicitComparator();

   private ByteComparators() {
      super();
   }

   public static ByteComparator oppositeComparator(ByteComparator var0) {
      return new ByteComparators.OppositeComparator(var0);
   }

   public static ByteComparator asByteComparator(final Comparator<? super Byte> var0) {
      return var0 != null && !(var0 instanceof ByteComparator) ? new ByteComparator() {
         public int compare(byte var1, byte var2) {
            return var0.compare(var1, var2);
         }

         public int compare(Byte var1, Byte var2) {
            return var0.compare(var1, var2);
         }
      } : (ByteComparator)var0;
   }

   protected static class OppositeComparator implements ByteComparator, Serializable {
      private static final long serialVersionUID = 1L;
      private final ByteComparator comparator;

      protected OppositeComparator(ByteComparator var1) {
         super();
         this.comparator = var1;
      }

      public final int compare(byte var1, byte var2) {
         return this.comparator.compare(var2, var1);
      }
   }

   protected static class OppositeImplicitComparator implements ByteComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected OppositeImplicitComparator() {
         super();
      }

      public final int compare(byte var1, byte var2) {
         return -Byte.compare(var1, var2);
      }

      private Object readResolve() {
         return ByteComparators.OPPOSITE_COMPARATOR;
      }
   }

   protected static class NaturalImplicitComparator implements ByteComparator, Serializable {
      private static final long serialVersionUID = 1L;

      protected NaturalImplicitComparator() {
         super();
      }

      public final int compare(byte var1, byte var2) {
         return Byte.compare(var1, var2);
      }

      private Object readResolve() {
         return ByteComparators.NATURAL_COMPARATOR;
      }
   }
}
