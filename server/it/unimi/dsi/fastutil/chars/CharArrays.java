package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class CharArrays {
   public static final char[] EMPTY_ARRAY = new char[0];
   public static final char[] DEFAULT_EMPTY_ARRAY = new char[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   private static final int DIGIT_BITS = 8;
   private static final int DIGIT_MASK = 255;
   private static final int DIGITS_PER_ELEMENT = 2;
   private static final int RADIXSORT_NO_REC = 1024;
   private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
   protected static final CharArrays.Segment POISON_PILL = new CharArrays.Segment(-1, -1, -1);
   public static final Hash.Strategy<char[]> HASH_STRATEGY = new CharArrays.ArrayHashStrategy();

   private CharArrays() {
      super();
   }

   public static char[] forceCapacity(char[] var0, int var1, int var2) {
      char[] var3 = new char[var1];
      System.arraycopy(var0, 0, var3, 0, var2);
      return var3;
   }

   public static char[] ensureCapacity(char[] var0, int var1) {
      return ensureCapacity(var0, var1, var0.length);
   }

   public static char[] ensureCapacity(char[] var0, int var1, int var2) {
      return var1 > var0.length ? forceCapacity(var0, var1, var2) : var0;
   }

   public static char[] grow(char[] var0, int var1) {
      return grow(var0, var1, var0.length);
   }

   public static char[] grow(char[] var0, int var1, int var2) {
      if (var1 > var0.length) {
         int var3 = (int)Math.max(Math.min((long)var0.length + (long)(var0.length >> 1), 2147483639L), (long)var1);
         char[] var4 = new char[var3];
         System.arraycopy(var0, 0, var4, 0, var2);
         return var4;
      } else {
         return var0;
      }
   }

   public static char[] trim(char[] var0, int var1) {
      if (var1 >= var0.length) {
         return var0;
      } else {
         char[] var2 = var1 == 0 ? EMPTY_ARRAY : new char[var1];
         System.arraycopy(var0, 0, var2, 0, var1);
         return var2;
      }
   }

   public static char[] setLength(char[] var0, int var1) {
      if (var1 == var0.length) {
         return var0;
      } else {
         return var1 < var0.length ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static char[] copy(char[] var0, int var1, int var2) {
      ensureOffsetLength(var0, var1, var2);
      char[] var3 = var2 == 0 ? EMPTY_ARRAY : new char[var2];
      System.arraycopy(var0, var1, var3, 0, var2);
      return var3;
   }

   public static char[] copy(char[] var0) {
      return (char[])var0.clone();
   }

   /** @deprecated */
   @Deprecated
   public static void fill(char[] var0, char var1) {
      for(int var2 = var0.length; var2-- != 0; var0[var2] = var1) {
      }

   }

   /** @deprecated */
   @Deprecated
   public static void fill(char[] var0, int var1, int var2, char var3) {
      ensureFromTo(var0, var1, var2);
      if (var1 == 0) {
         while(var2-- != 0) {
            var0[var2] = var3;
         }
      } else {
         for(int var4 = var1; var4 < var2; ++var4) {
            var0[var4] = var3;
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static boolean equals(char[] var0, char[] var1) {
      int var2 = var0.length;
      if (var2 != var1.length) {
         return false;
      } else {
         do {
            if (var2-- == 0) {
               return true;
            }
         } while(var0[var2] == var1[var2]);

         return false;
      }
   }

   public static void ensureFromTo(char[] var0, int var1, int var2) {
      Arrays.ensureFromTo(var0.length, var1, var2);
   }

   public static void ensureOffsetLength(char[] var0, int var1, int var2) {
      Arrays.ensureOffsetLength(var0.length, var1, var2);
   }

   public static void ensureSameLength(char[] var0, char[] var1) {
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("Array size mismatch: " + var0.length + " != " + var1.length);
      }
   }

   public static void swap(char[] var0, int var1, int var2) {
      char var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static void swap(char[] var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var2) {
         swap(var0, var1, var2);
         ++var4;
         ++var1;
      }

   }

   private static int med3(char[] var0, int var1, int var2, int var3, CharComparator var4) {
      int var5 = var4.compare(var0[var1], var0[var2]);
      int var6 = var4.compare(var0[var1], var0[var3]);
      int var7 = var4.compare(var0[var2], var0[var3]);
      return var5 < 0 ? (var7 < 0 ? var2 : (var6 < 0 ? var3 : var1)) : (var7 > 0 ? var2 : (var6 > 0 ? var3 : var1));
   }

   private static void selectionSort(char[] var0, int var1, int var2, CharComparator var3) {
      for(int var4 = var1; var4 < var2 - 1; ++var4) {
         int var5 = var4;

         for(int var6 = var4 + 1; var6 < var2; ++var6) {
            if (var3.compare(var0[var6], var0[var5]) < 0) {
               var5 = var6;
            }
         }

         if (var5 != var4) {
            char var7 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var7;
         }
      }

   }

   private static void insertionSort(char[] var0, int var1, int var2, CharComparator var3) {
      int var4 = var1;

      while(true) {
         ++var4;
         if (var4 >= var2) {
            return;
         }

         char var5 = var0[var4];
         int var6 = var4;

         for(char var7 = var0[var4 - 1]; var3.compare(var5, var7) < 0; var7 = var0[var6 - 1]) {
            var0[var6] = var7;
            if (var1 == var6 - 1) {
               --var6;
               break;
            }

            --var6;
         }

         var0[var6] = var5;
      }
   }

   public static void quickSort(char[] var0, int var1, int var2, CharComparator var3) {
      int var4 = var2 - var1;
      if (var4 < 16) {
         selectionSort(var0, var1, var2, var3);
      } else {
         int var5 = var1 + var4 / 2;
         int var6 = var1;
         int var7 = var2 - 1;
         if (var4 > 128) {
            int var8 = var4 / 8;
            var6 = med3(var0, var1, var1 + var8, var1 + 2 * var8, var3);
            var5 = med3(var0, var5 - var8, var5, var5 + var8, var3);
            var7 = med3(var0, var7 - 2 * var8, var7 - var8, var7, var3);
         }

         var5 = med3(var0, var6, var5, var7, var3);
         char var14 = var0[var5];
         int var9 = var1;
         int var10 = var1;
         int var11 = var2 - 1;
         int var12 = var11;

         while(true) {
            int var13;
            while(var10 > var11 || (var13 = var3.compare(var0[var10], var14)) > 0) {
               for(; var11 >= var10 && (var13 = var3.compare(var0[var11], var14)) >= 0; --var11) {
                  if (var13 == 0) {
                     swap(var0, var11, var12--);
                  }
               }

               if (var10 > var11) {
                  var13 = Math.min(var9 - var1, var10 - var9);
                  swap(var0, var1, var10 - var13, var13);
                  var13 = Math.min(var12 - var11, var2 - var12 - 1);
                  swap(var0, var10, var2 - var13, var13);
                  if ((var13 = var10 - var9) > 1) {
                     quickSort(var0, var1, var1 + var13, var3);
                  }

                  if ((var13 = var12 - var11) > 1) {
                     quickSort(var0, var2 - var13, var2, var3);
                  }

                  return;
               }

               swap(var0, var10++, var11--);
            }

            if (var13 == 0) {
               swap(var0, var9++, var10);
            }

            ++var10;
         }
      }
   }

   public static void quickSort(char[] var0, CharComparator var1) {
      quickSort(var0, 0, var0.length, var1);
   }

   public static void parallelQuickSort(char[] var0, int var1, int var2, CharComparator var3) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new CharArrays.ForkJoinQuickSortComp(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static void parallelQuickSort(char[] var0, CharComparator var1) {
      parallelQuickSort(var0, 0, var0.length, var1);
   }

   private static int med3(char[] var0, int var1, int var2, int var3) {
      int var4 = Character.compare(var0[var1], var0[var2]);
      int var5 = Character.compare(var0[var1], var0[var3]);
      int var6 = Character.compare(var0[var2], var0[var3]);
      return var4 < 0 ? (var6 < 0 ? var2 : (var5 < 0 ? var3 : var1)) : (var6 > 0 ? var2 : (var5 > 0 ? var3 : var1));
   }

   private static void selectionSort(char[] var0, int var1, int var2) {
      for(int var3 = var1; var3 < var2 - 1; ++var3) {
         int var4 = var3;

         for(int var5 = var3 + 1; var5 < var2; ++var5) {
            if (var0[var5] < var0[var4]) {
               var4 = var5;
            }
         }

         if (var4 != var3) {
            char var6 = var0[var3];
            var0[var3] = var0[var4];
            var0[var4] = var6;
         }
      }

   }

   private static void insertionSort(char[] var0, int var1, int var2) {
      int var3 = var1;

      while(true) {
         ++var3;
         if (var3 >= var2) {
            return;
         }

         char var4 = var0[var3];
         int var5 = var3;

         for(char var6 = var0[var3 - 1]; var4 < var6; var6 = var0[var5 - 1]) {
            var0[var5] = var6;
            if (var1 == var5 - 1) {
               --var5;
               break;
            }

            --var5;
         }

         var0[var5] = var4;
      }
   }

   public static void quickSort(char[] var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 < 16) {
         selectionSort(var0, var1, var2);
      } else {
         int var4 = var1 + var3 / 2;
         int var5 = var1;
         int var6 = var2 - 1;
         if (var3 > 128) {
            int var7 = var3 / 8;
            var5 = med3(var0, var1, var1 + var7, var1 + 2 * var7);
            var4 = med3(var0, var4 - var7, var4, var4 + var7);
            var6 = med3(var0, var6 - 2 * var7, var6 - var7, var6);
         }

         var4 = med3(var0, var5, var4, var6);
         char var13 = var0[var4];
         int var8 = var1;
         int var9 = var1;
         int var10 = var2 - 1;
         int var11 = var10;

         while(true) {
            int var12;
            while(var9 > var10 || (var12 = Character.compare(var0[var9], var13)) > 0) {
               for(; var10 >= var9 && (var12 = Character.compare(var0[var10], var13)) >= 0; --var10) {
                  if (var12 == 0) {
                     swap(var0, var10, var11--);
                  }
               }

               if (var9 > var10) {
                  var12 = Math.min(var8 - var1, var9 - var8);
                  swap(var0, var1, var9 - var12, var12);
                  var12 = Math.min(var11 - var10, var2 - var11 - 1);
                  swap(var0, var9, var2 - var12, var12);
                  if ((var12 = var9 - var8) > 1) {
                     quickSort(var0, var1, var1 + var12);
                  }

                  if ((var12 = var11 - var10) > 1) {
                     quickSort(var0, var2 - var12, var2);
                  }

                  return;
               }

               swap(var0, var9++, var10--);
            }

            if (var12 == 0) {
               swap(var0, var8++, var9);
            }

            ++var9;
         }
      }
   }

   public static void quickSort(char[] var0) {
      quickSort(var0, 0, var0.length);
   }

   public static void parallelQuickSort(char[] var0, int var1, int var2) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2);
      } else {
         ForkJoinPool var3 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var3.invoke(new CharArrays.ForkJoinQuickSort(var0, var1, var2));
         var3.shutdown();
      }

   }

   public static void parallelQuickSort(char[] var0) {
      parallelQuickSort(var0, 0, var0.length);
   }

   private static int med3Indirect(int[] var0, char[] var1, int var2, int var3, int var4) {
      char var5 = var1[var0[var2]];
      char var6 = var1[var0[var3]];
      char var7 = var1[var0[var4]];
      int var8 = Character.compare(var5, var6);
      int var9 = Character.compare(var5, var7);
      int var10 = Character.compare(var6, var7);
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var4 : var2)) : (var10 > 0 ? var3 : (var9 > 0 ? var4 : var2));
   }

   private static void insertionSortIndirect(int[] var0, char[] var1, int var2, int var3) {
      int var4 = var2;

      while(true) {
         ++var4;
         if (var4 >= var3) {
            return;
         }

         int var5 = var0[var4];
         int var6 = var4;

         for(int var7 = var0[var4 - 1]; var1[var5] < var1[var7]; var7 = var0[var6 - 1]) {
            var0[var6] = var7;
            if (var2 == var6 - 1) {
               --var6;
               break;
            }

            --var6;
         }

         var0[var6] = var5;
      }
   }

   public static void quickSortIndirect(int[] var0, char[] var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 < 16) {
         insertionSortIndirect(var0, var1, var2, var3);
      } else {
         int var5 = var2 + var4 / 2;
         int var6 = var2;
         int var7 = var3 - 1;
         if (var4 > 128) {
            int var8 = var4 / 8;
            var6 = med3Indirect(var0, var1, var2, var2 + var8, var2 + 2 * var8);
            var5 = med3Indirect(var0, var1, var5 - var8, var5, var5 + var8);
            var7 = med3Indirect(var0, var1, var7 - 2 * var8, var7 - var8, var7);
         }

         var5 = med3Indirect(var0, var1, var6, var5, var7);
         char var14 = var1[var0[var5]];
         int var9 = var2;
         int var10 = var2;
         int var11 = var3 - 1;
         int var12 = var11;

         while(true) {
            int var13;
            while(var10 > var11 || (var13 = Character.compare(var1[var0[var10]], var14)) > 0) {
               for(; var11 >= var10 && (var13 = Character.compare(var1[var0[var11]], var14)) >= 0; --var11) {
                  if (var13 == 0) {
                     IntArrays.swap(var0, var11, var12--);
                  }
               }

               if (var10 > var11) {
                  var13 = Math.min(var9 - var2, var10 - var9);
                  IntArrays.swap(var0, var2, var10 - var13, var13);
                  var13 = Math.min(var12 - var11, var3 - var12 - 1);
                  IntArrays.swap(var0, var10, var3 - var13, var13);
                  if ((var13 = var10 - var9) > 1) {
                     quickSortIndirect(var0, var1, var2, var2 + var13);
                  }

                  if ((var13 = var12 - var11) > 1) {
                     quickSortIndirect(var0, var1, var3 - var13, var3);
                  }

                  return;
               }

               IntArrays.swap(var0, var10++, var11--);
            }

            if (var13 == 0) {
               IntArrays.swap(var0, var9++, var10);
            }

            ++var10;
         }
      }
   }

   public static void quickSortIndirect(int[] var0, char[] var1) {
      quickSortIndirect(var0, var1, 0, var1.length);
   }

   public static void parallelQuickSortIndirect(int[] var0, char[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSortIndirect(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new CharArrays.ForkJoinQuickSortIndirect(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static void parallelQuickSortIndirect(int[] var0, char[] var1) {
      parallelQuickSortIndirect(var0, var1, 0, var1.length);
   }

   public static void stabilize(int[] var0, char[] var1, int var2, int var3) {
      int var4 = var2;

      for(int var5 = var2 + 1; var5 < var3; ++var5) {
         if (var1[var0[var5]] != var1[var0[var4]]) {
            if (var5 - var4 > 1) {
               IntArrays.parallelQuickSort(var0, var4, var5);
            }

            var4 = var5;
         }
      }

      if (var3 - var4 > 1) {
         IntArrays.parallelQuickSort(var0, var4, var3);
      }

   }

   public static void stabilize(int[] var0, char[] var1) {
      stabilize(var0, var1, 0, var0.length);
   }

   private static int med3(char[] var0, char[] var1, int var2, int var3, int var4) {
      int var5;
      int var6 = (var5 = Character.compare(var0[var2], var0[var3])) == 0 ? Character.compare(var1[var2], var1[var3]) : var5;
      int var7 = (var5 = Character.compare(var0[var2], var0[var4])) == 0 ? Character.compare(var1[var2], var1[var4]) : var5;
      int var8 = (var5 = Character.compare(var0[var3], var0[var4])) == 0 ? Character.compare(var1[var3], var1[var4]) : var5;
      return var6 < 0 ? (var8 < 0 ? var3 : (var7 < 0 ? var4 : var2)) : (var8 > 0 ? var3 : (var7 > 0 ? var4 : var2));
   }

   private static void swap(char[] var0, char[] var1, int var2, int var3) {
      char var4 = var0[var2];
      char var5 = var1[var2];
      var0[var2] = var0[var3];
      var1[var2] = var1[var3];
      var0[var3] = var4;
      var1[var3] = var5;
   }

   private static void swap(char[] var0, char[] var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < var4; ++var3) {
         swap(var0, var1, var2, var3);
         ++var5;
         ++var2;
      }

   }

   private static void selectionSort(char[] var0, char[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 - 1; ++var4) {
         int var5 = var4;

         for(int var7 = var4 + 1; var7 < var3; ++var7) {
            int var6;
            if ((var6 = Character.compare(var0[var7], var0[var5])) < 0 || var6 == 0 && var1[var7] < var1[var5]) {
               var5 = var7;
            }
         }

         if (var5 != var4) {
            char var8 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var8;
            var8 = var1[var4];
            var1[var4] = var1[var5];
            var1[var5] = var8;
         }
      }

   }

   public static void quickSort(char[] var0, char[] var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 < 16) {
         selectionSort(var0, var1, var2, var3);
      } else {
         int var5 = var2 + var4 / 2;
         int var6 = var2;
         int var7 = var3 - 1;
         if (var4 > 128) {
            int var8 = var4 / 8;
            var6 = med3(var0, var1, var2, var2 + var8, var2 + 2 * var8);
            var5 = med3(var0, var1, var5 - var8, var5, var5 + var8);
            var7 = med3(var0, var1, var7 - 2 * var8, var7 - var8, var7);
         }

         var5 = med3(var0, var1, var6, var5, var7);
         char var16 = var0[var5];
         char var9 = var1[var5];
         int var10 = var2;
         int var11 = var2;
         int var12 = var3 - 1;
         int var13 = var12;

         while(true) {
            int var14;
            int var15;
            while(var11 > var12 || (var14 = (var15 = Character.compare(var0[var11], var16)) == 0 ? Character.compare(var1[var11], var9) : var15) > 0) {
               for(; var12 >= var11 && (var14 = (var15 = Character.compare(var0[var12], var16)) == 0 ? Character.compare(var1[var12], var9) : var15) >= 0; --var12) {
                  if (var14 == 0) {
                     swap(var0, var1, var12, var13--);
                  }
               }

               if (var11 > var12) {
                  var14 = Math.min(var10 - var2, var11 - var10);
                  swap(var0, var1, var2, var11 - var14, var14);
                  var14 = Math.min(var13 - var12, var3 - var13 - 1);
                  swap(var0, var1, var11, var3 - var14, var14);
                  if ((var14 = var11 - var10) > 1) {
                     quickSort(var0, var1, var2, var2 + var14);
                  }

                  if ((var14 = var13 - var12) > 1) {
                     quickSort(var0, var1, var3 - var14, var3);
                  }

                  return;
               }

               swap(var0, var1, var11++, var12--);
            }

            if (var14 == 0) {
               swap(var0, var1, var10++, var11);
            }

            ++var11;
         }
      }
   }

   public static void quickSort(char[] var0, char[] var1) {
      ensureSameLength(var0, var1);
      quickSort(var0, var1, 0, var0.length);
   }

   public static void parallelQuickSort(char[] var0, char[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSort(var0, var1, var2, var3);
      }

      ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      var4.invoke(new CharArrays.ForkJoinQuickSort2(var0, var1, var2, var3));
      var4.shutdown();
   }

   public static void parallelQuickSort(char[] var0, char[] var1) {
      ensureSameLength(var0, var1);
      parallelQuickSort(var0, var1, 0, var0.length);
   }

   public static void mergeSort(char[] var0, int var1, int var2, char[] var3) {
      int var4 = var2 - var1;
      if (var4 < 16) {
         insertionSort(var0, var1, var2);
      } else {
         int var5 = var1 + var2 >>> 1;
         mergeSort(var3, var1, var5, var0);
         mergeSort(var3, var5, var2, var0);
         if (var3[var5 - 1] <= var3[var5]) {
            System.arraycopy(var3, var1, var0, var1, var4);
         } else {
            int var6 = var1;
            int var7 = var1;

            for(int var8 = var5; var6 < var2; ++var6) {
               if (var8 < var2 && (var7 >= var5 || var3[var7] > var3[var8])) {
                  var0[var6] = var3[var8++];
               } else {
                  var0[var6] = var3[var7++];
               }
            }

         }
      }
   }

   public static void mergeSort(char[] var0, int var1, int var2) {
      mergeSort(var0, var1, var2, (char[])var0.clone());
   }

   public static void mergeSort(char[] var0) {
      mergeSort(var0, 0, var0.length);
   }

   public static void mergeSort(char[] var0, int var1, int var2, CharComparator var3, char[] var4) {
      int var5 = var2 - var1;
      if (var5 < 16) {
         insertionSort(var0, var1, var2, var3);
      } else {
         int var6 = var1 + var2 >>> 1;
         mergeSort(var4, var1, var6, var3, var0);
         mergeSort(var4, var6, var2, var3, var0);
         if (var3.compare(var4[var6 - 1], var4[var6]) <= 0) {
            System.arraycopy(var4, var1, var0, var1, var5);
         } else {
            int var7 = var1;
            int var8 = var1;

            for(int var9 = var6; var7 < var2; ++var7) {
               if (var9 < var2 && (var8 >= var6 || var3.compare(var4[var8], var4[var9]) > 0)) {
                  var0[var7] = var4[var9++];
               } else {
                  var0[var7] = var4[var8++];
               }
            }

         }
      }
   }

   public static void mergeSort(char[] var0, int var1, int var2, CharComparator var3) {
      mergeSort(var0, var1, var2, var3, (char[])var0.clone());
   }

   public static void mergeSort(char[] var0, CharComparator var1) {
      mergeSort(var0, 0, var0.length, (CharComparator)var1);
   }

   public static int binarySearch(char[] var0, int var1, int var2, char var3) {
      --var2;

      while(var1 <= var2) {
         int var5 = var1 + var2 >>> 1;
         char var4 = var0[var5];
         if (var4 < var3) {
            var1 = var5 + 1;
         } else {
            if (var4 <= var3) {
               return var5;
            }

            var2 = var5 - 1;
         }
      }

      return -(var1 + 1);
   }

   public static int binarySearch(char[] var0, char var1) {
      return binarySearch(var0, 0, var0.length, var1);
   }

   public static int binarySearch(char[] var0, int var1, int var2, char var3, CharComparator var4) {
      --var2;

      while(var1 <= var2) {
         int var6 = var1 + var2 >>> 1;
         char var5 = var0[var6];
         int var7 = var4.compare(var5, var3);
         if (var7 < 0) {
            var1 = var6 + 1;
         } else {
            if (var7 <= 0) {
               return var6;
            }

            var2 = var6 - 1;
         }
      }

      return -(var1 + 1);
   }

   public static int binarySearch(char[] var0, char var1, CharComparator var2) {
      return binarySearch(var0, 0, var0.length, var1, var2);
   }

   public static void radixSort(char[] var0) {
      radixSort((char[])var0, 0, var0.length);
   }

   public static void radixSort(char[] var0, int var1, int var2) {
      if (var2 - var1 < 1024) {
         quickSort(var0, var1, var2);
      } else {
         boolean var3 = true;
         boolean var4 = true;
         byte var5 = 0;
         int[] var6 = new int[256];
         int[] var7 = new int[256];
         int[] var8 = new int[256];
         var6[var5] = var1;
         var7[var5] = var2 - var1;
         int var23 = var5 + 1;
         var8[var5] = 0;
         int[] var9 = new int[256];
         int[] var10 = new int[256];

         while(var23 > 0) {
            --var23;
            int var11 = var6[var23];
            int var12 = var7[var23];
            int var13 = var8[var23];
            boolean var14 = false;
            int var15 = (1 - var13 % 2) * 8;

            int var16;
            for(var16 = var11 + var12; var16-- != var11; ++var9[var0[var16] >>> var15 & 255 ^ 0]) {
            }

            var16 = -1;
            int var17 = 0;

            int var18;
            for(var18 = var11; var17 < 256; ++var17) {
               if (var9[var17] != 0) {
                  var16 = var17;
               }

               var10[var17] = var18 += var9[var17];
            }

            var17 = var11 + var12 - var9[var16];
            var18 = var11;

            int var25;
            for(boolean var19 = true; var18 <= var17; var9[var25] = 0) {
               char var21 = var0[var18];
               var25 = var21 >>> var15 & 255 ^ 0;
               if (var18 < var17) {
                  int var20;
                  while((var20 = --var10[var25]) > var18) {
                     char var22 = var21;
                     var21 = var0[var20];
                     var0[var20] = var22;
                     var25 = var21 >>> var15 & 255 ^ 0;
                  }

                  var0[var18] = var21;
               }

               if (var13 < 1 && var9[var25] > 1) {
                  if (var9[var25] < 1024) {
                     quickSort(var0, var18, var18 + var9[var25]);
                  } else {
                     var6[var23] = var18;
                     var7[var23] = var9[var25];
                     var8[var23++] = var13 + 1;
                  }
               }

               var18 += var9[var25];
            }
         }

      }
   }

   public static void parallelRadixSort(char[] var0, int var1, int var2) {
      if (var2 - var1 < 1024) {
         quickSort(var0, var1, var2);
      } else {
         boolean var3 = true;
         LinkedBlockingQueue var4 = new LinkedBlockingQueue();
         var4.add(new CharArrays.Segment(var1, var2 - var1, 0));
         AtomicInteger var5 = new AtomicInteger(1);
         int var6 = Runtime.getRuntime().availableProcessors();
         ExecutorService var7 = Executors.newFixedThreadPool(var6, Executors.defaultThreadFactory());
         ExecutorCompletionService var8 = new ExecutorCompletionService(var7);
         int var9 = var6;

         while(var9-- != 0) {
            var8.submit(() -> {
               int[] var4x = new int[256];
               int[] var5x = new int[256];

               while(true) {
                  if (var5.get() == 0) {
                     int var6x = var6;

                     while(var6x-- != 0) {
                        var4.add(POISON_PILL);
                     }
                  }

                  CharArrays.Segment var19 = (CharArrays.Segment)var4.take();
                  if (var19 == POISON_PILL) {
                     return null;
                  }

                  int var7 = var19.offset;
                  int var8 = var19.length;
                  int var9 = var19.level;
                  boolean var10 = false;
                  int var11 = (1 - var9 % 2) * 8;

                  int var12;
                  for(var12 = var7 + var8; var12-- != var7; ++var4x[var0[var12] >>> var11 & 255 ^ 0]) {
                  }

                  var12 = -1;
                  int var13 = 0;

                  int var14;
                  for(var14 = var7; var13 < 256; ++var13) {
                     if (var4x[var13] != 0) {
                        var12 = var13;
                     }

                     var5x[var13] = var14 += var4x[var13];
                  }

                  var13 = var7 + var8 - var4x[var12];
                  var14 = var7;

                  int var20;
                  for(boolean var15 = true; var14 <= var13; var4x[var20] = 0) {
                     char var17 = var0[var14];
                     var20 = var17 >>> var11 & 255 ^ 0;
                     if (var14 < var13) {
                        int var16;
                        while((var16 = --var5x[var20]) > var14) {
                           char var18 = var17;
                           var17 = var0[var16];
                           var0[var16] = var18;
                           var20 = var17 >>> var11 & 255 ^ 0;
                        }

                        var0[var14] = var17;
                     }

                     if (var9 < 1 && var4x[var20] > 1) {
                        if (var4x[var20] < 1024) {
                           quickSort(var0, var14, var14 + var4x[var20]);
                        } else {
                           var5.incrementAndGet();
                           var4.add(new CharArrays.Segment(var14, var4x[var20], var9 + 1));
                        }
                     }

                     var14 += var4x[var20];
                  }

                  var5.decrementAndGet();
               }
            });
         }

         Throwable var13 = null;
         int var10 = var6;

         while(var10-- != 0) {
            try {
               var8.take().get();
            } catch (Exception var12) {
               var13 = var12.getCause();
            }
         }

         var7.shutdown();
         if (var13 != null) {
            throw var13 instanceof RuntimeException ? (RuntimeException)var13 : new RuntimeException(var13);
         }
      }
   }

   public static void parallelRadixSort(char[] var0) {
      parallelRadixSort(var0, 0, var0.length);
   }

   public static void radixSortIndirect(int[] var0, char[] var1, boolean var2) {
      radixSortIndirect(var0, var1, 0, var0.length, var2);
   }

   public static void radixSortIndirect(int[] var0, char[] var1, int var2, int var3, boolean var4) {
      if (var3 - var2 < 1024) {
         insertionSortIndirect(var0, var1, var2, var3);
      } else {
         boolean var5 = true;
         boolean var6 = true;
         byte var7 = 0;
         int[] var8 = new int[256];
         int[] var9 = new int[256];
         int[] var10 = new int[256];
         var8[var7] = var2;
         var9[var7] = var3 - var2;
         int var26 = var7 + 1;
         var10[var7] = 0;
         int[] var11 = new int[256];
         int[] var12 = new int[256];
         int[] var13 = var4 ? new int[var0.length] : null;

         while(true) {
            while(var26 > 0) {
               --var26;
               int var14 = var8[var26];
               int var15 = var9[var26];
               int var16 = var10[var26];
               boolean var17 = false;
               int var18 = (1 - var16 % 2) * 8;

               int var19;
               for(var19 = var14 + var15; var19-- != var14; ++var11[var1[var0[var19]] >>> var18 & 255 ^ 0]) {
               }

               var19 = -1;
               int var20 = 0;

               int var21;
               for(var21 = var4 ? 0 : var14; var20 < 256; ++var20) {
                  if (var11[var20] != 0) {
                     var19 = var20;
                  }

                  var12[var20] = var21 += var11[var20];
               }

               if (var4) {
                  for(var20 = var14 + var15; var20-- != var14; var13[--var12[var1[var0[var20]] >>> var18 & 255 ^ 0]] = var0[var20]) {
                  }

                  System.arraycopy(var13, 0, var0, var14, var15);
                  var20 = 0;

                  for(var21 = var14; var20 <= var19; ++var20) {
                     if (var16 < 1 && var11[var20] > 1) {
                        if (var11[var20] < 1024) {
                           insertionSortIndirect(var0, var1, var21, var21 + var11[var20]);
                        } else {
                           var8[var26] = var21;
                           var9[var26] = var11[var20];
                           var10[var26++] = var16 + 1;
                        }
                     }

                     var21 += var11[var20];
                  }

                  java.util.Arrays.fill(var11, 0);
               } else {
                  var20 = var14 + var15 - var11[var19];
                  var21 = var14;

                  int var28;
                  for(boolean var22 = true; var21 <= var20; var11[var28] = 0) {
                     int var24 = var0[var21];
                     var28 = var1[var24] >>> var18 & 255 ^ 0;
                     if (var21 < var20) {
                        int var23;
                        while((var23 = --var12[var28]) > var21) {
                           int var25 = var24;
                           var24 = var0[var23];
                           var0[var23] = var25;
                           var28 = var1[var24] >>> var18 & 255 ^ 0;
                        }

                        var0[var21] = var24;
                     }

                     if (var16 < 1 && var11[var28] > 1) {
                        if (var11[var28] < 1024) {
                           insertionSortIndirect(var0, var1, var21, var21 + var11[var28]);
                        } else {
                           var8[var26] = var21;
                           var9[var26] = var11[var28];
                           var10[var26++] = var16 + 1;
                        }
                     }

                     var21 += var11[var28];
                  }
               }
            }

            return;
         }
      }
   }

   public static void parallelRadixSortIndirect(int[] var0, char[] var1, int var2, int var3, boolean var4) {
      if (var3 - var2 < 1024) {
         radixSortIndirect(var0, var1, var2, var3, var4);
      } else {
         boolean var5 = true;
         LinkedBlockingQueue var6 = new LinkedBlockingQueue();
         var6.add(new CharArrays.Segment(var2, var3 - var2, 0));
         AtomicInteger var7 = new AtomicInteger(1);
         int var8 = Runtime.getRuntime().availableProcessors();
         ExecutorService var9 = Executors.newFixedThreadPool(var8, Executors.defaultThreadFactory());
         ExecutorCompletionService var10 = new ExecutorCompletionService(var9);
         int[] var11 = var4 ? new int[var0.length] : null;
         int var12 = var8;

         while(var12-- != 0) {
            var10.submit(() -> {
               int[] var7x = new int[256];
               int[] var8x = new int[256];

               while(true) {
                  if (var7.get() == 0) {
                     int var9 = var8;

                     while(var9-- != 0) {
                        var6.add(POISON_PILL);
                     }
                  }

                  CharArrays.Segment var22 = (CharArrays.Segment)var6.take();
                  if (var22 == POISON_PILL) {
                     return null;
                  }

                  int var10 = var22.offset;
                  int var11x = var22.length;
                  int var12 = var22.level;
                  boolean var13 = false;
                  int var14 = (1 - var12 % 2) * 8;

                  int var15;
                  for(var15 = var10 + var11x; var15-- != var10; ++var7x[var1[var0[var15]] >>> var14 & 255 ^ 0]) {
                  }

                  var15 = -1;
                  int var16 = 0;

                  int var17;
                  for(var17 = var10; var16 < 256; ++var16) {
                     if (var7x[var16] != 0) {
                        var15 = var16;
                     }

                     var8x[var16] = var17 += var7x[var16];
                  }

                  if (var4) {
                     for(var16 = var10 + var11x; var16-- != var10; var11[--var8x[var1[var0[var16]] >>> var14 & 255 ^ 0]] = var0[var16]) {
                     }

                     System.arraycopy(var11, var10, var0, var10, var11x);
                     var16 = 0;

                     for(var17 = var10; var16 <= var15; ++var16) {
                        if (var12 < 1 && var7x[var16] > 1) {
                           if (var7x[var16] < 1024) {
                              radixSortIndirect(var0, var1, var17, var17 + var7x[var16], var4);
                           } else {
                              var7.incrementAndGet();
                              var6.add(new CharArrays.Segment(var17, var7x[var16], var12 + 1));
                           }
                        }

                        var17 += var7x[var16];
                     }

                     java.util.Arrays.fill(var7x, 0);
                  } else {
                     var16 = var10 + var11x - var7x[var15];
                     var17 = var10;

                     int var23;
                     for(boolean var18 = true; var17 <= var16; var7x[var23] = 0) {
                        int var20 = var0[var17];
                        var23 = var1[var20] >>> var14 & 255 ^ 0;
                        if (var17 < var16) {
                           int var19;
                           while((var19 = --var8x[var23]) > var17) {
                              int var21 = var20;
                              var20 = var0[var19];
                              var0[var19] = var21;
                              var23 = var1[var20] >>> var14 & 255 ^ 0;
                           }

                           var0[var17] = var20;
                        }

                        if (var12 < 1 && var7x[var23] > 1) {
                           if (var7x[var23] < 1024) {
                              radixSortIndirect(var0, var1, var17, var17 + var7x[var23], var4);
                           } else {
                              var7.incrementAndGet();
                              var6.add(new CharArrays.Segment(var17, var7x[var23], var12 + 1));
                           }
                        }

                        var17 += var7x[var23];
                     }
                  }

                  var7.decrementAndGet();
               }
            });
         }

         Throwable var16 = null;
         int var13 = var8;

         while(var13-- != 0) {
            try {
               var10.take().get();
            } catch (Exception var15) {
               var16 = var15.getCause();
            }
         }

         var9.shutdown();
         if (var16 != null) {
            throw var16 instanceof RuntimeException ? (RuntimeException)var16 : new RuntimeException(var16);
         }
      }
   }

   public static void parallelRadixSortIndirect(int[] var0, char[] var1, boolean var2) {
      parallelRadixSortIndirect(var0, var1, 0, var1.length, var2);
   }

   public static void radixSort(char[] var0, char[] var1) {
      ensureSameLength(var0, var1);
      radixSort(var0, var1, 0, var0.length);
   }

   public static void radixSort(char[] var0, char[] var1, int var2, int var3) {
      if (var3 - var2 < 1024) {
         selectionSort(var0, var1, var2, var3);
      } else {
         boolean var4 = true;
         boolean var5 = true;
         boolean var6 = true;
         byte var7 = 0;
         int[] var8 = new int[766];
         int[] var9 = new int[766];
         int[] var10 = new int[766];
         var8[var7] = var2;
         var9[var7] = var3 - var2;
         int var27 = var7 + 1;
         var10[var7] = 0;
         int[] var11 = new int[256];
         int[] var12 = new int[256];

         while(var27 > 0) {
            --var27;
            int var13 = var8[var27];
            int var14 = var9[var27];
            int var15 = var10[var27];
            boolean var16 = false;
            char[] var17 = var15 < 2 ? var0 : var1;
            int var18 = (1 - var15 % 2) * 8;

            int var19;
            for(var19 = var13 + var14; var19-- != var13; ++var11[var17[var19] >>> var18 & 255 ^ 0]) {
            }

            var19 = -1;
            int var20 = 0;

            int var21;
            for(var21 = var13; var20 < 256; ++var20) {
               if (var11[var20] != 0) {
                  var19 = var20;
               }

               var12[var20] = var21 += var11[var20];
            }

            var20 = var13 + var14 - var11[var19];
            var21 = var13;

            int var29;
            for(boolean var22 = true; var21 <= var20; var11[var29] = 0) {
               char var24 = var0[var21];
               char var25 = var1[var21];
               var29 = var17[var21] >>> var18 & 255 ^ 0;
               if (var21 < var20) {
                  int var23;
                  while((var23 = --var12[var29]) > var21) {
                     var29 = var17[var23] >>> var18 & 255 ^ 0;
                     char var26 = var24;
                     var24 = var0[var23];
                     var0[var23] = var26;
                     var26 = var25;
                     var25 = var1[var23];
                     var1[var23] = var26;
                  }

                  var0[var21] = var24;
                  var1[var21] = var25;
               }

               if (var15 < 3 && var11[var29] > 1) {
                  if (var11[var29] < 1024) {
                     selectionSort(var0, var1, var21, var21 + var11[var29]);
                  } else {
                     var8[var27] = var21;
                     var9[var27] = var11[var29];
                     var10[var27++] = var15 + 1;
                  }
               }

               var21 += var11[var29];
            }
         }

      }
   }

   public static void parallelRadixSort(char[] var0, char[] var1, int var2, int var3) {
      if (var3 - var2 < 1024) {
         quickSort(var0, var1, var2, var3);
      } else {
         boolean var4 = true;
         if (var0.length != var1.length) {
            throw new IllegalArgumentException("Array size mismatch.");
         } else {
            boolean var5 = true;
            LinkedBlockingQueue var6 = new LinkedBlockingQueue();
            var6.add(new CharArrays.Segment(var2, var3 - var2, 0));
            AtomicInteger var7 = new AtomicInteger(1);
            int var8 = Runtime.getRuntime().availableProcessors();
            ExecutorService var9 = Executors.newFixedThreadPool(var8, Executors.defaultThreadFactory());
            ExecutorCompletionService var10 = new ExecutorCompletionService(var9);
            int var11 = var8;

            while(var11-- != 0) {
               var10.submit(() -> {
                  int[] var5 = new int[256];
                  int[] var6x = new int[256];

                  while(true) {
                     if (var7.get() == 0) {
                        int var7x = var8;

                        while(var7x-- != 0) {
                           var6.add(POISON_PILL);
                        }
                     }

                     CharArrays.Segment var23 = (CharArrays.Segment)var6.take();
                     if (var23 == POISON_PILL) {
                        return null;
                     }

                     int var8x = var23.offset;
                     int var9 = var23.length;
                     int var10 = var23.level;
                     int var11 = var10 % 2 == 0 ? 128 : 0;
                     char[] var12 = var10 < 2 ? var0 : var1;
                     int var13 = (1 - var10 % 2) * 8;

                     int var14;
                     for(var14 = var8x + var9; var14-- != var8x; ++var5[var12[var14] >>> var13 & 255 ^ var11]) {
                     }

                     var14 = -1;
                     int var15 = 0;

                     int var16;
                     for(var16 = var8x; var15 < 256; ++var15) {
                        if (var5[var15] != 0) {
                           var14 = var15;
                        }

                        var6x[var15] = var16 += var5[var15];
                     }

                     var15 = var8x + var9 - var5[var14];
                     var16 = var8x;

                     int var24;
                     for(boolean var17 = true; var16 <= var15; var5[var24] = 0) {
                        char var19 = var0[var16];
                        char var20 = var1[var16];
                        var24 = var12[var16] >>> var13 & 255 ^ var11;
                        if (var16 < var15) {
                           int var18;
                           while((var18 = --var6x[var24]) > var16) {
                              var24 = var12[var18] >>> var13 & 255 ^ var11;
                              char var21 = var19;
                              char var22 = var20;
                              var19 = var0[var18];
                              var20 = var1[var18];
                              var0[var18] = var21;
                              var1[var18] = var22;
                           }

                           var0[var16] = var19;
                           var1[var16] = var20;
                        }

                        if (var10 < 3 && var5[var24] > 1) {
                           if (var5[var24] < 1024) {
                              quickSort(var0, var1, var16, var16 + var5[var24]);
                           } else {
                              var7.incrementAndGet();
                              var6.add(new CharArrays.Segment(var16, var5[var24], var10 + 1));
                           }
                        }

                        var16 += var5[var24];
                     }

                     var7.decrementAndGet();
                  }
               });
            }

            Throwable var15 = null;
            int var12 = var8;

            while(var12-- != 0) {
               try {
                  var10.take().get();
               } catch (Exception var14) {
                  var15 = var14.getCause();
               }
            }

            var9.shutdown();
            if (var15 != null) {
               throw var15 instanceof RuntimeException ? (RuntimeException)var15 : new RuntimeException(var15);
            }
         }
      }
   }

   public static void parallelRadixSort(char[] var0, char[] var1) {
      ensureSameLength(var0, var1);
      parallelRadixSort(var0, var1, 0, var0.length);
   }

   private static void insertionSortIndirect(int[] var0, char[] var1, char[] var2, int var3, int var4) {
      int var5 = var3;

      while(true) {
         ++var5;
         if (var5 >= var4) {
            return;
         }

         int var6 = var0[var5];
         int var7 = var5;

         for(int var8 = var0[var5 - 1]; var1[var6] < var1[var8] || var1[var6] == var1[var8] && var2[var6] < var2[var8]; var8 = var0[var7 - 1]) {
            var0[var7] = var8;
            if (var3 == var7 - 1) {
               --var7;
               break;
            }

            --var7;
         }

         var0[var7] = var6;
      }
   }

   public static void radixSortIndirect(int[] var0, char[] var1, char[] var2, boolean var3) {
      ensureSameLength(var1, var2);
      radixSortIndirect(var0, var1, var2, 0, var1.length, var3);
   }

   public static void radixSortIndirect(int[] var0, char[] var1, char[] var2, int var3, int var4, boolean var5) {
      if (var4 - var3 < 1024) {
         insertionSortIndirect(var0, var1, var2, var3, var4);
      } else {
         boolean var6 = true;
         boolean var7 = true;
         boolean var8 = true;
         byte var9 = 0;
         int[] var10 = new int[766];
         int[] var11 = new int[766];
         int[] var12 = new int[766];
         var10[var9] = var3;
         var11[var9] = var4 - var3;
         int var29 = var9 + 1;
         var12[var9] = 0;
         int[] var13 = new int[256];
         int[] var14 = new int[256];
         int[] var15 = var5 ? new int[var0.length] : null;

         while(true) {
            while(var29 > 0) {
               --var29;
               int var16 = var10[var29];
               int var17 = var11[var29];
               int var18 = var12[var29];
               boolean var19 = false;
               char[] var20 = var18 < 2 ? var1 : var2;
               int var21 = (1 - var18 % 2) * 8;

               int var22;
               for(var22 = var16 + var17; var22-- != var16; ++var13[var20[var0[var22]] >>> var21 & 255 ^ 0]) {
               }

               var22 = -1;
               int var23 = 0;

               int var24;
               for(var24 = var5 ? 0 : var16; var23 < 256; ++var23) {
                  if (var13[var23] != 0) {
                     var22 = var23;
                  }

                  var14[var23] = var24 += var13[var23];
               }

               if (var5) {
                  for(var23 = var16 + var17; var23-- != var16; var15[--var14[var20[var0[var23]] >>> var21 & 255 ^ 0]] = var0[var23]) {
                  }

                  System.arraycopy(var15, 0, var0, var16, var17);
                  var23 = 0;

                  for(var24 = var16; var23 < 256; ++var23) {
                     if (var18 < 3 && var13[var23] > 1) {
                        if (var13[var23] < 1024) {
                           insertionSortIndirect(var0, var1, var2, var24, var24 + var13[var23]);
                        } else {
                           var10[var29] = var24;
                           var11[var29] = var13[var23];
                           var12[var29++] = var18 + 1;
                        }
                     }

                     var24 += var13[var23];
                  }

                  java.util.Arrays.fill(var13, 0);
               } else {
                  var23 = var16 + var17 - var13[var22];
                  var24 = var16;

                  int var31;
                  for(boolean var25 = true; var24 <= var23; var13[var31] = 0) {
                     int var27 = var0[var24];
                     var31 = var20[var27] >>> var21 & 255 ^ 0;
                     if (var24 < var23) {
                        int var26;
                        while((var26 = --var14[var31]) > var24) {
                           int var28 = var27;
                           var27 = var0[var26];
                           var0[var26] = var28;
                           var31 = var20[var27] >>> var21 & 255 ^ 0;
                        }

                        var0[var24] = var27;
                     }

                     if (var18 < 3 && var13[var31] > 1) {
                        if (var13[var31] < 1024) {
                           insertionSortIndirect(var0, var1, var2, var24, var24 + var13[var31]);
                        } else {
                           var10[var29] = var24;
                           var11[var29] = var13[var31];
                           var12[var29++] = var18 + 1;
                        }
                     }

                     var24 += var13[var31];
                  }
               }
            }

            return;
         }
      }
   }

   private static void selectionSort(char[][] var0, int var1, int var2, int var3) {
      int var4 = var0.length;
      int var5 = var3 / 2;

      for(int var6 = var1; var6 < var2 - 1; ++var6) {
         int var7 = var6;

         int var8;
         for(var8 = var6 + 1; var8 < var2; ++var8) {
            for(int var9 = var5; var9 < var4; ++var9) {
               if (var0[var9][var8] < var0[var9][var7]) {
                  var7 = var8;
                  break;
               }

               if (var0[var9][var8] > var0[var9][var7]) {
                  break;
               }
            }
         }

         char var10;
         if (var7 != var6) {
            for(var8 = var4; var8-- != 0; var0[var8][var7] = var10) {
               var10 = var0[var8][var6];
               var0[var8][var6] = var0[var8][var7];
            }
         }
      }

   }

   public static void radixSort(char[][] var0) {
      radixSort((char[][])var0, 0, var0[0].length);
   }

   public static void radixSort(char[][] var0, int var1, int var2) {
      if (var2 - var1 < 1024) {
         selectionSort(var0, var1, var2, 0);
      } else {
         int var3 = var0.length;
         int var4 = 2 * var3 - 1;
         int var5 = var3;
         int var6 = var0[0].length;

         while(var5-- != 0) {
            if (var0[var5].length != var6) {
               throw new IllegalArgumentException("The array of index " + var5 + " has not the same length of the array of index 0.");
            }
         }

         var5 = 255 * (var3 * 2 - 1) + 1;
         byte var26 = 0;
         int[] var7 = new int[var5];
         int[] var8 = new int[var5];
         int[] var9 = new int[var5];
         var7[var26] = var1;
         var8[var26] = var2 - var1;
         var6 = var26 + 1;
         var9[var26] = 0;
         int[] var10 = new int[256];
         int[] var11 = new int[256];
         char[] var12 = new char[var3];

         while(var6 > 0) {
            --var6;
            int var13 = var7[var6];
            int var14 = var8[var6];
            int var15 = var9[var6];
            boolean var16 = false;
            char[] var17 = var0[var15 / 2];
            int var18 = (1 - var15 % 2) * 8;

            int var19;
            for(var19 = var13 + var14; var19-- != var13; ++var10[var17[var19] >>> var18 & 255 ^ 0]) {
            }

            var19 = -1;
            int var20 = 0;

            int var21;
            for(var21 = var13; var20 < 256; ++var20) {
               if (var10[var20] != 0) {
                  var19 = var20;
               }

               var11[var20] = var21 += var10[var20];
            }

            var20 = var13 + var14 - var10[var19];
            var21 = var13;

            int var28;
            for(boolean var22 = true; var21 <= var20; var10[var28] = 0) {
               int var24;
               for(var24 = var3; var24-- != 0; var12[var24] = var0[var24][var21]) {
               }

               var28 = var17[var21] >>> var18 & 255 ^ 0;
               if (var21 < var20) {
                  label87:
                  while(true) {
                     int var23;
                     if ((var23 = --var11[var28]) <= var21) {
                        var24 = var3;

                        while(true) {
                           if (var24-- == 0) {
                              break label87;
                           }

                           var0[var24][var21] = var12[var24];
                        }
                     }

                     var28 = var17[var23] >>> var18 & 255 ^ 0;

                     char var25;
                     for(var24 = var3; var24-- != 0; var0[var24][var23] = var25) {
                        var25 = var12[var24];
                        var12[var24] = var0[var24][var23];
                     }
                  }
               }

               if (var15 < var4 && var10[var28] > 1) {
                  if (var10[var28] < 1024) {
                     selectionSort(var0, var21, var21 + var10[var28], var15 + 1);
                  } else {
                     var7[var6] = var21;
                     var8[var6] = var10[var28];
                     var9[var6++] = var15 + 1;
                  }
               }

               var21 += var10[var28];
            }
         }

      }
   }

   public static char[] shuffle(char[] var0, int var1, int var2, Random var3) {
      int var5;
      char var6;
      for(int var4 = var2 - var1; var4-- != 0; var0[var1 + var5] = var6) {
         var5 = var3.nextInt(var4 + 1);
         var6 = var0[var1 + var4];
         var0[var1 + var4] = var0[var1 + var5];
      }

      return var0;
   }

   public static char[] shuffle(char[] var0, Random var1) {
      int var3;
      char var4;
      for(int var2 = var0.length; var2-- != 0; var0[var3] = var4) {
         var3 = var1.nextInt(var2 + 1);
         var4 = var0[var2];
         var0[var2] = var0[var3];
      }

      return var0;
   }

   public static char[] reverse(char[] var0) {
      int var1 = var0.length;

      char var3;
      for(int var2 = var1 / 2; var2-- != 0; var0[var2] = var3) {
         var3 = var0[var1 - var2 - 1];
         var0[var1 - var2 - 1] = var0[var2];
      }

      return var0;
   }

   public static char[] reverse(char[] var0, int var1, int var2) {
      int var3 = var2 - var1;

      char var5;
      for(int var4 = var3 / 2; var4-- != 0; var0[var1 + var4] = var5) {
         var5 = var0[var1 + var3 - var4 - 1];
         var0[var1 + var3 - var4 - 1] = var0[var1 + var4];
      }

      return var0;
   }

   private static final class ArrayHashStrategy implements Hash.Strategy<char[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
         super();
      }

      public int hashCode(char[] var1) {
         return java.util.Arrays.hashCode(var1);
      }

      public boolean equals(char[] var1, char[] var2) {
         return java.util.Arrays.equals(var1, var2);
      }

      // $FF: synthetic method
      ArrayHashStrategy(Object var1) {
         this();
      }
   }

   protected static final class Segment {
      protected final int offset;
      protected final int length;
      protected final int level;

      protected Segment(int var1, int var2, int var3) {
         super();
         this.offset = var1;
         this.length = var2;
         this.level = var3;
      }

      public String toString() {
         return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
      }
   }

   protected static class ForkJoinQuickSort2 extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final char[] x;
      private final char[] y;

      public ForkJoinQuickSort2(char[] var1, char[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var1;
         this.y = var2;
      }

      protected void compute() {
         char[] var1 = this.x;
         char[] var2 = this.y;
         int var3 = this.to - this.from;
         if (var3 < 8192) {
            CharArrays.quickSort(var1, var2, this.from, this.to);
         } else {
            int var4 = this.from + var3 / 2;
            int var5 = this.from;
            int var6 = this.to - 1;
            int var7 = var3 / 8;
            var5 = CharArrays.med3(var1, var2, var5, var5 + var7, var5 + 2 * var7);
            var4 = CharArrays.med3(var1, var2, var4 - var7, var4, var4 + var7);
            var6 = CharArrays.med3(var1, var2, var6 - 2 * var7, var6 - var7, var6);
            var4 = CharArrays.med3(var1, var2, var5, var4, var6);
            char var8 = var1[var4];
            char var9 = var2[var4];
            int var10 = this.from;
            int var11 = var10;
            int var12 = this.to - 1;
            int var13 = var12;

            while(true) {
               int var14;
               int var15;
               while(var11 > var12 || (var14 = (var15 = Character.compare(var1[var11], var8)) == 0 ? Character.compare(var2[var11], var9) : var15) > 0) {
                  for(; var12 >= var11 && (var14 = (var15 = Character.compare(var1[var12], var8)) == 0 ? Character.compare(var2[var12], var9) : var15) >= 0; --var12) {
                     if (var14 == 0) {
                        CharArrays.swap(var1, var2, var12, var13--);
                     }
                  }

                  if (var11 > var12) {
                     var7 = Math.min(var10 - this.from, var11 - var10);
                     CharArrays.swap(var1, var2, this.from, var11 - var7, var7);
                     var7 = Math.min(var13 - var12, this.to - var13 - 1);
                     CharArrays.swap(var1, var2, var11, this.to - var7, var7);
                     var7 = var11 - var10;
                     var14 = var13 - var12;
                     if (var7 > 1 && var14 > 1) {
                        invokeAll(new CharArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7), new CharArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to));
                     } else if (var7 > 1) {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to)});
                     }

                     return;
                  }

                  CharArrays.swap(var1, var2, var11++, var12--);
               }

               if (var14 == 0) {
                  CharArrays.swap(var1, var2, var10++, var11);
               }

               ++var11;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortIndirect extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] perm;
      private final char[] x;

      public ForkJoinQuickSortIndirect(int[] var1, char[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var2;
         this.perm = var1;
      }

      protected void compute() {
         char[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            CharArrays.quickSortIndirect(this.perm, var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = CharArrays.med3Indirect(this.perm, var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = CharArrays.med3Indirect(this.perm, var1, var3 - var6, var3, var3 + var6);
            var5 = CharArrays.med3Indirect(this.perm, var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = CharArrays.med3Indirect(this.perm, var1, var4, var3, var5);
            char var7 = var1[this.perm[var3]];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = Character.compare(var1[this.perm[var9]], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = Character.compare(var1[this.perm[var10]], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        IntArrays.swap(this.perm, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     IntArrays.swap(this.perm, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     IntArrays.swap(this.perm, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new CharArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6), new CharArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to)});
                     }

                     return;
                  }

                  IntArrays.swap(this.perm, var9++, var10--);
               }

               if (var12 == 0) {
                  IntArrays.swap(this.perm, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }

   protected static class ForkJoinQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final char[] x;

      public ForkJoinQuickSort(char[] var1, int var2, int var3) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
      }

      protected void compute() {
         char[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            CharArrays.quickSort(var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = CharArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = CharArrays.med3(var1, var3 - var6, var3, var3 + var6);
            var5 = CharArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = CharArrays.med3(var1, var4, var3, var5);
            char var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = Character.compare(var1[var9], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = Character.compare(var1[var10], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        CharArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     CharArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     CharArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new CharArrays.ForkJoinQuickSort(var1, this.from, this.from + var6), new CharArrays.ForkJoinQuickSort(var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSort(var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSort(var1, this.to - var12, this.to)});
                     }

                     return;
                  }

                  CharArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  CharArrays.swap(var1, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortComp extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final char[] x;
      private final CharComparator comp;

      public ForkJoinQuickSortComp(char[] var1, int var2, int var3, CharComparator var4) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
         this.comp = var4;
      }

      protected void compute() {
         char[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            CharArrays.quickSort(var1, this.from, this.to, this.comp);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = CharArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6, this.comp);
            var3 = CharArrays.med3(var1, var3 - var6, var3, var3 + var6, this.comp);
            var5 = CharArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5, this.comp);
            var3 = CharArrays.med3(var1, var4, var3, var5, this.comp);
            char var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = this.comp.compare(var1[var9], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = this.comp.compare(var1[var10], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        CharArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     CharArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     CharArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new CharArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp), new CharArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new CharArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp)});
                     }

                     return;
                  }

                  CharArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  CharArrays.swap(var1, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }
}
