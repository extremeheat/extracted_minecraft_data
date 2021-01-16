package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;

public final class WebSocketChunkedInput implements ChunkedInput<WebSocketFrame> {
   private final ChunkedInput<ByteBuf> input;
   private final int rsv;

   public WebSocketChunkedInput(ChunkedInput<ByteBuf> var1) {
      this(var1, 0);
   }

   public WebSocketChunkedInput(ChunkedInput<ByteBuf> var1, int var2) {
      super();
      this.input = (ChunkedInput)ObjectUtil.checkNotNull(var1, "input");
      this.rsv = var2;
   }

   public boolean isEndOfInput() throws Exception {
      return this.input.isEndOfInput();
   }

   public void close() throws Exception {
      this.input.close();
   }

   /** @deprecated */
   @Deprecated
   public WebSocketFrame readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public WebSocketFrame readChunk(ByteBufAllocator var1) throws Exception {
      ByteBuf var2 = (ByteBuf)this.input.readChunk(var1);
      return var2 == null ? null : new ContinuationWebSocketFrame(this.input.isEndOfInput(), this.rsv, var2);
   }

   public long length() {
      return this.input.length();
   }

   public long progress() {
      return this.input.progress();
   }
}
