package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2ResetFrame extends AbstractHttp2StreamFrame implements Http2ResetFrame {
   private final long errorCode;

   public DefaultHttp2ResetFrame(Http2Error var1) {
      super();
      this.errorCode = ((Http2Error)ObjectUtil.checkNotNull(var1, "error")).code();
   }

   public DefaultHttp2ResetFrame(long var1) {
      super();
      this.errorCode = var1;
   }

   public DefaultHttp2ResetFrame stream(Http2FrameStream var1) {
      super.stream(var1);
      return this;
   }

   public String name() {
      return "RST_STREAM";
   }

   public long errorCode() {
      return this.errorCode;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(stream=" + this.stream() + ", errorCode=" + this.errorCode + ')';
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttp2ResetFrame)) {
         return false;
      } else {
         DefaultHttp2ResetFrame var2 = (DefaultHttp2ResetFrame)var1;
         return super.equals(var1) && this.errorCode == var2.errorCode;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + (int)(this.errorCode ^ this.errorCode >>> 32);
      return var1;
   }
}
