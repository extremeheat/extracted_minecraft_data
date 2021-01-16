package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

final class DnsQueryContextManager {
   final Map<InetSocketAddress, IntObjectMap<DnsQueryContext>> map = new HashMap();

   DnsQueryContextManager() {
      super();
   }

   int add(DnsQueryContext var1) {
      IntObjectMap var2 = this.getOrCreateContextMap(var1.nameServerAddr());
      int var3 = PlatformDependent.threadLocalRandom().nextInt(65535) + 1;
      int var4 = 131070;
      int var5 = 0;
      synchronized(var2) {
         while(var2.containsKey(var3)) {
            var3 = var3 + 1 & '\uffff';
            ++var5;
            if (var5 >= 131070) {
               throw new IllegalStateException("query ID space exhausted: " + var1.question());
            }
         }

         var2.put(var3, var1);
         return var3;
      }
   }

   DnsQueryContext get(InetSocketAddress var1, int var2) {
      IntObjectMap var3 = this.getContextMap(var1);
      DnsQueryContext var4;
      if (var3 != null) {
         synchronized(var3) {
            var4 = (DnsQueryContext)var3.get(var2);
         }
      } else {
         var4 = null;
      }

      return var4;
   }

   DnsQueryContext remove(InetSocketAddress var1, int var2) {
      IntObjectMap var3 = this.getContextMap(var1);
      if (var3 == null) {
         return null;
      } else {
         synchronized(var3) {
            return (DnsQueryContext)var3.remove(var2);
         }
      }
   }

   private IntObjectMap<DnsQueryContext> getContextMap(InetSocketAddress var1) {
      synchronized(this.map) {
         return (IntObjectMap)this.map.get(var1);
      }
   }

   private IntObjectMap<DnsQueryContext> getOrCreateContextMap(InetSocketAddress var1) {
      synchronized(this.map) {
         IntObjectMap var3 = (IntObjectMap)this.map.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            IntObjectHashMap var4 = new IntObjectHashMap();
            InetAddress var5 = var1.getAddress();
            int var6 = var1.getPort();
            this.map.put(var1, var4);
            if (var5 instanceof Inet4Address) {
               Inet4Address var7 = (Inet4Address)var5;
               if (var7.isLoopbackAddress()) {
                  this.map.put(new InetSocketAddress(NetUtil.LOCALHOST6, var6), var4);
               } else {
                  this.map.put(new InetSocketAddress(toCompactAddress(var7), var6), var4);
               }
            } else if (var5 instanceof Inet6Address) {
               Inet6Address var10 = (Inet6Address)var5;
               if (var10.isLoopbackAddress()) {
                  this.map.put(new InetSocketAddress(NetUtil.LOCALHOST4, var6), var4);
               } else if (var10.isIPv4CompatibleAddress()) {
                  this.map.put(new InetSocketAddress(toIPv4Address(var10), var6), var4);
               }
            }

            return var4;
         }
      }
   }

   private static Inet6Address toCompactAddress(Inet4Address var0) {
      byte[] var1 = var0.getAddress();
      byte[] var2 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, var1[0], var1[1], var1[2], var1[3]};

      try {
         return (Inet6Address)InetAddress.getByAddress(var2);
      } catch (UnknownHostException var4) {
         throw new Error(var4);
      }
   }

   private static Inet4Address toIPv4Address(Inet6Address var0) {
      byte[] var1 = var0.getAddress();
      byte[] var2 = new byte[]{var1[12], var1[13], var1[14], var1[15]};

      try {
         return (Inet4Address)InetAddress.getByAddress(var2);
      } catch (UnknownHostException var4) {
         throw new Error(var4);
      }
   }
}
