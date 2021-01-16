package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

final class Bzip2BitWriter {
   private long bitBuffer;
   private int bitCount;

   Bzip2BitWriter() {
      super();
   }

   void writeBits(ByteBuf var1, int var2, long var3) {
      if (var2 >= 0 && var2 <= 32) {
         int var5 = this.bitCount;
         long var6 = this.bitBuffer | var3 << 64 - var2 >>> var5;
         var5 += var2;
         if (var5 >= 32) {
            var1.writeInt((int)(var6 >>> 32));
            var6 <<= 32;
            var5 -= 32;
         }

         this.bitBuffer = var6;
         this.bitCount = var5;
      } else {
         throw new IllegalArgumentException("count: " + var2 + " (expected: 0-32)");
      }
   }

   void writeBoolean(ByteBuf var1, boolean var2) {
      int var3 = this.bitCount + 1;
      long var4 = this.bitBuffer | (var2 ? 1L << 64 - var3 : 0L);
      if (var3 == 32) {
         var1.writeInt((int)(var4 >>> 32));
         var4 = 0L;
         var3 = 0;
      }

      this.bitBuffer = var4;
      this.bitCount = var3;
   }

   void writeUnary(ByteBuf var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("value: " + var2 + " (expected 0 or more)");
      } else {
         while(var2-- > 0) {
            this.writeBoolean(var1, true);
         }

         this.writeBoolean(var1, false);
      }
   }

   void writeInt(ByteBuf var1, int var2) {
      this.writeBits(var1, 32, (long)var2);
   }

   void flush(ByteBuf var1) {
      int var2 = this.bitCount;
      if (var2 > 0) {
         long var3 = this.bitBuffer;
         int var5 = 64 - var2;
         if (var2 <= 8) {
            var1.writeByte((int)(var3 >>> var5 << 8 - var2));
         } else if (var2 <= 16) {
            var1.writeShort((int)(var3 >>> var5 << 16 - var2));
         } else if (var2 <= 24) {
            var1.writeMedium((int)(var3 >>> var5 << 24 - var2));
         } else {
            var1.writeInt((int)(var3 >>> var5 << 32 - var2));
         }
      }

   }
}
