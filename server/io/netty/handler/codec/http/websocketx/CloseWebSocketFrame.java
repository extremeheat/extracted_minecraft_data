package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class CloseWebSocketFrame extends WebSocketFrame {
   public CloseWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public CloseWebSocketFrame(int var1, String var2) {
      this(true, 0, var1, var2);
   }

   public CloseWebSocketFrame(boolean var1, int var2) {
      this(var1, var2, Unpooled.buffer(0));
   }

   public CloseWebSocketFrame(boolean var1, int var2, int var3, String var4) {
      super(var1, var2, newBinaryData(var3, var4));
   }

   private static ByteBuf newBinaryData(int var0, String var1) {
      if (var1 == null) {
         var1 = "";
      }

      ByteBuf var2 = Unpooled.buffer(2 + var1.length());
      var2.writeShort(var0);
      if (!var1.isEmpty()) {
         var2.writeCharSequence(var1, CharsetUtil.UTF_8);
      }

      var2.readerIndex(0);
      return var2;
   }

   public CloseWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public int statusCode() {
      ByteBuf var1 = this.content();
      if (var1 != null && var1.capacity() != 0) {
         var1.readerIndex(0);
         short var2 = var1.readShort();
         var1.readerIndex(0);
         return var2;
      } else {
         return -1;
      }
   }

   public String reasonText() {
      ByteBuf var1 = this.content();
      if (var1 != null && var1.capacity() > 2) {
         var1.readerIndex(2);
         String var2 = var1.toString(CharsetUtil.UTF_8);
         var1.readerIndex(0);
         return var2;
      } else {
         return "";
      }
   }

   public CloseWebSocketFrame copy() {
      return (CloseWebSocketFrame)super.copy();
   }

   public CloseWebSocketFrame duplicate() {
      return (CloseWebSocketFrame)super.duplicate();
   }

   public CloseWebSocketFrame retainedDuplicate() {
      return (CloseWebSocketFrame)super.retainedDuplicate();
   }

   public CloseWebSocketFrame replace(ByteBuf var1) {
      return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public CloseWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public CloseWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public CloseWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public CloseWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
