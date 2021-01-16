package io.netty.handler.codec.rtsp;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;

/** @deprecated */
@ChannelHandler.Sharable
@Deprecated
public abstract class RtspObjectEncoder<H extends HttpMessage> extends HttpObjectEncoder<H> {
   protected RtspObjectEncoder() {
      super();
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 instanceof FullHttpMessage;
   }
}
