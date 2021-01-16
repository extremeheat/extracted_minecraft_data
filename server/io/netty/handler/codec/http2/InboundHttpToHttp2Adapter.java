package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpScheme;

public class InboundHttpToHttp2Adapter extends ChannelInboundHandlerAdapter {
   private final Http2Connection connection;
   private final Http2FrameListener listener;

   public InboundHttpToHttp2Adapter(Http2Connection var1, Http2FrameListener var2) {
      super();
      this.connection = var1;
      this.listener = var2;
   }

   private static int getStreamId(Http2Connection var0, HttpHeaders var1) {
      return var1.getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), var0.remote().incrementAndGetNextStreamId());
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof FullHttpMessage) {
         handle(var1, this.connection, this.listener, (FullHttpMessage)var2);
      } else {
         super.channelRead(var1, var2);
      }

   }

   static void handle(ChannelHandlerContext var0, Http2Connection var1, Http2FrameListener var2, FullHttpMessage var3) throws Http2Exception {
      try {
         int var4 = getStreamId(var1, var3.headers());
         Http2Stream var5 = var1.stream(var4);
         if (var5 == null) {
            var5 = var1.remote().createStream(var4, false);
         }

         var3.headers().set((CharSequence)HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), (Object)HttpScheme.HTTP.name());
         Http2Headers var6 = HttpConversionUtil.toHttp2Headers((HttpMessage)var3, true);
         boolean var7 = var3.content().isReadable();
         boolean var8 = !var3.trailingHeaders().isEmpty();
         var2.onHeadersRead(var0, var4, var6, 0, !var7 && !var8);
         if (var7) {
            var2.onDataRead(var0, var4, var3.content(), 0, !var8);
         }

         if (var8) {
            Http2Headers var9 = HttpConversionUtil.toHttp2Headers(var3.trailingHeaders(), true);
            var2.onHeadersRead(var0, var4, var9, 0, true);
         }

         var5.closeRemoteSide();
      } finally {
         var3.release();
      }

   }
}
