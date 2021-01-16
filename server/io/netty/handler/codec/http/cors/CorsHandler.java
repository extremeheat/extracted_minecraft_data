package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CorsHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
   private static final String ANY_ORIGIN = "*";
   private static final String NULL_ORIGIN = "null";
   private CorsConfig config;
   private HttpRequest request;
   private final List<CorsConfig> configList;
   private boolean isShortCircuit;

   public CorsHandler(CorsConfig var1) {
      this(Collections.singletonList(ObjectUtil.checkNotNull(var1, "config")), var1.isShortCircuit());
   }

   public CorsHandler(List<CorsConfig> var1, boolean var2) {
      super();
      ObjectUtil.checkNonEmpty((Collection)var1, "configList");
      this.configList = var1;
      this.isShortCircuit = var2;
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         this.request = (HttpRequest)var2;
         String var3 = this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
         this.config = this.getForOrigin(var3);
         if (isPreflightRequest(this.request)) {
            this.handlePreflight(var1, this.request);
            return;
         }

         if (this.isShortCircuit && var3 != null && this.config == null) {
            forbidden(var1, this.request);
            return;
         }
      }

      var1.fireChannelRead(var2);
   }

   private void handlePreflight(ChannelHandlerContext var1, HttpRequest var2) {
      DefaultFullHttpResponse var3 = new DefaultFullHttpResponse(var2.protocolVersion(), HttpResponseStatus.OK, true, true);
      if (this.setOrigin(var3)) {
         this.setAllowMethods(var3);
         this.setAllowHeaders(var3);
         this.setAllowCredentials(var3);
         this.setMaxAge(var3);
         this.setPreflightHeaders(var3);
      }

      if (!var3.headers().contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH)) {
         var3.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
      }

      ReferenceCountUtil.release(var2);
      respond(var1, var2, var3);
   }

   private void setPreflightHeaders(HttpResponse var1) {
      var1.headers().add(this.config.preflightResponseHeaders());
   }

   private CorsConfig getForOrigin(String var1) {
      Iterator var2 = this.configList.iterator();

      CorsConfig var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (CorsConfig)var2.next();
         if (var3.isAnyOriginSupported()) {
            return var3;
         }

         if (var3.origins().contains(var1)) {
            return var3;
         }
      } while(!var3.isNullOriginAllowed() && !"null".equals(var1));

      return var3;
   }

   private boolean setOrigin(HttpResponse var1) {
      String var2 = this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
      if (var2 != null && this.config != null) {
         if ("null".equals(var2) && this.config.isNullOriginAllowed()) {
            setNullOrigin(var1);
            return true;
         }

         if (this.config.isAnyOriginSupported()) {
            if (this.config.isCredentialsAllowed()) {
               this.echoRequestOrigin(var1);
               setVaryHeader(var1);
            } else {
               setAnyOrigin(var1);
            }

            return true;
         }

         if (this.config.origins().contains(var2)) {
            setOrigin(var1, var2);
            setVaryHeader(var1);
            return true;
         }

         logger.debug("Request origin [{}]] was not among the configured origins [{}]", var2, this.config.origins());
      }

      return false;
   }

   private void echoRequestOrigin(HttpResponse var1) {
      setOrigin(var1, this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN));
   }

   private static void setVaryHeader(HttpResponse var0) {
      var0.headers().set((CharSequence)HttpHeaderNames.VARY, (Object)HttpHeaderNames.ORIGIN);
   }

   private static void setAnyOrigin(HttpResponse var0) {
      setOrigin(var0, "*");
   }

   private static void setNullOrigin(HttpResponse var0) {
      setOrigin(var0, "null");
   }

   private static void setOrigin(HttpResponse var0, String var1) {
      var0.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, (Object)var1);
   }

   private void setAllowCredentials(HttpResponse var1) {
      if (this.config.isCredentialsAllowed() && !var1.headers().get((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals("*")) {
         var1.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)"true");
      }

   }

   private static boolean isPreflightRequest(HttpRequest var0) {
      HttpHeaders var1 = var0.headers();
      return var0.method().equals(HttpMethod.OPTIONS) && var1.contains((CharSequence)HttpHeaderNames.ORIGIN) && var1.contains((CharSequence)HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
   }

   private void setExposeHeaders(HttpResponse var1) {
      if (!this.config.exposedHeaders().isEmpty()) {
         var1.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, (Iterable)this.config.exposedHeaders());
      }

   }

   private void setAllowMethods(HttpResponse var1) {
      var1.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, (Iterable)this.config.allowedRequestMethods());
   }

   private void setAllowHeaders(HttpResponse var1) {
      var1.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, (Iterable)this.config.allowedRequestHeaders());
   }

   private void setMaxAge(HttpResponse var1) {
      var1.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, (Object)this.config.maxAge());
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.config != null && this.config.isCorsSupportEnabled() && var2 instanceof HttpResponse) {
         HttpResponse var4 = (HttpResponse)var2;
         if (this.setOrigin(var4)) {
            this.setAllowCredentials(var4);
            this.setExposeHeaders(var4);
         }
      }

      var1.write(var2, var3);
   }

   private static void forbidden(ChannelHandlerContext var0, HttpRequest var1) {
      DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(var1.protocolVersion(), HttpResponseStatus.FORBIDDEN);
      var2.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
      ReferenceCountUtil.release(var1);
      respond(var0, var1, var2);
   }

   private static void respond(ChannelHandlerContext var0, HttpRequest var1, HttpResponse var2) {
      boolean var3 = HttpUtil.isKeepAlive(var1);
      HttpUtil.setKeepAlive(var2, var3);
      ChannelFuture var4 = var0.writeAndFlush(var2);
      if (!var3) {
         var4.addListener(ChannelFutureListener.CLOSE);
      }

   }
}
