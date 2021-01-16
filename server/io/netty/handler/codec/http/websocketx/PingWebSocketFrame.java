package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PingWebSocketFrame extends WebSocketFrame {
   public PingWebSocketFrame() {
      super(true, 0, Unpooled.buffer(0));
   }

   public PingWebSocketFrame(ByteBuf var1) {
      super(var1);
   }

   public PingWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public PingWebSocketFrame copy() {
      return (PingWebSocketFrame)super.copy();
   }

   public PingWebSocketFrame duplicate() {
      return (PingWebSocketFrame)super.duplicate();
   }

   public PingWebSocketFrame retainedDuplicate() {
      return (PingWebSocketFrame)super.retainedDuplicate();
   }

   public PingWebSocketFrame replace(ByteBuf var1) {
      return new PingWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public PingWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public PingWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public PingWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public PingWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
