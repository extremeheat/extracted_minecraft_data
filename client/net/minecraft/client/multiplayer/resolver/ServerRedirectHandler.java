package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.util.Hashtable;
import java.util.Optional;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import org.slf4j.Logger;

@FunctionalInterface
public interface ServerRedirectHandler {
   Logger LOGGER = LogUtils.getLogger();
   ServerRedirectHandler EMPTY = (var0) -> {
      return Optional.empty();
   };

   Optional<ServerAddress> lookupRedirect(ServerAddress var1);

   static ServerRedirectHandler createDnsSrvRedirectHandler() {
      InitialDirContext var0;
      try {
         String var1 = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable var2 = new Hashtable();
         var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         var2.put("java.naming.provider.url", "dns:");
         var2.put("com.sun.jndi.dns.timeout.retries", "1");
         var0 = new InitialDirContext(var2);
      } catch (Throwable var3) {
         LOGGER.error("Failed to initialize SRV redirect resolved, some servers might not work", var3);
         return EMPTY;
      }

      return (var1x) -> {
         if (var1x.getPort() == 25565) {
            try {
               Attributes var2 = var0.getAttributes("_minecraft._tcp." + var1x.getHost(), new String[]{"SRV"});
               Attribute var3 = var2.get("srv");
               if (var3 != null) {
                  String[] var4 = var3.get().toString().split(" ", 4);
                  return Optional.of(new ServerAddress(var4[3], ServerAddress.parsePort(var4[2])));
               }
            } catch (Throwable var5) {
            }
         }

         return Optional.empty();
      };
   }
}
