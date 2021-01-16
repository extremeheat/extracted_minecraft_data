package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public class DecoratingHttp2ConnectionEncoder extends DecoratingHttp2FrameWriter implements Http2ConnectionEncoder {
   private final Http2ConnectionEncoder delegate;

   public DecoratingHttp2ConnectionEncoder(Http2ConnectionEncoder var1) {
      super(var1);
      this.delegate = (Http2ConnectionEncoder)ObjectUtil.checkNotNull(var1, "delegate");
   }

   public void lifecycleManager(Http2LifecycleManager var1) {
      this.delegate.lifecycleManager(var1);
   }

   public Http2Connection connection() {
      return this.delegate.connection();
   }

   public Http2RemoteFlowController flowController() {
      return this.delegate.flowController();
   }

   public Http2FrameWriter frameWriter() {
      return this.delegate.frameWriter();
   }

   public Http2Settings pollSentSettings() {
      return this.delegate.pollSentSettings();
   }

   public void remoteSettings(Http2Settings var1) throws Http2Exception {
      this.delegate.remoteSettings(var1);
   }
}
