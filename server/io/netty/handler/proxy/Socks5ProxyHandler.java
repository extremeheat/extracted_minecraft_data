package io.netty.handler.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;

public final class Socks5ProxyHandler extends ProxyHandler {
   private static final String PROTOCOL = "socks5";
   private static final String AUTH_PASSWORD = "password";
   private static final Socks5InitialRequest INIT_REQUEST_NO_AUTH;
   private static final Socks5InitialRequest INIT_REQUEST_PASSWORD;
   private final String username;
   private final String password;
   private String decoderName;
   private String encoderName;

   public Socks5ProxyHandler(SocketAddress var1) {
      this(var1, (String)null, (String)null);
   }

   public Socks5ProxyHandler(SocketAddress var1, String var2, String var3) {
      super(var1);
      if (var2 != null && var2.isEmpty()) {
         var2 = null;
      }

      if (var3 != null && var3.isEmpty()) {
         var3 = null;
      }

      this.username = var2;
      this.password = var3;
   }

   public String protocol() {
      return "socks5";
   }

   public String authScheme() {
      return this.socksAuthMethod() == Socks5AuthMethod.PASSWORD ? "password" : "none";
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
      Socks5InitialResponseDecoder var4 = new Socks5InitialResponseDecoder();
      var2.addBefore(var3, (String)null, var4);
      this.decoderName = var2.context((ChannelHandler)var4).name();
      this.encoderName = this.decoderName + ".encoder";
      var2.addBefore(var3, this.encoderName, Socks5ClientEncoder.DEFAULT);
   }

   protected void removeEncoder(ChannelHandlerContext var1) throws Exception {
      var1.pipeline().remove(this.encoderName);
   }

   protected void removeDecoder(ChannelHandlerContext var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      if (var2.context(this.decoderName) != null) {
         var2.remove(this.decoderName);
      }

   }

   protected Object newInitialMessage(ChannelHandlerContext var1) throws Exception {
      return this.socksAuthMethod() == Socks5AuthMethod.PASSWORD ? INIT_REQUEST_PASSWORD : INIT_REQUEST_NO_AUTH;
   }

   protected boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof Socks5InitialResponse) {
         Socks5InitialResponse var6 = (Socks5InitialResponse)var2;
         Socks5AuthMethod var4 = this.socksAuthMethod();
         if (var6.authMethod() != Socks5AuthMethod.NO_AUTH && var6.authMethod() != var4) {
            throw new ProxyConnectException(this.exceptionMessage("unexpected authMethod: " + var6.authMethod()));
         } else {
            if (var4 == Socks5AuthMethod.NO_AUTH) {
               this.sendConnectCommand(var1);
            } else {
               if (var4 != Socks5AuthMethod.PASSWORD) {
                  throw new Error();
               }

               var1.pipeline().replace((String)this.decoderName, this.decoderName, new Socks5PasswordAuthResponseDecoder());
               this.sendToProxyServer(new DefaultSocks5PasswordAuthRequest(this.username != null ? this.username : "", this.password != null ? this.password : ""));
            }

            return false;
         }
      } else if (var2 instanceof Socks5PasswordAuthResponse) {
         Socks5PasswordAuthResponse var5 = (Socks5PasswordAuthResponse)var2;
         if (var5.status() != Socks5PasswordAuthStatus.SUCCESS) {
            throw new ProxyConnectException(this.exceptionMessage("authStatus: " + var5.status()));
         } else {
            this.sendConnectCommand(var1);
            return false;
         }
      } else {
         Socks5CommandResponse var3 = (Socks5CommandResponse)var2;
         if (var3.status() != Socks5CommandStatus.SUCCESS) {
            throw new ProxyConnectException(this.exceptionMessage("status: " + var3.status()));
         } else {
            return true;
         }
      }
   }

   private Socks5AuthMethod socksAuthMethod() {
      Socks5AuthMethod var1;
      if (this.username == null && this.password == null) {
         var1 = Socks5AuthMethod.NO_AUTH;
      } else {
         var1 = Socks5AuthMethod.PASSWORD;
      }

      return var1;
   }

   private void sendConnectCommand(ChannelHandlerContext var1) throws Exception {
      InetSocketAddress var2 = (InetSocketAddress)this.destinationAddress();
      Socks5AddressType var3;
      String var4;
      if (var2.isUnresolved()) {
         var3 = Socks5AddressType.DOMAIN;
         var4 = var2.getHostString();
      } else {
         var4 = var2.getAddress().getHostAddress();
         if (NetUtil.isValidIpV4Address(var4)) {
            var3 = Socks5AddressType.IPv4;
         } else {
            if (!NetUtil.isValidIpV6Address(var4)) {
               throw new ProxyConnectException(this.exceptionMessage("unknown address type: " + StringUtil.simpleClassName((Object)var4)));
            }

            var3 = Socks5AddressType.IPv6;
         }
      }

      var1.pipeline().replace((String)this.decoderName, this.decoderName, new Socks5CommandResponseDecoder());
      this.sendToProxyServer(new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, var3, var4, var2.getPort()));
   }

   static {
      INIT_REQUEST_NO_AUTH = new DefaultSocks5InitialRequest(Collections.singletonList(Socks5AuthMethod.NO_AUTH));
      INIT_REQUEST_PASSWORD = new DefaultSocks5InitialRequest(Arrays.asList(Socks5AuthMethod.NO_AUTH, Socks5AuthMethod.PASSWORD));
   }
}
