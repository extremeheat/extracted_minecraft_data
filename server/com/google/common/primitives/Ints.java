package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@GwtCompatible
public final class Ints {
   public static final int BYTES = 4;
   public static final int MAX_POWER_OF_TWO = 1073741824;

   private Ints() {
      super();
   }

   public static int hashCode(int var0) {
      return var0;
   }

   public static int checkedCast(long var0) {
      int var2 = (int)var0;
      Preconditions.checkArgument((long)var2 == var0, "Out of range: %s", var0);
      return var2;
   }

   public static int saturatedCast(long var0) {
      if (var0 > 2147483647L) {
         return 2147483647;
      } else {
         return var0 < -2147483648L ? -2147483648 : (int)var0;
      }
   }

   public static int compare(int var0, int var1) {
      return var0 < var1 ? -1 : (var0 > var1 ? 1 : 0);
   }

   public static boolean contains(int[] var0, int var1) {
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if (var5 == var1) {
            return true;
         }
      }

      return false;
   }

   public static int indexOf(int[] var0, int var1) {
      return indexOf(var0, var1, 0, var0.length);
   }

   private static int indexOf(int[] var0, int var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3; ++var4) {
         if (var0[var4] == var1) {
            return var4;
         }
      }

      return -1;
   }

   public static int indexOf(int[] var0, int[] var1) {
      Preconditions.checkNotNull(var0, "array");
      Preconditions.checkNotNull(var1, "target");
      if (var1.length == 0) {
         return 0;
      } else {
         label28:
         for(int var2 = 0; var2 < var0.length - var1.length + 1; ++var2) {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               if (var0[var2 + var3] != var1[var3]) {
                  continue label28;
               }
            }

            return var2;
         }

         return -1;
      }
   }

   public static int lastIndexOf(int[] var0, int var1) {
      return lastIndexOf(var0, var1, 0, var0.length);
   }

   private static int lastIndexOf(int[] var0, int var1, int var2, int var3) {
      for(int var4 = var3 - 1; var4 >= var2; --var4) {
         if (var0[var4] == var1) {
            return var4;
         }
      }

      return -1;
   }

   public static int min(int... var0) {
      Preconditions.checkArgument(var0.length > 0);
      int var1 = var0[0];

      for(int var2 = 1; var2 < var0.length; ++var2) {
         if (var0[var2] < var1) {
            var1 = var0[var2];
         }
      }

      return var1;
   }

   public static int max(int... var0) {
      Preconditions.checkArgument(var0.length > 0);
      int var1 = var0[0];

      for(int var2 = 1; var2 < var0.length; ++var2) {
         if (var0[var2] > var1) {
            var1 = var0[var2];
         }
      }

      return var1;
   }

   @Beta
   public static int constrainToRange(int var0, int var1, int var2) {
      Preconditions.checkArgument(var1 <= var2, "min (%s) must be less than or equal to max (%s)", var1, var2);
      return Math.min(Math.max(var0, var1), var2);
   }

   public static int[] concat(int[]... var0) {
      int var1 = 0;
      int[][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int[] var5 = var2[var4];
         var1 += var5.length;
      }

      int[] var8 = new int[var1];
      var3 = 0;
      int[][] var9 = var0;
      int var10 = var0.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         int[] var7 = var9[var6];
         System.arraycopy(var7, 0, var8, var3, var7.length);
         var3 += var7.length;
      }

      return var8;
   }

   public static byte[] toByteArray(int var0) {
      return new byte[]{(byte)(var0 >> 24), (byte)(var0 >> 16), (byte)(var0 >> 8), (byte)var0};
   }

   public static int fromByteArray(byte[] var0) {
      Preconditions.checkArgument(var0.length >= 4, "array too small: %s < %s", (int)var0.length, (int)4);
      return fromBytes(var0[0], var0[1], var0[2], var0[3]);
   }

   public static int fromBytes(byte var0, byte var1, byte var2, byte var3) {
      return var0 << 24 | (var1 & 255) << 16 | (var2 & 255) << 8 | var3 & 255;
   }

   @Beta
   public static Converter<String, Integer> stringConverter() {
      return Ints.IntConverter.INSTANCE;
   }

   public static int[] ensureCapacity(int[] var0, int var1, int var2) {
      Preconditions.checkArgument(var1 >= 0, "Invalid minLength: %s", var1);
      Preconditions.checkArgument(var2 >= 0, "Invalid padding: %s", var2);
      return var0.length < var1 ? Arrays.copyOf(var0, var1 + var2) : var0;
   }

   public static String join(String var0, int... var1) {
      Preconditions.checkNotNull(var0);
      if (var1.length == 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1.length * 5);
         var2.append(var1[0]);

         for(int var3 = 1; var3 < var1.length; ++var3) {
            var2.append(var0).append(var1[var3]);
         }

         return var2.toString();
      }
   }

   public static Comparator<int[]> lexicographicalComparator() {
      return Ints.LexicographicalComparator.INSTANCE;
   }

   public static int[] toArray(Collection<? extends Number> var0) {
      if (var0 instanceof Ints.IntArrayAsList) {
         return ((Ints.IntArrayAsList)var0).toIntArray();
      } else {
         Object[] var1 = var0.toArray();
         int var2 = var1.length;
         int[] var3 = new int[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = ((Number)Preconditions.checkNotNull(var1[var4])).intValue();
         }

         return var3;
      }
   }

   public static List<Integer> asList(int... var0) {
      return (List)(var0.length == 0 ? Collections.emptyList() : new Ints.IntArrayAsList(var0));
   }

   @Nullable
   @CheckForNull
   @Beta
   public static Integer tryParse(String var0) {
      return tryParse(var0, 10);
   }

   @Nullable
   @CheckForNull
   @Beta
   public static Integer tryParse(String var0, int var1) {
      Long var2 = Longs.tryParse(var0, var1);
      return var2 != null && var2 == (long)var2.intValue() ? var2.intValue() : null;
   }

   @GwtCompatible
   private static class IntArrayAsList extends AbstractList<Integer> implements RandomAccess, Serializable {
      final int[] array;
      final int start;
      final int end;
      private static final long serialVersionUID = 0L;

      IntArrayAsList(int[] var1) {
         this(var1, 0, var1.length);
      }

      IntArrayAsList(int[] var1, int var2, int var3) {
         super();
         this.array = var1;
         this.start = var2;
         this.end = var3;
      }

      public int size() {
         return this.end - this.start;
      }

      public boolean isEmpty() {
         return false;
      }

      public Integer get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.array[this.start + var1];
      }

      public boolean contains(Object var1) {
         return var1 instanceof Integer && Ints.indexOf(this.array, (Integer)var1, this.start, this.end) != -1;
      }

      public int indexOf(Object var1) {
         if (var1 instanceof Integer) {
            int var2 = Ints.indexOf(this.array, (Integer)var1, this.start, this.end);
            if (var2 >= 0) {
               return var2 - this.start;
            }
         }

         return -1;
      }

      public int lastIndexOf(Object var1) {
         if (var1 instanceof Integer) {
            int var2 = Ints.lastIndexOf(this.array, (Integer)var1, this.start, this.end);
            if (var2 >= 0) {
               return var2 - this.start;
            }
         }

         return -1;
      }

      public Integer set(int var1, Integer var2) {
         Preconditions.checkElementIndex(var1, this.size());
         int var3 = this.array[this.start + var1];
         this.array[this.start + var1] = (Integer)Preconditions.checkNotNull(var2);
         return var3;
      }

      public List<Integer> subList(int var1, int var2) {
         int var3 = this.size();
         Preconditions.checkPositionIndexes(var1, var2, var3);
         return (List)(var1 == var2 ? Collections.emptyList() : new Ints.IntArrayAsList(this.array, this.start + var1, this.start + var2));
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 instanceof Ints.IntArrayAsList) {
            Ints.IntArrayAsList var2 = (Ints.IntArrayAsList)var1;
            int var3 = this.size();
            if (var2.size() != var3) {
               return false;
            } else {
               for(int var4 = 0; var4 < var3; ++var4) {
                  if (this.array[this.start + var4] != var2.array[var2.start + var4]) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return super.equals(var1);
         }
      }

      public int hashCode() {
         int var1 = 1;

         for(int var2 = this.start; var2 < this.end; ++var2) {
            var1 = 31 * var1 + Ints.hashCode(this.array[var2]);
         }

         return var1;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder(this.size() * 5);
         var1.append('[').append(this.array[this.start]);

         for(int var2 = this.start + 1; var2 < this.end; ++var2) {
            var1.append(", ").append(this.array[var2]);
         }

         return var1.append(']').toString();
      }

      int[] toIntArray() {
         return Arrays.copyOfRange(this.array, this.start, this.end);
      }
   }

   private static enum LexicographicalComparator implements Comparator<int[]> {
      INSTANCE;

      private LexicographicalComparator() {
      }

      public int compare(int[] var1, int[] var2) {
         int var3 = Math.min(var1.length, var2.length);

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = Ints.compare(var1[var4], var2[var4]);
            if (var5 != 0) {
               return var5;
            }
         }

         return var1.length - var2.length;
      }

      public String toString() {
         return "Ints.lexicographicalComparator()";
      }
   }

   private static final class IntConverter extends Converter<String, Integer> implements Serializable {
      static final Ints.IntConverter INSTANCE = new Ints.IntConverter();
      private static final long serialVersionUID = 1L;

      private IntConverter() {
         super();
      }

      protected Integer doForward(String var1) {
         return Integer.decode(var1);
      }

      protected String doBackward(Integer var1) {
         return var1.toString();
      }

      public String toString() {
         return "Ints.stringConverter()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }
}
