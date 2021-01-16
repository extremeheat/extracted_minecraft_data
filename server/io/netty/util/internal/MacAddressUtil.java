package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class MacAddressUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
   private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
   private static final int EUI48_MAC_ADDRESS_LENGTH = 6;

   public static byte[] bestAvailableMac() {
      byte[] var0 = EmptyArrays.EMPTY_BYTES;
      Object var1 = NetUtil.LOCALHOST4;
      LinkedHashMap var2 = new LinkedHashMap();

      InetAddress var6;
      try {
         Enumeration var3 = NetworkInterface.getNetworkInterfaces();
         if (var3 != null) {
            while(var3.hasMoreElements()) {
               NetworkInterface var4 = (NetworkInterface)var3.nextElement();
               Enumeration var5 = SocketUtils.addressesFromNetworkInterface(var4);
               if (var5.hasMoreElements()) {
                  var6 = (InetAddress)var5.nextElement();
                  if (!var6.isLoopbackAddress()) {
                     var2.put(var4, var6);
                  }
               }
            }
         }
      } catch (SocketException var11) {
         logger.warn("Failed to retrieve the list of available network interfaces", (Throwable)var11);
      }

      Iterator var12 = var2.entrySet().iterator();

      while(true) {
         NetworkInterface var15;
         do {
            if (!var12.hasNext()) {
               if (var0 == EmptyArrays.EMPTY_BYTES) {
                  return null;
               }

               switch(var0.length) {
               case 6:
                  byte[] var13 = new byte[8];
                  System.arraycopy(var0, 0, var13, 0, 3);
                  var13[3] = -1;
                  var13[4] = -2;
                  System.arraycopy(var0, 3, var13, 5, 3);
                  var0 = var13;
                  break;
               default:
                  var0 = Arrays.copyOf(var0, 8);
               }

               return var0;
            }

            Entry var14 = (Entry)var12.next();
            var15 = (NetworkInterface)var14.getKey();
            var6 = (InetAddress)var14.getValue();
         } while(var15.isVirtual());

         byte[] var7;
         try {
            var7 = SocketUtils.hardwareAddressFromNetworkInterface(var15);
         } catch (SocketException var10) {
            logger.debug("Failed to get the hardware address of a network interface: {}", var15, var10);
            continue;
         }

         boolean var8 = false;
         int var9 = compareAddresses(var0, var7);
         if (var9 < 0) {
            var8 = true;
         } else if (var9 == 0) {
            var9 = compareAddresses((InetAddress)var1, (InetAddress)var6);
            if (var9 < 0) {
               var8 = true;
            } else if (var9 == 0 && var0.length < var7.length) {
               var8 = true;
            }
         }

         if (var8) {
            var0 = var7;
            var1 = var6;
         }
      }
   }

   public static byte[] defaultMachineId() {
      byte[] var0 = bestAvailableMac();
      if (var0 == null) {
         var0 = new byte[8];
         PlatformDependent.threadLocalRandom().nextBytes(var0);
         logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", (Object)formatAddress(var0));
      }

      return var0;
   }

   public static byte[] parseMAC(String var0) {
      byte[] var1;
      char var2;
      switch(var0.length()) {
      case 17:
         var2 = var0.charAt(2);
         validateMacSeparator(var2);
         var1 = new byte[6];
         break;
      case 23:
         var2 = var0.charAt(2);
         validateMacSeparator(var2);
         var1 = new byte[8];
         break;
      default:
         throw new IllegalArgumentException("value is not supported [MAC-48, EUI-48, EUI-64]");
      }

      int var3 = var1.length - 1;
      int var4 = 0;

      for(int var5 = 0; var5 < var3; var4 += 3) {
         int var6 = var4 + 2;
         var1[var5] = StringUtil.decodeHexByte(var0, var4);
         if (var0.charAt(var6) != var2) {
            throw new IllegalArgumentException("expected separator '" + var2 + " but got '" + var0.charAt(var6) + "' at index: " + var6);
         }

         ++var5;
      }

      var1[var3] = StringUtil.decodeHexByte(var0, var4);
      return var1;
   }

   private static void validateMacSeparator(char var0) {
      if (var0 != ':' && var0 != '-') {
         throw new IllegalArgumentException("unsupported separator: " + var0 + " (expected: [:-])");
      }
   }

   public static String formatAddress(byte[] var0) {
      StringBuilder var1 = new StringBuilder(24);
      byte[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         var1.append(String.format("%02x:", var5 & 255));
      }

      return var1.substring(0, var1.length() - 1);
   }

   static int compareAddresses(byte[] var0, byte[] var1) {
      if (var1 != null && var1.length >= 6) {
         boolean var2 = true;
         byte[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte var6 = var3[var5];
            if (var6 != 0 && var6 != 1) {
               var2 = false;
               break;
            }
         }

         if (var2) {
            return 1;
         } else if ((var1[0] & 1) != 0) {
            return 1;
         } else if ((var1[0] & 2) == 0) {
            return var0.length != 0 && (var0[0] & 2) == 0 ? 0 : -1;
         } else {
            return var0.length != 0 && (var0[0] & 2) == 0 ? 1 : 0;
         }
      } else {
         return 1;
      }
   }

   private static int compareAddresses(InetAddress var0, InetAddress var1) {
      return scoreAddress(var0) - scoreAddress(var1);
   }

   private static int scoreAddress(InetAddress var0) {
      if (!var0.isAnyLocalAddress() && !var0.isLoopbackAddress()) {
         if (var0.isMulticastAddress()) {
            return 1;
         } else if (var0.isLinkLocalAddress()) {
            return 2;
         } else {
            return var0.isSiteLocalAddress() ? 3 : 4;
         }
      } else {
         return 0;
      }
   }

   private MacAddressUtil() {
      super();
   }
}
