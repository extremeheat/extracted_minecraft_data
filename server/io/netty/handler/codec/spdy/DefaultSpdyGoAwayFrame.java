package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyGoAwayFrame implements SpdyGoAwayFrame {
   private int lastGoodStreamId;
   private SpdySessionStatus status;

   public DefaultSpdyGoAwayFrame(int var1) {
      this(var1, 0);
   }

   public DefaultSpdyGoAwayFrame(int var1, int var2) {
      this(var1, SpdySessionStatus.valueOf(var2));
   }

   public DefaultSpdyGoAwayFrame(int var1, SpdySessionStatus var2) {
      super();
      this.setLastGoodStreamId(var1);
      this.setStatus(var2);
   }

   public int lastGoodStreamId() {
      return this.lastGoodStreamId;
   }

   public SpdyGoAwayFrame setLastGoodStreamId(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Last-good-stream-ID cannot be negative: " + var1);
      } else {
         this.lastGoodStreamId = var1;
         return this;
      }
   }

   public SpdySessionStatus status() {
      return this.status;
   }

   public SpdyGoAwayFrame setStatus(SpdySessionStatus var1) {
      this.status = var1;
      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + StringUtil.NEWLINE + "--> Last-good-stream-ID = " + this.lastGoodStreamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
   }
}
