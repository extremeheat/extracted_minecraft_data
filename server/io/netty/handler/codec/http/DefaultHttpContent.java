package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

public class DefaultHttpContent extends DefaultHttpObject implements HttpContent {
   private final ByteBuf content;

   public DefaultHttpContent(ByteBuf var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("content");
      } else {
         this.content = var1;
      }
   }

   public ByteBuf content() {
      return this.content;
   }

   public HttpContent copy() {
      return this.replace(this.content.copy());
   }

   public HttpContent duplicate() {
      return this.replace(this.content.duplicate());
   }

   public HttpContent retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public HttpContent replace(ByteBuf var1) {
      return new DefaultHttpContent(var1);
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public HttpContent retain() {
      this.content.retain();
      return this;
   }

   public HttpContent retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public HttpContent touch() {
      this.content.touch();
      return this;
   }

   public HttpContent touch(Object var1) {
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
      return StringUtil.simpleClassName((Object)this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
   }
}
