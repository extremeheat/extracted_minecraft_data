package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyPingFrame implements SpdyPingFrame {
   private int id;

   public DefaultSpdyPingFrame(int var1) {
      super();
      this.setId(var1);
   }

   public int id() {
      return this.id;
   }

   public SpdyPingFrame setId(int var1) {
      this.id = var1;
      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + StringUtil.NEWLINE + "--> ID = " + this.id();
   }
}
