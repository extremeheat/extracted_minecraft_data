package net.minecraft.server.jsonrpc.security;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;

@Sharable
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
   private final Logger LOGGER = LogUtils.getLogger();
   private static final AttributeKey<Boolean> AUTHENTICATED_KEY = AttributeKey.valueOf("authenticated");
   public static final String AUTH_HEADER = "Authorization";
   public static final String BEARER_PREFIX = "Bearer ";
   private final SecurityConfig securityConfig;

   public AuthenticationHandler(SecurityConfig var1) {
      super();
      this.securityConfig = var1;
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
      }

      Boolean var6 = (Boolean)var1.channel().attr(AUTHENTICATED_KEY).get();
      if (Boolean.TRUE.equals(var6)) {
         super.channelRead(var1, var2);
      } else {
         this.LOGGER.debug("Dropping unauthenticated connection with ip {}", var3);
         var1.close();
      }

   }

   private SecurityCheckResult performSecurityChecks(HttpRequest var1) {
      return !this.validateAuthentication(var1) ? AuthenticationHandler.SecurityCheckResult.denied("Invalid or missing API key") : AuthenticationHandler.SecurityCheckResult.allowed();
   }

   private boolean validateAuthentication(HttpRequest var1) {
      String var2 = var1.headers().get("Authorization");
      if (var2 != null && !var2.trim().isEmpty()) {
         if (var2.startsWith("Bearer ")) {
            String var3 = var2.substring("Bearer ".length()).trim();
            return this.isValidApiKey(var3);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isValidApiKey(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
         byte[] var3 = this.securityConfig.secretKey().getBytes(StandardCharsets.UTF_8);
         return MessageDigest.isEqual(var2, var3);
      } else {
         return false;
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

   static class SecurityCheckResult {
      private final boolean allowed;
      private final String reason;

      private SecurityCheckResult(boolean var1, String var2) {
         super();
         this.allowed = var1;
         this.reason = var2;
      }

      public static SecurityCheckResult allowed() {
         return new SecurityCheckResult(true, (String)null);
      }

      public static SecurityCheckResult denied(String var0) {
         return new SecurityCheckResult(false, var0);
      }

      public boolean isAllowed() {
         return this.allowed;
      }

      public String getReason() {
         return this.reason;
      }
   }
}
