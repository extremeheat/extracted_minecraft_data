package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

public class HttpChunkedInput implements ChunkedInput<HttpContent> {
   private final ChunkedInput<ByteBuf> input;
   private final LastHttpContent lastHttpContent;
   private boolean sentLastChunk;

   public HttpChunkedInput(ChunkedInput<ByteBuf> var1) {
      super();
      this.input = var1;
      this.lastHttpContent = LastHttpContent.EMPTY_LAST_CONTENT;
   }

   public HttpChunkedInput(ChunkedInput<ByteBuf> var1, LastHttpContent var2) {
      super();
      this.input = var1;
      this.lastHttpContent = var2;
   }

   public boolean isEndOfInput() throws Exception {
      return this.input.isEndOfInput() ? this.sentLastChunk : false;
   }

   public void close() throws Exception {
      this.input.close();
   }

   /** @deprecated */
   @Deprecated
   public HttpContent readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public HttpContent readChunk(ByteBufAllocator var1) throws Exception {
      if (this.input.isEndOfInput()) {
         if (this.sentLastChunk) {
            return null;
         } else {
            this.sentLastChunk = true;
            return this.lastHttpContent;
         }
      } else {
         ByteBuf var2 = (ByteBuf)this.input.readChunk(var1);
         return var2 == null ? null : new DefaultHttpContent(var2);
      }
   }

   public long length() {
      return this.input.length();
   }

   public long progress() {
      return this.input.progress();
   }
}
