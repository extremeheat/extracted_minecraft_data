package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;

class SpdyHeaderBlockJZlibEncoder extends SpdyHeaderBlockRawEncoder {
   private final Deflater z = new Deflater();
   private boolean finished;

   SpdyHeaderBlockJZlibEncoder(SpdyVersion var1, int var2, int var3, int var4) {
      super(var1);
      if (var2 >= 0 && var2 <= 9) {
         if (var3 >= 9 && var3 <= 15) {
            if (var4 >= 1 && var4 <= 9) {
               int var5 = this.z.deflateInit(var2, var3, var4, JZlib.W_ZLIB);
               if (var5 != 0) {
                  throw new CompressionException("failed to initialize an SPDY header block deflater: " + var5);
               } else {
                  var5 = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
                  if (var5 != 0) {
                     throw new CompressionException("failed to set the SPDY dictionary: " + var5);
                  }
               }
            } else {
               throw new IllegalArgumentException("memLevel: " + var4 + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + var3 + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   private void setInput(ByteBuf var1) {
      int var2 = var1.readableBytes();
      byte[] var3;
      int var4;
      if (var1.hasArray()) {
         var3 = var1.array();
         var4 = var1.arrayOffset() + var1.readerIndex();
      } else {
         var3 = new byte[var2];
         var1.getBytes(var1.readerIndex(), var3);
         var4 = 0;
      }

      this.z.next_in = var3;
      this.z.next_in_index = var4;
      this.z.avail_in = var2;
   }

   private ByteBuf encode(ByteBufAllocator var1) {
      boolean var2 = true;
      ByteBuf var3 = null;

      ByteBuf var9;
      try {
         int var4 = this.z.next_in_index;
         int var5 = this.z.next_out_index;
         int var6 = (int)Math.ceil((double)this.z.next_in.length * 1.001D) + 12;
         var3 = var1.heapBuffer(var6);
         this.z.next_out = var3.array();
         this.z.next_out_index = var3.arrayOffset() + var3.writerIndex();
         this.z.avail_out = var6;

         int var7;
         try {
            var7 = this.z.deflate(2);
         } finally {
            var3.skipBytes(this.z.next_in_index - var4);
         }

         if (var7 != 0) {
            throw new CompressionException("compression failure: " + var7);
         }

         int var8 = this.z.next_out_index - var5;
         if (var8 > 0) {
            var3.writerIndex(var3.writerIndex() + var8);
         }

         var2 = false;
         var9 = var3;
      } finally {
         this.z.next_in = null;
         this.z.next_out = null;
         if (var2 && var3 != null) {
            var3.release();
         }

      }

      return var9;
   }

   public ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception {
      if (var2 == null) {
         throw new IllegalArgumentException("frame");
      } else if (this.finished) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBuf var3 = super.encode(var1, var2);

         ByteBuf var4;
         try {
            if (!var3.isReadable()) {
               var4 = Unpooled.EMPTY_BUFFER;
               return var4;
            }

            this.setInput(var3);
            var4 = this.encode(var1);
         } finally {
            var3.release();
         }

         return var4;
      }
   }

   public void end() {
      if (!this.finished) {
         this.finished = true;
         this.z.deflateEnd();
         this.z.next_in = null;
         this.z.next_out = null;
      }
   }
}
