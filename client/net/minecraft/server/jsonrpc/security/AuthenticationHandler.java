package net.minecraft.server.jsonrpc.security;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;

@Sharable
public class AuthenticationHandler extends ChannelDuplexHandler {
   private final Logger LOGGER = LogUtils.getLogger();
   private static final AttributeKey<Boolean> AUTHENTICATED_KEY = AttributeKey.valueOf("authenticated");
   private static final AttributeKey<Boolean> ATTR_WEBSOCKET_ALLOWED = AttributeKey.valueOf("websocket_auth_allowed");
   private static final CharSequence SUBPROTOCOL_VALUE = "minecraft-v1";
   private static final String SUBPROTOCOL_HEADER_PREFIX;
   public static final String BEARER_PREFIX = "Bearer ";
   private final SecurityConfig securityConfig;
   private final Set<String> allowedOrigins;

   public AuthenticationHandler(SecurityConfig var1, String var2) {
      super();
      this.securityConfig = var1;
      this.allowedOrigins = Sets.newHashSet(var2.split(","));
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      String var3 = this.getClientIp(var1);
      if (var2 instanceof HttpRequest var4) {
         SecurityCheckResult var5 = this.performSecurityChecks(var4);
         if (!var5.isAllowed()) {
            this.LOGGER.debug("Authentication rejected for connection with ip {}: {}", var3, var5.getReason());
            var1.channel().attr(AUTHENTICATED_KEY).set(false);
            this.sendUnauthorizedResponse(var1, var5.getReason());
            return;
         }

         var1.channel().attr(AUTHENTICATED_KEY).set(true);
         if (var5.isTokenSentInSecWebsocketProtocol()) {
            var1.channel().attr(ATTR_WEBSOCKET_ALLOWED).set(Boolean.TRUE);
         }
      }

      Boolean var6 = (Boolean)var1.channel().attr(AUTHENTICATED_KEY).get();
      if (Boolean.TRUE.equals(var6)) {
         super.channelRead(var1, var2);
      } else {
         this.LOGGER.debug("Dropping unauthenticated connection with ip {}", var3);
         var1.close();
      }

   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpResponse var4) {
         if (var4.status().code() == HttpResponseStatus.SWITCHING_PROTOCOLS.code() && var1.channel().attr(ATTR_WEBSOCKET_ALLOWED).get() != null && ((Boolean)var1.channel().attr(ATTR_WEBSOCKET_ALLOWED).get()).equals(Boolean.TRUE)) {
            var4.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, SUBPROTOCOL_VALUE);
         }
      }

      super.write(var1, var2, var3);
   }

   private SecurityCheckResult performSecurityChecks(HttpRequest var1) {
      String var2 = this.parseTokenInAuthorizationHeader(var1);
      if (var2 != null) {
         return this.isValidApiKey(var2) ? AuthenticationHandler.SecurityCheckResult.allowed() : AuthenticationHandler.SecurityCheckResult.denied("Invalid API key");
      } else {
         String var3 = this.parseTokenInSecWebsocketProtocolHeader(var1);
         if (var3 != null) {
            if (!this.isAllowedOriginHeader(var1)) {
               return AuthenticationHandler.SecurityCheckResult.denied("Origin Not Allowed");
            } else {
               return this.isValidApiKey(var3) ? AuthenticationHandler.SecurityCheckResult.allowed(true) : AuthenticationHandler.SecurityCheckResult.denied("Invalid API key");
            }
         } else {
            return AuthenticationHandler.SecurityCheckResult.denied("Missing API key");
         }
      }
   }

   private boolean isAllowedOriginHeader(HttpRequest var1) {
      String var2 = var1.headers().get(HttpHeaderNames.ORIGIN);
      return var2 != null && !var2.isEmpty() ? this.allowedOrigins.contains(var2) : false;
   }

   @Nullable
   private String parseTokenInAuthorizationHeader(HttpRequest var1) {
      String var2 = var1.headers().get(HttpHeaderNames.AUTHORIZATION);
      return var2 != null && var2.startsWith("Bearer ") ? var2.substring("Bearer ".length()).trim() : null;
   }

   @Nullable
   private String parseTokenInSecWebsocketProtocolHeader(HttpRequest var1) {
      String var2 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
      return var2 != null && var2.startsWith(SUBPROTOCOL_HEADER_PREFIX) ? var2.substring(SUBPROTOCOL_HEADER_PREFIX.length()).trim() : null;
   }

   public boolean isValidApiKey(String var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
         byte[] var3 = this.securityConfig.secretKey().getBytes(StandardCharsets.UTF_8);
         return MessageDigest.isEqual(var2, var3);
      }
   }

   private String getClientIp(ChannelHandlerContext var1) {
      InetSocketAddress var2 = (InetSocketAddress)var1.channel().remoteAddress();
      return var2.getAddress().getHostAddress();
   }

   private void sendUnauthorizedResponse(ChannelHandlerContext var1, String var2) {
      String var3 = "{\"error\":\"Unauthorized\",\"message\":\"" + var2 + "\"}";
      byte[] var4 = var3.getBytes(StandardCharsets.UTF_8);
      DefaultFullHttpResponse var5 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.wrappedBuffer(var4));
      var5.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
      var5.headers().set(HttpHeaderNames.CONTENT_LENGTH, var4.length);
      var5.headers().set(HttpHeaderNames.CONNECTION, "close");
      var1.writeAndFlush(var5).addListener((var1x) -> var1.close());
   }

   static {
      SUBPROTOCOL_HEADER_PREFIX = String.format("%s,", SUBPROTOCOL_VALUE);
   }

   static class SecurityCheckResult {
      private final boolean allowed;
      private final String reason;
      private final boolean tokenSentInSecWebsocketProtocol;

      private SecurityCheckResult(boolean var1, String var2, boolean var3) {
         super();
         this.allowed = var1;
         this.reason = var2;
         this.tokenSentInSecWebsocketProtocol = var3;
      }

      public static SecurityCheckResult allowed() {
         return new SecurityCheckResult(true, (String)null, false);
      }

      public static SecurityCheckResult allowed(boolean var0) {
         return new SecurityCheckResult(true, (String)null, var0);
      }

      public static SecurityCheckResult denied(String var0) {
         return new SecurityCheckResult(false, var0, false);
      }

      public boolean isAllowed() {
         return this.allowed;
      }

      public String getReason() {
         return this.reason;
      }

      public boolean isTokenSentInSecWebsocketProtocol() {
         return this.tokenSentInSecWebsocketProtocol;
      }
   }
}
