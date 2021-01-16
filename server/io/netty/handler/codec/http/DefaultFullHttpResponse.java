package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;

public class DefaultFullHttpResponse extends DefaultHttpResponse implements FullHttpResponse {
   private final ByteBuf content;
   private final HttpHeaders trailingHeaders;
   private int hash;

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2) {
      this(var1, var2, Unpooled.buffer(0));
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, ByteBuf var3) {
      this(var1, var2, var3, true);
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3) {
      this(var1, var2, Unpooled.buffer(0), var3, false);
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3, boolean var4) {
      this(var1, var2, Unpooled.buffer(0), var3, var4);
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, ByteBuf var3, boolean var4) {
      this(var1, var2, var3, var4, false);
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, ByteBuf var3, boolean var4, boolean var5) {
      super(var1, var2, var4, var5);
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var3, "content");
      this.trailingHeaders = (HttpHeaders)(var5 ? new CombinedHttpHeaders(var4) : new DefaultHttpHeaders(var4));
   }

   public DefaultFullHttpResponse(HttpVersion var1, HttpResponseStatus var2, ByteBuf var3, HttpHeaders var4, HttpHeaders var5) {
      super(var1, var2, var4);
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var3, "content");
      this.trailingHeaders = (HttpHeaders)ObjectUtil.checkNotNull(var5, "trailingHeaders");
   }

   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   public ByteBuf content() {
      return this.content;
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public FullHttpResponse retain() {
      this.content.retain();
      return this;
   }

   public FullHttpResponse retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public FullHttpResponse touch() {
      this.content.touch();
      return this;
   }

   public FullHttpResponse touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public boolean release() {
      return this.content.release();
   }

   public boolean release(int var1) {
      return this.content.release(var1);
   }

   public FullHttpResponse setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   public FullHttpResponse setStatus(HttpResponseStatus var1) {
      super.setStatus(var1);
      return this;
   }

   public FullHttpResponse copy() {
      return this.replace(this.content().copy());
   }

   public FullHttpResponse duplicate() {
      return this.replace(this.content().duplicate());
   }

   public FullHttpResponse retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public FullHttpResponse replace(ByteBuf var1) {
      DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(this.protocolVersion(), this.status(), var1, this.headers().copy(), this.trailingHeaders().copy());
      var2.setDecoderResult(this.decoderResult());
      return var2;
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         if (this.content().refCnt() != 0) {
            try {
               var1 = 31 + this.content().hashCode();
            } catch (IllegalReferenceCountException var3) {
               var1 = 31;
            }
         } else {
            var1 = 31;
         }

         var1 = 31 * var1 + this.trailingHeaders().hashCode();
         var1 = 31 * var1 + super.hashCode();
         this.hash = var1;
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultFullHttpResponse)) {
         return false;
      } else {
         DefaultFullHttpResponse var2 = (DefaultFullHttpResponse)var1;
         return super.equals(var2) && this.content().equals(var2.content()) && this.trailingHeaders().equals(var2.trailingHeaders());
      }
   }

   public String toString() {
      return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
   }
}
