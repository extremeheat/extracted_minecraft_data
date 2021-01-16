package io.netty.handler.codec.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;

public abstract class Http2ChannelDuplexHandler extends ChannelDuplexHandler {
   private volatile Http2FrameCodec frameCodec;

   public Http2ChannelDuplexHandler() {
      super();
   }

   public final void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.frameCodec = requireHttp2FrameCodec(var1);
      this.handlerAdded0(var1);
   }

   protected void handlerAdded0(ChannelHandlerContext var1) throws Exception {
   }

   public final void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      try {
         this.handlerRemoved0(var1);
      } finally {
         this.frameCodec = null;
      }

   }

   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
   }

   public final Http2FrameStream newStream() {
      Http2FrameCodec var1 = this.frameCodec;
      if (var1 == null) {
         throw new IllegalStateException(StringUtil.simpleClassName(Http2FrameCodec.class) + " not found. Has the handler been added to a pipeline?");
      } else {
         return var1.newStream();
      }
   }

   protected final void forEachActiveStream(Http2FrameStreamVisitor var1) throws Http2Exception {
      this.frameCodec.forEachActiveStream(var1);
   }

   private static Http2FrameCodec requireHttp2FrameCodec(ChannelHandlerContext var0) {
      ChannelHandlerContext var1 = var0.pipeline().context(Http2FrameCodec.class);
      if (var1 == null) {
         throw new IllegalArgumentException(Http2FrameCodec.class.getSimpleName() + " was not found in the channel pipeline.");
      } else {
         return (Http2FrameCodec)var1.handler();
      }
   }
}
