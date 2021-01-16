package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DecoratingHttp2ConnectionDecoder implements Http2ConnectionDecoder {
   private final Http2ConnectionDecoder delegate;

   public DecoratingHttp2ConnectionDecoder(Http2ConnectionDecoder var1) {
      super();
      this.delegate = (Http2ConnectionDecoder)ObjectUtil.checkNotNull(var1, "delegate");
   }

   public void lifecycleManager(Http2LifecycleManager var1) {
      this.delegate.lifecycleManager(var1);
   }

   public Http2Connection connection() {
      return this.delegate.connection();
   }

   public Http2LocalFlowController flowController() {
      return this.delegate.flowController();
   }

   public void frameListener(Http2FrameListener var1) {
      this.delegate.frameListener(var1);
   }

   public Http2FrameListener frameListener() {
      return this.delegate.frameListener();
   }

   public void decodeFrame(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Http2Exception {
      this.delegate.decodeFrame(var1, var2, var3);
   }

   public Http2Settings localSettings() {
      return this.delegate.localSettings();
   }

   public boolean prefaceReceived() {
      return this.delegate.prefaceReceived();
   }

   public void close() {
      this.delegate.close();
   }
}
