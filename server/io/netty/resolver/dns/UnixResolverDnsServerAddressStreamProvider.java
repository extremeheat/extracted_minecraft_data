package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UnixResolverDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
   private static final String ETC_RESOLV_CONF_FILE = "/etc/resolv.conf";
   private static final String ETC_RESOLVER_DIR = "/etc/resolver";
   private static final String NAMESERVER_ROW_LABEL = "nameserver";
   private static final String SORTLIST_ROW_LABEL = "sortlist";
   private static final String OPTIONS_ROW_LABEL = "options";
   private static final String DOMAIN_ROW_LABEL = "domain";
   private static final String PORT_ROW_LABEL = "port";
   private static final String NDOTS_LABEL = "ndots:";
   static final int DEFAULT_NDOTS = 1;
   private final DnsServerAddresses defaultNameServerAddresses;
   private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;

   static DnsServerAddressStreamProvider parseSilently() {
      try {
         UnixResolverDnsServerAddressStreamProvider var0 = new UnixResolverDnsServerAddressStreamProvider("/etc/resolv.conf", "/etc/resolver");
         return (DnsServerAddressStreamProvider)(var0.mayOverrideNameServers() ? var0 : DefaultDnsServerAddressStreamProvider.INSTANCE);
      } catch (Exception var1) {
         logger.debug("failed to parse {} and/or {}", "/etc/resolv.conf", "/etc/resolver", var1);
         return DefaultDnsServerAddressStreamProvider.INSTANCE;
      }
   }

   public UnixResolverDnsServerAddressStreamProvider(File var1, File... var2) throws IOException {
      super();
      Map var3 = parse((File)ObjectUtil.checkNotNull(var1, "etcResolvConf"));
      boolean var4 = var2 != null && var2.length != 0;
      this.domainToNameServerStreamMap = var4 ? parse(var2) : var3;
      DnsServerAddresses var5 = (DnsServerAddresses)var3.get(var1.getName());
      if (var5 == null) {
         Collection var6 = var3.values();
         if (var6.isEmpty()) {
            throw new IllegalArgumentException(var1 + " didn't provide any name servers");
         }

         this.defaultNameServerAddresses = (DnsServerAddresses)var6.iterator().next();
      } else {
         this.defaultNameServerAddresses = var5;
      }

      if (var4) {
         this.domainToNameServerStreamMap.putAll(var3);
      }

   }

   public UnixResolverDnsServerAddressStreamProvider(String var1, String var2) throws IOException {
      this(var1 == null ? null : new File(var1), var2 == null ? null : (new File(var2)).listFiles());
   }

   public DnsServerAddressStream nameServerAddressStream(String var1) {
      while(true) {
         int var2 = var1.indexOf(46, 1);
         if (var2 < 0 || var2 == var1.length() - 1) {
            return this.defaultNameServerAddresses.stream();
         }

         DnsServerAddresses var3 = (DnsServerAddresses)this.domainToNameServerStreamMap.get(var1);
         if (var3 != null) {
            return var3.stream();
         }

         var1 = var1.substring(var2 + 1);
      }
   }

   private boolean mayOverrideNameServers() {
      return !this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses.stream().next() != null;
   }

   private static Map<String, DnsServerAddresses> parse(File... var0) throws IOException {
      HashMap var1 = new HashMap(var0.length << 1);
      File[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         File var5 = var2[var4];
         if (var5.isFile()) {
            FileReader var6 = new FileReader(var5);
            BufferedReader var7 = null;

            try {
               var7 = new BufferedReader(var6);
               ArrayList var8 = new ArrayList(2);
               String var9 = var5.getName();
               int var10 = 53;

               String var11;
               while((var11 = var7.readLine()) != null) {
                  var11 = var11.trim();
                  char var12;
                  if (!var11.isEmpty() && (var12 = var11.charAt(0)) != '#' && var12 != ';') {
                     int var13;
                     if (var11.startsWith("nameserver")) {
                        var13 = StringUtil.indexOfNonWhiteSpace(var11, "nameserver".length());
                        if (var13 < 0) {
                           throw new IllegalArgumentException("error parsing label nameserver in file " + var5 + ". value: " + var11);
                        }

                        String var14 = var11.substring(var13);
                        if (!NetUtil.isValidIpV4Address(var14) && !NetUtil.isValidIpV6Address(var14)) {
                           var13 = var14.lastIndexOf(46);
                           if (var13 + 1 >= var14.length()) {
                              throw new IllegalArgumentException("error parsing label nameserver in file " + var5 + ". invalid IP value: " + var11);
                           }

                           var10 = Integer.parseInt(var14.substring(var13 + 1));
                           var14 = var14.substring(0, var13);
                        }

                        var8.add(SocketUtils.socketAddress(var14, var10));
                     } else if (var11.startsWith("domain")) {
                        var13 = StringUtil.indexOfNonWhiteSpace(var11, "domain".length());
                        if (var13 < 0) {
                           throw new IllegalArgumentException("error parsing label domain in file " + var5 + " value: " + var11);
                        }

                        var9 = var11.substring(var13);
                        if (!var8.isEmpty()) {
                           putIfAbsent(var1, var9, (List)var8);
                        }

                        var8 = new ArrayList(2);
                     } else if (var11.startsWith("port")) {
                        var13 = StringUtil.indexOfNonWhiteSpace(var11, "port".length());
                        if (var13 < 0) {
                           throw new IllegalArgumentException("error parsing label port in file " + var5 + " value: " + var11);
                        }

                        var10 = Integer.parseInt(var11.substring(var13));
                     } else if (var11.startsWith("sortlist")) {
                        logger.info("row type {} not supported. ignoring line: {}", "sortlist", var11);
                     }
                  }
               }

               if (!var8.isEmpty()) {
                  putIfAbsent(var1, var9, (List)var8);
               }
            } finally {
               if (var7 == null) {
                  var6.close();
               } else {
                  var7.close();
               }

            }
         }
      }

      return var1;
   }

   private static void putIfAbsent(Map<String, DnsServerAddresses> var0, String var1, List<InetSocketAddress> var2) {
      putIfAbsent(var0, var1, DnsServerAddresses.sequential((Iterable)var2));
   }

   private static void putIfAbsent(Map<String, DnsServerAddresses> var0, String var1, DnsServerAddresses var2) {
      DnsServerAddresses var3 = (DnsServerAddresses)var0.put(var1, var2);
      if (var3 != null) {
         var0.put(var1, var3);
         logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", var1, var3, var2);
      }

   }

   static int parseEtcResolverFirstNdots() throws IOException {
      return parseEtcResolverFirstNdots(new File("/etc/resolv.conf"));
   }

   static int parseEtcResolverFirstNdots(File var0) throws IOException {
      FileReader var1 = new FileReader(var0);
      BufferedReader var2 = null;

      try {
         var2 = new BufferedReader(var1);

         String var3;
         while((var3 = var2.readLine()) != null) {
            if (var3.startsWith("options")) {
               int var4 = var3.indexOf("ndots:");
               if (var4 >= 0) {
                  var4 += "ndots:".length();
                  int var5 = var3.indexOf(32, var4);
                  int var6 = Integer.parseInt(var3.substring(var4, var5 < 0 ? var3.length() : var5));
                  return var6;
               }
               break;
            }
         }
      } finally {
         if (var2 == null) {
            var1.close();
         } else {
            var2.close();
         }

      }

      return 1;
   }
}
