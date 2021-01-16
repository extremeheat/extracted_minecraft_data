package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder extends SpdyHeaderBlockRawEncoder {
   private final Deflater compressor;
   private boolean finished;

   SpdyHeaderBlockZlibEncoder(SpdyVersion var1, int var2) {
      super(var1);
      if (var2 >= 0 && var2 <= 9) {
         this.compressor = new Deflater(var2);
         this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   private int setInput(ByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var1.hasArray()) {
         this.compressor.setInput(var1.array(), var1.arrayOffset() + var1.readerIndex(), var2);
      } else {
         byte[] var3 = new byte[var2];
         var1.getBytes(var1.readerIndex(), var3);
         this.compressor.setInput(var3, 0, var3.length);
      }

      return var2;
   }

   private ByteBuf encode(ByteBufAllocator var1, int var2) {
      ByteBuf var3 = var1.heapBuffer(var2);
      boolean var4 = true;

      ByteBuf var5;
      try {
         while(this.compressInto(var3)) {
            var3.ensureWritable(var3.capacity() << 1);
         }

         var4 = false;
         var5 = var3;
      } finally {
         if (var4) {
            var3.release();
         }

      }

      return var5;
   }

   private boolean compressInto(ByteBuf var1) {
      byte[] var2 = var1.array();
      int var3 = var1.arrayOffset() + var1.writerIndex();
      int var4 = var1.writableBytes();
      int var5 = this.compressor.deflate(var2, var3, var4, 2);
      var1.writerIndex(var1.writerIndex() + var5);
      return var5 == var4;
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
            if (var3.isReadable()) {
               int var9 = this.setInput(var3);
               ByteBuf var5 = this.encode(var1, var9);
               return var5;
            }

            var4 = Unpooled.EMPTY_BUFFER;
         } finally {
            var3.release();
         }

         return var4;
      }
   }

   public void end() {
      if (!this.finished) {
         this.finished = true;
         this.compressor.end();
         super.end();
      }
   }
}
