package net.minecraft.client.multiplayer.resolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FunctionalInterface
public interface ServerAddressResolver {
   Logger LOGGER = LogManager.getLogger();
   ServerAddressResolver SYSTEM = (var0) -> {
      try {
         InetAddress var1 = InetAddress.getByName(var0.getHost());
         return Optional.of(ResolvedServerAddress.from(new InetSocketAddress(var1, var0.getPort())));
      } catch (UnknownHostException var2) {
         LOGGER.debug("Couldn't resolve server {} address", var0.getHost(), var2);
         return Optional.empty();
      }
   };

   Optional<ResolvedServerAddress> resolve(ServerAddress var1);
}
