package net.minecraft.client.multiplayer.resolver;

import com.google.common.net.HostAndPort;
import com.mojang.logging.LogUtils;
import java.net.IDN;
import java.net.InetSocketAddress;
import java.util.Objects;
import net.minecraft.network.QueryProperties;
import net.minecraft.network.ServerConnectionDetails;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class ServerAddress {
   public static final char USER_SEPARATOR = '@';
   private static final Logger LOGGER = LogUtils.getLogger();
   private final HostAndPort hostAndPort;
   private final QueryProperties properties;
   private static final ServerAddress INVALID;

   public ServerAddress(final String host, final int port, final QueryProperties properties) {
      this(HostAndPort.fromParts(host, port), properties);
   }

   private ServerAddress(final HostAndPort hostAndPort, final QueryProperties properties) {
      super();
      this.hostAndPort = hostAndPort;
      this.properties = properties;
   }

   public String getHost() {
      try {
         return IDN.toASCII(this.hostAndPort.getHost());
      } catch (IllegalArgumentException var2) {
         return "";
      }
   }

   public int getPort() {
      return this.hostAndPort.getPort();
   }

   public ServerAddress withHostAndPort(final String host, final int port) {
      return new ServerAddress(host, port, this.properties);
   }

   public QueryProperties getProperties() {
      return this.properties;
   }

   public ServerConnectionDetails createConnectionDetails(final InetSocketAddress resolvedAddress) {
      return ServerConnectionDetails.remoteConnection(this.hostAndPort.getHost(), this.hostAndPort.getPort(), resolvedAddress, this.properties);
   }

   public static ServerAddress parseString(@Nullable String input) {
      if (input == null) {
         return INVALID;
      } else {
         try {
            QueryProperties queryProperties = QueryProperties.EMPTY;
            int queryStart = input.lastIndexOf(63);
            if (queryStart >= 0) {
               queryProperties = QueryProperties.fromString(input.substring(queryStart + 1));
               input = input.substring(0, queryStart);
            }

            int userEnd = input.indexOf(64);
            if (userEnd >= 0) {
               queryProperties = queryProperties.with("_id", input.substring(0, userEnd));
               input = input.substring(userEnd + 1);
            }

            HostAndPort result = HostAndPort.fromString(input).withDefaultPort(25565);
            return result.getHost().isEmpty() ? INVALID : new ServerAddress(result, queryProperties.forceParse());
         } catch (Exception e) {
            LOGGER.info("Failed to parse URL {}", input, e);
            return INVALID;
         }
      }
   }

   public static boolean isValidAddress(final String input) {
      ServerAddress result = parseString(input);
      return result != INVALID && !result.getHost().isEmpty();
   }

   public static int parsePort(final String str) {
      try {
         return Integer.parseInt(str.trim());
      } catch (Exception var2) {
         return 25565;
      }
   }

   public String toString() {
      if (this.properties.isEmpty()) {
         return this.hostAndPort.toString();
      } else {
         String var10000 = String.valueOf(this.hostAndPort);
         return var10000 + "?" + String.valueOf(this.properties);
      }
   }

   public boolean equals(final Object o) {
      boolean var10000;
      if (o instanceof ServerAddress that) {
         if (Objects.equals(this.hostAndPort, that.hostAndPort) && Objects.equals(this.properties, that.properties)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      int result = 1;
      result = 31 * result + this.hostAndPort.hashCode();
      result = 31 * result + this.properties.hashCode();
      return result;
   }

   static {
      INVALID = new ServerAddress(HostAndPort.fromParts("server.invalid", 25565), QueryProperties.EMPTY);
   }
}
