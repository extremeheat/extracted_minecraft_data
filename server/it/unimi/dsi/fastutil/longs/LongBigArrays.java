package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class LongBigArrays {
   public static final long[][] EMPTY_BIG_ARRAY = new long[0][];
   public static final long[][] DEFAULT_EMPTY_BIG_ARRAY = new long[0][];
   public static final Hash.Strategy HASH_STRATEGY = new LongBigArrays.BigArrayHashStrategy();
   private static final int SMALL = 7;
   private static final int MEDIUM = 40;
   private static final int DIGIT_BITS = 8;
   private static final int DIGIT_MASK = 255;
   private static final int DIGITS_PER_ELEMENT = 8;

   private LongBigArrays() {
      super();
   }

   public static long get(long[][] var0, long var1) {
      return var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
   }

   public static void set(long[][] var0, long var1, long var3) {
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var3;
   }

   public static void swap(long[][] var0, long var1, long var3) {
      long var5 = var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var0[BigArrays.segment(var3)][BigArrays.displacement(var3)];
      var0[BigArrays.segment(var3)][BigArrays.displacement(var3)] = var5;
   }

   public static void add(long[][] var0, long var1, long var3) {
      long[] var10000 = var0[BigArrays.segment(var1)];
      int var10001 = BigArrays.displacement(var1);
      var10000[var10001] += var3;
   }

   public static void mul(long[][] var0, long var1, long var3) {
      long[] var10000 = var0[BigArrays.segment(var1)];
      int var10001 = BigArrays.displacement(var1);
      var10000[var10001] *= var3;
   }

   public static void incr(long[][] var0, long var1) {
      int var10002 = var0[BigArrays.segment(var1)][BigArrays.displacement(var1)]++;
   }

   public static void decr(long[][] var0, long var1) {
      int var10002 = var0[BigArrays.segment(var1)][BigArrays.displacement(var1)]--;
   }

   public static long length(long[][] var0) {
      int var1 = var0.length;
      return var1 == 0 ? 0L : BigArrays.start(var1 - 1) + (long)var0[var1 - 1].length;
   }

   public static void copy(long[][] var0, long var1, long[][] var3, long var4, long var6) {
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

   public static void copyFromBig(long[][] var0, long var1, long[] var3, int var4, int var5) {
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

   public static void copyToBig(long[] var0, int var1, long[][] var2, long var3, long var5) {
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

   public static long[][] newBigArray(long var0) {
      if (var0 == 0L) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(var0);
         int var2 = (int)(var0 + 134217727L >>> 27);
         long[][] var3 = new long[var2][];
         int var4 = (int)(var0 & 134217727L);
         int var5;
         if (var4 != 0) {
            for(var5 = 0; var5 < var2 - 1; ++var5) {
               var3[var5] = new long[134217728];
            }

            var3[var2 - 1] = new long[var4];
         } else {
            for(var5 = 0; var5 < var2; ++var5) {
               var3[var5] = new long[134217728];
            }
         }

         return var3;
      }
   }

   public static long[][] wrap(long[] var0) {
      if (var0.length == 0) {
         return EMPTY_BIG_ARRAY;
      } else if (var0.length <= 134217728) {
         return new long[][]{var0};
      } else {
         long[][] var1 = newBigArray((long)var0.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            System.arraycopy(var0, (int)BigArrays.start(var2), var1[var2], 0, var1[var2].length);
         }

         return var1;
      }
   }

   public static long[][] ensureCapacity(long[][] var0, long var1) {
      return ensureCapacity(var0, var1, length(var0));
   }

   public static long[][] forceCapacity(long[][] var0, long var1, long var3) {
      BigArrays.ensureLength(var1);
      int var5 = var0.length - (var0.length != 0 && (var0.length <= 0 || var0[var0.length - 1].length != 134217728) ? 1 : 0);
      int var6 = (int)(var1 + 134217727L >>> 27);
      long[][] var7 = (long[][])Arrays.copyOf(var0, var6);
      int var8 = (int)(var1 & 134217727L);
      int var9;
      if (var8 != 0) {
         for(var9 = var5; var9 < var6 - 1; ++var9) {
            var7[var9] = new long[134217728];
         }

         var7[var6 - 1] = new long[var8];
      } else {
         for(var9 = var5; var9 < var6; ++var9) {
            var7[var9] = new long[134217728];
         }
      }

      if (var3 - (long)var5 * 134217728L > 0L) {
         copy(var0, (long)var5 * 134217728L, var7, (long)var5 * 134217728L, var3 - (long)var5 * 134217728L);
      }

      return var7;
   }

   public static long[][] ensureCapacity(long[][] var0, long var1, long var3) {
      return var1 > length(var0) ? forceCapacity(var0, var1, var3) : var0;
   }

   public static long[][] grow(long[][] var0, long var1) {
      long var3 = length(var0);
      return var1 > var3 ? grow(var0, var1, var3) : var0;
   }

   public static long[][] grow(long[][] var0, long var1, long var3) {
      long var5 = length(var0);
      return var1 > var5 ? ensureCapacity(var0, Math.max(var5 + (var5 >> 1), var1), var3) : var0;
   }

   public static long[][] trim(long[][] var0, long var1) {
      BigArrays.ensureLength(var1);
      long var3 = length(var0);
      if (var1 >= var3) {
         return var0;
      } else {
         int var5 = (int)(var1 + 134217727L >>> 27);
         long[][] var6 = (long[][])Arrays.copyOf(var0, var5);
         int var7 = (int)(var1 & 134217727L);
         if (var7 != 0) {
            var6[var5 - 1] = LongArrays.trim(var6[var5 - 1], var7);
         }

         return var6;
      }
   }

   public static long[][] setLength(long[][] var0, long var1) {
      long var3 = length(var0);
      if (var1 == var3) {
         return var0;
      } else {
         return var1 < var3 ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static long[][] copy(long[][] var0, long var1, long var3) {
      ensureOffsetLength(var0, var1, var3);
      long[][] var5 = newBigArray(var3);
      copy(var0, var1, var5, 0L, var3);
      return var5;
   }

   public static long[][] copy(long[][] var0) {
      long[][] var1 = (long[][])var0.clone();

      for(int var2 = var1.length; var2-- != 0; var1[var2] = (long[])var0[var2].clone()) {
      }

      return var1;
   }

   public static void fill(long[][] var0, long var1) {
      int var3 = var0.length;

      while(var3-- != 0) {
         Arrays.fill(var0[var3], var1);
      }

   }

   public static void fill(long[][] var0, long var1, long var3, long var5) {
      long var7 = length(var0);
      BigArrays.ensureFromTo(var7, var1, var3);
      if (var7 != 0L) {
         int var9 = BigArrays.segment(var1);
         int var10 = BigArrays.segment(var3);
         int var11 = BigArrays.displacement(var1);
         int var12 = BigArrays.displacement(var3);
         if (var9 == var10) {
            Arrays.fill(var0[var9], var11, var12, var5);
         } else {
            if (var12 != 0) {
               Arrays.fill(var0[var10], 0, var12, var5);
            }

            while(true) {
               --var10;
               if (var10 <= var9) {
                  Arrays.fill(var0[var9], var11, 134217728, var5);
                  return;
               }

               Arrays.fill(var0[var10], var5);
            }
         }
      }
   }

   public static boolean equals(long[][] var0, long[][] var1) {
      if (length(var0) != length(var1)) {
         return false;
      } else {
         int var2 = var0.length;

         while(var2-- != 0) {
            long[] var4 = var0[var2];
            long[] var5 = var1[var2];
            int var3 = var4.length;

            while(var3-- != 0) {
               if (var4[var3] != var5[var3]) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static String toString(long[][] var0) {
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

   public static void ensureFromTo(long[][] var0, long var1, long var3) {
      BigArrays.ensureFromTo(length(var0), var1, var3);
   }

   public static void ensureOffsetLength(long[][] var0, long var1, long var3) {
      BigArrays.ensureOffsetLength(length(var0), var1, var3);
   }

   private static void vecSwap(long[][] var0, long var1, long var3, long var5) {
      for(int var7 = 0; (long)var7 < var5; ++var3) {
         swap(var0, var1, var3);
         ++var7;
         ++var1;
      }

   }

   private static long med3(long[][] var0, long var1, long var3, long var5, LongComparator var7) {
      int var8 = var7.compare(get(var0, var1), get(var0, var3));
      int var9 = var7.compare(get(var0, var1), get(var0, var5));
      int var10 = var7.compare(get(var0, var3), get(var0, var5));
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var5 : var1)) : (var10 > 0 ? var3 : (var9 > 0 ? var5 : var1));
   }

   private static void selectionSort(long[][] var0, long var1, long var3, LongComparator var5) {
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

   public static void quickSort(long[][] var0, long var1, long var3, LongComparator var5) {
      long var6 = var3 - var1;
      if (var6 < 7L) {
         selectionSort(var0, var1, var3, var5);
      } else {
         long var8 = var1 + var6 / 2L;
         long var10;
         long var12;
         long var14;
         if (var6 > 7L) {
            var10 = var1;
            var12 = var3 - 1L;
            if (var6 > 40L) {
               var14 = var6 / 8L;
               var10 = med3(var0, var1, var1 + var14, var1 + 2L * var14, var5);
               var8 = med3(var0, var8 - var14, var8, var8 + var14, var5);
               var12 = med3(var0, var12 - 2L * var14, var12 - var14, var12, var5);
            }

            var8 = med3(var0, var10, var8, var12, var5);
         }

         var10 = get(var0, var8);
         var12 = var1;
         var14 = var1;
         long var16 = var3 - 1L;
         long var18 = var16;

         while(true) {
            int var20;
            while(var14 > var16 || (var20 = var5.compare(get(var0, var14), var10)) > 0) {
               for(; var16 >= var14 && (var20 = var5.compare(get(var0, var16), var10)) >= 0; --var16) {
                  if (var20 == 0) {
                     swap(var0, var16, var18--);
                  }
               }

               if (var14 > var16) {
                  long var24 = Math.min(var12 - var1, var14 - var12);
                  vecSwap(var0, var1, var14 - var24, var24);
                  var24 = Math.min(var18 - var16, var3 - var18 - 1L);
                  vecSwap(var0, var14, var3 - var24, var24);
                  if ((var24 = var14 - var12) > 1L) {
                     quickSort(var0, var1, var1 + var24, var5);
                  }

                  if ((var24 = var18 - var16) > 1L) {
                     quickSort(var0, var3 - var24, var3, var5);
                  }

                  return;
               }

               swap(var0, var14++, var16--);
            }

            if (var20 == 0) {
               swap(var0, var12++, var14);
            }

            ++var14;
         }
      }
   }

   private static long med3(long[][] var0, long var1, long var3, long var5) {
      int var7 = Long.compare(get(var0, var1), get(var0, var3));
      int var8 = Long.compare(get(var0, var1), get(var0, var5));
      int var9 = Long.compare(get(var0, var3), get(var0, var5));
      return var7 < 0 ? (var9 < 0 ? var3 : (var8 < 0 ? var5 : var1)) : (var9 > 0 ? var3 : (var8 > 0 ? var5 : var1));
   }

   private static void selectionSort(long[][] var0, long var1, long var3) {
      for(long var5 = var1; var5 < var3 - 1L; ++var5) {
         long var7 = var5;

         for(long var9 = var5 + 1L; var9 < var3; ++var9) {
            if (get(var0, var9) < get(var0, var7)) {
               var7 = var9;
            }
         }

         if (var7 != var5) {
            swap(var0, var5, var7);
         }
      }

   }

   public static void quickSort(long[][] var0, LongComparator var1) {
      quickSort(var0, 0L, length(var0), var1);
   }

   public static void quickSort(long[][] var0, long var1, long var3) {
      long var5 = var3 - var1;
      if (var5 < 7L) {
         selectionSort(var0, var1, var3);
      } else {
         long var7 = var1 + var5 / 2L;
         long var9;
         long var11;
         long var13;
         if (var5 > 7L) {
            var9 = var1;
            var11 = var3 - 1L;
            if (var5 > 40L) {
               var13 = var5 / 8L;
               var9 = med3(var0, var1, var1 + var13, var1 + 2L * var13);
               var7 = med3(var0, var7 - var13, var7, var7 + var13);
               var11 = med3(var0, var11 - 2L * var13, var11 - var13, var11);
            }

            var7 = med3(var0, var9, var7, var11);
         }

         var9 = get(var0, var7);
         var11 = var1;
         var13 = var1;
         long var15 = var3 - 1L;
         long var17 = var15;

         while(true) {
            int var19;
            while(var13 > var15 || (var19 = Long.compare(get(var0, var13), var9)) > 0) {
               for(; var15 >= var13 && (var19 = Long.compare(get(var0, var15), var9)) >= 0; --var15) {
                  if (var19 == 0) {
                     swap(var0, var15, var17--);
                  }
               }

               if (var13 > var15) {
                  long var23 = Math.min(var11 - var1, var13 - var11);
                  vecSwap(var0, var1, var13 - var23, var23);
                  var23 = Math.min(var17 - var15, var3 - var17 - 1L);
                  vecSwap(var0, var13, var3 - var23, var23);
                  if ((var23 = var13 - var11) > 1L) {
                     quickSort(var0, var1, var1 + var23);
                  }

                  if ((var23 = var17 - var15) > 1L) {
                     quickSort(var0, var3 - var23, var3);
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

   public static void quickSort(long[][] var0) {
      quickSort(var0, 0L, length(var0));
   }

   public static long binarySearch(long[][] var0, long var1, long var3, long var5) {
      --var3;

      while(var1 <= var3) {
         long var9 = var1 + var3 >>> 1;
         long var7 = get(var0, var9);
         if (var7 < var5) {
            var1 = var9 + 1L;
         } else {
            if (var7 <= var5) {
               return var9;
            }

            var3 = var9 - 1L;
         }
      }

      return -(var1 + 1L);
   }

   public static long binarySearch(long[][] var0, long var1) {
      return binarySearch(var0, 0L, length(var0), var1);
   }

   public static long binarySearch(long[][] var0, long var1, long var3, long var5, LongComparator var7) {
      --var3;

      while(var1 <= var3) {
         long var10 = var1 + var3 >>> 1;
         long var8 = get(var0, var10);
         int var12 = var7.compare(var8, var5);
         if (var12 < 0) {
            var1 = var10 + 1L;
         } else {
            if (var12 <= 0) {
               return var10;
            }

            var3 = var10 - 1L;
         }
      }

      return -(var1 + 1L);
   }

   public static long binarySearch(long[][] var0, long var1, LongComparator var3) {
      return binarySearch(var0, 0L, length(var0), var1, var3);
   }

   public static void radixSort(long[][] var0) {
      radixSort(var0, 0L, length(var0));
   }

   public static void radixSort(long[][] var0, long var1, long var3) {
      boolean var5 = true;
      boolean var6 = true;
      long[] var7 = new long[1786];
      byte var8 = 0;
      long[] var9 = new long[1786];
      byte var10 = 0;
      int[] var11 = new int[1786];
      byte var12 = 0;
      int var38 = var8 + 1;
      var7[var8] = var1;
      int var39 = var10 + 1;
      var9[var10] = var3 - var1;
      int var40 = var12 + 1;
      var11[var12] = 0;
      long[] var13 = new long[256];
      long[] var14 = new long[256];
      byte[][] var15 = ByteBigArrays.newBigArray(var3 - var1);

      while(true) {
         while(var38 > 0) {
            --var38;
            long var16 = var7[var38];
            --var39;
            long var18 = var9[var39];
            --var40;
            int var20 = var11[var40];
            int var21 = var20 % 8 == 0 ? 128 : 0;
            if (var18 < 40L) {
               selectionSort(var0, var16, var16 + var18);
            } else {
               int var22 = (7 - var20 % 8) * 8;
               long var23 = var18;

               while(var23-- != 0L) {
                  ByteBigArrays.set(var15, var23, (byte)((int)(get(var0, var16 + var23) >>> var22 & 255L ^ (long)var21)));
               }

               for(var23 = var18; var23-- != 0L; ++var13[ByteBigArrays.get(var15, var23) & 255]) {
               }

               int var42 = -1;
               long var24 = 0L;

               for(int var26 = 0; var26 < 256; ++var26) {
                  if (var13[var26] != 0L) {
                     var42 = var26;
                     if (var20 < 7 && var13[var26] > 1L) {
                        var7[var38++] = var24 + var16;
                        var9[var39++] = var13[var26];
                        var11[var40++] = var20 + 1;
                     }
                  }

                  var14[var26] = var24 += var13[var26];
               }

               long var43 = var18 - var13[var42];
               var13[var42] = 0L;
               boolean var28 = true;

               int var44;
               for(long var29 = 0L; var29 < var43; var13[var44] = 0L) {
                  long var33 = get(var0, var29 + var16);
                  var44 = ByteBigArrays.get(var15, var29) & 255;

                  long var31;
                  while((var31 = --var14[var44]) > var29) {
                     long var35 = var33;
                     int var37 = var44;
                     var33 = get(var0, var31 + var16);
                     var44 = ByteBigArrays.get(var15, var31) & 255;
                     set(var0, var31 + var16, var35);
                     ByteBigArrays.set(var15, var31, (byte)var37);
                  }

                  set(var0, var29 + var16, var33);
                  var29 += var13[var44];
               }
            }
         }

         return;
      }
   }

   private static void selectionSort(long[][] var0, long[][] var1, long var2, long var4) {
      for(long var6 = var2; var6 < var4 - 1L; ++var6) {
         long var8 = var6;

         long var10;
         for(var10 = var6 + 1L; var10 < var4; ++var10) {
            if (get(var0, var10) < get(var0, var8) || get(var0, var10) == get(var0, var8) && get(var1, var10) < get(var1, var8)) {
               var8 = var10;
            }
         }

         if (var8 != var6) {
            var10 = get(var0, var6);
            set(var0, var6, get(var0, var8));
            set(var0, var8, var10);
            var10 = get(var1, var6);
            set(var1, var6, get(var1, var8));
            set(var1, var8, var10);
         }
      }

   }

   public static void radixSort(long[][] var0, long[][] var1) {
      radixSort(var0, var1, 0L, length(var0));
   }

   public static void radixSort(long[][] var0, long[][] var1, long var2, long var4) {
      boolean var6 = true;
      if (length(var0) != length(var1)) {
         throw new IllegalArgumentException("Array size mismatch.");
      } else {
         boolean var7 = true;
         boolean var8 = true;
         long[] var9 = new long[3826];
         byte var10 = 0;
         long[] var11 = new long[3826];
         byte var12 = 0;
         int[] var13 = new int[3826];
         byte var14 = 0;
         int var43 = var10 + 1;
         var9[var10] = var2;
         int var44 = var12 + 1;
         var11[var12] = var4 - var2;
         int var45 = var14 + 1;
         var13[var14] = 0;
         long[] var15 = new long[256];
         long[] var16 = new long[256];
         byte[][] var17 = ByteBigArrays.newBigArray(var4 - var2);

         while(true) {
            while(var43 > 0) {
               --var43;
               long var18 = var9[var43];
               --var44;
               long var20 = var11[var44];
               --var45;
               int var22 = var13[var45];
               int var23 = var22 % 8 == 0 ? 128 : 0;
               if (var20 < 40L) {
                  selectionSort(var0, var1, var18, var18 + var20);
               } else {
                  long[][] var24 = var22 < 8 ? var0 : var1;
                  int var25 = (7 - var22 % 8) * 8;
                  long var26 = var20;

                  while(var26-- != 0L) {
                     ByteBigArrays.set(var17, var26, (byte)((int)(get(var24, var18 + var26) >>> var25 & 255L ^ (long)var23)));
                  }

                  for(var26 = var20; var26-- != 0L; ++var15[ByteBigArrays.get(var17, var26) & 255]) {
                  }

                  int var47 = -1;
                  long var27 = 0L;

                  for(int var29 = 0; var29 < 256; ++var29) {
                     if (var15[var29] != 0L) {
                        var47 = var29;
                        if (var22 < 15 && var15[var29] > 1L) {
                           var9[var43++] = var27 + var18;
                           var11[var44++] = var15[var29];
                           var13[var45++] = var22 + 1;
                        }
                     }

                     var16[var29] = var27 += var15[var29];
                  }

                  long var48 = var20 - var15[var47];
                  var15[var47] = 0L;
                  boolean var31 = true;

                  int var49;
                  for(long var32 = 0L; var32 < var48; var15[var49] = 0L) {
                     long var36 = get(var0, var32 + var18);
                     long var38 = get(var1, var32 + var18);
                     var49 = ByteBigArrays.get(var17, var32) & 255;

                     long var34;
                     while((var34 = --var16[var49]) > var32) {
                        long var40 = var36;
                        int var42 = var49;
                        var36 = get(var0, var34 + var18);
                        set(var0, var34 + var18, var40);
                        var40 = var38;
                        var38 = get(var1, var34 + var18);
                        set(var1, var34 + var18, var40);
                        var49 = ByteBigArrays.get(var17, var34) & 255;
                        ByteBigArrays.set(var17, var34, (byte)var42);
                     }

                     set(var0, var32 + var18, var36);
                     set(var1, var32 + var18, var38);
                     var32 += var15[var49];
                  }
               }
            }

            return;
         }
      }
   }

   public static long[][] shuffle(long[][] var0, long var1, long var3, Random var5) {
      long var6 = var3 - var1;

      while(var6-- != 0L) {
         long var8 = (var5.nextLong() & 9223372036854775807L) % (var6 + 1L);
         long var10 = get(var0, var1 + var6);
         set(var0, var1 + var6, get(var0, var1 + var8));
         set(var0, var1 + var8, var10);
      }

      return var0;
   }

   public static long[][] shuffle(long[][] var0, Random var1) {
      long var2 = length(var0);

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         long var6 = get(var0, var2);
         set(var0, var2, get(var0, var4));
         set(var0, var4, var6);
      }

      return var0;
   }

   private static final class BigArrayHashStrategy implements Hash.Strategy<long[][]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private BigArrayHashStrategy() {
         super();
      }

      public int hashCode(long[][] var1) {
         return Arrays.deepHashCode(var1);
      }

      public boolean equals(long[][] var1, long[][] var2) {
         return LongBigArrays.equals(var1, var2);
      }

      // $FF: synthetic method
      BigArrayHashStrategy(Object var1) {
         this();
      }
   }
}
