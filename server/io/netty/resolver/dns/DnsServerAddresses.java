package io.netty.resolver.dns;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class DnsServerAddresses {
   public DnsServerAddresses() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static List<InetSocketAddress> defaultAddressList() {
      return DefaultDnsServerAddressStreamProvider.defaultAddressList();
   }

   /** @deprecated */
   @Deprecated
   public static DnsServerAddresses defaultAddresses() {
      return DefaultDnsServerAddressStreamProvider.defaultAddresses();
   }

   public static DnsServerAddresses sequential(Iterable<? extends InetSocketAddress> var0) {
      return sequential0(sanitize(var0));
   }

   public static DnsServerAddresses sequential(InetSocketAddress... var0) {
      return sequential0(sanitize(var0));
   }

   private static DnsServerAddresses sequential0(InetSocketAddress... var0) {
      return (DnsServerAddresses)(var0.length == 1 ? singleton(var0[0]) : new DefaultDnsServerAddresses("sequential", var0) {
         public DnsServerAddressStream stream() {
            return new SequentialDnsServerAddressStream(this.addresses, 0);
         }
      });
   }

   public static DnsServerAddresses shuffled(Iterable<? extends InetSocketAddress> var0) {
      return shuffled0(sanitize(var0));
   }

   public static DnsServerAddresses shuffled(InetSocketAddress... var0) {
      return shuffled0(sanitize(var0));
   }

   private static DnsServerAddresses shuffled0(InetSocketAddress[] var0) {
      return (DnsServerAddresses)(var0.length == 1 ? singleton(var0[0]) : new DefaultDnsServerAddresses("shuffled", var0) {
         public DnsServerAddressStream stream() {
            return new ShuffledDnsServerAddressStream(this.addresses);
         }
      });
   }

   public static DnsServerAddresses rotational(Iterable<? extends InetSocketAddress> var0) {
      return rotational0(sanitize(var0));
   }

   public static DnsServerAddresses rotational(InetSocketAddress... var0) {
      return rotational0(sanitize(var0));
   }

   private static DnsServerAddresses rotational0(InetSocketAddress[] var0) {
      return (DnsServerAddresses)(var0.length == 1 ? singleton(var0[0]) : new RotationalDnsServerAddresses(var0));
   }

   public static DnsServerAddresses singleton(InetSocketAddress var0) {
      if (var0 == null) {
         throw new NullPointerException("address");
      } else if (var0.isUnresolved()) {
         throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var0);
      } else {
         return new SingletonDnsServerAddresses(var0);
      }
   }

   private static InetSocketAddress[] sanitize(Iterable<? extends InetSocketAddress> var0) {
      if (var0 == null) {
         throw new NullPointerException("addresses");
      } else {
         ArrayList var1;
         if (var0 instanceof Collection) {
            var1 = new ArrayList(((Collection)var0).size());
         } else {
            var1 = new ArrayList(4);
         }

         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            InetSocketAddress var3 = (InetSocketAddress)var2.next();
            if (var3 == null) {
               break;
            }

            if (var3.isUnresolved()) {
               throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var3);
            }

            var1.add(var3);
         }

         if (var1.isEmpty()) {
            throw new IllegalArgumentException("empty addresses");
         } else {
            return (InetSocketAddress[])var1.toArray(new InetSocketAddress[var1.size()]);
         }
      }
   }

   private static InetSocketAddress[] sanitize(InetSocketAddress[] var0) {
      if (var0 == null) {
         throw new NullPointerException("addresses");
      } else {
         ArrayList var1 = new ArrayList(var0.length);
         InetSocketAddress[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            InetSocketAddress var5 = var2[var4];
            if (var5 == null) {
               break;
            }

            if (var5.isUnresolved()) {
               throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var5);
            }

            var1.add(var5);
         }

         return var1.isEmpty() ? DefaultDnsServerAddressStreamProvider.defaultAddressArray() : (InetSocketAddress[])var1.toArray(new InetSocketAddress[var1.size()]);
      }
   }

   public abstract DnsServerAddressStream stream();
}
