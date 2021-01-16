package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class DefaultStompFrame extends DefaultStompHeadersSubframe implements StompFrame {
   private final ByteBuf content;

   public DefaultStompFrame(StompCommand var1) {
      this(var1, Unpooled.buffer(0));
   }

   public DefaultStompFrame(StompCommand var1, ByteBuf var2) {
      this(var1, var2, (DefaultStompHeaders)null);
   }

   DefaultStompFrame(StompCommand var1, ByteBuf var2, DefaultStompHeaders var3) {
      super(var1, var3);
      if (var2 == null) {
         throw new NullPointerException("content");
      } else {
         this.content = var2;
      }
   }

   public ByteBuf content() {
      return this.content;
   }

   public StompFrame copy() {
      return this.replace(this.content.copy());
   }

   public StompFrame duplicate() {
      return this.replace(this.content.duplicate());
   }

   public StompFrame retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public StompFrame replace(ByteBuf var1) {
      return new DefaultStompFrame(this.command, var1, this.headers.copy());
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public StompFrame retain() {
      this.content.retain();
      return this;
   }

   public StompFrame retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public StompFrame touch() {
      this.content.touch();
      return this;
   }

   public StompFrame touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public boolean release() {
      return this.content.release();
   }

   public boolean release(int var1) {
      return this.content.release(var1);
   }

   public String toString() {
      return "DefaultStompFrame{command=" + this.command + ", headers=" + this.headers + ", content=" + this.content.toString(CharsetUtil.UTF_8) + '}';
   }
}
