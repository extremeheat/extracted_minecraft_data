package io.netty.handler.codec.compression;

final class Bzip2BlockDecompressor {
   private final Bzip2BitReader reader;
   private final Crc32 crc = new Crc32();
   private final int blockCRC;
   private final boolean blockRandomised;
   int huffmanEndOfBlockSymbol;
   int huffmanInUse16;
   final byte[] huffmanSymbolMap = new byte[256];
   private final int[] bwtByteCounts = new int[256];
   private final byte[] bwtBlock;
   private final int bwtStartPointer;
   private int[] bwtMergedPointers;
   private int bwtCurrentMergedPointer;
   private int bwtBlockLength;
   private int bwtBytesDecoded;
   private int rleLastDecodedByte = -1;
   private int rleAccumulator;
   private int rleRepeat;
   private int randomIndex;
   private int randomCount = Bzip2Rand.rNums(0) - 1;
   private final Bzip2MoveToFrontTable symbolMTF = new Bzip2MoveToFrontTable();
   private int repeatCount;
   private int repeatIncrement = 1;
   private int mtfValue;

   Bzip2BlockDecompressor(int var1, int var2, boolean var3, int var4, Bzip2BitReader var5) {
      super();
      this.bwtBlock = new byte[var1];
      this.blockCRC = var2;
      this.blockRandomised = var3;
      this.bwtStartPointer = var4;
      this.reader = var5;
   }

   boolean decodeHuffmanData(Bzip2HuffmanStageDecoder var1) {
      Bzip2BitReader var2 = this.reader;
      byte[] var3 = this.bwtBlock;
      byte[] var4 = this.huffmanSymbolMap;
      int var5 = this.bwtBlock.length;
      int var6 = this.huffmanEndOfBlockSymbol;
      int[] var7 = this.bwtByteCounts;
      Bzip2MoveToFrontTable var8 = this.symbolMTF;
      int var9 = this.bwtBlockLength;
      int var10 = this.repeatCount;
      int var11 = this.repeatIncrement;
      int var12 = this.mtfValue;

      while(true) {
         while(var2.hasReadableBits(23)) {
            int var13 = var1.nextSymbol();
            if (var13 == 0) {
               var10 += var11;
               var11 <<= 1;
            } else if (var13 == 1) {
               var10 += var11 << 1;
               var11 <<= 1;
            } else {
               byte var14;
               if (var10 > 0) {
                  if (var9 + var10 > var5) {
                     throw new DecompressionException("block exceeds declared block size");
                  }

                  var14 = var4[var12];
                  var7[var14 & 255] += var10;

                  while(true) {
                     --var10;
                     if (var10 < 0) {
                        var10 = 0;
                        var11 = 1;
                        break;
                     }

                     var3[var9++] = var14;
                  }
               }

               if (var13 == var6) {
                  this.bwtBlockLength = var9;
                  this.initialiseInverseBWT();
                  return true;
               }

               if (var9 >= var5) {
                  throw new DecompressionException("block exceeds declared block size");
               }

               var12 = var8.indexToFront(var13 - 1) & 255;
               var14 = var4[var12];
               ++var7[var14 & 255];
               var3[var9++] = var14;
            }
         }

         this.bwtBlockLength = var9;
         this.repeatCount = var10;
         this.repeatIncrement = var11;
         this.mtfValue = var12;
         return false;
      }
   }

   private void initialiseInverseBWT() {
      int var1 = this.bwtStartPointer;
      byte[] var2 = this.bwtBlock;
      int[] var3 = new int[this.bwtBlockLength];
      int[] var4 = new int[256];
      if (var1 >= 0 && var1 < this.bwtBlockLength) {
         System.arraycopy(this.bwtByteCounts, 0, var4, 1, 255);

         int var5;
         for(var5 = 2; var5 <= 255; ++var5) {
            var4[var5] += var4[var5 - 1];
         }

         for(var5 = 0; var5 < this.bwtBlockLength; ++var5) {
            int var6 = var2[var5] & 255;
            var3[var4[var6]++] = (var5 << 8) + var6;
         }

         this.bwtMergedPointers = var3;
         this.bwtCurrentMergedPointer = var3[var1];
      } else {
         throw new DecompressionException("start pointer invalid");
      }
   }

   public int read() {
      while(this.rleRepeat < 1) {
         if (this.bwtBytesDecoded == this.bwtBlockLength) {
            return -1;
         }

         int var1 = this.decodeNextBWTByte();
         if (var1 != this.rleLastDecodedByte) {
            this.rleLastDecodedByte = var1;
            this.rleRepeat = 1;
            this.rleAccumulator = 1;
            this.crc.updateCRC(var1);
         } else if (++this.rleAccumulator == 4) {
            int var2 = this.decodeNextBWTByte() + 1;
            this.rleRepeat = var2;
            this.rleAccumulator = 0;
            this.crc.updateCRC(var1, var2);
         } else {
            this.rleRepeat = 1;
            this.crc.updateCRC(var1);
         }
      }

      --this.rleRepeat;
      return this.rleLastDecodedByte;
   }

   private int decodeNextBWTByte() {
      int var1 = this.bwtCurrentMergedPointer;
      int var2 = var1 & 255;
      this.bwtCurrentMergedPointer = this.bwtMergedPointers[var1 >>> 8];
      if (this.blockRandomised && --this.randomCount == 0) {
         var2 ^= 1;
         this.randomIndex = (this.randomIndex + 1) % 512;
         this.randomCount = Bzip2Rand.rNums(this.randomIndex);
      }

      ++this.bwtBytesDecoded;
      return var2;
   }

   public int blockLength() {
      return this.bwtBlockLength;
   }

   int checkCRC() {
      int var1 = this.crc.getCRC();
      if (this.blockCRC != var1) {
         throw new DecompressionException("block CRC error");
      } else {
         return var1;
      }
   }
}
