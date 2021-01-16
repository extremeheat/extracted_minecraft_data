package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame {
   private int streamId;
   private int deltaWindowSize;

   public DefaultSpdyWindowUpdateFrame(int var1, int var2) {
      super();
      this.setStreamId(var1);
      this.setDeltaWindowSize(var2);
   }

   public int streamId() {
      return this.streamId;
   }

   public SpdyWindowUpdateFrame setStreamId(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Stream-ID cannot be negative: " + var1);
      } else {
         this.streamId = var1;
         return this;
      }
   }

   public int deltaWindowSize() {
      return this.deltaWindowSize;
   }

   public SpdyWindowUpdateFrame setDeltaWindowSize(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Delta-Window-Size must be positive: " + var1);
      } else {
         this.deltaWindowSize = var1;
         return this;
      }
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + StringUtil.NEWLINE + "--> Stream-ID = " + this.streamId() + StringUtil.NEWLINE + "--> Delta-Window-Size = " + this.deltaWindowSize();
   }
}
