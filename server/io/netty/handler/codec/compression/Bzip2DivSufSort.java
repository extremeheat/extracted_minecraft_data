package io.netty.handler.codec.compression;

final class Bzip2DivSufSort {
   private static final int STACK_SIZE = 64;
   private static final int BUCKET_A_SIZE = 256;
   private static final int BUCKET_B_SIZE = 65536;
   private static final int SS_BLOCKSIZE = 1024;
   private static final int INSERTIONSORT_THRESHOLD = 8;
   private static final int[] LOG_2_TABLE = new int[]{-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
   private final int[] SA;
   private final byte[] T;
   private final int n;

   Bzip2DivSufSort(byte[] var1, int[] var2, int var3) {
      super();
      this.T = var1;
      this.SA = var2;
      this.n = var3;
   }

   private static void swapElements(int[] var0, int var1, int[] var2, int var3) {
      int var4 = var0[var1];
      var0[var1] = var2[var3];
      var2[var3] = var4;
   }

   private int ssCompare(int var1, int var2, int var3) {
      int[] var4 = this.SA;
      byte[] var5 = this.T;
      int var6 = var4[var1 + 1] + 2;
      int var7 = var4[var2 + 1] + 2;
      int var8 = var3 + var4[var1];

      int var9;
      for(var9 = var3 + var4[var2]; var8 < var6 && var9 < var7 && var5[var8] == var5[var9]; ++var9) {
         ++var8;
      }

      return var8 < var6 ? (var9 < var7 ? (var5[var8] & 255) - (var5[var9] & 255) : 1) : (var9 < var7 ? -1 : 0);
   }

   private int ssCompareLast(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;
      byte[] var7 = this.T;
      int var8 = var4 + var6[var2];
      int var9 = var4 + var6[var3];
      int var10 = var5;

      int var11;
      for(var11 = var6[var3 + 1] + 2; var8 < var10 && var9 < var11 && var7[var8] == var7[var9]; ++var9) {
         ++var8;
      }

      if (var8 < var10) {
         return var9 < var11 ? (var7[var8] & 255) - (var7[var9] & 255) : 1;
      } else if (var9 == var11) {
         return 1;
      } else {
         var8 %= var5;

         for(var10 = var6[var1] + 2; var8 < var10 && var9 < var11 && var7[var8] == var7[var9]; ++var9) {
            ++var8;
         }

         return var8 < var10 ? (var9 < var11 ? (var7[var8] & 255) - (var7[var9] & 255) : 1) : (var9 < var11 ? -1 : 0);
      }
   }

   private void ssInsertionSort(int var1, int var2, int var3, int var4) {
      int[] var5 = this.SA;

      for(int var6 = var3 - 2; var2 <= var6; --var6) {
         int var8 = var5[var6];
         int var7 = var6 + 1;

         int var9;
         while(0 < (var9 = this.ssCompare(var1 + var8, var1 + var5[var7], var4))) {
            do {
               var5[var7 - 1] = var5[var7];
               ++var7;
            } while(var7 < var3 && var5[var7] < 0);

            if (var3 <= var7) {
               break;
            }
         }

         if (var9 == 0) {
            var5[var7] = ~var5[var7];
         }

         var5[var7 - 1] = var8;
      }

   }

   private void ssFixdown(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;
      byte[] var7 = this.T;
      int var10 = var6[var3 + var4];

      int var8;
      int var9;
      for(int var11 = var7[var1 + var6[var2 + var10]] & 255; (var8 = 2 * var4 + 1) < var5; var4 = var9) {
         int var12 = var7[var1 + var6[var2 + var6[var3 + (var9 = var8++)]]] & 255;
         int var13;
         if (var12 < (var13 = var7[var1 + var6[var2 + var6[var3 + var8]]] & 255)) {
            var9 = var8;
            var12 = var13;
         }

         if (var12 <= var11) {
            break;
         }

         var6[var3 + var4] = var6[var3 + var9];
      }

      var6[var3 + var4] = var10;
   }

   private void ssHeapSort(int var1, int var2, int var3, int var4) {
      int[] var5 = this.SA;
      byte[] var6 = this.T;
      int var8 = var4;
      if (var4 % 2 == 0) {
         var8 = var4 - 1;
         if ((var6[var1 + var5[var2 + var5[var3 + var8 / 2]]] & 255) < (var6[var1 + var5[var2 + var5[var3 + var8]]] & 255)) {
            swapElements(var5, var3 + var8, var5, var3 + var8 / 2);
         }
      }

      int var7;
      for(var7 = var8 / 2 - 1; 0 <= var7; --var7) {
         this.ssFixdown(var1, var2, var3, var7, var8);
      }

      if (var4 % 2 == 0) {
         swapElements(var5, var3, var5, var3 + var8);
         this.ssFixdown(var1, var2, var3, 0, var8);
      }

      for(var7 = var8 - 1; 0 < var7; --var7) {
         int var9 = var5[var3];
         var5[var3] = var5[var3 + var7];
         this.ssFixdown(var1, var2, var3, 0, var7);
         var5[var3 + var7] = var9;
      }

   }

   private int ssMedian3(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;
      byte[] var7 = this.T;
      int var8 = var7[var1 + var6[var2 + var6[var3]]] & 255;
      int var9 = var7[var1 + var6[var2 + var6[var4]]] & 255;
      int var10 = var7[var1 + var6[var2 + var6[var5]]] & 255;
      if (var8 > var9) {
         int var11 = var3;
         var3 = var4;
         var4 = var11;
         int var12 = var8;
         var8 = var9;
         var9 = var12;
      }

      if (var9 > var10) {
         return var8 > var10 ? var3 : var5;
      } else {
         return var4;
      }
   }

   private int ssMedian5(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int[] var8 = this.SA;
      byte[] var9 = this.T;
      int var10 = var9[var1 + var8[var2 + var8[var3]]] & 255;
      int var11 = var9[var1 + var8[var2 + var8[var4]]] & 255;
      int var12 = var9[var1 + var8[var2 + var8[var5]]] & 255;
      int var13 = var9[var1 + var8[var2 + var8[var6]]] & 255;
      int var14 = var9[var1 + var8[var2 + var8[var7]]] & 255;
      int var15;
      int var16;
      if (var11 > var12) {
         var15 = var4;
         var4 = var5;
         var5 = var15;
         var16 = var11;
         var11 = var12;
         var12 = var16;
      }

      if (var13 > var14) {
         var15 = var6;
         var6 = var7;
         var7 = var15;
         var16 = var13;
         var13 = var14;
         var14 = var16;
      }

      if (var11 > var13) {
         var6 = var4;
         var13 = var11;
         var15 = var5;
         var5 = var7;
         var7 = var15;
         var16 = var12;
         var12 = var14;
         var14 = var16;
      }

      if (var10 > var12) {
         var15 = var3;
         var3 = var5;
         var5 = var15;
         var16 = var10;
         var10 = var12;
         var12 = var16;
      }

      if (var10 > var13) {
         var6 = var3;
         var13 = var10;
         var5 = var7;
         var12 = var14;
      }

      return var12 > var13 ? var6 : var5;
   }

   private int ssPivot(int var1, int var2, int var3, int var4) {
      int var6 = var4 - var3;
      int var5 = var3 + var6 / 2;
      if (var6 <= 512) {
         if (var6 <= 32) {
            return this.ssMedian3(var1, var2, var3, var5, var4 - 1);
         } else {
            var6 >>= 2;
            return this.ssMedian5(var1, var2, var3, var3 + var6, var5, var4 - 1 - var6, var4 - 1);
         }
      } else {
         var6 >>= 3;
         return this.ssMedian3(var1, var2, this.ssMedian3(var1, var2, var3, var3 + var6, var3 + (var6 << 1)), this.ssMedian3(var1, var2, var5 - var6, var5, var5 + var6), this.ssMedian3(var1, var2, var4 - 1 - (var6 << 1), var4 - 1 - var6, var4 - 1));
      }
   }

   private static int ssLog(int var0) {
      return (var0 & '\uff00') != 0 ? 8 + LOG_2_TABLE[var0 >> 8 & 255] : LOG_2_TABLE[var0 & 255];
   }

   private int ssSubstringPartition(int var1, int var2, int var3, int var4) {
      int[] var5 = this.SA;
      int var6 = var2 - 1;
      int var7 = var3;

      while(true) {
         while(true) {
            ++var6;
            if (var6 < var7 && var5[var1 + var5[var6]] + var4 >= var5[var1 + var5[var6] + 1] + 1) {
               var5[var6] = ~var5[var6];
            } else {
               --var7;

               while(var6 < var7 && var5[var1 + var5[var7]] + var4 < var5[var1 + var5[var7] + 1] + 1) {
                  --var7;
               }

               if (var7 <= var6) {
                  if (var2 < var6) {
                     var5[var2] = ~var5[var2];
                  }

                  return var6;
               }

               int var8 = ~var5[var7];
               var5[var7] = var5[var6];
               var5[var6] = var8;
            }
         }
      }
   }

   private void ssMultiKeyIntroSort(int var1, int var2, int var3, int var4) {
      int[] var5 = this.SA;
      byte[] var6 = this.T;
      Bzip2DivSufSort.StackEntry[] var7 = new Bzip2DivSufSort.StackEntry[64];
      int var20 = 0;
      int var17 = 0;
      int var18 = ssLog(var3 - var2);

      while(true) {
         while(var3 - var2 > 8) {
            int var8 = var4;
            if (var18-- == 0) {
               this.ssHeapSort(var4, var1, var2, var3 - var2);
            }

            int var9;
            int var19;
            if (var18 < 0) {
               var9 = var2 + 1;

               for(var19 = var6[var4 + var5[var1 + var5[var2]]] & 255; var9 < var3; ++var9) {
                  if ((var20 = var6[var8 + var5[var1 + var5[var9]]] & 255) != var19) {
                     if (1 < var9 - var2) {
                        break;
                     }

                     var19 = var20;
                     var2 = var9;
                  }
               }

               if ((var6[var8 + var5[var1 + var5[var2]] - 1] & 255) < var19) {
                  var2 = this.ssSubstringPartition(var1, var2, var9, var4);
               }

               if (var9 - var2 <= var3 - var9) {
                  if (1 < var9 - var2) {
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var9, var3, var4, -1);
                     var3 = var9;
                     ++var4;
                     var18 = ssLog(var9 - var2);
                  } else {
                     var2 = var9;
                     var18 = -1;
                  }
               } else if (1 < var3 - var9) {
                  var7[var17++] = new Bzip2DivSufSort.StackEntry(var2, var9, var4 + 1, ssLog(var9 - var2));
                  var2 = var9;
                  var18 = -1;
               } else {
                  var3 = var9;
                  ++var4;
                  var18 = ssLog(var9 - var2);
               }
            } else {
               var9 = this.ssPivot(var4, var1, var2, var3);
               var19 = var6[var4 + var5[var1 + var5[var9]]] & 255;
               swapElements(var5, var2, var5, var9);

               int var10;
               for(var10 = var2 + 1; var10 < var3 && (var20 = var6[var8 + var5[var1 + var5[var10]]] & 255) == var19; ++var10) {
               }

               var9 = var10;
               if (var10 < var3 && var20 < var19) {
                  while(true) {
                     ++var10;
                     if (var10 >= var3 || (var20 = var6[var8 + var5[var1 + var5[var10]]] & 255) > var19) {
                        break;
                     }

                     if (var20 == var19) {
                        swapElements(var5, var10, var5, var9);
                        ++var9;
                     }
                  }
               }

               int var11;
               for(var11 = var3 - 1; var10 < var11 && (var20 = var6[var8 + var5[var1 + var5[var11]]] & 255) == var19; --var11) {
               }

               int var12 = var11;
               if (var10 < var11 && var20 > var19) {
                  while(true) {
                     --var11;
                     if (var10 >= var11 || (var20 = var6[var8 + var5[var1 + var5[var11]]] & 255) < var19) {
                        break;
                     }

                     if (var20 == var19) {
                        swapElements(var5, var11, var5, var12);
                        --var12;
                     }
                  }
               }

               label161:
               while(var10 < var11) {
                  swapElements(var5, var10, var5, var11);

                  while(true) {
                     ++var10;
                     if (var10 >= var11 || (var20 = var6[var8 + var5[var1 + var5[var10]]] & 255) > var19) {
                        while(true) {
                           --var11;
                           if (var10 >= var11 || (var20 = var6[var8 + var5[var1 + var5[var11]]] & 255) < var19) {
                              continue label161;
                           }

                           if (var20 == var19) {
                              swapElements(var5, var11, var5, var12);
                              --var12;
                           }
                        }
                     }

                     if (var20 == var19) {
                        swapElements(var5, var10, var5, var9);
                        ++var9;
                     }
                  }
               }

               if (var9 > var12) {
                  ++var18;
                  if ((var6[var8 + var5[var1 + var5[var2]] - 1] & 255) < var19) {
                     var2 = this.ssSubstringPartition(var1, var2, var3, var4);
                     var18 = ssLog(var3 - var2);
                  }

                  ++var4;
               } else {
                  var11 = var10 - 1;
                  int var15;
                  int var16;
                  if ((var15 = var9 - var2) > (var16 = var10 - var9)) {
                     var15 = var16;
                  }

                  int var13 = var2;

                  int var14;
                  for(var14 = var10 - var15; 0 < var15; ++var14) {
                     swapElements(var5, var13, var5, var14);
                     --var15;
                     ++var13;
                  }

                  if ((var15 = var12 - var11) > (var16 = var3 - var12 - 1)) {
                     var15 = var16;
                  }

                  var13 = var10;

                  for(var14 = var3 - var15; 0 < var15; ++var14) {
                     swapElements(var5, var13, var5, var14);
                     --var15;
                     ++var13;
                  }

                  var9 = var2 + (var10 - var9);
                  var11 = var3 - (var12 - var11);
                  var10 = var19 <= (var6[var8 + var5[var1 + var5[var9]] - 1] & 255) ? var9 : this.ssSubstringPartition(var1, var9, var11, var4);
                  if (var9 - var2 <= var3 - var11) {
                     if (var3 - var11 <= var11 - var10) {
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var10, var11, var4 + 1, ssLog(var11 - var10));
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var11, var3, var4, var18);
                        var3 = var9;
                     } else if (var9 - var2 <= var11 - var10) {
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var11, var3, var4, var18);
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var10, var11, var4 + 1, ssLog(var11 - var10));
                        var3 = var9;
                     } else {
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var11, var3, var4, var18);
                        var7[var17++] = new Bzip2DivSufSort.StackEntry(var2, var9, var4, var18);
                        var2 = var10;
                        var3 = var11;
                        ++var4;
                        var18 = ssLog(var11 - var10);
                     }
                  } else if (var9 - var2 <= var11 - var10) {
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var10, var11, var4 + 1, ssLog(var11 - var10));
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var2, var9, var4, var18);
                     var2 = var11;
                  } else if (var3 - var11 <= var11 - var10) {
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var2, var9, var4, var18);
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var10, var11, var4 + 1, ssLog(var11 - var10));
                     var2 = var11;
                  } else {
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var2, var9, var4, var18);
                     var7[var17++] = new Bzip2DivSufSort.StackEntry(var11, var3, var4, var18);
                     var2 = var10;
                     var3 = var11;
                     ++var4;
                     var18 = ssLog(var11 - var10);
                  }
               }
            }
         }

         if (1 < var3 - var2) {
            this.ssInsertionSort(var1, var2, var3, var4);
         }

         if (var17 == 0) {
            return;
         }

         --var17;
         Bzip2DivSufSort.StackEntry var21 = var7[var17];
         var2 = var21.a;
         var3 = var21.b;
         var4 = var21.c;
         var18 = var21.d;
      }
   }

   private static void ssBlockSwap(int[] var0, int var1, int[] var2, int var3, int var4) {
      int var7 = var4;
      int var5 = var1;

      for(int var6 = var3; 0 < var7; ++var6) {
         swapElements(var0, var5, var2, var6);
         --var7;
         ++var5;
      }

   }

   private void ssMergeForward(int var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      int[] var8 = this.SA;
      int var9 = var3 + (var5 - var4) - 1;
      ssBlockSwap(var2, var3, var8, var4, var5 - var4);
      int var13 = var8[var4];
      int var10 = var4;
      int var11 = var3;
      int var12 = var5;

      while(true) {
         while(true) {
            int var14 = this.ssCompare(var1 + var2[var11], var1 + var8[var12], var7);
            if (var14 < 0) {
               while(true) {
                  var8[var10++] = var2[var11];
                  if (var9 <= var11) {
                     var2[var11] = var13;
                     return;
                  }

                  var2[var11++] = var8[var10];
                  if (var2[var11] >= 0) {
                     break;
                  }
               }
            } else if (var14 > 0) {
               while(true) {
                  var8[var10++] = var8[var12];
                  var8[var12++] = var8[var10];
                  if (var6 <= var12) {
                     while(var11 < var9) {
                        var8[var10++] = var2[var11];
                        var2[var11++] = var8[var10];
                     }

                     var8[var10] = var2[var11];
                     var2[var11] = var13;
                     return;
                  }

                  if (var8[var12] >= 0) {
                     break;
                  }
               }
            } else {
               var8[var12] = ~var8[var12];

               do {
                  var8[var10++] = var2[var11];
                  if (var9 <= var11) {
                     var2[var11] = var13;
                     return;
                  }

                  var2[var11++] = var8[var10];
               } while(var2[var11] < 0);

               while(true) {
                  var8[var10++] = var8[var12];
                  var8[var12++] = var8[var10];
                  if (var6 <= var12) {
                     while(var11 < var9) {
                        var8[var10++] = var2[var11];
                        var2[var11++] = var8[var10];
                     }

                     var8[var10] = var2[var11];
                     var2[var11] = var13;
                     return;
                  }

                  if (var8[var12] >= 0) {
                     break;
                  }
               }
            }
         }
      }
   }

   private void ssMergeBackward(int var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      int[] var8 = this.SA;
      int var11 = var3 + (var6 - var5);
      ssBlockSwap(var2, var3, var8, var5, var6 - var5);
      int var17 = 0;
      int var9;
      if (var2[var11 - 1] < 0) {
         var17 |= 1;
         var9 = var1 + ~var2[var11 - 1];
      } else {
         var9 = var1 + var2[var11 - 1];
      }

      int var10;
      if (var8[var5 - 1] < 0) {
         var17 |= 2;
         var10 = var1 + ~var8[var5 - 1];
      } else {
         var10 = var1 + var8[var5 - 1];
      }

      int var15 = var8[var6 - 1];
      int var12 = var6 - 1;
      int var13 = var11 - 1;
      int var14 = var5 - 1;

      while(true) {
         while(true) {
            int var16 = this.ssCompare(var9, var10, var7);
            if (var16 > 0) {
               if ((var17 & 1) != 0) {
                  do {
                     var8[var12--] = var2[var13];
                     var2[var13--] = var8[var12];
                  } while(var2[var13] < 0);

                  var17 ^= 1;
               }

               var8[var12--] = var2[var13];
               if (var13 <= var3) {
                  var2[var13] = var15;
                  return;
               }

               var2[var13--] = var8[var12];
               if (var2[var13] < 0) {
                  var17 |= 1;
                  var9 = var1 + ~var2[var13];
               } else {
                  var9 = var1 + var2[var13];
               }
            } else if (var16 < 0) {
               if ((var17 & 2) != 0) {
                  do {
                     var8[var12--] = var8[var14];
                     var8[var14--] = var8[var12];
                  } while(var8[var14] < 0);

                  var17 ^= 2;
               }

               var8[var12--] = var8[var14];
               var8[var14--] = var8[var12];
               if (var14 < var4) {
                  while(var3 < var13) {
                     var8[var12--] = var2[var13];
                     var2[var13--] = var8[var12];
                  }

                  var8[var12] = var2[var13];
                  var2[var13] = var15;
                  return;
               }

               if (var8[var14] < 0) {
                  var17 |= 2;
                  var10 = var1 + ~var8[var14];
               } else {
                  var10 = var1 + var8[var14];
               }
            } else {
               if ((var17 & 1) != 0) {
                  do {
                     var8[var12--] = var2[var13];
                     var2[var13--] = var8[var12];
                  } while(var2[var13] < 0);

                  var17 ^= 1;
               }

               var8[var12--] = ~var2[var13];
               if (var13 <= var3) {
                  var2[var13] = var15;
                  return;
               }

               var2[var13--] = var8[var12];
               if ((var17 & 2) != 0) {
                  do {
                     var8[var12--] = var8[var14];
                     var8[var14--] = var8[var12];
                  } while(var8[var14] < 0);

                  var17 ^= 2;
               }

               var8[var12--] = var8[var14];
               var8[var14--] = var8[var12];
               if (var14 < var4) {
                  while(var3 < var13) {
                     var8[var12--] = var2[var13];
                     var2[var13--] = var8[var12];
                  }

                  var8[var12] = var2[var13];
                  var2[var13] = var15;
                  return;
               }

               if (var2[var13] < 0) {
                  var17 |= 1;
                  var9 = var1 + ~var2[var13];
               } else {
                  var9 = var1 + var2[var13];
               }

               if (var8[var14] < 0) {
                  var17 |= 2;
                  var10 = var1 + ~var8[var14];
               } else {
                  var10 = var1 + var8[var14];
               }
            }
         }
      }
   }

   private static int getIDX(int var0) {
      return 0 <= var0 ? var0 : ~var0;
   }

   private void ssMergeCheckEqual(int var1, int var2, int var3) {
      int[] var4 = this.SA;
      if (0 <= var4[var3] && this.ssCompare(var1 + getIDX(var4[var3 - 1]), var1 + var4[var3], var2) == 0) {
         var4[var3] = ~var4[var3];
      }

   }

   private void ssMerge(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7, int var8) {
      int[] var9 = this.SA;
      Bzip2DivSufSort.StackEntry[] var10 = new Bzip2DivSufSort.StackEntry[64];
      int var17 = 0;
      int var16 = 0;

      while(true) {
         Bzip2DivSufSort.StackEntry var19;
         while(var4 - var3 > var7) {
            if (var3 - var2 <= var7) {
               if (var2 < var3) {
                  this.ssMergeForward(var1, var5, var6, var2, var3, var4, var8);
               }

               if ((var17 & 1) != 0) {
                  this.ssMergeCheckEqual(var1, var8, var2);
               }

               if ((var17 & 2) != 0) {
                  this.ssMergeCheckEqual(var1, var8, var4);
               }

               if (var16 == 0) {
                  return;
               }

               --var16;
               var19 = var10[var16];
               var2 = var19.a;
               var3 = var19.b;
               var4 = var19.c;
               var17 = var19.d;
            } else {
               int var13 = 0;
               int var14 = Math.min(var3 - var2, var4 - var3);

               for(int var15 = var14 >> 1; 0 < var14; var15 >>= 1) {
                  if (this.ssCompare(var1 + getIDX(var9[var3 + var13 + var15]), var1 + getIDX(var9[var3 - var13 - var15 - 1]), var8) < 0) {
                     var13 += var15 + 1;
                     var15 -= var14 & 1 ^ 1;
                  }

                  var14 = var15;
               }

               if (0 >= var13) {
                  if ((var17 & 1) != 0) {
                     this.ssMergeCheckEqual(var1, var8, var2);
                  }

                  this.ssMergeCheckEqual(var1, var8, var3);
                  if ((var17 & 2) != 0) {
                     this.ssMergeCheckEqual(var1, var8, var4);
                  }

                  if (var16 == 0) {
                     return;
                  }

                  --var16;
                  var19 = var10[var16];
                  var2 = var19.a;
                  var3 = var19.b;
                  var4 = var19.c;
                  var17 = var19.d;
               } else {
                  ssBlockSwap(var9, var3 - var13, var9, var3, var13);
                  int var12 = var3;
                  int var11 = var3;
                  int var18 = 0;
                  if (var3 + var13 < var4) {
                     if (var9[var3 + var13] < 0) {
                        while(var9[var11 - 1] < 0) {
                           --var11;
                        }

                        var9[var3 + var13] = ~var9[var3 + var13];
                     }

                     for(var12 = var3; var9[var12] < 0; ++var12) {
                     }

                     var18 = 1;
                  }

                  if (var11 - var2 <= var4 - var12) {
                     var10[var16++] = new Bzip2DivSufSort.StackEntry(var12, var3 + var13, var4, var17 & 2 | var18 & 1);
                     var3 -= var13;
                     var4 = var11;
                     var17 &= 1;
                  } else {
                     if (var11 == var3 && var3 == var12) {
                        var18 <<= 1;
                     }

                     var10[var16++] = new Bzip2DivSufSort.StackEntry(var2, var3 - var13, var11, var17 & 1 | var18 & 2);
                     var2 = var12;
                     var3 += var13;
                     var17 = var17 & 2 | var18 & 1;
                  }
               }
            }
         }

         if (var2 < var3 && var3 < var4) {
            this.ssMergeBackward(var1, var5, var6, var2, var3, var4, var8);
         }

         if ((var17 & 1) != 0) {
            this.ssMergeCheckEqual(var1, var8, var2);
         }

         if ((var17 & 2) != 0) {
            this.ssMergeCheckEqual(var1, var8, var4);
         }

         if (var16 == 0) {
            return;
         }

         --var16;
         var19 = var10[var16];
         var2 = var19.a;
         var3 = var19.b;
         var4 = var19.c;
         var17 = var19.d;
      }
   }

   private void subStringSort(int var1, int var2, int var3, int[] var4, int var5, int var6, int var7, boolean var8, int var9) {
      int[] var10 = this.SA;
      if (var8) {
         ++var2;
      }

      int var11 = var2;

      int var15;
      int var17;
      for(var15 = 0; var11 + 1024 < var3; ++var15) {
         this.ssMultiKeyIntroSort(var1, var11, var11 + 1024, var7);
         int[] var13 = var10;
         int var14 = var11 + 1024;
         int var18 = var3 - (var11 + 1024);
         if (var18 <= var6) {
            var18 = var6;
            var13 = var4;
            var14 = var5;
         }

         int var12 = var11;
         var17 = 1024;

         for(int var16 = var15; (var16 & 1) != 0; var16 >>>= 1) {
            this.ssMerge(var1, var12 - var17, var12, var12 + var17, var13, var14, var18, var7);
            var12 -= var17;
            var17 <<= 1;
         }

         var11 += 1024;
      }

      this.ssMultiKeyIntroSort(var1, var11, var3, var7);

      for(var17 = 1024; var15 != 0; var15 >>= 1) {
         if ((var15 & 1) != 0) {
            this.ssMerge(var1, var11 - var17, var11, var3, var4, var5, var6, var7);
            var11 -= var17;
         }

         var17 <<= 1;
      }

      if (var8) {
         var11 = var2;
         var15 = var10[var2 - 1];

         int var19;
         for(var19 = 1; var11 < var3 && (var10[var11] < 0 || 0 < (var19 = this.ssCompareLast(var1, var1 + var15, var1 + var10[var11], var7, var9))); ++var11) {
            var10[var11 - 1] = var10[var11];
         }

         if (var19 == 0) {
            var10[var11] = ~var10[var11];
         }

         var10[var11 - 1] = var15;
      }

   }

   private int trGetC(int var1, int var2, int var3, int var4) {
      return var2 + var4 < var3 ? this.SA[var2 + var4] : this.SA[var1 + (var2 - var1 + var4) % (var3 - var1)];
   }

   private void trFixdown(int var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = this.SA;
      int var10 = var7[var4 + var5];

      int var8;
      int var9;
      for(int var11 = this.trGetC(var1, var2, var3, var10); (var8 = 2 * var5 + 1) < var6; var5 = var9) {
         var9 = var8++;
         int var12 = this.trGetC(var1, var2, var3, var7[var4 + var9]);
         int var13;
         if (var12 < (var13 = this.trGetC(var1, var2, var3, var7[var4 + var8]))) {
            var9 = var8;
            var12 = var13;
         }

         if (var12 <= var11) {
            break;
         }

         var7[var4 + var5] = var7[var4 + var9];
      }

      var7[var4 + var5] = var10;
   }

   private void trHeapSort(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;
      int var8 = var5;
      if (var5 % 2 == 0) {
         var8 = var5 - 1;
         if (this.trGetC(var1, var2, var3, var6[var4 + var8 / 2]) < this.trGetC(var1, var2, var3, var6[var4 + var8])) {
            swapElements(var6, var4 + var8, var6, var4 + var8 / 2);
         }
      }

      int var7;
      for(var7 = var8 / 2 - 1; 0 <= var7; --var7) {
         this.trFixdown(var1, var2, var3, var4, var7, var8);
      }

      if (var5 % 2 == 0) {
         swapElements(var6, var4, var6, var4 + var8);
         this.trFixdown(var1, var2, var3, var4, 0, var8);
      }

      for(var7 = var8 - 1; 0 < var7; --var7) {
         int var9 = var6[var4];
         var6[var4] = var6[var4 + var7];
         this.trFixdown(var1, var2, var3, var4, 0, var7);
         var6[var4 + var7] = var9;
      }

   }

   private void trInsertionSort(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;

      for(int var7 = var4 + 1; var7 < var5; ++var7) {
         int var9 = var6[var7];
         int var8 = var7 - 1;

         int var10;
         while(0 > (var10 = this.trGetC(var1, var2, var3, var9) - this.trGetC(var1, var2, var3, var6[var8]))) {
            do {
               var6[var8 + 1] = var6[var8];
               --var8;
            } while(var4 <= var8 && var6[var8] < 0);

            if (var8 < var4) {
               break;
            }
         }

         if (var10 == 0) {
            var6[var8] = ~var6[var8];
         }

         var6[var8 + 1] = var9;
      }

   }

   private static int trLog(int var0) {
      return (var0 & -65536) != 0 ? ((var0 & -16777216) != 0 ? 24 + LOG_2_TABLE[var0 >> 24 & 255] : LOG_2_TABLE[var0 >> 16 & 271]) : ((var0 & '\uff00') != 0 ? 8 + LOG_2_TABLE[var0 >> 8 & 255] : LOG_2_TABLE[var0 & 255]);
   }

   private int trMedian3(int var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = this.SA;
      int var8 = this.trGetC(var1, var2, var3, var7[var4]);
      int var9 = this.trGetC(var1, var2, var3, var7[var5]);
      int var10 = this.trGetC(var1, var2, var3, var7[var6]);
      if (var8 > var9) {
         int var11 = var4;
         var4 = var5;
         var5 = var11;
         int var12 = var8;
         var8 = var9;
         var9 = var12;
      }

      if (var9 > var10) {
         return var8 > var10 ? var4 : var6;
      } else {
         return var5;
      }
   }

   private int trMedian5(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      int[] var9 = this.SA;
      int var10 = this.trGetC(var1, var2, var3, var9[var4]);
      int var11 = this.trGetC(var1, var2, var3, var9[var5]);
      int var12 = this.trGetC(var1, var2, var3, var9[var6]);
      int var13 = this.trGetC(var1, var2, var3, var9[var7]);
      int var14 = this.trGetC(var1, var2, var3, var9[var8]);
      int var15;
      int var16;
      if (var11 > var12) {
         var15 = var5;
         var5 = var6;
         var6 = var15;
         var16 = var11;
         var11 = var12;
         var12 = var16;
      }

      if (var13 > var14) {
         var15 = var7;
         var7 = var8;
         var8 = var15;
         var16 = var13;
         var13 = var14;
         var14 = var16;
      }

      if (var11 > var13) {
         var7 = var5;
         var13 = var11;
         var15 = var6;
         var6 = var8;
         var8 = var15;
         var16 = var12;
         var12 = var14;
         var14 = var16;
      }

      if (var10 > var12) {
         var15 = var4;
         var4 = var6;
         var6 = var15;
         var16 = var10;
         var10 = var12;
         var12 = var16;
      }

      if (var10 > var13) {
         var7 = var4;
         var13 = var10;
         var6 = var8;
         var12 = var14;
      }

      return var12 > var13 ? var7 : var6;
   }

   private int trPivot(int var1, int var2, int var3, int var4, int var5) {
      int var7 = var5 - var4;
      int var6 = var4 + var7 / 2;
      if (var7 <= 512) {
         if (var7 <= 32) {
            return this.trMedian3(var1, var2, var3, var4, var6, var5 - 1);
         } else {
            var7 >>= 2;
            return this.trMedian5(var1, var2, var3, var4, var4 + var7, var6, var5 - 1 - var7, var5 - 1);
         }
      } else {
         var7 >>= 3;
         return this.trMedian3(var1, var2, var3, this.trMedian3(var1, var2, var3, var4, var4 + var7, var4 + (var7 << 1)), this.trMedian3(var1, var2, var3, var6 - var7, var6, var6 + var7), this.trMedian3(var1, var2, var3, var5 - 1 - (var7 << 1), var5 - 1 - var7, var5 - 1));
      }
   }

   private void lsUpdateGroup(int var1, int var2, int var3) {
      int[] var4 = this.SA;

      for(int var5 = var2; var5 < var3; ++var5) {
         int var6;
         if (0 <= var4[var5]) {
            var6 = var5;

            do {
               var4[var1 + var4[var5]] = var5++;
            } while(var5 < var3 && 0 <= var4[var5]);

            var4[var6] = var6 - var5;
            if (var3 <= var5) {
               break;
            }
         }

         var6 = var5;

         do {
            var4[var5] = ~var4[var5];
            ++var5;
         } while(var4[var5] < 0);

         int var7 = var5;

         do {
            var4[var1 + var4[var6]] = var7;
            ++var6;
         } while(var6 <= var5);
      }

   }

   private void lsIntroSort(int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = this.SA;
      Bzip2DivSufSort.StackEntry[] var7 = new Bzip2DivSufSort.StackEntry[64];
      int var18 = 0;
      int var19 = 0;
      int var16 = trLog(var5 - var4);

      while(true) {
         Bzip2DivSufSort.StackEntry var20;
         while(var5 - var4 > 8) {
            int var8;
            int var9;
            if (var16-- == 0) {
               this.trHeapSort(var1, var2, var3, var4, var5 - var4);

               for(var8 = var5 - 1; var4 < var8; var8 = var9) {
                  var18 = this.trGetC(var1, var2, var3, var6[var8]);

                  for(var9 = var8 - 1; var4 <= var9 && this.trGetC(var1, var2, var3, var6[var9]) == var18; --var9) {
                     var6[var9] = ~var6[var9];
                  }
               }

               this.lsUpdateGroup(var1, var4, var5);
               if (var19 == 0) {
                  return;
               }

               --var19;
               var20 = var7[var19];
               var4 = var20.a;
               var5 = var20.b;
               var16 = var20.c;
            } else {
               var8 = this.trPivot(var1, var2, var3, var4, var5);
               swapElements(var6, var4, var6, var8);
               int var17 = this.trGetC(var1, var2, var3, var6[var4]);

               for(var9 = var4 + 1; var9 < var5 && (var18 = this.trGetC(var1, var2, var3, var6[var9])) == var17; ++var9) {
               }

               var8 = var9;
               if (var9 < var5 && var18 < var17) {
                  while(true) {
                     ++var9;
                     if (var9 >= var5 || (var18 = this.trGetC(var1, var2, var3, var6[var9])) > var17) {
                        break;
                     }

                     if (var18 == var17) {
                        swapElements(var6, var9, var6, var8);
                        ++var8;
                     }
                  }
               }

               int var10;
               for(var10 = var5 - 1; var9 < var10 && (var18 = this.trGetC(var1, var2, var3, var6[var10])) == var17; --var10) {
               }

               int var11 = var10;
               if (var9 < var10 && var18 > var17) {
                  while(true) {
                     --var10;
                     if (var9 >= var10 || (var18 = this.trGetC(var1, var2, var3, var6[var10])) < var17) {
                        break;
                     }

                     if (var18 == var17) {
                        swapElements(var6, var10, var6, var11);
                        --var11;
                     }
                  }
               }

               label166:
               while(var9 < var10) {
                  swapElements(var6, var9, var6, var10);

                  while(true) {
                     ++var9;
                     if (var9 >= var10 || (var18 = this.trGetC(var1, var2, var3, var6[var9])) > var17) {
                        while(true) {
                           --var10;
                           if (var9 >= var10 || (var18 = this.trGetC(var1, var2, var3, var6[var10])) < var17) {
                              continue label166;
                           }

                           if (var18 == var17) {
                              swapElements(var6, var10, var6, var11);
                              --var11;
                           }
                        }
                     }

                     if (var18 == var17) {
                        swapElements(var6, var9, var6, var8);
                        ++var8;
                     }
                  }
               }

               if (var8 > var11) {
                  if (var19 == 0) {
                     return;
                  }

                  --var19;
                  var20 = var7[var19];
                  var4 = var20.a;
                  var5 = var20.b;
                  var16 = var20.c;
               } else {
                  var10 = var9 - 1;
                  int var14;
                  int var15;
                  if ((var14 = var8 - var4) > (var15 = var9 - var8)) {
                     var14 = var15;
                  }

                  int var12 = var4;

                  int var13;
                  for(var13 = var9 - var14; 0 < var14; ++var13) {
                     swapElements(var6, var12, var6, var13);
                     --var14;
                     ++var12;
                  }

                  if ((var14 = var11 - var10) > (var15 = var5 - var11 - 1)) {
                     var14 = var15;
                  }

                  var12 = var9;

                  for(var13 = var5 - var14; 0 < var14; ++var13) {
                     swapElements(var6, var12, var6, var13);
                     --var14;
                     ++var12;
                  }

                  var8 = var4 + (var9 - var8);
                  var9 = var5 - (var11 - var10);
                  var10 = var4;

                  for(var17 = var8 - 1; var10 < var8; ++var10) {
                     var6[var1 + var6[var10]] = var17;
                  }

                  if (var9 < var5) {
                     var10 = var8;

                     for(var17 = var9 - 1; var10 < var9; ++var10) {
                        var6[var1 + var6[var10]] = var17;
                     }
                  }

                  if (var9 - var8 == 1) {
                     var6[var8] = -1;
                  }

                  if (var8 - var4 <= var5 - var9) {
                     if (var4 < var8) {
                        var7[var19++] = new Bzip2DivSufSort.StackEntry(var9, var5, var16, 0);
                        var5 = var8;
                     } else {
                        var4 = var9;
                     }
                  } else if (var9 < var5) {
                     var7[var19++] = new Bzip2DivSufSort.StackEntry(var4, var8, var16, 0);
                     var4 = var9;
                  } else {
                     var5 = var8;
                  }
               }
            }
         }

         if (1 < var5 - var4) {
            this.trInsertionSort(var1, var2, var3, var4, var5);
            this.lsUpdateGroup(var1, var4, var5);
         } else if (var5 - var4 == 1) {
            var6[var4] = -1;
         }

         if (var19 == 0) {
            return;
         }

         --var19;
         var20 = var7[var19];
         var4 = var20.a;
         var5 = var20.b;
         var16 = var20.c;
      }
   }

   private void lsSort(int var1, int var2, int var3) {
      int[] var4 = this.SA;

      for(int var5 = var1 + var3; -var2 < var4[0]; var5 += var5 - var1) {
         int var6 = 0;
         int var10 = 0;

         int var7;
         int var9;
         do {
            if ((var9 = var4[var6]) < 0) {
               var6 -= var9;
               var10 += var9;
            } else {
               if (var10 != 0) {
                  var4[var6 + var10] = var10;
                  var10 = 0;
               }

               var7 = var4[var1 + var9] + 1;
               this.lsIntroSort(var1, var5, var1 + var2, var6, var7);
               var6 = var7;
            }
         } while(var6 < var2);

         if (var10 != 0) {
            var4[var6 + var10] = var10;
         }

         if (var2 < var5 - var1) {
            var6 = 0;

            do {
               if ((var9 = var4[var6]) < 0) {
                  var6 -= var9;
               } else {
                  var7 = var4[var1 + var9] + 1;

                  for(int var8 = var6; var8 < var7; var4[var1 + var4[var8]] = var8++) {
                  }

                  var6 = var7;
               }
            } while(var6 < var2);

            return;
         }
      }

   }

   private Bzip2DivSufSort.PartitionResult trPartition(int var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = this.SA;
      int var16 = 0;

      int var9;
      for(var9 = var4; var9 < var5 && (var16 = this.trGetC(var1, var2, var3, var7[var9])) == var6; ++var9) {
      }

      int var8 = var9;
      if (var9 < var5 && var16 < var6) {
         while(true) {
            ++var9;
            if (var9 >= var5 || (var16 = this.trGetC(var1, var2, var3, var7[var9])) > var6) {
               break;
            }

            if (var16 == var6) {
               swapElements(var7, var9, var7, var8);
               ++var8;
            }
         }
      }

      int var10;
      for(var10 = var5 - 1; var9 < var10 && (var16 = this.trGetC(var1, var2, var3, var7[var10])) == var6; --var10) {
      }

      int var11 = var10;
      if (var9 < var10 && var16 > var6) {
         while(true) {
            --var10;
            if (var9 >= var10 || (var16 = this.trGetC(var1, var2, var3, var7[var10])) < var6) {
               break;
            }

            if (var16 == var6) {
               swapElements(var7, var10, var7, var11);
               --var11;
            }
         }
      }

      label85:
      while(var9 < var10) {
         swapElements(var7, var9, var7, var10);

         while(true) {
            ++var9;
            if (var9 >= var10 || (var16 = this.trGetC(var1, var2, var3, var7[var9])) > var6) {
               while(true) {
                  --var10;
                  if (var9 >= var10 || (var16 = this.trGetC(var1, var2, var3, var7[var10])) < var6) {
                     continue label85;
                  }

                  if (var16 == var6) {
                     swapElements(var7, var10, var7, var11);
                     --var11;
                  }
               }
            }

            if (var16 == var6) {
               swapElements(var7, var9, var7, var8);
               ++var8;
            }
         }
      }

      if (var8 <= var11) {
         var10 = var9 - 1;
         int var14;
         int var15;
         if ((var15 = var8 - var4) > (var14 = var9 - var8)) {
            var15 = var14;
         }

         int var12 = var4;

         int var13;
         for(var13 = var9 - var15; 0 < var15; ++var13) {
            swapElements(var7, var12, var7, var13);
            --var15;
            ++var12;
         }

         if ((var15 = var11 - var10) > (var14 = var5 - var11 - 1)) {
            var15 = var14;
         }

         var12 = var9;

         for(var13 = var5 - var15; 0 < var15; ++var13) {
            swapElements(var7, var12, var7, var13);
            --var15;
            ++var12;
         }

         var4 += var9 - var8;
         var5 -= var11 - var10;
      }

      return new Bzip2DivSufSort.PartitionResult(var4, var5);
   }

   private void trCopy(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int[] var8 = this.SA;
      int var13 = var5 - 1;
      int var9 = var3;

      int var10;
      int var12;
      for(var10 = var4 - 1; var9 <= var10; ++var9) {
         if ((var12 = var8[var9] - var7) < 0) {
            var12 += var2 - var1;
         }

         if (var8[var1 + var12] == var13) {
            ++var10;
            var8[var10] = var12;
            var8[var1 + var12] = var10;
         }
      }

      var9 = var6 - 1;
      int var11 = var10 + 1;

      for(var10 = var5; var11 < var10; --var9) {
         if ((var12 = var8[var9] - var7) < 0) {
            var12 += var2 - var1;
         }

         if (var8[var1 + var12] == var13) {
            --var10;
            var8[var10] = var12;
            var8[var1 + var12] = var10;
         }
      }

   }

   private void trIntroSort(int var1, int var2, int var3, int var4, int var5, Bzip2DivSufSort.TRBudget var6, int var7) {
      int[] var8 = this.SA;
      Bzip2DivSufSort.StackEntry[] var9 = new Bzip2DivSufSort.StackEntry[64];
      int var19 = 0;
      int var22 = 0;
      int var20 = trLog(var5 - var4);

      int var16;
      while(true) {
         int var10;
         int var11;
         int var12;
         int var18;
         int var21;
         Bzip2DivSufSort.StackEntry var23;
         if (var20 < 0) {
            if (var20 == -1) {
               if (!var6.update(var7, var5 - var4)) {
                  break;
               }

               Bzip2DivSufSort.PartitionResult var25 = this.trPartition(var1, var2 - 1, var3, var4, var5, var5 - 1);
               var10 = var25.first;
               var11 = var25.last;
               Bzip2DivSufSort.StackEntry var24;
               if (var4 >= var10 && var11 >= var5) {
                  for(var12 = var4; var12 < var5; var8[var1 + var8[var12]] = var12++) {
                  }

                  if (var22 == 0) {
                     return;
                  }

                  --var22;
                  var24 = var9[var22];
                  var2 = var24.a;
                  var4 = var24.b;
                  var5 = var24.c;
                  var20 = var24.d;
               } else {
                  if (var10 < var5) {
                     var12 = var4;

                     for(var18 = var10 - 1; var12 < var10; ++var12) {
                        var8[var1 + var8[var12]] = var18;
                     }
                  }

                  if (var11 < var5) {
                     var12 = var10;

                     for(var18 = var11 - 1; var12 < var11; ++var12) {
                        var8[var1 + var8[var12]] = var18;
                     }
                  }

                  var9[var22++] = new Bzip2DivSufSort.StackEntry(0, var10, var11, 0);
                  var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 - 1, var4, var5, -2);
                  if (var10 - var4 <= var5 - var11) {
                     if (1 < var10 - var4) {
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, trLog(var5 - var11));
                        var5 = var10;
                        var20 = trLog(var10 - var4);
                     } else if (1 < var5 - var11) {
                        var4 = var11;
                        var20 = trLog(var5 - var11);
                     } else {
                        if (var22 == 0) {
                           return;
                        }

                        --var22;
                        var24 = var9[var22];
                        var2 = var24.a;
                        var4 = var24.b;
                        var5 = var24.c;
                        var20 = var24.d;
                     }
                  } else if (1 < var5 - var11) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, trLog(var10 - var4));
                     var4 = var11;
                     var20 = trLog(var5 - var11);
                  } else if (1 < var10 - var4) {
                     var5 = var10;
                     var20 = trLog(var10 - var4);
                  } else {
                     if (var22 == 0) {
                        return;
                     }

                     --var22;
                     var24 = var9[var22];
                     var2 = var24.a;
                     var4 = var24.b;
                     var5 = var24.c;
                     var20 = var24.d;
                  }
               }
            } else if (var20 == -2) {
               --var22;
               var10 = var9[var22].b;
               var11 = var9[var22].c;
               this.trCopy(var1, var3, var4, var10, var11, var5, var2 - var1);
               if (var22 == 0) {
                  return;
               }

               --var22;
               var23 = var9[var22];
               var2 = var23.a;
               var4 = var23.b;
               var5 = var23.c;
               var20 = var23.d;
            } else {
               if (0 <= var8[var4]) {
                  var10 = var4;

                  do {
                     var8[var1 + var8[var10]] = var10++;
                  } while(var10 < var5 && 0 <= var8[var10]);

                  var4 = var10;
               }

               if (var4 >= var5) {
                  if (var22 == 0) {
                     return;
                  }

                  --var22;
                  var23 = var9[var22];
                  var2 = var23.a;
                  var4 = var23.b;
                  var5 = var23.c;
                  var20 = var23.d;
               } else {
                  var10 = var4;

                  do {
                     var8[var10] = ~var8[var10];
                     ++var10;
                  } while(var8[var10] < 0);

                  var21 = var8[var1 + var8[var10]] != var8[var2 + var8[var10]] ? trLog(var10 - var4 + 1) : -1;
                  ++var10;
                  if (var10 < var5) {
                     var11 = var4;

                     for(var18 = var10 - 1; var11 < var10; ++var11) {
                        var8[var1 + var8[var11]] = var18;
                     }
                  }

                  if (var10 - var4 <= var5 - var10) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var10, var5, -3);
                     ++var2;
                     var5 = var10;
                     var20 = var21;
                  } else if (1 < var5 - var10) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var4, var10, var21);
                     var4 = var10;
                     var20 = -3;
                  } else {
                     ++var2;
                     var5 = var10;
                     var20 = var21;
                  }
               }
            }
         } else if (var5 - var4 <= 8) {
            if (!var6.update(var7, var5 - var4)) {
               break;
            }

            this.trInsertionSort(var1, var2, var3, var4, var5);
            var20 = -3;
         } else if (var20-- == 0) {
            if (!var6.update(var7, var5 - var4)) {
               break;
            }

            this.trHeapSort(var1, var2, var3, var4, var5 - var4);

            for(var10 = var5 - 1; var4 < var10; var10 = var11) {
               var19 = this.trGetC(var1, var2, var3, var8[var10]);

               for(var11 = var10 - 1; var4 <= var11 && this.trGetC(var1, var2, var3, var8[var11]) == var19; --var11) {
                  var8[var11] = ~var8[var11];
               }
            }

            var20 = -3;
         } else {
            var10 = this.trPivot(var1, var2, var3, var4, var5);
            swapElements(var8, var4, var8, var10);
            var18 = this.trGetC(var1, var2, var3, var8[var4]);

            for(var11 = var4 + 1; var11 < var5 && (var19 = this.trGetC(var1, var2, var3, var8[var11])) == var18; ++var11) {
            }

            var10 = var11;
            if (var11 < var5 && var19 < var18) {
               while(true) {
                  ++var11;
                  if (var11 >= var5 || (var19 = this.trGetC(var1, var2, var3, var8[var11])) > var18) {
                     break;
                  }

                  if (var19 == var18) {
                     swapElements(var8, var11, var8, var10);
                     ++var10;
                  }
               }
            }

            for(var12 = var5 - 1; var11 < var12 && (var19 = this.trGetC(var1, var2, var3, var8[var12])) == var18; --var12) {
            }

            int var13 = var12;
            if (var11 < var12 && var19 > var18) {
               while(true) {
                  --var12;
                  if (var11 >= var12 || (var19 = this.trGetC(var1, var2, var3, var8[var12])) < var18) {
                     break;
                  }

                  if (var19 == var18) {
                     swapElements(var8, var12, var8, var13);
                     --var13;
                  }
               }
            }

            label354:
            while(var11 < var12) {
               swapElements(var8, var11, var8, var12);

               while(true) {
                  ++var11;
                  if (var11 >= var12 || (var19 = this.trGetC(var1, var2, var3, var8[var11])) > var18) {
                     while(true) {
                        --var12;
                        if (var11 >= var12 || (var19 = this.trGetC(var1, var2, var3, var8[var12])) < var18) {
                           continue label354;
                        }

                        if (var19 == var18) {
                           swapElements(var8, var12, var8, var13);
                           --var13;
                        }
                     }
                  }

                  if (var19 == var18) {
                     swapElements(var8, var11, var8, var10);
                     ++var10;
                  }
               }
            }

            if (var10 > var13) {
               if (!var6.update(var7, var5 - var4)) {
                  break;
               }

               ++var20;
               ++var2;
            } else {
               var12 = var11 - 1;
               int var17;
               if ((var16 = var10 - var4) > (var17 = var11 - var10)) {
                  var16 = var17;
               }

               int var14 = var4;

               int var15;
               for(var15 = var11 - var16; 0 < var16; ++var15) {
                  swapElements(var8, var14, var8, var15);
                  --var16;
                  ++var14;
               }

               if ((var16 = var13 - var12) > (var17 = var5 - var13 - 1)) {
                  var16 = var17;
               }

               var14 = var11;

               for(var15 = var5 - var16; 0 < var16; ++var15) {
                  swapElements(var8, var14, var8, var15);
                  --var16;
                  ++var14;
               }

               var10 = var4 + (var11 - var10);
               var11 = var5 - (var13 - var12);
               var21 = var8[var1 + var8[var10]] != var18 ? trLog(var11 - var10) : -1;
               var12 = var4;

               for(var18 = var10 - 1; var12 < var10; ++var12) {
                  var8[var1 + var8[var12]] = var18;
               }

               if (var11 < var5) {
                  var12 = var10;

                  for(var18 = var11 - 1; var12 < var11; ++var12) {
                     var8[var1 + var8[var12]] = var18;
                  }
               }

               if (var10 - var4 <= var5 - var11) {
                  if (var5 - var11 <= var11 - var10) {
                     if (1 < var10 - var4) {
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                        var5 = var10;
                     } else if (1 < var5 - var11) {
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                        var4 = var11;
                     } else if (1 < var11 - var10) {
                        ++var2;
                        var4 = var10;
                        var5 = var11;
                        var20 = var21;
                     } else {
                        if (var22 == 0) {
                           return;
                        }

                        --var22;
                        var23 = var9[var22];
                        var2 = var23.a;
                        var4 = var23.b;
                        var5 = var23.c;
                        var20 = var23.d;
                     }
                  } else if (var10 - var4 <= var11 - var10) {
                     if (1 < var10 - var4) {
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                        var5 = var10;
                     } else if (1 < var11 - var10) {
                        var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                        ++var2;
                        var4 = var10;
                        var5 = var11;
                        var20 = var21;
                     } else {
                        var4 = var11;
                     }
                  } else if (1 < var11 - var10) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                     ++var2;
                     var4 = var10;
                     var5 = var11;
                     var20 = var21;
                  } else {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                     var5 = var10;
                  }
               } else if (var10 - var4 <= var11 - var10) {
                  if (1 < var5 - var11) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                     var4 = var11;
                  } else if (1 < var10 - var4) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                     var5 = var10;
                  } else if (1 < var11 - var10) {
                     ++var2;
                     var4 = var10;
                     var5 = var11;
                     var20 = var21;
                  } else {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var5, var20);
                  }
               } else if (var5 - var11 <= var11 - var10) {
                  if (1 < var5 - var11) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2 + 1, var10, var11, var21);
                     var4 = var11;
                  } else if (1 < var11 - var10) {
                     var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                     ++var2;
                     var4 = var10;
                     var5 = var11;
                     var20 = var21;
                  } else {
                     var5 = var10;
                  }
               } else if (1 < var11 - var10) {
                  var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                  var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var11, var5, var20);
                  ++var2;
                  var4 = var10;
                  var5 = var11;
                  var20 = var21;
               } else {
                  var9[var22++] = new Bzip2DivSufSort.StackEntry(var2, var4, var10, var20);
                  var4 = var11;
               }
            }
         }
      }

      for(var16 = 0; var16 < var22; ++var16) {
         if (var9[var16].d == -3) {
            this.lsUpdateGroup(var1, var9[var16].b, var9[var16].c);
         }
      }

   }

   private void trSort(int var1, int var2, int var3) {
      int[] var4 = this.SA;
      int var5 = 0;
      if (-var2 < var4[0]) {
         Bzip2DivSufSort.TRBudget var8 = new Bzip2DivSufSort.TRBudget(var2, trLog(var2) * 2 / 3 + 1);

         do {
            int var7;
            if ((var7 = var4[var5]) < 0) {
               var5 -= var7;
            } else {
               int var6 = var4[var1 + var7] + 1;
               if (1 < var6 - var5) {
                  this.trIntroSort(var1, var1 + var3, var1 + var2, var5, var6, var8, var2);
                  if (var8.chance == 0) {
                     if (0 < var5) {
                        var4[0] = -var5;
                     }

                     this.lsSort(var1, var2, var3);
                     break;
                  }
               }

               var5 = var6;
            }
         } while(var5 < var2);
      }

   }

   private static int BUCKET_B(int var0, int var1) {
      return var1 << 8 | var0;
   }

   private static int BUCKET_BSTAR(int var0, int var1) {
      return var0 << 8 | var1;
   }

   private int sortTypeBstar(int[] var1, int[] var2) {
      byte[] var3 = this.T;
      int[] var4 = this.SA;
      int var5 = this.n;
      int[] var6 = new int[256];
      int var11 = 1;

      boolean var19;
      for(var19 = true; var11 < var5; ++var11) {
         if (var3[var11 - 1] != var3[var11]) {
            if ((var3[var11 - 1] & 255) > (var3[var11] & 255)) {
               var19 = false;
            }
            break;
         }
      }

      var11 = var5 - 1;
      int var15 = var5;
      int var20;
      int var21;
      int var22;
      if ((var20 = var3[var11] & 255) < (var22 = var3[0] & 255) || var3[var11] == var3[0] && var19) {
         if (!var19) {
            ++var2[BUCKET_BSTAR(var20, var22)];
            var15 = var5 - 1;
            var4[var15] = var11;
         } else {
            ++var2[BUCKET_B(var20, var22)];
         }

         --var11;

         while(0 <= var11 && (var20 = var3[var11] & 255) <= (var21 = var3[var11 + 1] & 255)) {
            ++var2[BUCKET_B(var20, var21)];
            --var11;
         }
      }

      while(true) {
         do {
            if (0 > var11) {
               var15 = var5 - var15;
               if (var15 == 0) {
                  for(var11 = 0; var11 < var5; var4[var11] = var11++) {
                  }

                  return 0;
               }

               int var17 = 0;
               var11 = -1;

               int var12;
               int var14;
               int var18;
               for(var12 = 0; var17 < 256; ++var17) {
                  var14 = var11 + var1[var17];
                  var1[var17] = var11 + var12;
                  var11 = var14 + var2[BUCKET_B(var17, var17)];

                  for(var18 = var17 + 1; var18 < 256; ++var18) {
                     var12 += var2[BUCKET_BSTAR(var17, var18)];
                     var2[var17 << 8 | var18] = var12;
                     var11 += var2[BUCKET_B(var17, var18)];
                  }
               }

               int var8 = var5 - var15;
               int var9 = var15;

               for(var11 = var15 - 2; 0 <= var11; var4[--var2[BUCKET_BSTAR(var17, var18)]] = var11--) {
                  var14 = var4[var8 + var11];
                  var17 = var3[var14] & 255;
                  var18 = var3[var14 + 1] & 255;
               }

               var14 = var4[var8 + var15 - 1];
               var17 = var3[var14] & 255;
               var18 = var3[var14 + 1] & 255;
               var4[--var2[BUCKET_BSTAR(var17, var18)]] = var15 - 1;
               int[] var7 = var4;
               int var10 = var15;
               int var16 = var5 - 2 * var15;
               if (var16 <= 256) {
                  var7 = var6;
                  var10 = 0;
                  var16 = 256;
               }

               var17 = 255;

               for(var12 = var15; 0 < var12; --var17) {
                  for(var18 = 255; var17 < var18; --var18) {
                     var11 = var2[BUCKET_BSTAR(var17, var18)];
                     if (1 < var12 - var11) {
                        this.subStringSort(var8, var11, var12, var7, var10, var16, 2, var4[var11] == var15 - 1, var5);
                     }

                     var12 = var11;
                  }
               }

               for(var11 = var15 - 1; 0 <= var11; --var11) {
                  if (0 <= var4[var11]) {
                     var12 = var11;

                     do {
                        var4[var9 + var4[var11]] = var11--;
                     } while(0 <= var11 && 0 <= var4[var11]);

                     var4[var11 + 1] = var11 - var12;
                     if (var11 <= 0) {
                        break;
                     }
                  }

                  var12 = var11;

                  do {
                     var4[var9 + (var4[var11] = ~var4[var11])] = var12;
                     --var11;
                  } while(var4[var11] < 0);

                  var4[var9 + var4[var11]] = var12;
               }

               this.trSort(var9, var15, 1);
               var11 = var5 - 1;
               var12 = var15;
               if ((var3[var11] & 255) < (var3[0] & 255) || var3[var11] == var3[0] && var19) {
                  if (!var19) {
                     var12 = var15 - 1;
                     var4[var4[var9 + var12]] = var11;
                  }

                  --var11;

                  while(0 <= var11 && (var3[var11] & 255) <= (var3[var11 + 1] & 255)) {
                     --var11;
                  }
               }

               while(true) {
                  do {
                     if (0 > var11) {
                        var17 = 255;
                        var11 = var5 - 1;

                        for(int var13 = var15 - 1; 0 <= var17; --var17) {
                           for(var18 = 255; var17 < var18; --var18) {
                              var14 = var11 - var2[BUCKET_B(var17, var18)];
                              var2[BUCKET_B(var17, var18)] = var11 + 1;
                              var11 = var14;

                              for(var12 = var2[BUCKET_BSTAR(var17, var18)]; var12 <= var13; --var13) {
                                 var4[var11] = var4[var13];
                                 --var11;
                              }
                           }

                           var14 = var11 - var2[BUCKET_B(var17, var17)];
                           var2[BUCKET_B(var17, var17)] = var11 + 1;
                           if (var17 < 255) {
                              var2[BUCKET_BSTAR(var17, var17 + 1)] = var14 + 1;
                           }

                           var11 = var1[var17];
                        }

                        return var15;
                     }

                     --var11;

                     while(0 <= var11 && (var3[var11] & 255) >= (var3[var11 + 1] & 255)) {
                        --var11;
                     }
                  } while(0 > var11);

                  --var12;

                  for(var4[var4[var9 + var12]] = var11--; 0 <= var11 && (var3[var11] & 255) <= (var3[var11 + 1] & 255); --var11) {
                  }
               }
            }

            do {
               ++var1[var3[var11] & 255];
               --var11;
            } while(0 <= var11 && (var3[var11] & 255) >= (var3[var11 + 1] & 255));
         } while(0 > var11);

         ++var2[BUCKET_BSTAR(var3[var11] & 255, var3[var11 + 1] & 255)];
         --var15;

         for(var4[var15] = var11--; 0 <= var11 && (var20 = var3[var11] & 255) <= (var21 = var3[var11 + 1] & 255); --var11) {
            ++var2[BUCKET_B(var20, var21)];
         }
      }
   }

   private int constructBWT(int[] var1, int[] var2) {
      byte[] var3 = this.T;
      int[] var4 = this.SA;
      int var5 = this.n;
      int var8 = 0;
      int var13 = 0;
      int var14 = -1;

      int var6;
      int var9;
      int var10;
      int var11;
      for(int var12 = 254; 0 <= var12; --var12) {
         var6 = var2[BUCKET_BSTAR(var12, var12 + 1)];
         int var7 = var1[var12 + 1];
         var8 = 0;

         for(var13 = -1; var6 <= var7; --var7) {
            if (0 <= (var10 = var9 = var4[var7])) {
               --var9;
               if (var9 < 0) {
                  var9 = var5 - 1;
               }

               if ((var11 = var3[var9] & 255) <= var12) {
                  var4[var7] = ~var10;
                  if (0 < var9 && (var3[var9 - 1] & 255) > var11) {
                     var9 = ~var9;
                  }

                  if (var13 == var11) {
                     --var8;
                     var4[var8] = var9;
                  } else {
                     if (0 <= var13) {
                        var2[BUCKET_B(var13, var12)] = var8;
                     }

                     var13 = var11;
                     var4[var8 = var2[BUCKET_B(var11, var12)] - 1] = var9;
                  }
               }
            } else {
               var4[var7] = ~var9;
            }
         }
      }

      for(var6 = 0; var6 < var5; ++var6) {
         if (0 <= (var10 = var9 = var4[var6])) {
            --var9;
            if (var9 < 0) {
               var9 = var5 - 1;
            }

            if ((var11 = var3[var9] & 255) >= (var3[var9 + 1] & 255)) {
               if (0 < var9 && (var3[var9 - 1] & 255) < var11) {
                  var9 = ~var9;
               }

               if (var11 == var13) {
                  ++var8;
                  var4[var8] = var9;
               } else {
                  if (var13 != -1) {
                     var1[var13] = var8;
                  }

                  var13 = var11;
                  var4[var8 = var1[var11] + 1] = var9;
               }
            }
         } else {
            var10 = ~var10;
         }

         if (var10 == 0) {
            var4[var6] = var3[var5 - 1];
            var14 = var6;
         } else {
            var4[var6] = var3[var10 - 1];
         }
      }

      return var14;
   }

   public int bwt() {
      int[] var1 = this.SA;
      byte[] var2 = this.T;
      int var3 = this.n;
      int[] var4 = new int[256];
      int[] var5 = new int[65536];
      if (var3 == 0) {
         return 0;
      } else if (var3 == 1) {
         var1[0] = var2[0];
         return 0;
      } else {
         int var6 = this.sortTypeBstar(var4, var5);
         return 0 < var6 ? this.constructBWT(var4, var5) : 0;
      }
   }

   private static class TRBudget {
      int budget;
      int chance;

      TRBudget(int var1, int var2) {
         super();
         this.budget = var1;
         this.chance = var2;
      }

      boolean update(int var1, int var2) {
         this.budget -= var2;
         if (this.budget <= 0) {
            if (--this.chance == 0) {
               return false;
            }

            this.budget += var1;
         }

         return true;
      }
   }

   private static class PartitionResult {
      final int first;
      final int last;

      PartitionResult(int var1, int var2) {
         super();
         this.first = var1;
         this.last = var2;
      }
   }

   private static class StackEntry {
      final int a;
      final int b;
      final int c;
      final int d;

      StackEntry(int var1, int var2, int var3, int var4) {
         super();
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
      }
   }
}
