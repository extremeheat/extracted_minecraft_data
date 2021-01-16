package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class TextWebSocketFrame extends WebSocketFrame {
   public TextWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public TextWebSocketFrame(String var1) {
      super(fromText(var1));
   }

   public TextWebSocketFrame(ByteBuf var1) {
      super(var1);
   }

   public TextWebSocketFrame(boolean var1, int var2, String var3) {
      super(var1, var2, fromText(var3));
   }

   private static ByteBuf fromText(String var0) {
      return var0 != null && !var0.isEmpty() ? Unpooled.copiedBuffer((CharSequence)var0, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
   }

   public TextWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public String text() {
      return this.content().toString(CharsetUtil.UTF_8);
   }

   public TextWebSocketFrame copy() {
      return (TextWebSocketFrame)super.copy();
   }

   public TextWebSocketFrame duplicate() {
      return (TextWebSocketFrame)super.duplicate();
   }

   public TextWebSocketFrame retainedDuplicate() {
      return (TextWebSocketFrame)super.retainedDuplicate();
   }

   public TextWebSocketFrame replace(ByteBuf var1) {
      return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public TextWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public TextWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public TextWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public TextWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
