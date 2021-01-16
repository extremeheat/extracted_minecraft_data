package io.netty.handler.codec.http2;

import io.netty.util.internal.StringUtil;

public class DefaultHttp2PingFrame implements Http2PingFrame {
   private final long content;
   private final boolean ack;

   public DefaultHttp2PingFrame(long var1) {
      this(var1, false);
   }

   DefaultHttp2PingFrame(long var1, boolean var3) {
      super();
      this.content = var1;
      this.ack = var3;
   }

   public boolean ack() {
      return this.ack;
   }

   public String name() {
      return "PING";
   }

   public long content() {
      return this.content;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Http2PingFrame)) {
         return false;
      } else {
         Http2PingFrame var2 = (Http2PingFrame)var1;
         return this.ack == var2.ack() && this.content == var2.content();
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + (this.ack ? 1 : 0);
      return var1;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(content=" + this.content + ", ack=" + this.ack + ')';
   }
}
