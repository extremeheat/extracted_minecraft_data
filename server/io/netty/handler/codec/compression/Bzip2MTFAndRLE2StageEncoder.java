package io.netty.handler.codec.compression;

final class Bzip2MTFAndRLE2StageEncoder {
   private final int[] bwtBlock;
   private final int bwtLength;
   private final boolean[] bwtValuesPresent;
   private final char[] mtfBlock;
   private int mtfLength;
   private final int[] mtfSymbolFrequencies = new int[258];
   private int alphabetSize;

   Bzip2MTFAndRLE2StageEncoder(int[] var1, int var2, boolean[] var3) {
      super();
      this.bwtBlock = var1;
      this.bwtLength = var2;
      this.bwtValuesPresent = var3;
      this.mtfBlock = new char[var2 + 1];
   }

   void encode() {
      int var1 = this.bwtLength;
      boolean[] var2 = this.bwtValuesPresent;
      int[] var3 = this.bwtBlock;
      char[] var4 = this.mtfBlock;
      int[] var5 = this.mtfSymbolFrequencies;
      byte[] var6 = new byte[256];
      Bzip2MoveToFrontTable var7 = new Bzip2MoveToFrontTable();
      int var8 = 0;

      int var9;
      for(var9 = 0; var9 < var6.length; ++var9) {
         if (var2[var9]) {
            var6[var9] = (byte)(var8++);
         }
      }

      var9 = var8 + 1;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;

      for(int var14 = 0; var14 < var1; ++var14) {
         int var15 = var7.valueToFront(var6[var3[var14] & 255]);
         if (var15 == 0) {
            ++var11;
         } else {
            if (var11 > 0) {
               --var11;

               while(true) {
                  if ((var11 & 1) == 0) {
                     var4[var10++] = 0;
                     ++var12;
                  } else {
                     var4[var10++] = 1;
                     ++var13;
                  }

                  if (var11 <= 1) {
                     var11 = 0;
                     break;
                  }

                  var11 = var11 - 2 >>> 1;
               }
            }

            var4[var10++] = (char)(var15 + 1);
            ++var5[var15 + 1];
         }
      }

      if (var11 > 0) {
         --var11;

         while(true) {
            if ((var11 & 1) == 0) {
               var4[var10++] = 0;
               ++var12;
            } else {
               var4[var10++] = 1;
               ++var13;
            }

            if (var11 <= 1) {
               break;
            }

            var11 = var11 - 2 >>> 1;
         }
      }

      var4[var10] = (char)var9;
      int var10002 = var5[var9]++;
      var5[0] += var12;
      var5[1] += var13;
      this.mtfLength = var10 + 1;
      this.alphabetSize = var9 + 1;
   }

   char[] mtfBlock() {
      return this.mtfBlock;
   }

   int mtfLength() {
      return this.mtfLength;
   }

   int mtfAlphabetSize() {
      return this.alphabetSize;
   }

   int[] mtfSymbolFrequencies() {
      return this.mtfSymbolFrequencies;
   }
}
