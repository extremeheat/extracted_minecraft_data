package io.netty.handler.timeout;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.TimeUnit;

public class ReadTimeoutHandler extends IdleStateHandler {
   private boolean closed;

   public ReadTimeoutHandler(int var1) {
      this((long)var1, TimeUnit.SECONDS);
   }

   public ReadTimeoutHandler(long var1, TimeUnit var3) {
      super(var1, 0L, 0L, var3);
   }

   protected final void channelIdle(ChannelHandlerContext var1, IdleStateEvent var2) throws Exception {
      assert var2.state() == IdleState.READER_IDLE;

      this.readTimedOut(var1);
   }

   protected void readTimedOut(ChannelHandlerContext var1) throws Exception {
      if (!this.closed) {
         var1.fireExceptionCaught(ReadTimeoutException.INSTANCE);
         var1.close();
         this.closed = true;
      }

   }
}
