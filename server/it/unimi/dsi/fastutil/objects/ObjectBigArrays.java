package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public final class ObjectBigArrays {
   public static final Object[][] EMPTY_BIG_ARRAY = new Object[0][];
   public static final Object[][] DEFAULT_EMPTY_BIG_ARRAY = new Object[0][];
   public static final Hash.Strategy HASH_STRATEGY = new ObjectBigArrays.BigArrayHashStrategy();
   private static final int SMALL = 7;
   private static final int MEDIUM = 40;

   private ObjectBigArrays() {
      super();
   }

   public static <K> K get(K[][] var0, long var1) {
      return var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
   }

   public static <K> void set(K[][] var0, long var1, K var3) {
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var3;
   }

   public static <K> void swap(K[][] var0, long var1, long var3) {
      Object var5 = var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var0[BigArrays.segment(var3)][BigArrays.displacement(var3)];
      var0[BigArrays.segment(var3)][BigArrays.displacement(var3)] = var5;
   }

   public static <K> long length(K[][] var0) {
      int var1 = var0.length;
      return var1 == 0 ? 0L : BigArrays.start(var1 - 1) + (long)var0[var1 - 1].length;
   }

   public static <K> void copy(K[][] var0, long var1, K[][] var3, long var4, long var6) {
      int var8;
      int var9;
      int var10;
      int var11;
      int var12;
      if (var4 <= var1) {
         var8 = BigArrays.segment(var1);
         var9 = BigArrays.segment(var4);
         var10 = BigArrays.displacement(var1);

         for(var11 = BigArrays.displacement(var4); var6 > 0L; var6 -= (long)var12) {
            var12 = (int)Math.min(var6, (long)Math.min(var0[var8].length - var10, var3[var9].length - var11));
            System.arraycopy(var0[var8], var10, var3[var9], var11, var12);
            if ((var10 += var12) == 134217728) {
               var10 = 0;
               ++var8;
            }

            if ((var11 += var12) == 134217728) {
               var11 = 0;
               ++var9;
            }
         }
      } else {
         var8 = BigArrays.segment(var1 + var6);
         var9 = BigArrays.segment(var4 + var6);
         var10 = BigArrays.displacement(var1 + var6);

         for(var11 = BigArrays.displacement(var4 + var6); var6 > 0L; var6 -= (long)var12) {
            if (var10 == 0) {
               var10 = 134217728;
               --var8;
            }

            if (var11 == 0) {
               var11 = 134217728;
               --var9;
            }

            var12 = (int)Math.min(var6, (long)Math.min(var10, var11));
            System.arraycopy(var0[var8], var10 - var12, var3[var9], var11 - var12, var12);
            var10 -= var12;
            var11 -= var12;
         }
      }

   }

   public static <K> void copyFromBig(K[][] var0, long var1, K[] var3, int var4, int var5) {
      int var6 = BigArrays.segment(var1);

      int var8;
      for(int var7 = BigArrays.displacement(var1); var5 > 0; var5 -= var8) {
         var8 = Math.min(var0[var6].length - var7, var5);
         System.arraycopy(var0[var6], var7, var3, var4, var8);
         if ((var7 += var8) == 134217728) {
            var7 = 0;
            ++var6;
         }

         var4 += var8;
      }

   }

   public static <K> void copyToBig(K[] var0, int var1, K[][] var2, long var3, long var5) {
      int var7 = BigArrays.segment(var3);

      int var9;
      for(int var8 = BigArrays.displacement(var3); var5 > 0L; var5 -= (long)var9) {
         var9 = (int)Math.min((long)(var2[var7].length - var8), var5);
         System.arraycopy(var0, var1, var2[var7], var8, var9);
         if ((var8 += var9) == 134217728) {
            var8 = 0;
            ++var7;
         }

         var1 += var9;
      }

   }

   public static <K> K[][] newBigArray(K[][] var0, long var1) {
      return newBigArray(var0.getClass().getComponentType(), var1);
   }

   private static Object[][] newBigArray(Class<?> var0, long var1) {
      if (var1 == 0L && var0 == Object[].class) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(var1);
         int var3 = (int)(var1 + 134217727L >>> 27);
         Object[][] var4 = (Object[][])Array.newInstance(var0, var3);
         int var5 = (int)(var1 & 134217727L);
         int var6;
         if (var5 != 0) {
            for(var6 = 0; var6 < var3 - 1; ++var6) {
               var4[var6] = (Object[])Array.newInstance(var0.getComponentType(), 134217728);
            }

            var4[var3 - 1] = (Object[])Array.newInstance(var0.getComponentType(), var5);
         } else {
            for(var6 = 0; var6 < var3; ++var6) {
               var4[var6] = (Object[])Array.newInstance(var0.getComponentType(), 134217728);
            }
         }

         return var4;
      }
   }

   public static Object[][] newBigArray(long var0) {
      if (var0 == 0L) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(var0);
         int var2 = (int)(var0 + 134217727L >>> 27);
         Object[][] var3 = new Object[var2][];
         int var4 = (int)(var0 & 134217727L);
         int var5;
         if (var4 != 0) {
            for(var5 = 0; var5 < var2 - 1; ++var5) {
               var3[var5] = new Object[134217728];
            }

            var3[var2 - 1] = new Object[var4];
         } else {
            for(var5 = 0; var5 < var2; ++var5) {
               var3[var5] = new Object[134217728];
            }
         }

         return var3;
      }
   }

   public static <K> K[][] wrap(K[] var0) {
      if (var0.length == 0 && var0.getClass() == Object[].class) {
         return EMPTY_BIG_ARRAY;
      } else {
         Object[][] var1;
         if (var0.length <= 134217728) {
            var1 = (Object[][])Array.newInstance(var0.getClass(), 1);
            var1[0] = var0;
            return var1;
         } else {
            var1 = newBigArray(var0.getClass(), (long)var0.length);

            for(int var2 = 0; var2 < var1.length; ++var2) {
               System.arraycopy(var0, (int)BigArrays.start(var2), var1[var2], 0, var1[var2].length);
            }

            return var1;
         }
      }
   }

   public static <K> K[][] ensureCapacity(K[][] var0, long var1) {
      return ensureCapacity(var0, var1, length(var0));
   }

   public static <K> K[][] forceCapacity(K[][] var0, long var1, long var3) {
      BigArrays.ensureLength(var1);
      int var5 = var0.length - (var0.length != 0 && (var0.length <= 0 || var0[var0.length - 1].length != 134217728) ? 1 : 0);
      int var6 = (int)(var1 + 134217727L >>> 27);
      Object[][] var7 = (Object[][])Arrays.copyOf(var0, var6);
      Class var8 = var0.getClass().getComponentType();
      int var9 = (int)(var1 & 134217727L);
      int var10;
      if (var9 != 0) {
         for(var10 = var5; var10 < var6 - 1; ++var10) {
            var7[var10] = (Object[])Array.newInstance(var8.getComponentType(), 134217728);
         }

         var7[var6 - 1] = (Object[])Array.newInstance(var8.getComponentType(), var9);
      } else {
         for(var10 = var5; var10 < var6; ++var10) {
            var7[var10] = (Object[])Array.newInstance(var8.getComponentType(), 134217728);
         }
      }

      if (var3 - (long)var5 * 134217728L > 0L) {
         copy(var0, (long)var5 * 134217728L, var7, (long)var5 * 134217728L, var3 - (long)var5 * 134217728L);
      }

      return var7;
   }

   public static <K> K[][] ensureCapacity(K[][] var0, long var1, long var3) {
      return var1 > length(var0) ? forceCapacity(var0, var1, var3) : var0;
   }

   public static <K> K[][] grow(K[][] var0, long var1) {
      long var3 = length(var0);
      return var1 > var3 ? grow(var0, var1, var3) : var0;
   }

   public static <K> K[][] grow(K[][] var0, long var1, long var3) {
      long var5 = length(var0);
      return var1 > var5 ? ensureCapacity(var0, Math.max(var5 + (var5 >> 1), var1), var3) : var0;
   }

   public static <K> K[][] trim(K[][] var0, long var1) {
      BigArrays.ensureLength(var1);
      long var3 = length(var0);
      if (var1 >= var3) {
         return var0;
      } else {
         int var5 = (int)(var1 + 134217727L >>> 27);
         Object[][] var6 = (Object[][])Arrays.copyOf(var0, var5);
         int var7 = (int)(var1 & 134217727L);
         if (var7 != 0) {
            var6[var5 - 1] = ObjectArrays.trim(var6[var5 - 1], var7);
         }

         return var6;
      }
   }

   public static <K> K[][] setLength(K[][] var0, long var1) {
      long var3 = length(var0);
      if (var1 == var3) {
         return var0;
      } else {
         return var1 < var3 ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static <K> K[][] copy(K[][] var0, long var1, long var3) {
      ensureOffsetLength(var0, var1, var3);
      Object[][] var5 = newBigArray(var0, var3);
      copy(var0, var1, var5, 0L, var3);
      return var5;
   }

   public static <K> K[][] copy(K[][] var0) {
      Object[][] var1 = (Object[][])var0.clone();

      for(int var2 = var1.length; var2-- != 0; var1[var2] = (Object[])var0[var2].clone()) {
      }

      return var1;
   }

   public static <K> void fill(K[][] var0, K var1) {
      int var2 = var0.length;

      while(var2-- != 0) {
         Arrays.fill(var0[var2], var1);
      }

   }

   public static <K> void fill(K[][] var0, long var1, long var3, K var5) {
      long var6 = length(var0);
      BigArrays.ensureFromTo(var6, var1, var3);
      if (var6 != 0L) {
         int var8 = BigArrays.segment(var1);
         int var9 = BigArrays.segment(var3);
         int var10 = BigArrays.displacement(var1);
         int var11 = BigArrays.displacement(var3);
         if (var8 == var9) {
            Arrays.fill(var0[var8], var10, var11, var5);
         } else {
            if (var11 != 0) {
               Arrays.fill(var0[var9], 0, var11, var5);
            }

            while(true) {
               --var9;
               if (var9 <= var8) {
                  Arrays.fill(var0[var8], var10, 134217728, var5);
                  return;
               }

               Arrays.fill(var0[var9], var5);
            }
         }
      }
   }

   public static <K> boolean equals(K[][] var0, K[][] var1) {
      if (length(var0) != length(var1)) {
         return false;
      } else {
         int var2 = var0.length;

         while(var2-- != 0) {
            Object[] var4 = var0[var2];
            Object[] var5 = var1[var2];
            int var3 = var4.length;

            while(var3-- != 0) {
               if (!Objects.equals(var4[var3], var5[var3])) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static <K> String toString(K[][] var0) {
      if (var0 == null) {
         return "null";
      } else {
         long var1 = length(var0) - 1L;
         if (var1 == -1L) {
            return "[]";
         } else {
            StringBuilder var3 = new StringBuilder();
            var3.append('[');
            long var4 = 0L;

            while(true) {
               var3.append(String.valueOf(get(var0, var4)));
               if (var4 == var1) {
                  return var3.append(']').toString();
               }

               var3.append(", ");
               ++var4;
            }
         }
      }
   }

   public static <K> void ensureFromTo(K[][] var0, long var1, long var3) {
      BigArrays.ensureFromTo(length(var0), var1, var3);
   }

   public static <K> void ensureOffsetLength(K[][] var0, long var1, long var3) {
      BigArrays.ensureOffsetLength(length(var0), var1, var3);
   }

   private static <K> void vecSwap(K[][] var0, long var1, long var3, long var5) {
      for(int var7 = 0; (long)var7 < var5; ++var3) {
         swap(var0, var1, var3);
         ++var7;
         ++var1;
      }

   }

   private static <K> long med3(K[][] var0, long var1, long var3, long var5, Comparator<K> var7) {
      int var8 = var7.compare(get(var0, var1), get(var0, var3));
      int var9 = var7.compare(get(var0, var1), get(var0, var5));
      int var10 = var7.compare(get(var0, var3), get(var0, var5));
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var5 : var1)) : (var10 > 0 ? var3 : (var9 > 0 ? var5 : var1));
   }

   private static <K> void selectionSort(K[][] var0, long var1, long var3, Comparator<K> var5) {
      for(long var6 = var1; var6 < var3 - 1L; ++var6) {
         long var8 = var6;

         for(long var10 = var6 + 1L; var10 < var3; ++var10) {
            if (var5.compare(get(var0, var10), get(var0, var8)) < 0) {
               var8 = var10;
            }
         }

         if (var8 != var6) {
            swap(var0, var6, var8);
         }
      }

   }

   public static <K> void quickSort(K[][] var0, long var1, long var3, Comparator<K> var5) {
      long var6 = var3 - var1;
      if (var6 < 7L) {
         selectionSort(var0, var1, var3, var5);
      } else {
         long var8 = var1 + var6 / 2L;
         if (var6 > 7L) {
            long var10 = var1;
            long var12 = var3 - 1L;
            if (var6 > 40L) {
               long var14 = var6 / 8L;
               var10 = med3(var0, var1, var1 + var14, var1 + 2L * var14, var5);
               var8 = med3(var0, var8 - var14, var8, var8 + var14, var5);
               var12 = med3(var0, var12 - 2L * var14, var12 - var14, var12, var5);
            }

            var8 = med3(var0, var10, var8, var12, var5);
         }

         Object var23 = get(var0, var8);
         long var11 = var1;
         long var13 = var1;
         long var15 = var3 - 1L;
         long var17 = var15;

         while(true) {
            int var19;
            while(var13 > var15 || (var19 = var5.compare(get(var0, var13), var23)) > 0) {
               for(; var15 >= var13 && (var19 = var5.compare(get(var0, var15), var23)) >= 0; --var15) {
                  if (var19 == 0) {
                     swap(var0, var15, var17--);
                  }
               }

               if (var13 > var15) {
                  long var24 = Math.min(var11 - var1, var13 - var11);
                  vecSwap(var0, var1, var13 - var24, var24);
                  var24 = Math.min(var17 - var15, var3 - var17 - 1L);
                  vecSwap(var0, var13, var3 - var24, var24);
                  if ((var24 = var13 - var11) > 1L) {
                     quickSort(var0, var1, var1 + var24, var5);
                  }

                  if ((var24 = var17 - var15) > 1L) {
                     quickSort(var0, var3 - var24, var3, var5);
                  }

                  return;
               }

               swap(var0, var13++, var15--);
            }

            if (var19 == 0) {
               swap(var0, var11++, var13);
            }

            ++var13;
         }
      }
   }

   private static <K> long med3(K[][] var0, long var1, long var3, long var5) {
      int var7 = ((Comparable)get(var0, var1)).compareTo(get(var0, var3));
      int var8 = ((Comparable)get(var0, var1)).compareTo(get(var0, var5));
      int var9 = ((Comparable)get(var0, var3)).compareTo(get(var0, var5));
      return var7 < 0 ? (var9 < 0 ? var3 : (var8 < 0 ? var5 : var1)) : (var9 > 0 ? var3 : (var8 > 0 ? var5 : var1));
   }

   private static <K> void selectionSort(K[][] var0, long var1, long var3) {
      for(long var5 = var1; var5 < var3 - 1L; ++var5) {
         long var7 = var5;

         for(long var9 = var5 + 1L; var9 < var3; ++var9) {
            if (((Comparable)get(var0, var9)).compareTo(get(var0, var7)) < 0) {
               var7 = var9;
            }
         }

         if (var7 != var5) {
            swap(var0, var5, var7);
         }
      }

   }

   public static <K> void quickSort(K[][] var0, Comparator<K> var1) {
      quickSort(var0, 0L, length(var0), var1);
   }

   public static <K> void quickSort(K[][] var0, long var1, long var3) {
      long var5 = var3 - var1;
      if (var5 < 7L) {
         selectionSort(var0, var1, var3);
      } else {
         long var7 = var1 + var5 / 2L;
         if (var5 > 7L) {
            long var9 = var1;
            long var11 = var3 - 1L;
            if (var5 > 40L) {
               long var13 = var5 / 8L;
               var9 = med3(var0, var1, var1 + var13, var1 + 2L * var13);
               var7 = med3(var0, var7 - var13, var7, var7 + var13);
               var11 = med3(var0, var11 - 2L * var13, var11 - var13, var11);
            }

            var7 = med3(var0, var9, var7, var11);
         }

         Object var22 = get(var0, var7);
         long var10 = var1;
         long var12 = var1;
         long var14 = var3 - 1L;
         long var16 = var14;

         while(true) {
            int var18;
            while(var12 > var14 || (var18 = ((Comparable)get(var0, var12)).compareTo(var22)) > 0) {
               for(; var14 >= var12 && (var18 = ((Comparable)get(var0, var14)).compareTo(var22)) >= 0; --var14) {
                  if (var18 == 0) {
                     swap(var0, var14, var16--);
                  }
               }

               if (var12 > var14) {
                  long var23 = Math.min(var10 - var1, var12 - var10);
                  vecSwap(var0, var1, var12 - var23, var23);
                  var23 = Math.min(var16 - var14, var3 - var16 - 1L);
                  vecSwap(var0, var12, var3 - var23, var23);
                  if ((var23 = var12 - var10) > 1L) {
                     quickSort(var0, var1, var1 + var23);
                  }

                  if ((var23 = var16 - var14) > 1L) {
                     quickSort(var0, var3 - var23, var3);
                  }

                  return;
               }

               swap(var0, var12++, var14--);
            }

            if (var18 == 0) {
               swap(var0, var10++, var12);
            }

            ++var12;
         }
      }
   }

   public static <K> void quickSort(K[][] var0) {
      quickSort(var0, 0L, length(var0));
   }

   public static <K> long binarySearch(K[][] var0, long var1, long var3, K var5) {
      --var3;

      while(var1 <= var3) {
         long var7 = var1 + var3 >>> 1;
         Object var6 = get(var0, var7);
         int var9 = ((Comparable)var6).compareTo(var5);
         if (var9 < 0) {
            var1 = var7 + 1L;
         } else {
            if (var9 <= 0) {
               return var7;
            }

            var3 = var7 - 1L;
         }
      }

      return -(var1 + 1L);
   }

   public static <K> long binarySearch(K[][] var0, Object var1) {
      return binarySearch(var0, 0L, length(var0), var1);
   }

   public static <K> long binarySearch(K[][] var0, long var1, long var3, K var5, Comparator<K> var6) {
      --var3;

      while(var1 <= var3) {
         long var8 = var1 + var3 >>> 1;
         Object var7 = get(var0, var8);
         int var10 = var6.compare(var7, var5);
         if (var10 < 0) {
            var1 = var8 + 1L;
         } else {
            if (var10 <= 0) {
               return var8;
            }

            var3 = var8 - 1L;
         }
      }

      return -(var1 + 1L);
   }

   public static <K> long binarySearch(K[][] var0, K var1, Comparator<K> var2) {
      return binarySearch(var0, 0L, length(var0), var1, var2);
   }

   public static <K> K[][] shuffle(K[][] var0, long var1, long var3, Random var5) {
      long var6 = var3 - var1;

      while(var6-- != 0L) {
         long var8 = (var5.nextLong() & 9223372036854775807L) % (var6 + 1L);
         Object var10 = get(var0, var1 + var6);
         set(var0, var1 + var6, get(var0, var1 + var8));
         set(var0, var1 + var8, var10);
      }

      return var0;
   }

   public static <K> K[][] shuffle(K[][] var0, Random var1) {
      long var2 = length(var0);

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         Object var6 = get(var0, var2);
         set(var0, var2, get(var0, var4));
         set(var0, var4, var6);
      }

      return var0;
   }

   private static final class BigArrayHashStrategy<K> implements Hash.Strategy<K[][]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private BigArrayHashStrategy() {
         super();
      }

      public int hashCode(K[][] var1) {
         return Arrays.deepHashCode(var1);
      }

      public boolean equals(K[][] var1, K[][] var2) {
         return ObjectBigArrays.equals(var1, var2);
      }

      // $FF: synthetic method
      BigArrayHashStrategy(Object var1) {
         this();
      }
   }
}
