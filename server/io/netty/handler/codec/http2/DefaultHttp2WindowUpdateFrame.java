package io.netty.handler.codec.http2;

public class DefaultHttp2WindowUpdateFrame extends AbstractHttp2StreamFrame implements Http2WindowUpdateFrame {
   private final int windowUpdateIncrement;

   public DefaultHttp2WindowUpdateFrame(int var1) {
      super();
      this.windowUpdateIncrement = var1;
   }

   public DefaultHttp2WindowUpdateFrame stream(Http2FrameStream var1) {
      super.stream(var1);
      return this;
   }

   public String name() {
      return "WINDOW_UPDATE";
   }

   public int windowSizeIncrement() {
      return this.windowUpdateIncrement;
   }
}
