package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class ObjectArrays {
   public static final Object[] EMPTY_ARRAY = new Object[0];
   public static final Object[] DEFAULT_EMPTY_ARRAY = new Object[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   public static final Hash.Strategy HASH_STRATEGY = new ObjectArrays.ArrayHashStrategy();

   private ObjectArrays() {
      super();
   }

   private static <K> K[] newArray(K[] var0, int var1) {
      Class var2 = var0.getClass();
      if (var2 == Object[].class) {
         return var1 == 0 ? EMPTY_ARRAY : new Object[var1];
      } else {
         return (Object[])Array.newInstance(var2.getComponentType(), var1);
      }
   }

   public static <K> K[] forceCapacity(K[] var0, int var1, int var2) {
      Object[] var3 = newArray(var0, var1);
      System.arraycopy(var0, 0, var3, 0, var2);
      return var3;
   }

   public static <K> K[] ensureCapacity(K[] var0, int var1) {
      return ensureCapacity(var0, var1, var0.length);
   }

   public static <K> K[] ensureCapacity(K[] var0, int var1, int var2) {
      return var1 > var0.length ? forceCapacity(var0, var1, var2) : var0;
   }

   public static <K> K[] grow(K[] var0, int var1) {
      return grow(var0, var1, var0.length);
   }

   public static <K> K[] grow(K[] var0, int var1, int var2) {
      if (var1 > var0.length) {
         int var3 = (int)Math.max(Math.min((long)var0.length + (long)(var0.length >> 1), 2147483639L), (long)var1);
         Object[] var4 = newArray(var0, var3);
         System.arraycopy(var0, 0, var4, 0, var2);
         return var4;
      } else {
         return var0;
      }
   }

   public static <K> K[] trim(K[] var0, int var1) {
      if (var1 >= var0.length) {
         return var0;
      } else {
         Object[] var2 = newArray(var0, var1);
         System.arraycopy(var0, 0, var2, 0, var1);
         return var2;
      }
   }

   public static <K> K[] setLength(K[] var0, int var1) {
      if (var1 == var0.length) {
         return var0;
      } else {
         return var1 < var0.length ? trim(var0, var1) : ensureCapacity(var0, var1);
      }
   }

   public static <K> K[] copy(K[] var0, int var1, int var2) {
      ensureOffsetLength(var0, var1, var2);
      Object[] var3 = newArray(var0, var2);
      System.arraycopy(var0, var1, var3, 0, var2);
      return var3;
   }

   public static <K> K[] copy(K[] var0) {
      return (Object[])var0.clone();
   }

   /** @deprecated */
   @Deprecated
   public static <K> void fill(K[] var0, K var1) {
      for(int var2 = var0.length; var2-- != 0; var0[var2] = var1) {
      }

   }

   /** @deprecated */
   @Deprecated
   public static <K> void fill(K[] var0, int var1, int var2, K var3) {
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
   public static <K> boolean equals(K[] var0, K[] var1) {
      int var2 = var0.length;
      if (var2 != var1.length) {
         return false;
      } else {
         do {
            if (var2-- == 0) {
               return true;
            }
         } while(Objects.equals(var0[var2], var1[var2]));

         return false;
      }
   }

   public static <K> void ensureFromTo(K[] var0, int var1, int var2) {
      Arrays.ensureFromTo(var0.length, var1, var2);
   }

   public static <K> void ensureOffsetLength(K[] var0, int var1, int var2) {
      Arrays.ensureOffsetLength(var0.length, var1, var2);
   }

   public static <K> void ensureSameLength(K[] var0, K[] var1) {
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("Array size mismatch: " + var0.length + " != " + var1.length);
      }
   }

   public static <K> void swap(K[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static <K> void swap(K[] var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var2) {
         swap(var0, var1, var2);
         ++var4;
         ++var1;
      }

   }

   private static <K> int med3(K[] var0, int var1, int var2, int var3, Comparator<K> var4) {
      int var5 = var4.compare(var0[var1], var0[var2]);
      int var6 = var4.compare(var0[var1], var0[var3]);
      int var7 = var4.compare(var0[var2], var0[var3]);
      return var5 < 0 ? (var7 < 0 ? var2 : (var6 < 0 ? var3 : var1)) : (var7 > 0 ? var2 : (var6 > 0 ? var3 : var1));
   }

   private static <K> void selectionSort(K[] var0, int var1, int var2, Comparator<K> var3) {
      for(int var4 = var1; var4 < var2 - 1; ++var4) {
         int var5 = var4;

         for(int var6 = var4 + 1; var6 < var2; ++var6) {
            if (var3.compare(var0[var6], var0[var5]) < 0) {
               var5 = var6;
            }
         }

         if (var5 != var4) {
            Object var7 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var7;
         }
      }

   }

   private static <K> void insertionSort(K[] var0, int var1, int var2, Comparator<K> var3) {
      int var4 = var1;

      while(true) {
         ++var4;
         if (var4 >= var2) {
            return;
         }

         Object var5 = var0[var4];
         int var6 = var4;

         for(Object var7 = var0[var4 - 1]; var3.compare(var5, var7) < 0; var7 = var0[var6 - 1]) {
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

   public static <K> void quickSort(K[] var0, int var1, int var2, Comparator<K> var3) {
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
         Object var14 = var0[var5];
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

   public static <K> void quickSort(K[] var0, Comparator<K> var1) {
      quickSort(var0, 0, var0.length, var1);
   }

   public static <K> void parallelQuickSort(K[] var0, int var1, int var2, Comparator<K> var3) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new ObjectArrays.ForkJoinQuickSortComp(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static <K> void parallelQuickSort(K[] var0, Comparator<K> var1) {
      parallelQuickSort(var0, 0, var0.length, var1);
   }

   private static <K> int med3(K[] var0, int var1, int var2, int var3) {
      int var4 = ((Comparable)var0[var1]).compareTo(var0[var2]);
      int var5 = ((Comparable)var0[var1]).compareTo(var0[var3]);
      int var6 = ((Comparable)var0[var2]).compareTo(var0[var3]);
      return var4 < 0 ? (var6 < 0 ? var2 : (var5 < 0 ? var3 : var1)) : (var6 > 0 ? var2 : (var5 > 0 ? var3 : var1));
   }

   private static <K> void selectionSort(K[] var0, int var1, int var2) {
      for(int var3 = var1; var3 < var2 - 1; ++var3) {
         int var4 = var3;

         for(int var5 = var3 + 1; var5 < var2; ++var5) {
            if (((Comparable)var0[var5]).compareTo(var0[var4]) < 0) {
               var4 = var5;
            }
         }

         if (var4 != var3) {
            Object var6 = var0[var3];
            var0[var3] = var0[var4];
            var0[var4] = var6;
         }
      }

   }

   private static <K> void insertionSort(K[] var0, int var1, int var2) {
      int var3 = var1;

      while(true) {
         ++var3;
         if (var3 >= var2) {
            return;
         }

         Object var4 = var0[var3];
         int var5 = var3;

         for(Object var6 = var0[var3 - 1]; ((Comparable)var4).compareTo(var6) < 0; var6 = var0[var5 - 1]) {
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

   public static <K> void quickSort(K[] var0, int var1, int var2) {
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
         Object var13 = var0[var4];
         int var8 = var1;
         int var9 = var1;
         int var10 = var2 - 1;
         int var11 = var10;

         while(true) {
            int var12;
            while(var9 > var10 || (var12 = ((Comparable)var0[var9]).compareTo(var13)) > 0) {
               for(; var10 >= var9 && (var12 = ((Comparable)var0[var10]).compareTo(var13)) >= 0; --var10) {
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

   public static <K> void quickSort(K[] var0) {
      quickSort(var0, 0, var0.length);
   }

   public static <K> void parallelQuickSort(K[] var0, int var1, int var2) {
      if (var2 - var1 < 8192) {
         quickSort(var0, var1, var2);
      } else {
         ForkJoinPool var3 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var3.invoke(new ObjectArrays.ForkJoinQuickSort(var0, var1, var2));
         var3.shutdown();
      }

   }

   public static <K> void parallelQuickSort(K[] var0) {
      parallelQuickSort(var0, 0, var0.length);
   }

   private static <K> int med3Indirect(int[] var0, K[] var1, int var2, int var3, int var4) {
      Object var5 = var1[var0[var2]];
      Object var6 = var1[var0[var3]];
      Object var7 = var1[var0[var4]];
      int var8 = ((Comparable)var5).compareTo(var6);
      int var9 = ((Comparable)var5).compareTo(var7);
      int var10 = ((Comparable)var6).compareTo(var7);
      return var8 < 0 ? (var10 < 0 ? var3 : (var9 < 0 ? var4 : var2)) : (var10 > 0 ? var3 : (var9 > 0 ? var4 : var2));
   }

   private static <K> void insertionSortIndirect(int[] var0, K[] var1, int var2, int var3) {
      int var4 = var2;

      while(true) {
         ++var4;
         if (var4 >= var3) {
            return;
         }

         int var5 = var0[var4];
         int var6 = var4;

         for(int var7 = var0[var4 - 1]; ((Comparable)var1[var5]).compareTo(var1[var7]) < 0; var7 = var0[var6 - 1]) {
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

   public static <K> void quickSortIndirect(int[] var0, K[] var1, int var2, int var3) {
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
         Object var14 = var1[var0[var5]];
         int var9 = var2;
         int var10 = var2;
         int var11 = var3 - 1;
         int var12 = var11;

         while(true) {
            int var13;
            while(var10 > var11 || (var13 = ((Comparable)var1[var0[var10]]).compareTo(var14)) > 0) {
               for(; var11 >= var10 && (var13 = ((Comparable)var1[var0[var11]]).compareTo(var14)) >= 0; --var11) {
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

   public static <K> void quickSortIndirect(int[] var0, K[] var1) {
      quickSortIndirect(var0, var1, 0, var1.length);
   }

   public static <K> void parallelQuickSortIndirect(int[] var0, K[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSortIndirect(var0, var1, var2, var3);
      } else {
         ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
         var4.invoke(new ObjectArrays.ForkJoinQuickSortIndirect(var0, var1, var2, var3));
         var4.shutdown();
      }

   }

   public static <K> void parallelQuickSortIndirect(int[] var0, K[] var1) {
      parallelQuickSortIndirect(var0, var1, 0, var1.length);
   }

   public static <K> void stabilize(int[] var0, K[] var1, int var2, int var3) {
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

   public static <K> void stabilize(int[] var0, K[] var1) {
      stabilize(var0, var1, 0, var0.length);
   }

   private static <K> int med3(K[] var0, K[] var1, int var2, int var3, int var4) {
      int var5;
      int var6 = (var5 = ((Comparable)var0[var2]).compareTo(var0[var3])) == 0 ? ((Comparable)var1[var2]).compareTo(var1[var3]) : var5;
      int var7 = (var5 = ((Comparable)var0[var2]).compareTo(var0[var4])) == 0 ? ((Comparable)var1[var2]).compareTo(var1[var4]) : var5;
      int var8 = (var5 = ((Comparable)var0[var3]).compareTo(var0[var4])) == 0 ? ((Comparable)var1[var3]).compareTo(var1[var4]) : var5;
      return var6 < 0 ? (var8 < 0 ? var3 : (var7 < 0 ? var4 : var2)) : (var8 > 0 ? var3 : (var7 > 0 ? var4 : var2));
   }

   private static <K> void swap(K[] var0, K[] var1, int var2, int var3) {
      Object var4 = var0[var2];
      Object var5 = var1[var2];
      var0[var2] = var0[var3];
      var1[var2] = var1[var3];
      var0[var3] = var4;
      var1[var3] = var5;
   }

   private static <K> void swap(K[] var0, K[] var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < var4; ++var3) {
         swap(var0, var1, var2, var3);
         ++var5;
         ++var2;
      }

   }

   private static <K> void selectionSort(K[] var0, K[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 - 1; ++var4) {
         int var5 = var4;

         for(int var7 = var4 + 1; var7 < var3; ++var7) {
            int var6;
            if ((var6 = ((Comparable)var0[var7]).compareTo(var0[var5])) < 0 || var6 == 0 && ((Comparable)var1[var7]).compareTo(var1[var5]) < 0) {
               var5 = var7;
            }
         }

         if (var5 != var4) {
            Object var8 = var0[var4];
            var0[var4] = var0[var5];
            var0[var5] = var8;
            var8 = var1[var4];
            var1[var4] = var1[var5];
            var1[var5] = var8;
         }
      }

   }

   public static <K> void quickSort(K[] var0, K[] var1, int var2, int var3) {
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
         Object var16 = var0[var5];
         Object var9 = var1[var5];
         int var10 = var2;
         int var11 = var2;
         int var12 = var3 - 1;
         int var13 = var12;

         while(true) {
            int var14;
            int var15;
            while(var11 > var12 || (var14 = (var15 = ((Comparable)var0[var11]).compareTo(var16)) == 0 ? ((Comparable)var1[var11]).compareTo(var9) : var15) > 0) {
               for(; var12 >= var11 && (var14 = (var15 = ((Comparable)var0[var12]).compareTo(var16)) == 0 ? ((Comparable)var1[var12]).compareTo(var9) : var15) >= 0; --var12) {
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

   public static <K> void quickSort(K[] var0, K[] var1) {
      ensureSameLength(var0, var1);
      quickSort(var0, var1, 0, var0.length);
   }

   public static <K> void parallelQuickSort(K[] var0, K[] var1, int var2, int var3) {
      if (var3 - var2 < 8192) {
         quickSort(var0, var1, var2, var3);
      }

      ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      var4.invoke(new ObjectArrays.ForkJoinQuickSort2(var0, var1, var2, var3));
      var4.shutdown();
   }

   public static <K> void parallelQuickSort(K[] var0, K[] var1) {
      ensureSameLength(var0, var1);
      parallelQuickSort(var0, var1, 0, var0.length);
   }

   public static <K> void mergeSort(K[] var0, int var1, int var2, K[] var3) {
      int var4 = var2 - var1;
      if (var4 < 16) {
         insertionSort(var0, var1, var2);
      } else {
         int var5 = var1 + var2 >>> 1;
         mergeSort(var3, var1, var5, var0);
         mergeSort(var3, var5, var2, var0);
         if (((Comparable)var3[var5 - 1]).compareTo(var3[var5]) <= 0) {
            System.arraycopy(var3, var1, var0, var1, var4);
         } else {
            int var6 = var1;
            int var7 = var1;

            for(int var8 = var5; var6 < var2; ++var6) {
               if (var8 < var2 && (var7 >= var5 || ((Comparable)var3[var7]).compareTo(var3[var8]) > 0)) {
                  var0[var6] = var3[var8++];
               } else {
                  var0[var6] = var3[var7++];
               }
            }

         }
      }
   }

   public static <K> void mergeSort(K[] var0, int var1, int var2) {
      mergeSort(var0, var1, var2, (Object[])var0.clone());
   }

   public static <K> void mergeSort(K[] var0) {
      mergeSort(var0, 0, var0.length);
   }

   public static <K> void mergeSort(K[] var0, int var1, int var2, Comparator<K> var3, K[] var4) {
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

   public static <K> void mergeSort(K[] var0, int var1, int var2, Comparator<K> var3) {
      mergeSort(var0, var1, var2, var3, (Object[])var0.clone());
   }

   public static <K> void mergeSort(K[] var0, Comparator<K> var1) {
      mergeSort(var0, 0, var0.length, (Comparator)var1);
   }

   public static <K> int binarySearch(K[] var0, int var1, int var2, K var3) {
      --var2;

      while(var1 <= var2) {
         int var5 = var1 + var2 >>> 1;
         Object var4 = var0[var5];
         int var6 = ((Comparable)var4).compareTo(var3);
         if (var6 < 0) {
            var1 = var5 + 1;
         } else {
            if (var6 <= 0) {
               return var5;
            }

            var2 = var5 - 1;
         }
      }

      return -(var1 + 1);
   }

   public static <K> int binarySearch(K[] var0, K var1) {
      return binarySearch(var0, 0, var0.length, var1);
   }

   public static <K> int binarySearch(K[] var0, int var1, int var2, K var3, Comparator<K> var4) {
      --var2;

      while(var1 <= var2) {
         int var6 = var1 + var2 >>> 1;
         Object var5 = var0[var6];
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

   public static <K> int binarySearch(K[] var0, K var1, Comparator<K> var2) {
      return binarySearch(var0, 0, var0.length, var1, var2);
   }

   public static <K> K[] shuffle(K[] var0, int var1, int var2, Random var3) {
      int var5;
      Object var6;
      for(int var4 = var2 - var1; var4-- != 0; var0[var1 + var5] = var6) {
         var5 = var3.nextInt(var4 + 1);
         var6 = var0[var1 + var4];
         var0[var1 + var4] = var0[var1 + var5];
      }

      return var0;
   }

   public static <K> K[] shuffle(K[] var0, Random var1) {
      int var3;
      Object var4;
      for(int var2 = var0.length; var2-- != 0; var0[var3] = var4) {
         var3 = var1.nextInt(var2 + 1);
         var4 = var0[var2];
         var0[var2] = var0[var3];
      }

      return var0;
   }

   public static <K> K[] reverse(K[] var0) {
      int var1 = var0.length;

      Object var3;
      for(int var2 = var1 / 2; var2-- != 0; var0[var2] = var3) {
         var3 = var0[var1 - var2 - 1];
         var0[var1 - var2 - 1] = var0[var2];
      }

      return var0;
   }

   public static <K> K[] reverse(K[] var0, int var1, int var2) {
      int var3 = var2 - var1;

      Object var5;
      for(int var4 = var3 / 2; var4-- != 0; var0[var1 + var4] = var5) {
         var5 = var0[var1 + var3 - var4 - 1];
         var0[var1 + var3 - var4 - 1] = var0[var1 + var4];
      }

      return var0;
   }

   private static final class ArrayHashStrategy<K> implements Hash.Strategy<K[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
         super();
      }

      public int hashCode(K[] var1) {
         return java.util.Arrays.hashCode(var1);
      }

      public boolean equals(K[] var1, K[] var2) {
         return java.util.Arrays.equals(var1, var2);
      }

      // $FF: synthetic method
      ArrayHashStrategy(Object var1) {
         this();
      }
   }

   protected static class ForkJoinQuickSort2<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final K[] x;
      private final K[] y;

      public ForkJoinQuickSort2(K[] var1, K[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var1;
         this.y = var2;
      }

      protected void compute() {
         Object[] var1 = this.x;
         Object[] var2 = this.y;
         int var3 = this.to - this.from;
         if (var3 < 8192) {
            ObjectArrays.quickSort(var1, var2, this.from, this.to);
         } else {
            int var4 = this.from + var3 / 2;
            int var5 = this.from;
            int var6 = this.to - 1;
            int var7 = var3 / 8;
            var5 = ObjectArrays.med3(var1, var2, var5, var5 + var7, var5 + 2 * var7);
            var4 = ObjectArrays.med3(var1, var2, var4 - var7, var4, var4 + var7);
            var6 = ObjectArrays.med3(var1, var2, var6 - 2 * var7, var6 - var7, var6);
            var4 = ObjectArrays.med3(var1, var2, var5, var4, var6);
            Object var8 = var1[var4];
            Object var9 = var2[var4];
            int var10 = this.from;
            int var11 = var10;
            int var12 = this.to - 1;
            int var13 = var12;

            while(true) {
               int var14;
               int var15;
               while(var11 > var12 || (var14 = (var15 = ((Comparable)var1[var11]).compareTo(var8)) == 0 ? ((Comparable)var2[var11]).compareTo(var9) : var15) > 0) {
                  for(; var12 >= var11 && (var14 = (var15 = ((Comparable)var1[var12]).compareTo(var8)) == 0 ? ((Comparable)var2[var12]).compareTo(var9) : var15) >= 0; --var12) {
                     if (var14 == 0) {
                        ObjectArrays.swap(var1, var2, var12, var13--);
                     }
                  }

                  if (var11 > var12) {
                     var7 = Math.min(var10 - this.from, var11 - var10);
                     ObjectArrays.swap(var1, var2, this.from, var11 - var7, var7);
                     var7 = Math.min(var13 - var12, this.to - var13 - 1);
                     ObjectArrays.swap(var1, var2, var11, this.to - var7, var7);
                     var7 = var11 - var10;
                     var14 = var13 - var12;
                     if (var7 > 1 && var14 > 1) {
                        invokeAll(new ObjectArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7), new ObjectArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to));
                     } else if (var7 > 1) {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSort2(var1, var2, this.from, this.from + var7)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSort2(var1, var2, this.to - var14, this.to)});
                     }

                     return;
                  }

                  ObjectArrays.swap(var1, var2, var11++, var12--);
               }

               if (var14 == 0) {
                  ObjectArrays.swap(var1, var2, var10++, var11);
               }

               ++var11;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortIndirect<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] perm;
      private final K[] x;

      public ForkJoinQuickSortIndirect(int[] var1, K[] var2, int var3, int var4) {
         super();
         this.from = var3;
         this.to = var4;
         this.x = var2;
         this.perm = var1;
      }

      protected void compute() {
         Object[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            ObjectArrays.quickSortIndirect(this.perm, var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = ObjectArrays.med3Indirect(this.perm, var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = ObjectArrays.med3Indirect(this.perm, var1, var3 - var6, var3, var3 + var6);
            var5 = ObjectArrays.med3Indirect(this.perm, var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = ObjectArrays.med3Indirect(this.perm, var1, var4, var3, var5);
            Object var7 = var1[this.perm[var3]];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = ((Comparable)var1[this.perm[var9]]).compareTo(var7)) > 0) {
                  for(; var10 >= var9 && (var12 = ((Comparable)var1[this.perm[var10]]).compareTo(var7)) >= 0; --var10) {
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
                        invokeAll(new ObjectArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6), new ObjectArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSortIndirect(this.perm, var1, this.to - var12, this.to)});
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

   protected static class ForkJoinQuickSort<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final K[] x;

      public ForkJoinQuickSort(K[] var1, int var2, int var3) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
      }

      protected void compute() {
         Object[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            ObjectArrays.quickSort(var1, this.from, this.to);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = ObjectArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6);
            var3 = ObjectArrays.med3(var1, var3 - var6, var3, var3 + var6);
            var5 = ObjectArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5);
            var3 = ObjectArrays.med3(var1, var4, var3, var5);
            Object var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = ((Comparable)var1[var9]).compareTo(var7)) > 0) {
                  for(; var10 >= var9 && (var12 = ((Comparable)var1[var10]).compareTo(var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        ObjectArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     ObjectArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     ObjectArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new ObjectArrays.ForkJoinQuickSort(var1, this.from, this.from + var6), new ObjectArrays.ForkJoinQuickSort(var1, this.to - var12, this.to));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSort(var1, this.from, this.from + var6)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSort(var1, this.to - var12, this.to)});
                     }

                     return;
                  }

                  ObjectArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  ObjectArrays.swap(var1, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortComp<K> extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final K[] x;
      private final Comparator<K> comp;

      public ForkJoinQuickSortComp(K[] var1, int var2, int var3, Comparator<K> var4) {
         super();
         this.from = var2;
         this.to = var3;
         this.x = var1;
         this.comp = var4;
      }

      protected void compute() {
         Object[] var1 = this.x;
         int var2 = this.to - this.from;
         if (var2 < 8192) {
            ObjectArrays.quickSort(var1, this.from, this.to, this.comp);
         } else {
            int var3 = this.from + var2 / 2;
            int var4 = this.from;
            int var5 = this.to - 1;
            int var6 = var2 / 8;
            var4 = ObjectArrays.med3(var1, var4, var4 + var6, var4 + 2 * var6, this.comp);
            var3 = ObjectArrays.med3(var1, var3 - var6, var3, var3 + var6, this.comp);
            var5 = ObjectArrays.med3(var1, var5 - 2 * var6, var5 - var6, var5, this.comp);
            var3 = ObjectArrays.med3(var1, var4, var3, var5, this.comp);
            Object var7 = var1[var3];
            int var8 = this.from;
            int var9 = var8;
            int var10 = this.to - 1;
            int var11 = var10;

            while(true) {
               int var12;
               while(var9 > var10 || (var12 = this.comp.compare(var1[var9], var7)) > 0) {
                  for(; var10 >= var9 && (var12 = this.comp.compare(var1[var10], var7)) >= 0; --var10) {
                     if (var12 == 0) {
                        ObjectArrays.swap(var1, var10, var11--);
                     }
                  }

                  if (var9 > var10) {
                     var6 = Math.min(var8 - this.from, var9 - var8);
                     ObjectArrays.swap(var1, this.from, var9 - var6, var6);
                     var6 = Math.min(var11 - var10, this.to - var11 - 1);
                     ObjectArrays.swap(var1, var9, this.to - var6, var6);
                     var6 = var9 - var8;
                     var12 = var11 - var10;
                     if (var6 > 1 && var12 > 1) {
                        invokeAll(new ObjectArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp), new ObjectArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp));
                     } else if (var6 > 1) {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSortComp(var1, this.from, this.from + var6, this.comp)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new ObjectArrays.ForkJoinQuickSortComp(var1, this.to - var12, this.to, this.comp)});
                     }

                     return;
                  }

                  ObjectArrays.swap(var1, var9++, var10--);
               }

               if (var12 == 0) {
                  ObjectArrays.swap(var1, var8++, var9);
               }

               ++var9;
            }
         }
      }
   }
}
