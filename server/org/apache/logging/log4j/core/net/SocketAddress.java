package org.apache.logging.log4j.core.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidHost;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;

@Plugin(
   name = "SocketAddress",
   category = "Core",
   printObject = true
)
public class SocketAddress {
   private final InetSocketAddress socketAddress;

   public static SocketAddress getLoopback() {
      return new SocketAddress(InetAddress.getLoopbackAddress(), 0);
   }

   private SocketAddress(InetAddress var1, int var2) {
      super();
      this.socketAddress = new InetSocketAddress(var1, var2);
   }

   public InetSocketAddress getSocketAddress() {
      return this.socketAddress;
   }

   public int getPort() {
      return this.socketAddress.getPort();
   }

   public InetAddress getAddress() {
      return this.socketAddress.getAddress();
   }

   public String getHostName() {
      return this.socketAddress.getHostName();
   }

   @PluginBuilderFactory
   public static SocketAddress.Builder newBuilder() {
      return new SocketAddress.Builder();
   }

   // $FF: synthetic method
   SocketAddress(InetAddress var1, int var2, Object var3) {
      this(var1, var2);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<SocketAddress> {
      @PluginBuilderAttribute
      @ValidHost
      private InetAddress host;
      @PluginBuilderAttribute
      @ValidPort
      private int port;

      public Builder() {
         super();
      }

      public SocketAddress.Builder setHost(InetAddress var1) {
         this.host = var1;
         return this;
      }

      public SocketAddress.Builder setPort(int var1) {
         this.port = var1;
         return this;
      }

      public SocketAddress build() {
         return new SocketAddress(this.host, this.port);
      }
   }
}
