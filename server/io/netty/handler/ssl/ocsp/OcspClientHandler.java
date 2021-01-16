package io.netty.handler.ssl.ocsp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import javax.net.ssl.SSLHandshakeException;

public abstract class OcspClientHandler extends ChannelInboundHandlerAdapter {
   private static final SSLHandshakeException OCSP_VERIFICATION_EXCEPTION = (SSLHandshakeException)ThrowableUtil.unknownStackTrace(new SSLHandshakeException("Bad OCSP response"), OcspClientHandler.class, "verify(...)");
   private final ReferenceCountedOpenSslEngine engine;

   protected OcspClientHandler(ReferenceCountedOpenSslEngine var1) {
      super();
      this.engine = (ReferenceCountedOpenSslEngine)ObjectUtil.checkNotNull(var1, "engine");
   }

   protected abstract boolean verify(ChannelHandlerContext var1, ReferenceCountedOpenSslEngine var2) throws Exception;

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof SslHandshakeCompletionEvent) {
         var1.pipeline().remove((ChannelHandler)this);
         SslHandshakeCompletionEvent var3 = (SslHandshakeCompletionEvent)var2;
         if (var3.isSuccess() && !this.verify(var1, this.engine)) {
            throw OCSP_VERIFICATION_EXCEPTION;
         }
      }

      var1.fireUserEventTriggered(var2);
   }
}
