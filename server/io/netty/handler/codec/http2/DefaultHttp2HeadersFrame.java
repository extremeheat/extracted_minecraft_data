package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2HeadersFrame extends AbstractHttp2StreamFrame implements Http2HeadersFrame {
   private final Http2Headers headers;
   private final boolean endStream;
   private final int padding;

   public DefaultHttp2HeadersFrame(Http2Headers var1) {
      this(var1, false);
   }

   public DefaultHttp2HeadersFrame(Http2Headers var1, boolean var2) {
      this(var1, var2, 0);
   }

   public DefaultHttp2HeadersFrame(Http2Headers var1, boolean var2, int var3) {
      super();
      this.headers = (Http2Headers)ObjectUtil.checkNotNull(var1, "headers");
      this.endStream = var2;
      Http2CodecUtil.verifyPadding(var3);
      this.padding = var3;
   }

   public DefaultHttp2HeadersFrame stream(Http2FrameStream var1) {
      super.stream(var1);
      return this;
   }

   public String name() {
      return "HEADERS";
   }

   public Http2Headers headers() {
      return this.headers;
   }

   public boolean isEndStream() {
      return this.endStream;
   }

   public int padding() {
      return this.padding;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(stream=" + this.stream() + ", headers=" + this.headers + ", endStream=" + this.endStream + ", padding=" + this.padding + ')';
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttp2HeadersFrame)) {
         return false;
      } else {
         DefaultHttp2HeadersFrame var2 = (DefaultHttp2HeadersFrame)var1;
         return super.equals(var2) && this.headers.equals(var2.headers) && this.endStream == var2.endStream && this.padding == var2.padding;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + this.headers.hashCode();
      var1 = var1 * 31 + (this.endStream ? 0 : 1);
      var1 = var1 * 31 + this.padding;
      return var1;
   }
}
