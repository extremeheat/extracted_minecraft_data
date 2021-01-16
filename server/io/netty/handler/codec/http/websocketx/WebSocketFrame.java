package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public abstract class WebSocketFrame extends DefaultByteBufHolder {
   private final boolean finalFragment;
   private final int rsv;

   protected WebSocketFrame(ByteBuf var1) {
      this(true, 0, var1);
   }

   protected WebSocketFrame(boolean var1, int var2, ByteBuf var3) {
      super(var3);
      this.finalFragment = var1;
      this.rsv = var2;
   }

   public boolean isFinalFragment() {
      return this.finalFragment;
   }

   public int rsv() {
      return this.rsv;
   }

   public WebSocketFrame copy() {
      return (WebSocketFrame)super.copy();
   }

   public WebSocketFrame duplicate() {
      return (WebSocketFrame)super.duplicate();
   }

   public WebSocketFrame retainedDuplicate() {
      return (WebSocketFrame)super.retainedDuplicate();
   }

   public abstract WebSocketFrame replace(ByteBuf var1);

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(data: " + this.contentToString() + ')';
   }

   public WebSocketFrame retain() {
      super.retain();
      return this;
   }

   public WebSocketFrame retain(int var1) {
      super.retain(var1);
      return this;
   }

   public WebSocketFrame touch() {
      super.touch();
      return this;
   }

   public WebSocketFrame touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
