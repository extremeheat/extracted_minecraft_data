package io.netty.resolver;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Map;

public final class DefaultHostsFileEntriesResolver implements HostsFileEntriesResolver {
   private final Map<String, Inet4Address> inet4Entries;
   private final Map<String, Inet6Address> inet6Entries;

   public DefaultHostsFileEntriesResolver() {
      this(HostsFileParser.parseSilently());
   }

   DefaultHostsFileEntriesResolver(HostsFileEntries var1) {
      super();
      this.inet4Entries = var1.inet4Entries();
      this.inet6Entries = var1.inet6Entries();
   }

   public InetAddress address(String var1, ResolvedAddressTypes var2) {
      String var3 = this.normalize(var1);
      switch(var2) {
      case IPV4_ONLY:
         return (InetAddress)this.inet4Entries.get(var3);
      case IPV6_ONLY:
         return (InetAddress)this.inet6Entries.get(var3);
      case IPV4_PREFERRED:
         Inet4Address var4 = (Inet4Address)this.inet4Entries.get(var3);
         return (InetAddress)(var4 != null ? var4 : (InetAddress)this.inet6Entries.get(var3));
      case IPV6_PREFERRED:
         Inet6Address var5 = (Inet6Address)this.inet6Entries.get(var3);
         return (InetAddress)(var5 != null ? var5 : (InetAddress)this.inet4Entries.get(var3));
      default:
         throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + var2);
      }
   }

   String normalize(String var1) {
      return var1.toLowerCase(Locale.ENGLISH);
   }
}
