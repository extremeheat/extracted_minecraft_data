package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class OptionalSslHandler extends ByteToMessageDecoder {
   private final SslContext sslContext;

   public OptionalSslHandler(SslContext var1) {
      super();
      this.sslContext = (SslContext)ObjectUtil.checkNotNull(var1, "sslContext");
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.readableBytes() >= 5) {
         if (SslHandler.isEncrypted(var2)) {
            this.handleSsl(var1);
         } else {
            this.handleNonSsl(var1);
         }

      }
   }

   private void handleSsl(ChannelHandlerContext var1) {
      SslHandler var2 = null;

      try {
         var2 = this.newSslHandler(var1, this.sslContext);
         var1.pipeline().replace((ChannelHandler)this, this.newSslHandlerName(), var2);
         var2 = null;
      } finally {
         if (var2 != null) {
            ReferenceCountUtil.safeRelease(var2.engine());
         }

      }

   }

   private void handleNonSsl(ChannelHandlerContext var1) {
      ChannelHandler var2 = this.newNonSslHandler(var1);
      if (var2 != null) {
         var1.pipeline().replace((ChannelHandler)this, this.newNonSslHandlerName(), var2);
      } else {
         var1.pipeline().remove((ChannelHandler)this);
      }

   }

   protected String newSslHandlerName() {
      return null;
   }

   protected SslHandler newSslHandler(ChannelHandlerContext var1, SslContext var2) {
      return var2.newHandler(var1.alloc());
   }

   protected String newNonSslHandlerName() {
      return null;
   }

   protected ChannelHandler newNonSslHandler(ChannelHandlerContext var1) {
      return null;
   }
}
