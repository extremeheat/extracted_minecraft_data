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
public final class Longs {
   public static final int BYTES = 8;
   public static final long MAX_POWER_OF_TWO = 4611686018427387904L;
   private static final byte[] asciiDigits = createAsciiDigits();

   private Longs() {
      super();
   }

   public static int hashCode(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public static int compare(long var0, long var2) {
      return var0 < var2 ? -1 : (var0 > var2 ? 1 : 0);
   }

   public static boolean contains(long[] var0, long var1) {
      long[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long var6 = var3[var5];
         if (var6 == var1) {
            return true;
         }
      }

      return false;
   }

   public static int indexOf(long[] var0, long var1) {
      return indexOf(var0, var1, 0, var0.length);
   }

   private static int indexOf(long[] var0, long var1, int var3, int var4) {
      for(int var5 = var3; var5 < var4; ++var5) {
         if (var0[var5] == var1) {
            return var5;
         }
      }

      return -1;
   }

   public static int indexOf(long[] var0, long[] var1) {
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

   public static int lastIndexOf(long[] var0, long var1) {
      return lastIndexOf(var0, var1, 0, var0.length);
   }

   private static int lastIndexOf(long[] var0, long var1, int var3, int var4) {
      for(int var5 = var4 - 1; var5 >= var3; --var5) {
         if (var0[var5] == var1) {
            return var5;
         }
      }

      return -1;
   }

   public static long min(long... var0) {
      Preconditions.checkArgument(var0.length > 0);
      long var1 = var0[0];

      for(int var3 = 1; var3 < var0.length; ++var3) {
         if (var0[var3] < var1) {
            var1 = var0[var3];
         }
      }

      return var1;
   }

   public static long max(long... var0) {
      Preconditions.checkArgument(var0.length > 0);
      long var1 = var0[0];

      for(int var3 = 1; var3 < var0.length; ++var3) {
         if (var0[var3] > var1) {
            var1 = var0[var3];
         }
      }

      return var1;
   }

   @Beta
   public static long constrainToRange(long var0, long var2, long var4) {
      Preconditions.checkArgument(var2 <= var4, "min (%s) must be less than or equal to max (%s)", var2, var4);
      return Math.min(Math.max(var0, var2), var4);
   }

   public static long[] concat(long[]... var0) {
      int var1 = 0;
      long[][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long[] var5 = var2[var4];
         var1 += var5.length;
      }

      long[] var8 = new long[var1];
      var3 = 0;
      long[][] var9 = var0;
      int var10 = var0.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         long[] var7 = var9[var6];
         System.arraycopy(var7, 0, var8, var3, var7.length);
         var3 += var7.length;
      }

      return var8;
   }

   public static byte[] toByteArray(long var0) {
      byte[] var2 = new byte[8];

      for(int var3 = 7; var3 >= 0; --var3) {
         var2[var3] = (byte)((int)(var0 & 255L));
         var0 >>= 8;
      }

      return var2;
   }

   public static long fromByteArray(byte[] var0) {
      Preconditions.checkArgument(var0.length >= 8, "array too small: %s < %s", (int)var0.length, (int)8);
      return fromBytes(var0[0], var0[1], var0[2], var0[3], var0[4], var0[5], var0[6], var0[7]);
   }

   public static long fromBytes(byte var0, byte var1, byte var2, byte var3, byte var4, byte var5, byte var6, byte var7) {
      return ((long)var0 & 255L) << 56 | ((long)var1 & 255L) << 48 | ((long)var2 & 255L) << 40 | ((long)var3 & 255L) << 32 | ((long)var4 & 255L) << 24 | ((long)var5 & 255L) << 16 | ((long)var6 & 255L) << 8 | (long)var7 & 255L;
   }

   private static byte[] createAsciiDigits() {
      byte[] var0 = new byte[128];
      Arrays.fill(var0, (byte)-1);

      int var1;
      for(var1 = 0; var1 <= 9; ++var1) {
         var0[48 + var1] = (byte)var1;
      }

      for(var1 = 0; var1 <= 26; ++var1) {
         var0[65 + var1] = (byte)(10 + var1);
         var0[97 + var1] = (byte)(10 + var1);
      }

      return var0;
   }

   private static int digit(char var0) {
      return var0 < 128 ? asciiDigits[var0] : -1;
   }

   @Nullable
   @CheckForNull
   @Beta
   public static Long tryParse(String var0) {
      return tryParse(var0, 10);
   }

   @Nullable
   @CheckForNull
   @Beta
   public static Long tryParse(String var0, int var1) {
      if (((String)Preconditions.checkNotNull(var0)).isEmpty()) {
         return null;
      } else if (var1 >= 2 && var1 <= 36) {
         boolean var2 = var0.charAt(0) == '-';
         int var3 = var2 ? 1 : 0;
         if (var3 == var0.length()) {
            return null;
         } else {
            int var4 = digit(var0.charAt(var3++));
            if (var4 >= 0 && var4 < var1) {
               long var5 = (long)(-var4);

               for(long var7 = -9223372036854775808L / (long)var1; var3 < var0.length(); var5 -= (long)var4) {
                  var4 = digit(var0.charAt(var3++));
                  if (var4 < 0 || var4 >= var1 || var5 < var7) {
                     return null;
                  }

                  var5 *= (long)var1;
                  if (var5 < -9223372036854775808L + (long)var4) {
                     return null;
                  }
               }

               if (var2) {
                  return var5;
               } else if (var5 == -9223372036854775808L) {
                  return null;
               } else {
                  return -var5;
               }
            } else {
               return null;
            }
         }
      } else {
         throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + var1);
      }
   }

   @Beta
   public static Converter<String, Long> stringConverter() {
      return Longs.LongConverter.INSTANCE;
   }

   public static long[] ensureCapacity(long[] var0, int var1, int var2) {
      Preconditions.checkArgument(var1 >= 0, "Invalid minLength: %s", var1);
      Preconditions.checkArgument(var2 >= 0, "Invalid padding: %s", var2);
      return var0.length < var1 ? Arrays.copyOf(var0, var1 + var2) : var0;
   }

   public static String join(String var0, long... var1) {
      Preconditions.checkNotNull(var0);
      if (var1.length == 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1.length * 10);
         var2.append(var1[0]);

         for(int var3 = 1; var3 < var1.length; ++var3) {
            var2.append(var0).append(var1[var3]);
         }

         return var2.toString();
      }
   }

   public static Comparator<long[]> lexicographicalComparator() {
      return Longs.LexicographicalComparator.INSTANCE;
   }

   public static long[] toArray(Collection<? extends Number> var0) {
      if (var0 instanceof Longs.LongArrayAsList) {
         return ((Longs.LongArrayAsList)var0).toLongArray();
      } else {
         Object[] var1 = var0.toArray();
         int var2 = var1.length;
         long[] var3 = new long[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = ((Number)Preconditions.checkNotNull(var1[var4])).longValue();
         }

         return var3;
      }
   }

   public static List<Long> asList(long... var0) {
      return (List)(var0.length == 0 ? Collections.emptyList() : new Longs.LongArrayAsList(var0));
   }

   @GwtCompatible
   private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable {
      final long[] array;
      final int start;
      final int end;
      private static final long serialVersionUID = 0L;

      LongArrayAsList(long[] var1) {
         this(var1, 0, var1.length);
      }

      LongArrayAsList(long[] var1, int var2, int var3) {
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

      public Long get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.array[this.start + var1];
      }

      public boolean contains(Object var1) {
         return var1 instanceof Long && Longs.indexOf(this.array, (Long)var1, this.start, this.end) != -1;
      }

      public int indexOf(Object var1) {
         if (var1 instanceof Long) {
            int var2 = Longs.indexOf(this.array, (Long)var1, this.start, this.end);
            if (var2 >= 0) {
               return var2 - this.start;
            }
         }

         return -1;
      }

      public int lastIndexOf(Object var1) {
         if (var1 instanceof Long) {
            int var2 = Longs.lastIndexOf(this.array, (Long)var1, this.start, this.end);
            if (var2 >= 0) {
               return var2 - this.start;
            }
         }

         return -1;
      }

      public Long set(int var1, Long var2) {
         Preconditions.checkElementIndex(var1, this.size());
         long var3 = this.array[this.start + var1];
         this.array[this.start + var1] = (Long)Preconditions.checkNotNull(var2);
         return var3;
      }

      public List<Long> subList(int var1, int var2) {
         int var3 = this.size();
         Preconditions.checkPositionIndexes(var1, var2, var3);
         return (List)(var1 == var2 ? Collections.emptyList() : new Longs.LongArrayAsList(this.array, this.start + var1, this.start + var2));
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 instanceof Longs.LongArrayAsList) {
            Longs.LongArrayAsList var2 = (Longs.LongArrayAsList)var1;
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
            var1 = 31 * var1 + Longs.hashCode(this.array[var2]);
         }

         return var1;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder(this.size() * 10);
         var1.append('[').append(this.array[this.start]);

         for(int var2 = this.start + 1; var2 < this.end; ++var2) {
            var1.append(", ").append(this.array[var2]);
         }

         return var1.append(']').toString();
      }

      long[] toLongArray() {
         return Arrays.copyOfRange(this.array, this.start, this.end);
      }
   }

   private static enum LexicographicalComparator implements Comparator<long[]> {
      INSTANCE;

      private LexicographicalComparator() {
      }

      public int compare(long[] var1, long[] var2) {
         int var3 = Math.min(var1.length, var2.length);

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = Longs.compare(var1[var4], var2[var4]);
            if (var5 != 0) {
               return var5;
            }
         }

         return var1.length - var2.length;
      }

      public String toString() {
         return "Longs.lexicographicalComparator()";
      }
   }

   private static final class LongConverter extends Converter<String, Long> implements Serializable {
      static final Longs.LongConverter INSTANCE = new Longs.LongConverter();
      private static final long serialVersionUID = 1L;

      private LongConverter() {
         super();
      }

      protected Long doForward(String var1) {
         return Long.decode(var1);
      }

      protected String doBackward(Long var1) {
         return var1.toString();
      }

      public String toString() {
         return "Longs.stringConverter()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }
}
