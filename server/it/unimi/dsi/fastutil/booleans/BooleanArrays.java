package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class BooleanArrays {
   public static final boolean[] EMPTY_ARRAY = new boolean[0];
   public static final boolean[] DEFAULT_EMPTY_ARRAY = new boolean[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   public static final Hash.Strategy<boolean[]> HASH_STRATEGY = new BooleanArrays.ArrayHashStrategy();

   private BooleanArrays() {
      super();
   }

   public static boolean[] forceCapacity(boolean[] var0, int var1, int var2) {
      boolean[] var3 = new boolean[var1];
      System.arraycopy(var0, 0, var3, 0, var2);
      return var3;
   }

   public static boolean[] ensureCapacity(boolean[] var0, int var1) {
      return ensureCapacity(var0, var1, var0.length);
   }

   public static boolean[] ensureCapacity(boolean[] var0, int var1, int var2) {
      return var1 > var0.length ? forceCapacity(var0, var1, var2) : var0;
   }

   public static boolean[] grow(boolean[] var0, int var1) {
      return grow(var0, var1, var0.length);
   }

   public static boolean[] grow(boolean[] var0, int var1, int var2) {
      if (var1 > var0.length) {
         int var3 = (int)Math.max(Math.min((long)var0.length + (long)(var0.length >> 1), 2147483639L), (long)var1);
         boolean[] var4 = new boolean[var3];
         System.arraycopy(var0, 0, var4, 0, var2);
         return var4;
      } else {
         return var0;
      }
   }

   public static boolean[] trim(boolean[] var0, int var1) {
      if (var1 >= var0.length) {
         return var0;
      } else {
         boolean[] var2 = var1 == 0 ? EMPTY_ARRAY : new boolean[var1];
         System.arraycopy(var0, 0, var2, 0, var1);
         return var2;
      }
   }

   public static boolean[] setLength(boolean[] var0, int var1) {
      if (var1 == var0.length) {
         return var0;
      } else {
         return var1 < var0.length ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static boolean[] copy(boolean[] var0, int var1, int var2) {
      ensureOffsetLength(var0, var1, var2);
      boolean[] var3 = var2 == 0 ? EMPTY_ARRAY : new boolean[var2];
      System.arraycopy(var0, var1, var3, 0, var2);
      return var3;
   }

   public static boolean[] copy(boolean[] var0) {
      return (boolean[])var0.clone();
   }

   /** @deprecated */
   @Deprecated
   public static void fill(boolean[] var0, boolean var1) {
      for(int var2 = var0.length; var2-- != 0; var0[var2] = var1) {
      }

   }

   /** @deprecated */
   @Deprecated
   public static void fill(boolean[] var0, int var1, int var2, boolean var3) {
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
   public static boolean equals(boolean[] var0, boolean[] var1) {
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

   public static void ensureFromTo(boolean[] var0, int var1, int var2) {
      Arrays.ensureFromTo(var0.length, var1, var2);
   }

   public static void ensureOffsetLength(boolean[] var0, int var1, int var2) {
      Arrays.ensureOffsetLength(var0.length, var1, var2);
   }

   public static void ensureSameLength(boolean[] var0, boolean[] var1) {
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("Array size mismatch: " + var0.length + " != " + var1.length);
      }
   }

   public static void swap(boolean[] var0, int var1, int var2) {
      boolean var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static void swap(boolean[] var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var2) {
         swap(var0, var1, var2);
         ++var4;
         ++var1;
      }

   }

   private static int med3(boolean[] var0, int var1, int var2, int var3, BooleanComparator var4) {
      int var5 = var4.compare(var0[var1], var0[var2]);
      int var6 = var4.compare(var0[var1], var0[var3]);
      int var7 = var4.compare(var0[var2], var0[var3]);
      return var5 < 0 ? (var7 < 0 ? var2 : (var6 < 0 ? var3 : var1)) : (var7 > 0 ? var2 : (var6 > 0 ? var3 : var1));
   }

   private static void selectionSort(boolean[] var0, int var1, int var2, BooleanComparator var3) {
      for(int var4 = var1; var4 < var2 - 1; ++var4) {
         int var5 = var4;

         for(int var6 = var4 + 1; var6 < var2; ++var6) {
            if (var3.compare(var0[var6], var0[var5]) < 0) {
               var5 = var6;
            }
         }

         if (var5 != var4) {
            boolean var7 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var7;
         }
      }

   }

   private static void insertionSort(boolean[] var0, int var1, int var2, BooleanComparator var3) {
      int var4 = var1;

      while(true) {
         ++var4;
         if (var4 >= var2) {
            return;
         }

         boolean var5 = var0[var4];
         int var6 = var4;

         for(boolean var7 = var0[var4 - 1]; var3.compare(var5, var7) < 0; var7 = var0[var6 - 1]) {
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

   public static void quickSort(boolean[] var0, int var1, int var2, BooleanComparator var3) {
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
         boolean var14 = var0[var5];
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

   public static void quickSort(boolean[] var0, BooleanComparator var1) {
      quickSort(var0, 0, var0.length, var1);
   }

   public static void parallelQuickSort(boolean[] var0, int var1, int var2, BooleanComparator var3) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new BooleanArrays.ForkJoinQuickSortComp(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static void parallelQuickSort(boolean[] var0, BooleanComparator var1) {
      parallelQuickSort(var0, 0, var0.length, var1);
   }

   private static int med3(boolean[] var0, int var1, int var2, int var3) {
      int var4 = Boolean.compare(var0[var1], var0[var2]);
      int var5 = Boolean.compare(var0[var1], var0[var3]);
      int var6 = Boolean.compare(var0[var2], var0[var3]);
      return var4 < 0 ? (var6 < 0 ? var2 : (var5 < 0 ? var3 : var1)) : (var6 > 0 ? var2 : (var5 > 0 ? var3 : var1));
   }

   private static void selectionSort(boolean[] var0, int var1, int var2) {
      for(int var3 = var1; var3 < var2 - 1; ++var3) {
         int var4 = var3;

         for(int var5 = var3 + 1; var5 < var2; ++var5) {
            if (!var0[var5] && var0[var4]) {
               var4 = var5;
            }
         }

         if (var4 != var3) {
            boolean var6 = var0[var3];
            var0[var3] = var0[var4];
            var0[var4] = var6;
         }
      }

   }

   private static void insertionSort(boolean[] var0, int var1, int var2) {
      int var3 = var1;

      while(true) {
         ++var3;
         if (var3 >= var2) {
            return;
         }

         boolean var4 = var0[var3];
         int var5 = var3;

         for(boolean var6 = var0[var3 - 1]; !var4 && var6; var6 = var0[var5 - 1]) {
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

   public static void quickSort(boolean[] var0, int var1, int var2) {
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
         boolean var13 = var0[var4];
         int var8 = var1;
         int var9 = var1;
         int var10 = var2 - 1;
         int var11 = var10;

         while(true) {
            int var12;
            while(var9 > var10 || (var12 = Boolean.compare(var0[var9], var13)) > 0) {
               for(; var10 >= var9 && (var12 = Boolean.compare(var0[var10], var13)) >= 0; --var10) {
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

   public static void quickSort(boolean[] var0) {
      quickSort(var0, 0, var0.length);
   }

   public static void parallelQuickSort(boolean[] var0, int var1, int var2) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2);
      } else {
         ForkJoinPool var3 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var3.invoke(new BooleanArrays.ForkJoinQuickSort(var0, var1, var2));
         var3.shutdown();
      }

   }

   public static void parallelQuickSort(boolean[] var0) {
      parallelQuickSort(var0, 0, var0.length);
   }

   private static int med3Indirect(int[] var0, boolean[] var1, int var2, int var3, int var4) {
      boolean var5 = var1[var0[var2]];
      boolean var6 = var1[var0[var3]];
      boolean var7 = var1[var0[var4]];
      int var8 = Boolean.compare(var5, var6);
      int var9 = Boolean.compare(var5, var7);
      int var10 = Boolean.compare(var6, var7);
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var4 : var2)) : (var10 > 0 ? var3 : (var9 > 0 ? var4 : var2));
   }

   private static void insertionSortIndirect(int[] var0, boolean[] var1, int var2, int var3) {
      int var4 = var2;

      while(true) {
         ++var4;
         if (var4 >= var3) {
            return;
         }

         int var5 = var0[var4];
         int var6 = var4;

         for(int var7 = var0[var4 - 1]; !var1[var5] && var1[var7]; var7 = var0[var6 - 1]) {
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

   public static void quickSortIndirect(int[] var0, boolean[] var1, int var2, int var3) {
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
         boolean var14 = var1[var0[var5]];
         int var9 = var2;
         int var10 = var2;
         int var11 = var3 - 1;
         int var12 = var11;

         while(true) {
            int var13;
            while(var10 > var11 || (var13 = Boolean.compare(var1[var0[var10]], var14)) > 0) {
               for(; var11 >= var10 && (var13 = Boolean.compare(var1[var0[var11]], var14)) >= 0; --var11) {
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

   public static void quickSortIndirect(int[] var0, boolean[] var1) {
      quickSortIndirect(var0, var1, 0, var1.length);
   }

   public static void parallelQuickSortIndirect(int[] var0, boolean[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSortIndirect(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new BooleanArrays.ForkJoinQuickSortIndirect(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static void parallelQuickSortIndirect(int[] var0, boolean[] var1) {
      parallelQuickSortIndirect(var0, var1, 0, var1.length);
   }

   public static void stabilize(int[] var0, boolean[] var1, int var2, int var3) {
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

   public static void stabilize(int[] var0, boolean[] var1) {
      stabilize(var0, var1, 0, var0.length);
   }

   private static int med3(boolean[] var0, boolean[] var1, int var2, int var3, int var4) {
      int var5;
      int var6 = (var5 = Boolean.compare(var0[var2], var0[var3])) == 0 ? Boolean.compare(var1[var2], var1[var3]) : var5;
      int var7 = (var5 = Boolean.compare(var0[var2], var0[var4])) == 0 ? Boolean.compare(var1[var2], var1[var4]) : var5;
      int var8 = (var5 = Boolean.compare(var0[var3], var0[var4])) == 0 ? Boolean.compare(var1[var3], var1[var4]) : var5;
      return var6 < 0 ? (var8 < 0 ? var3 : (var7 < 0 ? var4 : var2)) : (var8 > 0 ? var3 : (var7 > 0 ? var4 : var2));
   }

   private static void swap(boolean[] var0, boolean[] var1, int var2, int var3) {
      boolean var4 = var0[var2];
      boolean var5 = var1[var2];
      var0[var2] = var0[var3];
      var1[var2] = var1[var3];
      var0[var3] = var4;
      var1[var3] = var5;
   }

   private static void swap(boolean[] var0, boolean[] var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < var4; ++var3) {
         swap(var0, var1, var2, var3);
         ++var5;
         ++var2;
      }

   }

   private static void selectionSort(boolean[] var0, boolean[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 - 1; ++var4) {
         int var5 = var4;

         for(int var7 = var4 + 1; var7 < var3; ++var7) {
            int var6;
            if ((var6 = Boolean.compare(var0[var7], var0[var5])) < 0 || var6 == 0 && !var1[var7] && var1[var5]) {
               var5 = var7;
            }
         }

         if (var5 != var4) {
            boolean var8 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var8;
            var8 = var1[var4];
            var1[var4] = var1[var5];
            var1[var5] = var8;
         }
      }

   }

   public static void quickSort(boolean[] var0, boolean[] var1, int var2, int var3) {
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
         boolean var16 = var0[var5];
         boolean var9 = var1[var5];
         int var10 = var2;
         int var11 = var2;
         int var12 = var3 - 1;
         int var13 = var12;

         while(true) {
            int var14;
            int var15;
            while(var11 > var12 || (var14 = (var15 = Boolean.compare(var0[var11], var16)) == 0 ? Boolean.compare(var1[var11], var9) : var15) > 0) {
               for(; var12 >= var11 && (var14 = (var15 = Boolean.compare(var0[var12], var16)) == 0 ? Boolean.compare(var1[var12], var9) : var15) >= 0; --var12) {
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

   public static void quickSort(boolean[] var0, boolean[] var1) {
      ensureSameLength(var0, var1);
      quickSort(var0, var1, 0, var0.length);
   }

   public static void parallelQuickSort(boolean[] var0, boolean[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSort(var0, var1, var2, var3);
      }

      ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      var4.invoke(new BooleanArrays.ForkJoinQuickSort2(var0, var1, var2, var3));
      var4.shutdown();
   }

   public static void parallelQuickSort(boolean[] var0, boolean[] var1) {
      ensureSameLength(var0, var1);
      parallelQuickSort(var0, var1, 0, var0.length);
   }

   public static void mergeSort(boolean[] var0, int var1, int var2, boolean[] var3) {
      int var4 = var2 - var1;
      if (var4 < 16) {
         insertionSort(var0, var1, var2);
      } else {
         int var5 = var1 + var2 >>> 1;
         mergeSort(var3, var1, var5, var0);
         mergeSort(var3, var5, var2, var0);
         if (var3[var5 - 1] && !var3[var5]) {
            int var6 = var1;
            int var7 = var1;

            for(int var8 = var5; var6 < var2; ++var6) {
               if (var8 < var2 && (var7 >= var5 || var3[var7] && !var3[var8])) {
                  var0[var6] = var3[var8++];
               } else {
                  var0[var6] = var3[var7++];
               }
            }

         } else {
            System.arraycopy(var3, var1, var0, var1, var4);
         }
      }
   }

   public static void mergeSort(boolean[] var0, int var1, int var2) {
      mergeSort(var0, var1, var2, (boolean[])var0.clone());
   }

   public static void mergeSort(boolean[] var0) {
      mergeSort(var0, 0, var0.length);
   }

   public static void mergeSort(boolean[] var0, int var1, int var2, BooleanComparator var3, boolean[] var4) {
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

   public static void mergeSort(boolean[] var0, int var1, int var2, BooleanComparator var3) {
      mergeSort(var0, var1, var2, var3, (boolean[])var0.clone());
   }

   public static void mergeSort(boolean[] var0, BooleanComparator var1) {
      mergeSort(var0, 0, var0.length, (BooleanComparator)var1);
   }

   public static boolean[] shuffle(boolean[] var0, int var1, int var2, Random var3) {
      int var5;
      boolean var6;
      for(int var4 = var2 - var1; var4-- != 0; var0[var1 + var5] = var6) {
         var5 = var3.nextInt(var4 + 1);
         var6 = var0[var1 + var4];
         var0[var1 + var4] = var0[var1 + var5];
      }

      return var0;
   }

   public static boolean[] shuffle(boolean[] var0, Random var1) {
      int var3;
      boolean var4;
      for(int var2 = var0.length; var2-- != 0; var0[var3] = var4) {
         var3 = var1.nextInt(var2 + 1);
         var4 = var0[var2];
         var0[var2] = var0[var3];
      }

      return var0;
   }

   public static boolean[] reverse(boolean[] var0) {
      int var1 = var0.length;

      boolean var3;
      for(int var2 = var1 / 2; var2-- != 0; var0[var2] = var3) {
         var3 = var0[var1 - var2 - 1];
         var0[var1 - var2 - 1] = var0[var2];
      }

      return var0;
   }

   public static boolean[] reverse(boolean[] var0, int var1, int var2) {
      int var3 = var2 - var1;

      boolean var5;
      for(int var4 = var3 / 2; var4-- != 0; var0[var1 + var4] = var5) {
         var5 = var0[var1 + var3 - var4 - 1];
         var0[var1 + var3 - var4 - 1] = var0[var1 + var4];
      }

      return var0;
   }

   private static final class ArrayHashStrategy implements Hash.Strategy<boolean[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
         super();
      }

      public int hashCode(boolean[] var1) {
         return java.util.Arrays.hashCode(var1);
      }

      public boolean equals(boolean[] var1, boolean[] var2) {
         return java.util.Arrays.equals(var1, var2);
      }

      // $FF: synthetic method
      ArrayHashStrategy(Object var1) {
         this();
      }
   }

   protected static class ForkJoinQuickSort2 extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final boolean[] x;
      private final boolean[] y;

      public ForkJoinQuickSort2(boolean[] var1, boolean[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var1;
         this.y = var2;
      }

      protected void compute() {
         boolean[] var1 = this.x;
         boolean[] var2 = this.y;
         int var3 = this.to - this.from;
         if (var3 < 8192) {
            BooleanArrays.quickSort(var1, var2, this.from, this.to);
         } else {
            int var4 = this.from + var3 / 2;
            int var5 = this.from;
            int var6 = this.to - 1;
            int var7 = var3 / 8;
            var5 = BooleanArrays.med3(var1, var2, var5, var5 + var7, var5 + 2 * var7);
            var4 = BooleanArrays.med3(var1, var2, var4 - var7, var4, var4 + var7);
            var6 = BooleanArrays.med3(var1, var2, var6 - 2 * var7, var6 - var7, var6);
            var4 = BooleanArrays.med3(var1, var2, var5, var4, var6);
            boolean var8 = var1[var4];
            boolean var9 = var2[var4];
            int var10 = this.from;
            int var11 = var10;
            int var12 = this.to - 1;
            int var13 = var12;

            while(true) {
               int var14;
               int var15;
               while(var11 > var12 || (var14 = (var15 = Boolean.compare(var1[var11], var8)) == 0 ? Boolean.compare(var2[var11], var9) : var15) > 0) {
                  for(; var12 >= var11 && (var14 = (var15 = Boolean.compare(var1[var12], var8)) == 0 ? Boolean.compare(var2[var12], var9) : var15) >= 0; --var12) {
                     if (var14 == 0) {
                        BooleanArrays.swap(var1, var2, var12, var13--);
                     }
                  }

                  if (var11 > var12) {
                     var7 = Math.min(var10 - this.from, var11 - var10);
                     BooleanArrays.swap(var1, var2, this.from, var11 - var7, var7);
                     var7 = Math.min(var13 - var12, this.to - var13 - 1);
                     BooleanArrays.swap(var1, var2, var11, this.to - var7, var7);
                     var7 = var11 - var10;
                     var14 = var13 - var12;
                     if (var7 > 1 && var14 > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7), new BooleanArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to));
                     } else if (var7 > 1) {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to)});
                     }

                     return;
                  }

                  BooleanArrays.swap(var1, var2, var11++, var12--);
               }

               if (var14 == 0) {
                  BooleanArrays.swap(var1, var2, var10++, var11);
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
      private final boolean[] x;

      public ForkJoinQuickSortIndirect(int[] var1, boolean[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var2;
         this.perm = var1;
      }

      protected void compute() {
         boolean[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            BooleanArrays.quickSortIndirect(this.perm, var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = BooleanArrays.med3Indirect(this.perm, var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = BooleanArrays.med3Indirect(this.perm, var1, var3 - var6, var3, var3 + var6);
            var5 = BooleanArrays.med3Indirect(this.perm, var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = BooleanArrays.med3Indirect(this.perm, var1, var4, var3, var5);
            boolean var7 = var1[this.perm[var3]];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = Boolean.compare(var1[this.perm[var9]], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = Boolean.compare(var1[this.perm[var10]], var7)) >= 0; --var10) {
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
                        invokeAll(new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6), new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to)});
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
      private final boolean[] x;

      public ForkJoinQuickSort(boolean[] var1, int var2, int var3) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
      }

      protected void compute() {
         boolean[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            BooleanArrays.quickSort(var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = BooleanArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = BooleanArrays.med3(var1, var3 - var6, var3, var3 + var6);
            var5 = BooleanArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = BooleanArrays.med3(var1, var4, var3, var5);
            boolean var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = Boolean.compare(var1[var9], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = Boolean.compare(var1[var10], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        BooleanArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     BooleanArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     BooleanArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSort(var1, this.from, this.from + var6), new BooleanArrays.ForkJoinQuickSort(var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSort(var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSort(var1, this.to - var12, this.to)});
                     }

                     return;
                  }

                  BooleanArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  BooleanArrays.swap(var1, var8++, var9);
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
      private final boolean[] x;
      private final BooleanComparator comp;

      public ForkJoinQuickSortComp(boolean[] var1, int var2, int var3, BooleanComparator var4) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
         this.comp = var4;
      }

      protected void compute() {
         boolean[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            BooleanArrays.quickSort(var1, this.from, this.to, this.comp);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = BooleanArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6, this.comp);
            var3 = BooleanArrays.med3(var1, var3 - var6, var3, var3 + var6, this.comp);
            var5 = BooleanArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5, this.comp);
            var3 = BooleanArrays.med3(var1, var4, var3, var5, this.comp);
            boolean var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = this.comp.compare(var1[var9], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = this.comp.compare(var1[var10], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        BooleanArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     BooleanArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     BooleanArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new BooleanArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp), new BooleanArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new BooleanArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp)});
                     }

                     return;
                  }

                  BooleanArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  BooleanArrays.swap(var1, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }
}
