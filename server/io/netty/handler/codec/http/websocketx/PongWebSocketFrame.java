package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PongWebSocketFrame extends WebSocketFrame {
   public PongWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public PongWebSocketFrame(ByteBuf var1) {
      super(var1);
   }

   public PongWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public PongWebSocketFrame copy() {
      return (PongWebSocketFrame)super.copy();
   }

   public PongWebSocketFrame duplicate() {
      return (PongWebSocketFrame)super.duplicate();
   }

   public PongWebSocketFrame retainedDuplicate() {
      return (PongWebSocketFrame)super.retainedDuplicate();
   }

   public PongWebSocketFrame replace(ByteBuf var1) {
      return new PongWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public PongWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public PongWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public PongWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public PongWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
