package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BinaryWebSocketFrame extends WebSocketFrame {
   public BinaryWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public BinaryWebSocketFrame(ByteBuf var1) {
      super(var1);
   }

   public BinaryWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public BinaryWebSocketFrame copy() {
      return (BinaryWebSocketFrame)super.copy();
   }

   public BinaryWebSocketFrame duplicate() {
      return (BinaryWebSocketFrame)super.duplicate();
   }

   public BinaryWebSocketFrame retainedDuplicate() {
      return (BinaryWebSocketFrame)super.retainedDuplicate();
   }

   public BinaryWebSocketFrame replace(ByteBuf var1) {
      return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public BinaryWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public BinaryWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public BinaryWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public BinaryWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
