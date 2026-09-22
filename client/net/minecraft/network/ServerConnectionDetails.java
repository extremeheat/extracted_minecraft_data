package net.minecraft.network;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;

public record ServerConnectionDetails(String host, int port, QueryProperties properties) {
   public static final char QUERY_START = '?';
   public static final char QUERY_SEPARATOR = '&';
   public static final char QUERY_KEY_VALUE_SEPARATOR = '=';
   public static final String KEY_ORIGIN_ADDRESS = "_o";
   public static final String KEY_ID = "_id";

   public ServerConnectionDetails {
      super();
   }

   private static ServerConnectionDetails plain(final String hostname, final int port) {
      return new ServerConnectionDetails(hostname, port, QueryProperties.EMPTY);
   }

   public static ServerConnectionDetails plain(final InetSocketAddress socketAddress) {
      return plain(socketAddress.getHostName(), socketAddress.getPort());
   }

   public static ServerConnectionDetails local(final SocketAddress socketAddress) {
      if (socketAddress instanceof InetSocketAddress inetSocketAddress) {
         return plain(inetSocketAddress);
      } else {
         return plain(socketAddress.toString(), 0);
      }
   }

   public static ServerConnectionDetails remoteConnection(final String originalHostname, final int originalPort, final InetSocketAddress resolvedAddress, final QueryProperties extraProperties) {
      boolean isOriginalAndResolvedSame = originalHostname.equals(resolvedAddress.getHostName()) && originalPort == resolvedAddress.getPort();
      boolean hasExistingResolvedAddress = extraProperties.get("_o") != null;
      if (isOriginalAndResolvedSame && !hasExistingResolvedAddress) {
         return new ServerConnectionDetails(originalHostname, originalPort, extraProperties);
      } else {
         ImmutableMap.Builder<String, String> propertiesBuilder = ImmutableMap.builder();
         if (hasExistingResolvedAddress) {
            propertiesBuilder.putAll(Maps.filterKeys(extraProperties.asMap(), (k) -> !k.equals("_o")));
         } else {
            propertiesBuilder.putAll(extraProperties.asMap());
         }

         if (!isOriginalAndResolvedSame) {
            propertiesBuilder.put("_o", originalHostname + ":" + originalPort);
         }

         return new ServerConnectionDetails(resolvedAddress.getHostName(), resolvedAddress.getPort(), QueryProperties.fromMap(propertiesBuilder.buildKeepingLast()));
      }
   }

   public String hostForIntentPacket() {
      if (this.properties.isEmpty()) {
         return this.host;
      } else {
         String var10000 = this.host;
         return var10000 + "?" + this.properties.asString();
      }
   }

   public static ServerConnectionDetails fromIntentPacket(final String host, final int port) {
      int queryStart = host.indexOf(63);
      String actualHost;
      QueryProperties properties;
      if (queryStart >= 0) {
         actualHost = host.substring(0, queryStart);
         properties = QueryProperties.fromString(host.substring(queryStart + 1));
      } else {
         actualHost = host;
         properties = QueryProperties.EMPTY;
      }

      return new ServerConnectionDetails(actualHost, port, properties);
   }

   public static ServerConnectionDetails fromIntentPacket(final ClientIntentionPacket packet) {
      return fromIntentPacket(packet.hostName(), packet.port());
   }
}
