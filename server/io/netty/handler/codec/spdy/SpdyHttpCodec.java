package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;

public final class SpdyHttpCodec extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder> {
   public SpdyHttpCodec(SpdyVersion var1, int var2) {
      super(new SpdyHttpDecoder(var1, var2), new SpdyHttpEncoder(var1));
   }

   public SpdyHttpCodec(SpdyVersion var1, int var2, boolean var3) {
      super(new SpdyHttpDecoder(var1, var2, var3), new SpdyHttpEncoder(var1));
   }
}
