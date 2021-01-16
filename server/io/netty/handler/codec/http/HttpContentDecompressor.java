package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

public class HttpContentDecompressor extends HttpContentDecoder {
   private final boolean strict;

   public HttpContentDecompressor() {
      this(false);
   }

   public HttpContentDecompressor(boolean var1) {
      super();
      this.strict = var1;
   }

   protected EmbeddedChannel newContentDecoder(String var1) throws Exception {
      if (!HttpHeaderValues.GZIP.contentEqualsIgnoreCase(var1) && !HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(var1)) {
         if (!HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(var1) && !HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(var1)) {
            return null;
         } else {
            ZlibWrapper var2 = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
            return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(var2)});
         }
      } else {
         return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP)});
      }
   }
}
