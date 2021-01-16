package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;

final class Bzip2HuffmanStageEncoder {
   private static final int HUFFMAN_HIGH_SYMBOL_COST = 15;
   private final Bzip2BitWriter writer;
   private final char[] mtfBlock;
   private final int mtfLength;
   private final int mtfAlphabetSize;
   private final int[] mtfSymbolFrequencies;
   private final int[][] huffmanCodeLengths;
   private final int[][] huffmanMergedCodeSymbols;
   private final byte[] selectors;

   Bzip2HuffmanStageEncoder(Bzip2BitWriter var1, char[] var2, int var3, int var4, int[] var5) {
      super();
      this.writer = var1;
      this.mtfBlock = var2;
      this.mtfLength = var3;
      this.mtfAlphabetSize = var4;
      this.mtfSymbolFrequencies = var5;
      int var6 = selectTableCount(var3);
      this.huffmanCodeLengths = new int[var6][var4];
      this.huffmanMergedCodeSymbols = new int[var6][var4];
      this.selectors = new byte[(var3 + 50 - 1) / 50];
   }

   private static int selectTableCount(int var0) {
      if (var0 >= 2400) {
         return 6;
      } else if (var0 >= 1200) {
         return 5;
      } else if (var0 >= 600) {
         return 4;
      } else {
         return var0 >= 200 ? 3 : 2;
      }
   }

   private static void generateHuffmanCodeLengths(int var0, int[] var1, int[] var2) {
      int[] var3 = new int[var0];
      int[] var4 = new int[var0];

      int var5;
      for(var5 = 0; var5 < var0; ++var5) {
         var3[var5] = var1[var5] << 9 | var5;
      }

      Arrays.sort(var3);

      for(var5 = 0; var5 < var0; ++var5) {
         var4[var5] = var3[var5] >>> 9;
      }

      Bzip2HuffmanAllocator.allocateHuffmanCodeLengths(var4, 20);

      for(var5 = 0; var5 < var0; ++var5) {
         var2[var3[var5] & 511] = var4[var5];
      }

   }

   private void generateHuffmanOptimisationSeeds() {
      int[][] var1 = this.huffmanCodeLengths;
      int[] var2 = this.mtfSymbolFrequencies;
      int var3 = this.mtfAlphabetSize;
      int var4 = var1.length;
      int var5 = this.mtfLength;
      int var6 = -1;

      for(int var7 = 0; var7 < var4; ++var7) {
         int var8 = var5 / (var4 - var7);
         int var9 = var6 + 1;

         int var10;
         for(var10 = 0; var10 < var8 && var6 < var3 - 1; var10 += var2[var6]) {
            ++var6;
         }

         if (var6 > var9 && var7 != 0 && var7 != var4 - 1 && (var4 - var7 & 1) == 0) {
            var10 -= var2[var6--];
         }

         int[] var11 = var1[var7];

         for(int var12 = 0; var12 < var3; ++var12) {
            if (var12 < var9 || var12 > var6) {
               var11[var12] = 15;
            }
         }

         var5 -= var10;
      }

   }

   private void optimiseSelectorsAndHuffmanTables(boolean var1) {
      char[] var2 = this.mtfBlock;
      byte[] var3 = this.selectors;
      int[][] var4 = this.huffmanCodeLengths;
      int var5 = this.mtfLength;
      int var6 = this.mtfAlphabetSize;
      int var7 = var4.length;
      int[][] var8 = new int[var7][var6];
      int var9 = 0;

      int var10;
      int var11;
      for(var10 = 0; var10 < var5; var10 = var11 + 1) {
         var11 = Math.min(var10 + 50, var5) - 1;
         short[] var12 = new short[var7];

         for(int var13 = var10; var13 <= var11; ++var13) {
            char var14 = var2[var13];

            for(int var15 = 0; var15 < var7; ++var15) {
               var12[var15] = (short)(var12[var15] + var4[var15][var14]);
            }
         }

         byte var17 = 0;
         short var18 = var12[0];

         for(byte var19 = 1; var19 < var7; ++var19) {
            short var16 = var12[var19];
            if (var16 < var18) {
               var18 = var16;
               var17 = var19;
            }
         }

         int[] var20 = var8[var17];

         for(int var21 = var10; var21 <= var11; ++var21) {
            ++var20[var2[var21]];
         }

         if (var1) {
            var3[var9++] = var17;
         }
      }

      for(var10 = 0; var10 < var7; ++var10) {
         generateHuffmanCodeLengths(var6, var8[var10], var4[var10]);
      }

   }

   private void assignHuffmanCodeSymbols() {
      int[][] var1 = this.huffmanMergedCodeSymbols;
      int[][] var2 = this.huffmanCodeLengths;
      int var3 = this.mtfAlphabetSize;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int[] var6 = var2[var5];
         int var7 = 32;
         int var8 = 0;

         int var9;
         int var10;
         for(var9 = 0; var9 < var3; ++var9) {
            var10 = var6[var9];
            if (var10 > var8) {
               var8 = var10;
            }

            if (var10 < var7) {
               var7 = var10;
            }
         }

         var9 = 0;

         for(var10 = var7; var10 <= var8; ++var10) {
            for(int var11 = 0; var11 < var3; ++var11) {
               if ((var2[var5][var11] & 255) == var10) {
                  var1[var5][var11] = var10 << 24 | var9;
                  ++var9;
               }
            }

            var9 <<= 1;
         }
      }

   }

   private void writeSelectorsAndHuffmanTables(ByteBuf var1) {
      Bzip2BitWriter var2 = this.writer;
      byte[] var3 = this.selectors;
      int var4 = var3.length;
      int[][] var5 = this.huffmanCodeLengths;
      int var6 = var5.length;
      int var7 = this.mtfAlphabetSize;
      var2.writeBits(var1, 3, (long)var6);
      var2.writeBits(var1, 15, (long)var4);
      Bzip2MoveToFrontTable var8 = new Bzip2MoveToFrontTable();
      byte[] var9 = var3;
      int var10 = var3.length;

      int var11;
      for(var11 = 0; var11 < var10; ++var11) {
         byte var12 = var9[var11];
         var2.writeUnary(var1, var8.valueToFront(var12));
      }

      int[][] var18 = var5;
      var10 = var5.length;

      for(var11 = 0; var11 < var10; ++var11) {
         int[] var19 = var18[var11];
         int var13 = var19[0];
         var2.writeBits(var1, 5, (long)var13);

         for(int var14 = 0; var14 < var7; ++var14) {
            int var15 = var19[var14];
            int var16 = var13 < var15 ? 2 : 3;
            int var17 = Math.abs(var15 - var13);

            while(var17-- > 0) {
               var2.writeBits(var1, 2, (long)var16);
            }

            var2.writeBoolean(var1, false);
            var13 = var15;
         }
      }

   }

   private void writeBlockData(ByteBuf var1) {
      Bzip2BitWriter var2 = this.writer;
      int[][] var3 = this.huffmanMergedCodeSymbols;
      byte[] var4 = this.selectors;
      char[] var5 = this.mtfBlock;
      int var6 = this.mtfLength;
      int var7 = 0;
      int var8 = 0;

      while(var8 < var6) {
         int var9 = Math.min(var8 + 50, var6) - 1;
         int[] var10 = var3[var4[var7++]];

         while(var8 <= var9) {
            int var11 = var10[var5[var8++]];
            var2.writeBits(var1, var11 >>> 24, (long)var11);
         }
      }

   }

   void encode(ByteBuf var1) {
      this.generateHuffmanOptimisationSeeds();

      for(int var2 = 3; var2 >= 0; --var2) {
         this.optimiseSelectorsAndHuffmanTables(var2 == 0);
      }

      this.assignHuffmanCodeSymbols();
      this.writeSelectorsAndHuffmanTables(var1);
      this.writeBlockData(var1);
   }
}
