package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;

final class Bzip2BlockCompressor {
   private final ByteProcessor writeProcessor = new ByteProcessor() {
      public boolean process(byte var1) throws Exception {
         return Bzip2BlockCompressor.this.write(var1);
      }
   };
   private final Bzip2BitWriter writer;
   private final Crc32 crc = new Crc32();
   private final byte[] block;
   private int blockLength;
   private final int blockLengthLimit;
   private final boolean[] blockValuesPresent = new boolean[256];
   private final int[] bwtBlock;
   private int rleCurrentValue = -1;
   private int rleLength;

   Bzip2BlockCompressor(Bzip2BitWriter var1, int var2) {
      super();
      this.writer = var1;
      this.block = new byte[var2 + 1];
      this.bwtBlock = new int[var2 + 1];
      this.blockLengthLimit = var2 - 6;
   }

   private void writeSymbolMap(ByteBuf var1) {
      Bzip2BitWriter var2 = this.writer;
      boolean[] var3 = this.blockValuesPresent;
      boolean[] var4 = new boolean[16];

      int var5;
      int var6;
      int var7;
      for(var5 = 0; var5 < var4.length; ++var5) {
         var6 = 0;

         for(var7 = var5 << 4; var6 < 16; ++var7) {
            if (var3[var7]) {
               var4[var5] = true;
            }

            ++var6;
         }
      }

      boolean[] var9 = var4;
      var6 = var4.length;

      for(var7 = 0; var7 < var6; ++var7) {
         boolean var8 = var9[var7];
         var2.writeBoolean(var1, var8);
      }

      for(var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5]) {
            var6 = 0;

            for(var7 = var5 << 4; var6 < 16; ++var7) {
               var2.writeBoolean(var1, var3[var7]);
               ++var6;
            }
         }
      }

   }

   private void writeRun(int var1, int var2) {
      int var3 = this.blockLength;
      byte[] var4 = this.block;
      this.blockValuesPresent[var1] = true;
      this.crc.updateCRC(var1, var2);
      byte var5 = (byte)var1;
      switch(var2) {
      case 1:
         var4[var3] = var5;
         this.blockLength = var3 + 1;
         break;
      case 2:
         var4[var3] = var5;
         var4[var3 + 1] = var5;
         this.blockLength = var3 + 2;
         break;
      case 3:
         var4[var3] = var5;
         var4[var3 + 1] = var5;
         var4[var3 + 2] = var5;
         this.blockLength = var3 + 3;
         break;
      default:
         var2 -= 4;
         this.blockValuesPresent[var2] = true;
         var4[var3] = var5;
         var4[var3 + 1] = var5;
         var4[var3 + 2] = var5;
         var4[var3 + 3] = var5;
         var4[var3 + 4] = (byte)var2;
         this.blockLength = var3 + 5;
      }

   }

   boolean write(int var1) {
      if (this.blockLength > this.blockLengthLimit) {
         return false;
      } else {
         int var2 = this.rleCurrentValue;
         int var3 = this.rleLength;
         if (var3 == 0) {
            this.rleCurrentValue = var1;
            this.rleLength = 1;
         } else if (var2 != var1) {
            this.writeRun(var2 & 255, var3);
            this.rleCurrentValue = var1;
            this.rleLength = 1;
         } else if (var3 == 254) {
            this.writeRun(var2 & 255, 255);
            this.rleLength = 0;
         } else {
            this.rleLength = var3 + 1;
         }

         return true;
      }
   }

   int write(ByteBuf var1, int var2, int var3) {
      int var4 = var1.forEachByte(var2, var3, this.writeProcessor);
      return var4 == -1 ? var3 : var4 - var2;
   }

   void close(ByteBuf var1) {
      if (this.rleLength > 0) {
         this.writeRun(this.rleCurrentValue & 255, this.rleLength);
      }

      this.block[this.blockLength] = this.block[0];
      Bzip2DivSufSort var2 = new Bzip2DivSufSort(this.block, this.bwtBlock, this.blockLength);
      int var3 = var2.bwt();
      Bzip2BitWriter var4 = this.writer;
      var4.writeBits(var1, 24, 3227993L);
      var4.writeBits(var1, 24, 2511705L);
      var4.writeInt(var1, this.crc.getCRC());
      var4.writeBoolean(var1, false);
      var4.writeBits(var1, 24, (long)var3);
      this.writeSymbolMap(var1);
      Bzip2MTFAndRLE2StageEncoder var5 = new Bzip2MTFAndRLE2StageEncoder(this.bwtBlock, this.blockLength, this.blockValuesPresent);
      var5.encode();
      Bzip2HuffmanStageEncoder var6 = new Bzip2HuffmanStageEncoder(var4, var5.mtfBlock(), var5.mtfLength(), var5.mtfAlphabetSize(), var5.mtfSymbolFrequencies());
      var6.encode(var1);
   }

   int availableSize() {
      return this.blockLength == 0 ? this.blockLengthLimit + 2 : this.blockLengthLimit - this.blockLength + 1;
   }

   boolean isFull() {
      return this.blockLength > this.blockLengthLimit;
   }

   boolean isEmpty() {
      return this.blockLength == 0 && this.rleLength == 0;
   }

   int crc() {
      return this.crc.getCRC();
   }
}
