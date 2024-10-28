package net.minecraft.client.multiplayer.resolver;

import com.google.common.net.HostAndPort;
import com.mojang.logging.LogUtils;
import java.net.IDN;
import org.slf4j.Logger;

public final class ServerAddress {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final HostAndPort hostAndPort;
   private static final ServerAddress INVALID = new ServerAddress(HostAndPort.fromParts("server.invalid", 25565));

   public ServerAddress(String var1, int var2) {
      this(HostAndPort.fromParts(var1, var2));
   }

   private ServerAddress(HostAndPort var1) {
      super();
      this.hostAndPort = var1;
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

   public static ServerAddress parseString(String var0) {
      if (var0 == null) {
         return INVALID;
      } else {
         try {
            HostAndPort var1 = HostAndPort.fromString(var0).withDefaultPort(25565);
            return var1.getHost().isEmpty() ? INVALID : new ServerAddress(var1);
         } catch (IllegalArgumentException var2) {
            LOGGER.info("Failed to parse URL {}", var0, var2);
            return INVALID;
         }
      }
   }

   public static boolean isValidAddress(String var0) {
      try {
         HostAndPort var1 = HostAndPort.fromString(var0);
         String var2 = var1.getHost();
         if (!var2.isEmpty()) {
            IDN.toASCII(var2);
            return true;
         }
      } catch (IllegalArgumentException var3) {
      }

      return false;
   }

   static int parsePort(String var0) {
      try {
         return Integer.parseInt(var0.trim());
      } catch (Exception var2) {
         return 25565;
      }
   }

   public String toString() {
      return this.hostAndPort.toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ServerAddress ? this.hostAndPort.equals(((ServerAddress)var1).hostAndPort) : false;
      }
   }

   public int hashCode() {
      return this.hostAndPort.hashCode();
   }
}
