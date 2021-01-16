package io.netty.handler.codec.http2;

public abstract class AbstractHttp2StreamFrame implements Http2StreamFrame {
   private Http2FrameStream stream;

   public AbstractHttp2StreamFrame() {
      super();
   }

   public AbstractHttp2StreamFrame stream(Http2FrameStream var1) {
      this.stream = var1;
      return this;
   }

   public Http2FrameStream stream() {
      return this.stream;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Http2StreamFrame)) {
         return false;
      } else {
         Http2StreamFrame var2 = (Http2StreamFrame)var1;
         return this.stream == var2.stream() || this.stream != null && this.stream.equals(var2.stream());
      }
   }

   public int hashCode() {
      Http2FrameStream var1 = this.stream;
      return var1 == null ? super.hashCode() : var1.hashCode();
   }
}
