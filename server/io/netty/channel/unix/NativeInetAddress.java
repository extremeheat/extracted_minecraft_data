package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class NativeInetAddress {
   private static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
   final byte[] address;
   final int scopeId;

   public static NativeInetAddress newInstance(InetAddress var0) {
      byte[] var1 = var0.getAddress();
      return var0 instanceof Inet6Address ? new NativeInetAddress(var1, ((Inet6Address)var0).getScopeId()) : new NativeInetAddress(ipv4MappedIpv6Address(var1));
   }

   public NativeInetAddress(byte[] var1, int var2) {
      super();
      this.address = var1;
      this.scopeId = var2;
   }

   public NativeInetAddress(byte[] var1) {
      this(var1, 0);
   }

   public byte[] address() {
      return this.address;
   }

   public int scopeId() {
      return this.scopeId;
   }

   public static byte[] ipv4MappedIpv6Address(byte[] var0) {
      byte[] var1 = new byte[16];
      System.arraycopy(IPV4_MAPPED_IPV6_PREFIX, 0, var1, 0, IPV4_MAPPED_IPV6_PREFIX.length);
      System.arraycopy(var0, 0, var1, 12, var0.length);
      return var1;
   }

   public static InetSocketAddress address(byte[] var0, int var1, int var2) {
      int var3 = decodeInt(var0, var1 + var2 - 4);

      try {
         Object var4;
         switch(var2) {
         case 8:
            byte[] var5 = new byte[4];
            System.arraycopy(var0, var1, var5, 0, 4);
            var4 = InetAddress.getByAddress(var5);
            break;
         case 24:
            byte[] var6 = new byte[16];
            System.arraycopy(var0, var1, var6, 0, 16);
            int var7 = decodeInt(var0, var1 + var2 - 8);
            var4 = Inet6Address.getByAddress((String)null, var6, var7);
            break;
         default:
            throw new Error();
         }

         return new InetSocketAddress((InetAddress)var4, var3);
      } catch (UnknownHostException var8) {
         throw new Error("Should never happen", var8);
      }
   }

   static int decodeInt(byte[] var0, int var1) {
      return (var0[var1] & 255) << 24 | (var0[var1 + 1] & 255) << 16 | (var0[var1 + 2] & 255) << 8 | var0[var1 + 3] & 255;
   }
}
