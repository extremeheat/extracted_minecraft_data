package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {
   private static final int HASH_CODE_PRIME = 31;
   private HttpMethod method;
   private String uri;

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3) {
      this(var1, var2, var3, true);
   }

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3, boolean var4) {
      super(var1, var4, false);
      this.method = (HttpMethod)ObjectUtil.checkNotNull(var2, "method");
      this.uri = (String)ObjectUtil.checkNotNull(var3, "uri");
   }

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3, HttpHeaders var4) {
      super(var1, var4);
      this.method = (HttpMethod)ObjectUtil.checkNotNull(var2, "method");
      this.uri = (String)ObjectUtil.checkNotNull(var3, "uri");
   }

   /** @deprecated */
   @Deprecated
   public HttpMethod getMethod() {
      return this.method();
   }

   public HttpMethod method() {
      return this.method;
   }

   /** @deprecated */
   @Deprecated
   public String getUri() {
      return this.uri();
   }

   public String uri() {
      return this.uri;
   }

   public HttpRequest setMethod(HttpMethod var1) {
      if (var1 == null) {
         throw new NullPointerException("method");
      } else {
         this.method = var1;
         return this;
      }
   }

   public HttpRequest setUri(String var1) {
      if (var1 == null) {
         throw new NullPointerException("uri");
      } else {
         this.uri = var1;
         return this;
      }
   }

   public HttpRequest setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   public int hashCode() {
      byte var1 = 1;
      int var2 = 31 * var1 + this.method.hashCode();
      var2 = 31 * var2 + this.uri.hashCode();
      var2 = 31 * var2 + super.hashCode();
      return var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpRequest)) {
         return false;
      } else {
         DefaultHttpRequest var2 = (DefaultHttpRequest)var1;
         return this.method().equals(var2.method()) && this.uri().equalsIgnoreCase(var2.uri()) && super.equals(var1);
      }
   }

   public String toString() {
      return HttpMessageUtil.appendRequest(new StringBuilder(256), this).toString();
   }
}
