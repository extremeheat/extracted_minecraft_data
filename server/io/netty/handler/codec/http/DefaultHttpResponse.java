package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
   private HttpResponseStatus status;

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2) {
      this(var1, var2, true, false);
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3, boolean var4) {
      super(var1, var3, var4);
      this.status = (HttpResponseStatus)ObjectUtil.checkNotNull(var2, "status");
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, HttpHeaders var3) {
      super(var1, var3);
      this.status = (HttpResponseStatus)ObjectUtil.checkNotNull(var2, "status");
   }

   /** @deprecated */
   @Deprecated
   public HttpResponseStatus getStatus() {
      return this.status();
   }

   public HttpResponseStatus status() {
      return this.status;
   }

   public HttpResponse setStatus(HttpResponseStatus var1) {
      if (var1 == null) {
         throw new NullPointerException("status");
      } else {
         this.status = var1;
         return this;
      }
   }

   public HttpResponse setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   public String toString() {
      return HttpMessageUtil.appendResponse(new StringBuilder(256), this).toString();
   }
}
