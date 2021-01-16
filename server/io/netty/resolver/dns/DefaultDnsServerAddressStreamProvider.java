package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public final class DefaultDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDnsServerAddressStreamProvider.class);
   public static final DefaultDnsServerAddressStreamProvider INSTANCE = new DefaultDnsServerAddressStreamProvider();
   private static final List<InetSocketAddress> DEFAULT_NAME_SERVER_LIST;
   private static final InetSocketAddress[] DEFAULT_NAME_SERVER_ARRAY;
   private static final DnsServerAddresses DEFAULT_NAME_SERVERS;
   static final int DNS_PORT = 53;

   private DefaultDnsServerAddressStreamProvider() {
      super();
   }

   public DnsServerAddressStream nameServerAddressStream(String var1) {
      return DEFAULT_NAME_SERVERS.stream();
   }

   public static List<InetSocketAddress> defaultAddressList() {
      return DEFAULT_NAME_SERVER_LIST;
   }

   public static DnsServerAddresses defaultAddresses() {
      return DEFAULT_NAME_SERVERS;
   }

   static InetSocketAddress[] defaultAddressArray() {
      return (InetSocketAddress[])DEFAULT_NAME_SERVER_ARRAY.clone();
   }

   static {
      ArrayList var0 = new ArrayList(2);
      Hashtable var1 = new Hashtable();
      var1.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      var1.put("java.naming.provider.url", "dns://");

      String var8;
      try {
         InitialDirContext var2 = new InitialDirContext(var1);
         String var3 = (String)var2.getEnvironment().get("java.naming.provider.url");
         if (var3 != null && !var3.isEmpty()) {
            String[] var4 = var3.split(" ");
            String[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var8 = var5[var7];

               try {
                  URI var9 = new URI(var8);
                  String var10 = (new URI(var8)).getHost();
                  if (var10 != null && !var10.isEmpty()) {
                     int var11 = var9.getPort();
                     var0.add(SocketUtils.socketAddress(var9.getHost(), var11 == -1 ? 53 : var11));
                  } else {
                     logger.debug("Skipping a nameserver URI as host portion could not be extracted: {}", (Object)var8);
                  }
               } catch (URISyntaxException var13) {
                  logger.debug("Skipping a malformed nameserver URI: {}", var8, var13);
               }
            }
         }
      } catch (NamingException var14) {
      }

      if (var0.isEmpty()) {
         try {
            Class var15 = Class.forName("sun.net.dns.ResolverConfiguration");
            Method var16 = var15.getMethod("open");
            Method var17 = var15.getMethod("nameservers");
            Object var18 = var16.invoke((Object)null);
            List var19 = (List)var17.invoke(var18);
            Iterator var20 = var19.iterator();

            while(var20.hasNext()) {
               var8 = (String)var20.next();
               if (var8 != null) {
                  var0.add(new InetSocketAddress(SocketUtils.addressByName(var8), 53));
               }
            }
         } catch (Exception var12) {
         }
      }

      if (!var0.isEmpty()) {
         if (logger.isDebugEnabled()) {
            logger.debug("Default DNS servers: {} (sun.net.dns.ResolverConfiguration)", (Object)var0);
         }
      } else {
         if (!NetUtil.isIpV6AddressesPreferred() && (!(NetUtil.LOCALHOST instanceof Inet6Address) || NetUtil.isIpV4StackPreferred())) {
            Collections.addAll(var0, new InetSocketAddress[]{SocketUtils.socketAddress("8.8.8.8", 53), SocketUtils.socketAddress("8.8.4.4", 53)});
         } else {
            Collections.addAll(var0, new InetSocketAddress[]{SocketUtils.socketAddress("2001:4860:4860::8888", 53), SocketUtils.socketAddress("2001:4860:4860::8844", 53)});
         }

         if (logger.isWarnEnabled()) {
            logger.warn("Default DNS servers: {} (Google Public DNS as a fallback)", (Object)var0);
         }
      }

      DEFAULT_NAME_SERVER_LIST = Collections.unmodifiableList(var0);
      DEFAULT_NAME_SERVER_ARRAY = (InetSocketAddress[])var0.toArray(new InetSocketAddress[var0.size()]);
      DEFAULT_NAME_SERVERS = DnsServerAddresses.sequential(DEFAULT_NAME_SERVER_ARRAY);
   }
}
