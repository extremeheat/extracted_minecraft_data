package io.netty.handler.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4ClientDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ClientEncoder;
import io.netty.handler.codec.socksx.v4.Socks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v4.Socks4CommandType;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class Socks4ProxyHandler extends ProxyHandler {
   private static final String PROTOCOL = "socks4";
   private static final String AUTH_USERNAME = "username";
   private final String username;
   private String decoderName;
   private String encoderName;

   public Socks4ProxyHandler(SocketAddress var1) {
      this(var1, (String)null);
   }

   public Socks4ProxyHandler(SocketAddress var1, String var2) {
      super(var1);
      if (var2 != null && var2.isEmpty()) {
         var2 = null;
      }

      this.username = var2;
   }

   public String protocol() {
      return "socks4";
   }

   public String authScheme() {
      return this.username != null ? "username" : "none";
   }

   public String username() {
      return this.username;
   }

   protected void addCodec(ChannelHandlerContext var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      String var3 = var1.name();
      Socks4ClientDecoder var4 = new Socks4ClientDecoder();
      var2.addBefore(var3, (String)null, var4);
      this.decoderName = var2.context((ChannelHandler)var4).name();
      this.encoderName = this.decoderName + ".encoder";
      var2.addBefore(var3, this.encoderName, Socks4ClientEncoder.INSTANCE);
   }

   protected void removeEncoder(ChannelHandlerContext var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      var2.remove(this.encoderName);
   }

   protected void removeDecoder(ChannelHandlerContext var1) throws Exception {
      ChannelPipeline var2 = var1.pipeline();
      var2.remove(this.decoderName);
   }

   protected Object newInitialMessage(ChannelHandlerContext var1) throws Exception {
      InetSocketAddress var2 = (InetSocketAddress)this.destinationAddress();
      String var3;
      if (var2.isUnresolved()) {
         var3 = var2.getHostString();
      } else {
         var3 = var2.getAddress().getHostAddress();
      }

      return new DefaultSocks4CommandRequest(Socks4CommandType.CONNECT, var3, var2.getPort(), this.username != null ? this.username : "");
   }

   protected boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception {
      Socks4CommandResponse var3 = (Socks4CommandResponse)var2;
      Socks4CommandStatus var4 = var3.status();
      if (var4 == Socks4CommandStatus.SUCCESS) {
         return true;
      } else {
         throw new ProxyConnectException(this.exceptionMessage("status: " + var4));
      }
   }
}
