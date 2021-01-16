package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

class Bzip2BitReader {
   private static final int MAX_COUNT_OF_READABLE_BYTES = 268435455;
   private ByteBuf in;
   private long bitBuffer;
   private int bitCount;

   Bzip2BitReader() {
      super();
   }

   void setByteBuf(ByteBuf var1) {
      this.in = var1;
   }

   int readBits(int var1) {
      if (var1 >= 0 && var1 <= 32) {
         int var2 = this.bitCount;
         long var3 = this.bitBuffer;
         if (var2 < var1) {
            long var5;
            byte var7;
            switch(this.in.readableBytes()) {
            case 1:
               var5 = (long)this.in.readUnsignedByte();
               var7 = 8;
               break;
            case 2:
               var5 = (long)this.in.readUnsignedShort();
               var7 = 16;
               break;
            case 3:
               var5 = (long)this.in.readUnsignedMedium();
               var7 = 24;
               break;
            default:
               var5 = this.in.readUnsignedInt();
               var7 = 32;
            }

            var3 = var3 << var7 | var5;
            var2 += var7;
            this.bitBuffer = var3;
         }

         this.bitCount = var2 -= var1;
         return (int)(var3 >>> var2 & (var1 != 32 ? (long)((1 << var1) - 1) : 4294967295L));
      } else {
         throw new IllegalArgumentException("count: " + var1 + " (expected: 0-32 )");
      }
   }

   boolean readBoolean() {
      return this.readBits(1) != 0;
   }

   int readInt() {
      return this.readBits(32);
   }

   void refill() {
      short var1 = this.in.readUnsignedByte();
      this.bitBuffer = this.bitBuffer << 8 | (long)var1;
      this.bitCount += 8;
   }

   boolean isReadable() {
      return this.bitCount > 0 || this.in.isReadable();
   }

   boolean hasReadableBits(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("count: " + var1 + " (expected value greater than 0)");
      } else {
         return this.bitCount >= var1 || (this.in.readableBytes() << 3 & 2147483647) >= var1 - this.bitCount;
      }
   }

   boolean hasReadableBytes(int var1) {
      if (var1 >= 0 && var1 <= 268435455) {
         return this.hasReadableBits(var1 << 3);
      } else {
         throw new IllegalArgumentException("count: " + var1 + " (expected: 0-" + 268435455 + ')');
      }
   }
}
