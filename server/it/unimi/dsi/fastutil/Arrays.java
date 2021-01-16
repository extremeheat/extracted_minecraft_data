package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Arrays {
   public static final int MAX_ARRAY_SIZE = 2147483639;
   private static final int MERGESORT_NO_REC = 16;
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;

   private Arrays() {
      super();
   }

   public static void ensureFromTo(int var0, int var1, int var2) {
      if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException("Start index (" + var1 + ") is negative");
      } else if (var1 > var2) {
         throw new IllegalArgumentException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
      } else if (var2 > var0) {
         throw new ArrayIndexOutOfBoundsException("End index (" + var2 + ") is greater than array length (" + var0 + ")");
      }
   }

   public static void ensureOffsetLength(int var0, int var1, int var2) {
      if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var1 + ") is negative");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("Length (" + var2 + ") is negative");
      } else if (var1 + var2 > var0) {
         throw new ArrayIndexOutOfBoundsException("Last index (" + (var1 + var2) + ") is greater than array length (" + var0 + ")");
      }
   }

   private static void inPlaceMerge(int var0, int var1, int var2, IntComparator var3, Swapper var4) {
      if (var0 < var1 && var1 < var2) {
         if (var2 - var0 == 2) {
            if (var3.compare(var1, var0) < 0) {
               var4.swap(var0, var1);
            }

         } else {
            int var5;
            int var6;
            if (var1 - var0 > var2 - var1) {
               var5 = var0 + (var1 - var0) / 2;
               var6 = lowerBound(var1, var2, var5, var3);
            } else {
               var6 = var1 + (var2 - var1) / 2;
               var5 = upperBound(var0, var1, var6, var3);
            }

            if (var1 != var5 && var1 != var6) {
               int var10 = var5;
               int var11 = var1;

               label43:
               while(true) {
                  --var11;
                  if (var10 >= var11) {
                     var10 = var1;
                     var11 = var6;

                     while(true) {
                        --var11;
                        if (var10 >= var11) {
                           var10 = var5;
                           var11 = var6;

                           while(true) {
                              --var11;
                              if (var10 >= var11) {
                                 break label43;
                              }

                              var4.swap(var10++, var11);
                           }
                        }

                        var4.swap(var10++, var11);
                     }
                  }

                  var4.swap(var10++, var11);
               }
            }

            var1 = var5 + (var6 - var1);
            inPlaceMerge(var0, var5, var1, var3, var4);
            inPlaceMerge(var1, var6, var2, var3, var4);
         }
      }
   }

   private static int lowerBound(int var0, int var1, int var2, IntComparator var3) {
      int var4 = var1 - var0;

      while(var4 > 0) {
         int var5 = var4 / 2;
         int var6 = var0 + var5;
         if (var3.compare(var6, var2) < 0) {
            var0 = var6 + 1;
            var4 -= var5 + 1;
         } else {
            var4 = var5;
         }
      }

      return var0;
   }

   private static int upperBound(int var0, int var1, int var2, IntComparator var3) {
      int var4 = var1 - var0;

      while(var4 > 0) {
         int var5 = var4 / 2;
         int var6 = var0 + var5;
         if (var3.compare(var2, var6) < 0) {
            var4 = var5;
         } else {
            var0 = var6 + 1;
            var4 -= var5 + 1;
         }
      }

      return var0;
   }

   private static int med3(int var0, int var1, int var2, IntComparator var3) {
      int var4 = var3.compare(var0, var1);
      int var5 = var3.compare(var0, var2);
      int var6 = var3.compare(var1, var2);
      return var4 < 0 ? (var6 < 0 ? var1 : (var5 < 0 ? var2 : var0)) : (var6 > 0 ? var1 : (var5 > 0 ? var2 : var0));
   }

   public static void mergeSort(int var0, int var1, IntComparator var2, Swapper var3) {
      int var4 = var1 - var0;
      int var5;
      if (var4 >= 16) {
         var5 = var0 + var1 >>> 1;
         mergeSort(var0, var5, var2, var3);
         mergeSort(var5, var1, var2, var3);
         if (var2.compare(var5 - 1, var5) > 0) {
            inPlaceMerge(var0, var5, var1, var2, var3);
         }
      } else {
         for(var5 = var0; var5 < var1; ++var5) {
            for(int var6 = var5; var6 > var0 && var2.compare(var6 - 1, var6) > 0; --var6) {
               var3.swap(var6, var6 - 1);
            }
         }

      }
   }

   protected static void swap(Swapper var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var2) {
         var0.swap(var1, var2);
         ++var4;
         ++var1;
      }

   }

   public static void parallelQuickSort(int var0, int var1, IntComparator var2, Swapper var3) {
      ForkJoinPool var4 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      var4.invoke(new Arrays.ForkJoinGenericQuickSort(var0, var1, var2, var3));
      var4.shutdown();
   }

   public static void quickSort(int var0, int var1, IntComparator var2, Swapper var3) {
      int var4 = var1 - var0;
      int var5;
      int var6;
      if (var4 < 16) {
         for(var5 = var0; var5 < var1; ++var5) {
            for(var6 = var5; var6 > var0 && var2.compare(var6 - 1, var6) > 0; --var6) {
               var3.swap(var6, var6 - 1);
            }
         }

      } else {
         var5 = var0 + var4 / 2;
         var6 = var0;
         int var7 = var1 - 1;
         int var8;
         if (var4 > 128) {
            var8 = var4 / 8;
            var6 = med3(var0, var0 + var8, var0 + 2 * var8, var2);
            var5 = med3(var5 - var8, var5, var5 + var8, var2);
            var7 = med3(var7 - 2 * var8, var7 - var8, var7, var2);
         }

         var5 = med3(var6, var5, var7, var2);
         var8 = var0;
         int var9 = var0;
         int var10 = var1 - 1;
         int var11 = var10;

         while(true) {
            int var12;
            for(; var9 > var10 || (var12 = var2.compare(var9, var5)) > 0; var3.swap(var9++, var10--)) {
               for(; var10 >= var9 && (var12 = var2.compare(var10, var5)) >= 0; --var10) {
                  if (var12 == 0) {
                     if (var10 == var5) {
                        var5 = var11;
                     } else if (var11 == var5) {
                        var5 = var10;
                     }

                     var3.swap(var10, var11--);
                  }
               }

               if (var9 > var10) {
                  var12 = Math.min(var8 - var0, var9 - var8);
                  swap(var3, var0, var9 - var12, var12);
                  var12 = Math.min(var11 - var10, var1 - var11 - 1);
                  swap(var3, var9, var1 - var12, var12);
                  if ((var12 = var9 - var8) > 1) {
                     quickSort(var0, var0 + var12, var2, var3);
                  }

                  if ((var12 = var11 - var10) > 1) {
                     quickSort(var1 - var12, var1, var2, var3);
                  }

                  return;
               }

               if (var9 == var5) {
                  var5 = var11;
               } else if (var10 == var5) {
                  var5 = var10;
               }
            }

            if (var12 == 0) {
               if (var8 == var5) {
                  var5 = var9;
               } else if (var9 == var5) {
                  var5 = var8;
               }

               var3.swap(var8++, var9);
            }

            ++var9;
         }
      }
   }

   protected static class ForkJoinGenericQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final IntComparator comp;
      private final Swapper swapper;

      public ForkJoinGenericQuickSort(int var1, int var2, IntComparator var3, Swapper var4) {
         super();
         this.from = var1;
         this.to = var2;
         this.comp = var3;
         this.swapper = var4;
      }

      protected void compute() {
         int var1 = this.to - this.from;
         if (var1 < 8192) {
            Arrays.quickSort(this.from, this.to, this.comp, this.swapper);
         } else {
            int var2 = this.from + var1 / 2;
            int var3 = this.from;
            int var4 = this.to - 1;
            int var5 = var1 / 8;
            var3 = Arrays.med3(var3, var3 + var5, var3 + 2 * var5, this.comp);
            var2 = Arrays.med3(var2 - var5, var2, var2 + var5, this.comp);
            var4 = Arrays.med3(var4 - 2 * var5, var4 - var5, var4, this.comp);
            var2 = Arrays.med3(var3, var2, var4, this.comp);
            int var6 = this.from;
            int var7 = var6;
            int var8 = this.to - 1;
            int var9 = var8;

            while(true) {
               int var10;
               for(; var7 > var8 || (var10 = this.comp.compare(var7, var2)) > 0; this.swapper.swap(var7++, var8--)) {
                  for(; var8 >= var7 && (var10 = this.comp.compare(var8, var2)) >= 0; --var8) {
                     if (var10 == 0) {
                        if (var8 == var2) {
                           var2 = var9;
                        } else if (var9 == var2) {
                           var2 = var8;
                        }

                        this.swapper.swap(var8, var9--);
                     }
                  }

                  if (var7 > var8) {
                     var5 = Math.min(var6 - this.from, var7 - var6);
                     Arrays.swap(this.swapper, this.from, var7 - var5, var5);
                     var5 = Math.min(var9 - var8, this.to - var9 - 1);
                     Arrays.swap(this.swapper, var7, this.to - var5, var5);
                     var5 = var7 - var6;
                     var10 = var9 - var8;
                     if (var5 > 1 && var10 > 1) {
                        invokeAll(new Arrays.ForkJoinGenericQuickSort(this.from, this.from + var5, this.comp, this.swapper), new Arrays.ForkJoinGenericQuickSort(this.to - var10, this.to, this.comp, this.swapper));
                     } else if (var5 > 1) {
                        invokeAll(new ForkJoinTask[]{new Arrays.ForkJoinGenericQuickSort(this.from, this.from + var5, this.comp, this.swapper)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new Arrays.ForkJoinGenericQuickSort(this.to - var10, this.to, this.comp, this.swapper)});
                     }

                     return;
                  }

                  if (var7 == var2) {
                     var2 = var9;
                  } else if (var8 == var2) {
                     var2 = var8;
                  }
               }

               if (var10 == 0) {
                  if (var6 == var2) {
                     var2 = var7;
                  } else if (var7 == var2) {
                     var2 = var6;
                  }

                  this.swapper.swap(var6++, var7);
               }

               ++var7;
            }
         }
      }
   }
}
