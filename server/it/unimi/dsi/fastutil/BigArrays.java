package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;

public class BigArrays {
   public static final int SEGMENT_SHIFT = 27;
   public static final int SEGMENT_SIZE = 134217728;
   public static final int SEGMENT_MASK = 134217727;
   private static final int SMALL = 7;
   private static final int MEDIUM = 40;

   protected BigArrays() {
      super();
   }

   public static int segment(long var0) {
      return (int)(var0 >>> 27);
   }

   public static int displacement(long var0) {
      return (int)(var0 & 134217727L);
   }

   public static long start(int var0) {
      return (long)var0 << 27;
   }

   public static long index(int var0, int var1) {
      return start(var0) + (long)var1;
   }

   public static void ensureFromTo(long var0, long var2, long var4) {
      if (var2 < 0L) {
         throw new ArrayIndexOutOfBoundsException("Start index (" + var2 + ") is negative");
      } else if (var2 > var4) {
         throw new IllegalArgumentException("Start index (" + var2 + ") is greater than end index (" + var4 + ")");
      } else if (var4 > var0) {
         throw new ArrayIndexOutOfBoundsException("End index (" + var4 + ") is greater than big-array length (" + var0 + ")");
      }
   }

   public static void ensureOffsetLength(long var0, long var2, long var4) {
      if (var2 < 0L) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + var2 + ") is negative");
      } else if (var4 < 0L) {
         throw new IllegalArgumentException("Length (" + var4 + ") is negative");
      } else if (var2 + var4 > var0) {
         throw new ArrayIndexOutOfBoundsException("Last index (" + (var2 + var4) + ") is greater than big-array length (" + var0 + ")");
      }
   }

   public static void ensureLength(long var0) {
      if (var0 < 0L) {
         throw new IllegalArgumentException("Negative big-array size: " + var0);
      } else if (var0 >= 288230376017494016L) {
         throw new IllegalArgumentException("Big-array size too big: " + var0);
      }
   }

   private static void inPlaceMerge(long var0, long var2, long var4, LongComparator var6, BigSwapper var7) {
      if (var0 < var2 && var2 < var4) {
         if (var4 - var0 == 2L) {
            if (var6.compare(var2, var0) < 0) {
               var7.swap(var0, var2);
            }

         } else {
            long var8;
            long var10;
            if (var2 - var0 > var4 - var2) {
               var8 = var0 + (var2 - var0) / 2L;
               var10 = lowerBound(var2, var4, var8, var6);
            } else {
               var10 = var2 + (var4 - var2) / 2L;
               var8 = upperBound(var0, var2, var10, var6);
            }

            if (var2 != var8 && var2 != var10) {
               long var18 = var8;
               long var20 = var2;

               while(var18 < --var20) {
                  var7.swap(var18++, var20);
               }

               var18 = var2;
               var20 = var10;

               while(var18 < --var20) {
                  var7.swap(var18++, var20);
               }

               var18 = var8;
               var20 = var10;

               while(var18 < --var20) {
                  var7.swap(var18++, var20);
               }
            }

            var2 = var8 + (var10 - var2);
            inPlaceMerge(var0, var8, var2, var6, var7);
            inPlaceMerge(var2, var10, var4, var6, var7);
         }
      }
   }

   private static long lowerBound(long var0, long var2, long var4, LongComparator var6) {
      long var7 = var2 - var0;

      while(var7 > 0L) {
         long var9 = var7 / 2L;
         long var11 = var0 + var9;
         if (var6.compare(var11, var4) < 0) {
            var0 = var11 + 1L;
            var7 -= var9 + 1L;
         } else {
            var7 = var9;
         }
      }

      return var0;
   }

   private static long med3(long var0, long var2, long var4, LongComparator var6) {
      int var7 = var6.compare(var0, var2);
      int var8 = var6.compare(var0, var4);
      int var9 = var6.compare(var2, var4);
      return var7 < 0 ? (var9 < 0 ? var2 : (var8 < 0 ? var4 : var0)) : (var9 > 0 ? var2 : (var8 > 0 ? var4 : var0));
   }

   public static void mergeSort(long var0, long var2, LongComparator var4, BigSwapper var5) {
      long var6 = var2 - var0;
      long var8;
      if (var6 >= 7L) {
         var8 = var0 + var2 >>> 1;
         mergeSort(var0, var8, var4, var5);
         mergeSort(var8, var2, var4, var5);
         if (var4.compare(var8 - 1L, var8) > 0) {
            inPlaceMerge(var0, var8, var2, var4, var5);
         }
      } else {
         for(var8 = var0; var8 < var2; ++var8) {
            for(long var10 = var8; var10 > var0 && var4.compare(var10 - 1L, var10) > 0; --var10) {
               var5.swap(var10, var10 - 1L);
            }
         }

      }
   }

   public static void quickSort(long var0, long var2, LongComparator var4, BigSwapper var5) {
      long var6 = var2 - var0;
      long var8;
      long var10;
      if (var6 < 7L) {
         for(var8 = var0; var8 < var2; ++var8) {
            for(var10 = var8; var10 > var0 && var4.compare(var10 - 1L, var10) > 0; --var10) {
               var5.swap(var10, var10 - 1L);
            }
         }

      } else {
         var8 = var0 + var6 / 2L;
         long var12;
         long var14;
         if (var6 > 7L) {
            var10 = var0;
            var12 = var2 - 1L;
            if (var6 > 40L) {
               var14 = var6 / 8L;
               var10 = med3(var0, var0 + var14, var0 + 2L * var14, var4);
               var8 = med3(var8 - var14, var8, var8 + var14, var4);
               var12 = med3(var12 - 2L * var14, var12 - var14, var12, var4);
            }

            var8 = med3(var10, var8, var12, var4);
         }

         var10 = var0;
         var12 = var0;
         var14 = var2 - 1L;
         long var16 = var14;

         while(true) {
            int var18;
            for(; var12 > var14 || (var18 = var4.compare(var12, var8)) > 0; var5.swap(var12++, var14--)) {
               for(; var14 >= var12 && (var18 = var4.compare(var14, var8)) >= 0; --var14) {
                  if (var18 == 0) {
                     if (var14 == var8) {
                        var8 = var16;
                     } else if (var16 == var8) {
                        var8 = var14;
                     }

                     var5.swap(var14, var16--);
                  }
               }

               if (var12 > var14) {
                  long var20 = var0 + var6;
                  long var22 = Math.min(var10 - var0, var12 - var10);
                  vecSwap(var5, var0, var12 - var22, var22);
                  var22 = Math.min(var16 - var14, var20 - var16 - 1L);
                  vecSwap(var5, var12, var20 - var22, var22);
                  if ((var22 = var12 - var10) > 1L) {
                     quickSort(var0, var0 + var22, var4, var5);
                  }

                  if ((var22 = var16 - var14) > 1L) {
                     quickSort(var20 - var22, var20, var4, var5);
                  }

                  return;
               }

               if (var12 == var8) {
                  var8 = var16;
               } else if (var14 == var8) {
                  var8 = var14;
               }
            }

            if (var18 == 0) {
               if (var10 == var8) {
                  var8 = var12;
               } else if (var12 == var8) {
                  var8 = var10;
               }

               var5.swap(var10++, var12);
            }

            ++var12;
         }
      }
   }

   private static long upperBound(long var0, long var2, long var4, LongComparator var6) {
      long var7 = var2 - var0;

      while(var7 > 0L) {
         long var9 = var7 / 2L;
         long var11 = var0 + var9;
         if (var6.compare(var4, var11) < 0) {
            var7 = var9;
         } else {
            var0 = var11 + 1L;
            var7 -= var9 + 1L;
         }
      }

      return var0;
   }

   private static void vecSwap(BigSwapper var0, long var1, long var3, long var5) {
      for(int var7 = 0; (long)var7 < var5; ++var3) {
         var0.swap(var1, var3);
         ++var7;
         ++var1;
      }

   }

   public static void main(String[] var0) {
      int[][] var1 = IntBigArrays.newBigArray(1L << Integer.parseInt(var0[0]));
      int var10 = 10;

      while(var10-- != 0) {
         long var8 = -System.currentTimeMillis();
         long var2 = 0L;

         long var11;
         for(var11 = IntBigArrays.length(var1); var11-- != 0L; var2 ^= var11 ^ (long)IntBigArrays.get(var1, var11)) {
         }

         if (var2 == 0L) {
            System.err.println();
         }

         System.out.println("Single loop: " + (var8 + System.currentTimeMillis()) + "ms");
         var8 = -System.currentTimeMillis();
         long var4 = 0L;
         int var16 = var1.length;

         int var13;
         while(var16-- != 0) {
            int[] var12 = var1[var16];

            for(var13 = var12.length; var13-- != 0; var4 ^= (long)var12[var13] ^ index(var16, var13)) {
            }
         }

         if (var4 == 0L) {
            System.err.println();
         }

         if (var2 != var4) {
            throw new AssertionError();
         }

         System.out.println("Double loop: " + (var8 + System.currentTimeMillis()) + "ms");
         long var6 = 0L;
         var11 = IntBigArrays.length(var1);
         var13 = var1.length;

         while(var13-- != 0) {
            int[] var14 = var1[var13];

            for(int var15 = var14.length; var15-- != 0; var4 ^= (long)var14[var15] ^ --var11) {
            }
         }

         if (var6 == 0L) {
            System.err.println();
         }

         if (var2 != var6) {
            throw new AssertionError();
         }

         System.out.println("Double loop (with additional index): " + (var8 + System.currentTimeMillis()) + "ms");
      }

   }
}
