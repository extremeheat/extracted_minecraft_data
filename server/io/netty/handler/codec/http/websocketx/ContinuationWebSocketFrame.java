package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ContinuationWebSocketFrame extends WebSocketFrame {
   public ContinuationWebSocketFrame() {
      this(Unpooled.buffer(0));
   }

   public ContinuationWebSocketFrame(ByteBuf var1) {
      super(var1);
   }

   public ContinuationWebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public ContinuationWebSocketFrame(boolean var1, int var2, String var3) {
      this(var1, var2, fromText(var3));
   }

   public String text() {
      return this.content().toString(CharsetUtil.UTF_8);
   }

   private static ByteBuf fromText(String var0) {
      return var0 != null && !var0.isEmpty() ? Unpooled.copiedBuffer((CharSequence)var0, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
   }

   public ContinuationWebSocketFrame copy() {
      return (ContinuationWebSocketFrame)super.copy();
   }

   public ContinuationWebSocketFrame duplicate() {
      return (ContinuationWebSocketFrame)super.duplicate();
   }

   public ContinuationWebSocketFrame retainedDuplicate() {
      return (ContinuationWebSocketFrame)super.retainedDuplicate();
   }

   public ContinuationWebSocketFrame replace(ByteBuf var1) {
      return new ContinuationWebSocketFrame(this.isFinalFragment(), this.rsv(), var1);
   }

   public ContinuationWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public ContinuationWebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public ContinuationWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public ContinuationWebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
