package net.minecraft.server.jsonrpc;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class HandshakeTimeoutHandler extends ChannelInboundHandlerAdapter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long timeoutMillis;
   private @Nullable ScheduledFuture<?> timeoutFuture;

   public HandshakeTimeoutHandler(final long timeoutMillis) {
      super();
      this.timeoutMillis = timeoutMillis;
   }

   public void handlerAdded(final ChannelHandlerContext ctx) {
      if (ctx.channel().isActive()) {
         this.schedule(ctx);
      }

   }

   public void channelActive(final ChannelHandlerContext ctx) throws Exception {
      this.schedule(ctx);
      super.channelActive(ctx);
   }

   public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
      this.cancel();
      super.channelInactive(ctx);
   }

   public void handlerRemoved(final ChannelHandlerContext ctx) {
      this.cancel();
   }

   public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
      if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
         ctx.pipeline().remove(this);
      }

      super.userEventTriggered(ctx, evt);
   }

   private void schedule(final ChannelHandlerContext ctx) {
      if (this.timeoutFuture == null && this.timeoutMillis > 0L) {
         this.timeoutFuture = ctx.executor().schedule(() -> {
            if (ctx.channel().isOpen()) {
               LOGGER.debug("Closing connection from {}: handshake not completed within {} ms", ctx.channel().remoteAddress(), this.timeoutMillis);
               ctx.close();
            }

         }, this.timeoutMillis, TimeUnit.MILLISECONDS);
      }
   }

   private void cancel() {
      if (this.timeoutFuture != null) {
         this.timeoutFuture.cancel(false);
         this.timeoutFuture = null;
      }

   }
}
