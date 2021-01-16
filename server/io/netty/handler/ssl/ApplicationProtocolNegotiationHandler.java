package io.netty.handler.ssl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class ApplicationProtocolNegotiationHandler extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
   private final String fallbackProtocol;

   protected ApplicationProtocolNegotiationHandler(String var1) {
      super();
      this.fallbackProtocol = (String)ObjectUtil.checkNotNull(var1, "fallbackProtocol");
   }

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof SslHandshakeCompletionEvent) {
         var1.pipeline().remove((ChannelHandler)this);
         SslHandshakeCompletionEvent var3 = (SslHandshakeCompletionEvent)var2;
         if (var3.isSuccess()) {
            SslHandler var4 = (SslHandler)var1.pipeline().get(SslHandler.class);
            if (var4 == null) {
               throw new IllegalStateException("cannot find a SslHandler in the pipeline (required for application-level protocol negotiation)");
            }

            String var5 = var4.applicationProtocol();
            this.configurePipeline(var1, var5 != null ? var5 : this.fallbackProtocol);
         } else {
            this.handshakeFailure(var1, var3.cause());
         }
      }

      var1.fireUserEventTriggered(var2);
   }

   protected abstract void configurePipeline(ChannelHandlerContext var1, String var2) throws Exception;

   protected void handshakeFailure(ChannelHandlerContext var1, Throwable var2) throws Exception {
      logger.warn("{} TLS handshake failed:", var1.channel(), var2);
      var1.close();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      logger.warn("{} Failed to select the application-level protocol:", var1.channel(), var2);
      var1.close();
   }
}
