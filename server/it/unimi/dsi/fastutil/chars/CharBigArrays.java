package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class CharBigArrays {
   public static final char[][] EMPTY_BIG_ARRAY = new char[0][];
   public static final char[][] DEFAULT_EMPTY_BIG_ARRAY = new char[0][];
   public static final Hash.Strategy HASH_STRATEGY = new CharBigArrays.BigArrayHashStrategy();
   private static final int SMALL = 7;
   private static final int MEDIUM = 40;
   private static final int DIGIT_BITS = 8;
   private static final int DIGIT_MASK = 255;
   private static final int DIGITS_PER_ELEMENT = 2;

   private CharBigArrays() {
      super();
   }

   public static char get(char[][] var0, long var1) {
      return var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
   }

   public static void set(char[][] var0, long var1, char var3) {
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var3;
   }

   public static void swap(char[][] var0, long var1, long var3) {
      char var5 = var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
      var0[BigArrays.segment(var1)][BigArrays.displacement(var1)] = var0[BigArrays.segment(var3)][BigArrays.displacement(var3)];
      var0[BigArrays.segment(var3)][BigArrays.displacement(var3)] = var5;
   }

   public static void add(char[][] var0, long var1, char var3) {
      char[] var10000 = var0[BigArrays.segment(var1)];
      int var10001 = BigArrays.displacement(var1);
      var10000[var10001] += var3;
   }

   public static void mul(char[][] var0, long var1, char var3) {
      char[] var10000 = var0[BigArrays.segment(var1)];
      int var10001 = BigArrays.displacement(var1);
      var10000[var10001] *= var3;
   }

   public static void incr(char[][] var0, long var1) {
      ++var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
   }

   public static void decr(char[][] var0, long var1) {
      --var0[BigArrays.segment(var1)][BigArrays.displacement(var1)];
   }

   public static long length(char[][] var0) {
      int var1 = var0.length;
      return var1 == 0 ? 0L : BigArrays.start(var1 - 1) + (long)var0[var1 - 1].length;
   }

   public static void copy(char[][] var0, long var1, char[][] var3, long var4, long var6) {
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

   public static void copyFromBig(char[][] var0, long var1, char[] var3, int var4, int var5) {
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

   public static void copyToBig(char[] var0, int var1, char[][] var2, long var3, long var5) {
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

   public static char[][] newBigArray(long var0) {
      if (var0 == 0L) {
         return EMPTY_BIG_ARRAY;
      } else {
         BigArrays.ensureLength(var0);
         int var2 = (int)(var0 + 134217727L >>> 27);
         char[][] var3 = new char[var2][];
         int var4 = (int)(var0 & 134217727L);
         int var5;
         if (var4 != 0) {
            for(var5 = 0; var5 < var2 - 1; ++var5) {
               var3[var5] = new char[134217728];
            }

            var3[var2 - 1] = new char[var4];
         } else {
            for(var5 = 0; var5 < var2; ++var5) {
               var3[var5] = new char[134217728];
            }
         }

         return var3;
      }
   }

   public static char[][] wrap(char[] var0) {
      if (var0.length == 0) {
         return EMPTY_BIG_ARRAY;
      } else if (var0.length <= 134217728) {
         return new char[][]{var0};
      } else {
         char[][] var1 = newBigArray((long)var0.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            System.arraycopy(var0, (int)BigArrays.start(var2), var1[var2], 0, var1[var2].length);
         }

         return var1;
      }
   }

   public static char[][] ensureCapacity(char[][] var0, long var1) {
      return ensureCapacity(var0, var1, length(var0));
   }

   public static char[][] forceCapacity(char[][] var0, long var1, long var3) {
      BigArrays.ensureLength(var1);
      int var5 = var0.length - (var0.length != 0 && (var0.length <= 0 || var0[var0.length - 1].length != 134217728) ? 1 : 0);
      int var6 = (int)(var1 + 134217727L >>> 27);
      char[][] var7 = (char[][])Arrays.copyOf(var0, var6);
      int var8 = (int)(var1 & 134217727L);
      int var9;
      if (var8 != 0) {
         for(var9 = var5; var9 < var6 - 1; ++var9) {
            var7[var9] = new char[134217728];
         }

         var7[var6 - 1] = new char[var8];
      } else {
         for(var9 = var5; var9 < var6; ++var9) {
            var7[var9] = new char[134217728];
         }
      }

      if (var3 - (long)var5 * 134217728L > 0L) {
         copy(var0, (long)var5 * 134217728L, var7, (long)var5 * 134217728L, var3 - (long)var5 * 134217728L);
      }

      return var7;
   }

   public static char[][] ensureCapacity(char[][] var0, long var1, long var3) {
      return var1 > length(var0) ? forceCapacity(var0, var1, var3) : var0;
   }

   public static char[][] grow(char[][] var0, long var1) {
      long var3 = length(var0);
      return var1 > var3 ? grow(var0, var1, var3) : var0;
   }

   public static char[][] grow(char[][] var0, long var1, long var3) {
      long var5 = length(var0);
      return var1 > var5 ? ensureCapacity(var0, Math.max(var5 + (var5 >> 1), var1), var3) : var0;
   }

   public static char[][] trim(char[][] var0, long var1) {
      BigArrays.ensureLength(var1);
      long var3 = length(var0);
      if (var1 >= var3) {
         return var0;
      } else {
         int var5 = (int)(var1 + 134217727L >>> 27);
         char[][] var6 = (char[][])Arrays.copyOf(var0, var5);
         int var7 = (int)(var1 & 134217727L);
         if (var7 != 0) {
            var6[var5 - 1] = CharArrays.trim(var6[var5 - 1], var7);
         }

         return var6;
      }
   }

   public static char[][] setLength(char[][] var0, long var1) {
      long var3 = length(var0);
      if (var1 == var3) {
         return var0;
      } else {
         return var1 < var3 ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static char[][] copy(char[][] var0, long var1, long var3) {
      ensureOffsetLength(var0, var1, var3);
      char[][] var5 = newBigArray(var3);
      copy(var0, var1, var5, 0L, var3);
      return var5;
   }

   public static char[][] copy(char[][] var0) {
      char[][] var1 = (char[][])var0.clone();

      for(int var2 = var1.length; var2-- != 0; var1[var2] = (char[])var0[var2].clone()) {
      }

      return var1;
   }

   public static void fill(char[][] var0, char var1) {
      int var2 = var0.length;

      while(var2-- != 0) {
         Arrays.fill(var0[var2], var1);
      }

   }

   public static void fill(char[][] var0, long var1, long var3, char var5) {
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

   public static boolean equals(char[][] var0, char[][] var1) {
      if (length(var0) != length(var1)) {
         return false;
      } else {
         int var2 = var0.length;

         while(var2-- != 0) {
            char[] var4 = var0[var2];
            char[] var5 = var1[var2];
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

   public static String toString(char[][] var0) {
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

   public static void ensureFromTo(char[][] var0, long var1, long var3) {
      BigArrays.ensureFromTo(length(var0), var1, var3);
   }

   public static void ensureOffsetLength(char[][] var0, long var1, long var3) {
      BigArrays.ensureOffsetLength(length(var0), var1, var3);
   }

   private static void vecSwap(char[][] var0, long var1, long var3, long var5) {
      for(int var7 = 0; (long)var7 < var5; ++var3) {
         swap(var0, var1, var3);
         ++var7;
         ++var1;
      }

   }

   private static long med3(char[][] var0, long var1, long var3, long var5, CharComparator var7) {
      int var8 = var7.compare(get(var0, var1), get(var0, var3));
      int var9 = var7.compare(get(var0, var1), get(var0, var5));
      int var10 = var7.compare(get(var0, var3), get(var0, var5));
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var5 : var1)) : (var10 > 0 ? var3 : (var9 > 0 ? var5 : var1));
   }

   private static void selectionSort(char[][] var0, long var1, long var3, CharComparator var5) {
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

   public static void quickSort(char[][] var0, long var1, long var3, CharComparator var5) {
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

         char var23 = get(var0, var8);
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

   private static long med3(char[][] var0, long var1, long var3, long var5) {
      int var7 = Character.compare(get(var0, var1), get(var0, var3));
      int var8 = Character.compare(get(var0, var1), get(var0, var5));
      int var9 = Character.compare(get(var0, var3), get(var0, var5));
      return var7 < 0 ? (var9 < 0 ? var3 : (var8 < 0 ? var5 : var1)) : (var9 > 0 ? var3 : (var8 > 0 ? var5 : var1));
   }

   private static void selectionSort(char[][] var0, long var1, long var3) {
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

   public static void quickSort(char[][] var0, CharComparator var1) {
      quickSort(var0, 0L, length(var0), var1);
   }

   public static void quickSort(char[][] var0, long var1, long var3) {
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

         char var22 = get(var0, var7);
         long var10 = var1;
         long var12 = var1;
         long var14 = var3 - 1L;
         long var16 = var14;

         while(true) {
            int var18;
            while(var12 > var14 || (var18 = Character.compare(get(var0, var12), var22)) > 0) {
               for(; var14 >= var12 && (var18 = Character.compare(get(var0, var14), var22)) >= 0; --var14) {
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

   public static void quickSort(char[][] var0) {
      quickSort(var0, 0L, length(var0));
   }

   public static long binarySearch(char[][] var0, long var1, long var3, char var5) {
      --var3;

      while(var1 <= var3) {
         long var7 = var1 + var3 >>> 1;
         char var6 = get(var0, var7);
         if (var6 < var5) {
            var1 = var7 + 1L;
         } else {
            if (var6 <= var5) {
               return var7;
            }

            var3 = var7 - 1L;
         }
      }

      return -(var1 + 1L);
   }

   public static long binarySearch(char[][] var0, char var1) {
      return binarySearch(var0, 0L, length(var0), var1);
   }

   public static long binarySearch(char[][] var0, long var1, long var3, char var5, CharComparator var6) {
      --var3;

      while(var1 <= var3) {
         long var8 = var1 + var3 >>> 1;
         char var7 = get(var0, var8);
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

   public static long binarySearch(char[][] var0, char var1, CharComparator var2) {
      return binarySearch(var0, 0L, length(var0), var1, var2);
   }

   public static void radixSort(char[][] var0) {
      radixSort(var0, 0L, length(var0));
   }

   public static void radixSort(char[][] var0, long var1, long var3) {
      boolean var5 = true;
      boolean var6 = true;
      long[] var7 = new long[256];
      byte var8 = 0;
      long[] var9 = new long[256];
      byte var10 = 0;
      int[] var11 = new int[256];
      byte var12 = 0;
      int var36 = var8 + 1;
      var7[var8] = var1;
      int var37 = var10 + 1;
      var9[var10] = var3 - var1;
      int var38 = var12 + 1;
      var11[var12] = 0;
      long[] var13 = new long[256];
      long[] var14 = new long[256];
      byte[][] var15 = ByteBigArrays.newBigArray(var3 - var1);

      while(true) {
         while(var36 > 0) {
            --var36;
            long var16 = var7[var36];
            --var37;
            long var18 = var9[var37];
            --var38;
            int var20 = var11[var38];
            boolean var21 = false;
            if (var18 < 40L) {
               selectionSort(var0, var16, var16 + var18);
            } else {
               int var22 = (1 - var20 % 2) * 8;
               long var23 = var18;

               while(var23-- != 0L) {
                  ByteBigArrays.set(var15, var23, (byte)(get(var0, var16 + var23) >>> var22 & 255 ^ 0));
               }

               for(var23 = var18; var23-- != 0L; ++var13[ByteBigArrays.get(var15, var23) & 255]) {
               }

               int var40 = -1;
               long var24 = 0L;

               for(int var26 = 0; var26 < 256; ++var26) {
                  if (var13[var26] != 0L) {
                     var40 = var26;
                     if (var20 < 1 && var13[var26] > 1L) {
                        var7[var36++] = var24 + var16;
                        var9[var37++] = var13[var26];
                        var11[var38++] = var20 + 1;
                     }
                  }

                  var14[var26] = var24 += var13[var26];
               }

               long var41 = var18 - var13[var40];
               var13[var40] = 0L;
               boolean var28 = true;

               int var42;
               for(long var29 = 0L; var29 < var41; var13[var42] = 0L) {
                  char var33 = get(var0, var29 + var16);
                  var42 = ByteBigArrays.get(var15, var29) & 255;

                  long var31;
                  while((var31 = --var14[var42]) > var29) {
                     char var34 = var33;
                     int var35 = var42;
                     var33 = get(var0, var31 + var16);
                     var42 = ByteBigArrays.get(var15, var31) & 255;
                     set(var0, var31 + var16, var34);
                     ByteBigArrays.set(var15, var31, (byte)var35);
                  }

                  set(var0, var29 + var16, var33);
                  var29 += var13[var42];
               }
            }
         }

         return;
      }
   }

   private static void selectionSort(char[][] var0, char[][] var1, long var2, long var4) {
      for(long var6 = var2; var6 < var4 - 1L; ++var6) {
         long var8 = var6;

         for(long var10 = var6 + 1L; var10 < var4; ++var10) {
            if (get(var0, var10) < get(var0, var8) || get(var0, var10) == get(var0, var8) && get(var1, var10) < get(var1, var8)) {
               var8 = var10;
            }
         }

         if (var8 != var6) {
            char var12 = get(var0, var6);
            set(var0, var6, get(var0, var8));
            set(var0, var8, var12);
            var12 = get(var1, var6);
            set(var1, var6, get(var1, var8));
            set(var1, var8, var12);
         }
      }

   }

   public static void radixSort(char[][] var0, char[][] var1) {
      radixSort(var0, var1, 0L, length(var0));
   }

   public static void radixSort(char[][] var0, char[][] var1, long var2, long var4) {
      boolean var6 = true;
      if (length(var0) != length(var1)) {
         throw new IllegalArgumentException("Array size mismatch.");
      } else {
         boolean var7 = true;
         boolean var8 = true;
         long[] var9 = new long[766];
         byte var10 = 0;
         long[] var11 = new long[766];
         byte var12 = 0;
         int[] var13 = new int[766];
         byte var14 = 0;
         int var40 = var10 + 1;
         var9[var10] = var2;
         int var41 = var12 + 1;
         var11[var12] = var4 - var2;
         int var42 = var14 + 1;
         var13[var14] = 0;
         long[] var15 = new long[256];
         long[] var16 = new long[256];
         byte[][] var17 = ByteBigArrays.newBigArray(var4 - var2);

         while(true) {
            while(var40 > 0) {
               --var40;
               long var18 = var9[var40];
               --var41;
               long var20 = var11[var41];
               --var42;
               int var22 = var13[var42];
               boolean var23 = false;
               if (var20 < 40L) {
                  selectionSort(var0, var1, var18, var18 + var20);
               } else {
                  char[][] var24 = var22 < 2 ? var0 : var1;
                  int var25 = (1 - var22 % 2) * 8;
                  long var26 = var20;

                  while(var26-- != 0L) {
                     ByteBigArrays.set(var17, var26, (byte)(get(var24, var18 + var26) >>> var25 & 255 ^ 0));
                  }

                  for(var26 = var20; var26-- != 0L; ++var15[ByteBigArrays.get(var17, var26) & 255]) {
                  }

                  int var44 = -1;
                  long var27 = 0L;

                  for(int var29 = 0; var29 < 256; ++var29) {
                     if (var15[var29] != 0L) {
                        var44 = var29;
                        if (var22 < 3 && var15[var29] > 1L) {
                           var9[var40++] = var27 + var18;
                           var11[var41++] = var15[var29];
                           var13[var42++] = var22 + 1;
                        }
                     }

                     var16[var29] = var27 += var15[var29];
                  }

                  long var45 = var20 - var15[var44];
                  var15[var44] = 0L;
                  boolean var31 = true;

                  int var46;
                  for(long var32 = 0L; var32 < var45; var15[var46] = 0L) {
                     char var36 = get(var0, var32 + var18);
                     char var37 = get(var1, var32 + var18);
                     var46 = ByteBigArrays.get(var17, var32) & 255;

                     long var34;
                     while((var34 = --var16[var46]) > var32) {
                        char var38 = var36;
                        int var39 = var46;
                        var36 = get(var0, var34 + var18);
                        set(var0, var34 + var18, var38);
                        var38 = var37;
                        var37 = get(var1, var34 + var18);
                        set(var1, var34 + var18, var38);
                        var46 = ByteBigArrays.get(var17, var34) & 255;
                        ByteBigArrays.set(var17, var34, (byte)var39);
                     }

                     set(var0, var32 + var18, var36);
                     set(var1, var32 + var18, var37);
                     var32 += var15[var46];
                  }
               }
            }

            return;
         }
      }
   }

   public static char[][] shuffle(char[][] var0, long var1, long var3, Random var5) {
      long var6 = var3 - var1;

      while(var6-- != 0L) {
         long var8 = (var5.nextLong() & 9223372036854775807L) % (var6 + 1L);
         char var10 = get(var0, var1 + var6);
         set(var0, var1 + var6, get(var0, var1 + var8));
         set(var0, var1 + var8, var10);
      }

      return var0;
   }

   public static char[][] shuffle(char[][] var0, Random var1) {
      long var2 = length(var0);

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         char var6 = get(var0, var2);
         set(var0, var2, get(var0, var4));
         set(var0, var4, var6);
      }

      return var0;
   }

   private static final class BigArrayHashStrategy implements Hash.Strategy<char[][]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private BigArrayHashStrategy() {
         super();
      }

      public int hashCode(char[][] var1) {
         return Arrays.deepHashCode(var1);
      }

      public boolean equals(char[][] var1, char[][] var2) {
         return CharBigArrays.equals(var1, var2);
      }

      // $FF: synthetic method
      BigArrayHashStrategy(Object var1) {
         this();
      }
   }
}
