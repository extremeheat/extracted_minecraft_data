package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

final class SpdyHeaderBlockZlibDecoder extends SpdyHeaderBlockRawDecoder {
   private static final int DEFAULT_BUFFER_CAPACITY = 4096;
   private static final SpdyProtocolException INVALID_HEADER_BLOCK = new SpdyProtocolException("Invalid Header Block");
   private final Inflater decompressor = new Inflater();
   private ByteBuf decompressed;

   SpdyHeaderBlockZlibDecoder(SpdyVersion var1, int var2) {
      super(var1, var2);
   }

   void decode(ByteBufAllocator var1, ByteBuf var2, SpdyHeadersFrame var3) throws Exception {
      int var4 = this.setInput(var2);

      int var5;
      do {
         var5 = this.decompress(var1, var3);
      } while(var5 > 0);

      if (this.decompressor.getRemaining() != 0) {
         throw INVALID_HEADER_BLOCK;
      } else {
         var2.skipBytes(var4);
      }
   }

   private int setInput(ByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var1.hasArray()) {
         this.decompressor.setInput(var1.array(), var1.arrayOffset() + var1.readerIndex(), var2);
      } else {
         byte[] var3 = new byte[var2];
         var1.getBytes(var1.readerIndex(), var3);
         this.decompressor.setInput(var3, 0, var3.length);
      }

      return var2;
   }

   private int decompress(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception {
      this.ensureBuffer(var1);
      byte[] var3 = this.decompressed.array();
      int var4 = this.decompressed.arrayOffset() + this.decompressed.writerIndex();

      try {
         int var5 = this.decompressor.inflate(var3, var4, this.decompressed.writableBytes());
         if (var5 == 0 && this.decompressor.needsDictionary()) {
            try {
               this.decompressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
            } catch (IllegalArgumentException var7) {
               throw INVALID_HEADER_BLOCK;
            }

            var5 = this.decompressor.inflate(var3, var4, this.decompressed.writableBytes());
         }

         if (var2 != null) {
            this.decompressed.writerIndex(this.decompressed.writerIndex() + var5);
            this.decodeHeaderBlock(this.decompressed, var2);
            this.decompressed.discardReadBytes();
         }

         return var5;
      } catch (DataFormatException var8) {
         throw new SpdyProtocolException("Received invalid header block", var8);
      }
   }

   private void ensureBuffer(ByteBufAllocator var1) {
      if (this.decompressed == null) {
         this.decompressed = var1.heapBuffer(4096);
      }

      this.decompressed.ensureWritable(1);
   }

   void endHeaderBlock(SpdyHeadersFrame var1) throws Exception {
      super.endHeaderBlock(var1);
      this.releaseBuffer();
   }

   public void end() {
      super.end();
      this.releaseBuffer();
      this.decompressor.end();
   }

   private void releaseBuffer() {
      if (this.decompressed != null) {
         this.decompressed.release();
         this.decompressed = null;
      }

   }
}
