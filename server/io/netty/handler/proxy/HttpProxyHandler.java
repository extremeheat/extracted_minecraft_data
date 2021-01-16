package io.netty.handler.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class HttpProxyHandler extends ProxyHandler {
   private static final String PROTOCOL = "http";
   private static final String AUTH_BASIC = "basic";
   private final HttpClientCodec codec;
   private final String username;
   private final String password;
   private final CharSequence authorization;
   private final boolean ignoreDefaultPortsInConnectHostHeader;
   private HttpResponseStatus status;
   private HttpHeaders headers;

   public HttpProxyHandler(SocketAddress var1) {
      this(var1, (HttpHeaders)null);
   }

   public HttpProxyHandler(SocketAddress var1, HttpHeaders var2) {
      this(var1, var2, false);
   }

   public HttpProxyHandler(SocketAddress var1, HttpHeaders var2, boolean var3) {
      super(var1);
      this.codec = new HttpClientCodec();
      this.username = null;
      this.password = null;
      this.authorization = null;
      this.headers = var2;
      this.ignoreDefaultPortsInConnectHostHeader = var3;
   }

   public HttpProxyHandler(SocketAddress var1, String var2, String var3) {
      this(var1, var2, var3, (HttpHeaders)null);
   }

   public HttpProxyHandler(SocketAddress var1, String var2, String var3, HttpHeaders var4) {
      this(var1, var2, var3, var4, false);
   }

   public HttpProxyHandler(SocketAddress var1, String var2, String var3, HttpHeaders var4, boolean var5) {
      super(var1);
      this.codec = new HttpClientCodec();
      if (var2 == null) {
         throw new NullPointerException("username");
      } else if (var3 == null) {
         throw new NullPointerException("password");
      } else {
         this.username = var2;
         this.password = var3;
         ByteBuf var6 = Unpooled.copiedBuffer((CharSequence)(var2 + ':' + var3), CharsetUtil.UTF_8);
         ByteBuf var7 = Base64.encode(var6, false);
         this.authorization = new AsciiString("Basic " + var7.toString(CharsetUtil.US_ASCII));
         var6.release();
         var7.release();
         this.headers = var4;
         this.ignoreDefaultPortsInConnectHostHeader = var5;
      }
   }

   public String protocol() {
      return "http";
   }

   public String authScheme() {
      return this.authorization != null ? "basic" : "none";
   }

   public String username() {
      return this.username;
   }

   public String password() {
      return this.password;
   }

   protected void addCodec(ChannelHandlerContext var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      String var3 = var1.name();
      var2.addBefore(var3, (String)null, this.codec);
   }

   protected void removeEncoder(ChannelHandlerContext var1) throws Exception {
      this.codec.removeOutboundHandler();
   }

   protected void removeDecoder(ChannelHandlerContext var1) throws Exception {
      this.codec.removeInboundHandler();
   }

   protected Object newInitialMessage(ChannelHandlerContext var1) throws Exception {
      InetSocketAddress var2 = (InetSocketAddress)this.destinationAddress();
      String var3 = HttpUtil.formatHostnameForHttp(var2);
      int var4 = var2.getPort();
      String var5 = var3 + ":" + var4;
      String var6 = !this.ignoreDefaultPortsInConnectHostHeader || var4 != 80 && var4 != 443 ? var5 : var3;
      DefaultFullHttpRequest var7 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, var5, Unpooled.EMPTY_BUFFER, false);
      var7.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)var6);
      if (this.authorization != null) {
         var7.headers().set((CharSequence)HttpHeaderNames.PROXY_AUTHORIZATION, (Object)this.authorization);
      }

      if (this.headers != null) {
         var7.headers().add(this.headers);
      }

      return var7;
   }

   protected boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpResponse) {
         if (this.status != null) {
            throw new ProxyConnectException(this.exceptionMessage("too many responses"));
         }

         this.status = ((HttpResponse)var2).status();
      }

      boolean var3 = var2 instanceof LastHttpContent;
      if (var3) {
         if (this.status == null) {
            throw new ProxyConnectException(this.exceptionMessage("missing response"));
         }

         if (this.status.code() != 200) {
            throw new ProxyConnectException(this.exceptionMessage("status: " + this.status));
         }
      }

      return var3;
   }
}
