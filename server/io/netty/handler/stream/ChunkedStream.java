package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream implements ChunkedInput<ByteBuf> {
   static final int DEFAULT_CHUNK_SIZE = 8192;
   private final PushbackInputStream in;
   private final int chunkSize;
   private long offset;
   private boolean closed;

   public ChunkedStream(InputStream var1) {
      this(var1, 8192);
   }

   public ChunkedStream(InputStream var1, int var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("in");
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("chunkSize: " + var2 + " (expected: a positive integer)");
      } else {
         if (var1 instanceof PushbackInputStream) {
            this.in = (PushbackInputStream)var1;
         } else {
            this.in = new PushbackInputStream(var1);
         }

         this.chunkSize = var2;
      }
   }

   public long transferredBytes() {
      return this.offset;
   }

   public boolean isEndOfInput() throws Exception {
      if (this.closed) {
         return true;
      } else {
         int var1 = this.in.read();
         if (var1 < 0) {
            return true;
         } else {
            this.in.unread(var1);
            return false;
         }
      }
   }

   public void close() throws Exception {
      this.closed = true;
      this.in.close();
   }

   /** @deprecated */
   @Deprecated
   public ByteBuf readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public ByteBuf readChunk(ByteBufAllocator var1) throws Exception {
      if (this.isEndOfInput()) {
         return null;
      } else {
         int var2 = this.in.available();
         int var3;
         if (var2 <= 0) {
            var3 = this.chunkSize;
         } else {
            var3 = Math.min(this.chunkSize, this.in.available());
         }

         boolean var4 = true;
         ByteBuf var5 = var1.buffer(var3);

         ByteBuf var6;
         try {
            this.offset += (long)var5.writeBytes((InputStream)this.in, var3);
            var4 = false;
            var6 = var5;
         } finally {
            if (var4) {
               var5.release();
            }

         }

         return var6;
      }
   }

   public long length() {
      return -1L;
   }

   public long progress() {
      return this.offset;
   }
}
