package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public final class NetUtil {
   public static final Inet4Address LOCALHOST4;
   public static final Inet6Address LOCALHOST6;
   public static final InetAddress LOCALHOST;
   public static final NetworkInterface LOOPBACK_IF;
   public static final int SOMAXCONN;
   private static final int IPV6_WORD_COUNT = 8;
   private static final int IPV6_MAX_CHAR_COUNT = 39;
   private static final int IPV6_BYTE_COUNT = 16;
   private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
   private static final int IPV6_MIN_SEPARATORS = 2;
   private static final int IPV6_MAX_SEPARATORS = 8;
   private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
   private static final int IPV4_SEPARATORS = 3;
   private static final boolean IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
   private static final boolean IPV6_ADDRESSES_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv6Addresses", false);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);

   private static Integer sysctlGetInt(String var0) throws IOException {
      Process var1 = (new ProcessBuilder(new String[]{"sysctl", var0})).start();

      Object var6;
      try {
         InputStream var2 = var1.getInputStream();
         InputStreamReader var3 = new InputStreamReader(var2);
         BufferedReader var4 = new BufferedReader(var3);

         try {
            String var5 = var4.readLine();
            if (var5.startsWith(var0)) {
               for(var6 = var5.length() - 1; var6 > var0.length(); --var6) {
                  if (!Character.isDigit(var5.charAt((int)var6))) {
                     Integer var7 = Integer.valueOf(var5.substring(var6 + 1, var5.length()));
                     return var7;
                  }
               }
            }

            var6 = null;
         } finally {
            var4.close();
         }
      } finally {
         if (var1 != null) {
            var1.destroy();
         }

      }

      return (Integer)var6;
   }

   public static boolean isIpV4StackPreferred() {
      return IPV4_PREFERRED;
   }

   public static boolean isIpV6AddressesPreferred() {
      return IPV6_ADDRESSES_PREFERRED;
   }

   public static byte[] createByteArrayFromIpAddressString(String var0) {
      if (isValidIpV4Address(var0)) {
         return validIpV4ToBytes(var0);
      } else if (isValidIpV6Address(var0)) {
         if (var0.charAt(0) == '[') {
            var0 = var0.substring(1, var0.length() - 1);
         }

         int var1 = var0.indexOf(37);
         if (var1 >= 0) {
            var0 = var0.substring(0, var1);
         }

         return getIPv6ByName(var0, true);
      } else {
         return null;
      }
   }

   private static int decimalDigit(String var0, int var1) {
      return var0.charAt(var1) - 48;
   }

   private static byte ipv4WordToByte(String var0, int var1, int var2) {
      int var3 = decimalDigit(var0, var1);
      ++var1;
      if (var1 == var2) {
         return (byte)var3;
      } else {
         var3 = var3 * 10 + decimalDigit(var0, var1);
         ++var1;
         return var1 == var2 ? (byte)var3 : (byte)(var3 * 10 + decimalDigit(var0, var1));
      }
   }

   static byte[] validIpV4ToBytes(String var0) {
      int var1;
      return new byte[]{ipv4WordToByte(var0, 0, var1 = var0.indexOf(46, 1)), ipv4WordToByte(var0, var1 + 1, var1 = var0.indexOf(46, var1 + 2)), ipv4WordToByte(var0, var1 + 1, var1 = var0.indexOf(46, var1 + 2)), ipv4WordToByte(var0, var1 + 1, var0.length())};
   }

   public static String intToIpAddress(int var0) {
      StringBuilder var1 = new StringBuilder(15);
      var1.append(var0 >> 24 & 255);
      var1.append('.');
      var1.append(var0 >> 16 & 255);
      var1.append('.');
      var1.append(var0 >> 8 & 255);
      var1.append('.');
      var1.append(var0 & 255);
      return var1.toString();
   }

   public static String bytesToIpAddress(byte[] var0) {
      return bytesToIpAddress(var0, 0, var0.length);
   }

   public static String bytesToIpAddress(byte[] var0, int var1, int var2) {
      switch(var2) {
      case 4:
         return (new StringBuilder(15)).append(var0[var1] & 255).append('.').append(var0[var1 + 1] & 255).append('.').append(var0[var1 + 2] & 255).append('.').append(var0[var1 + 3] & 255).toString();
      case 16:
         return toAddressString(var0, var1, false);
      default:
         throw new IllegalArgumentException("length: " + var2 + " (expected: 4 or 16)");
      }
   }

   public static boolean isValidIpV6Address(String var0) {
      return isValidIpV6Address((CharSequence)var0);
   }

   public static boolean isValidIpV6Address(CharSequence var0) {
      int var1 = var0.length();
      if (var1 < 2) {
         return false;
      } else {
         char var3 = var0.charAt(0);
         int var2;
         if (var3 == '[') {
            --var1;
            if (var0.charAt(var1) != ']') {
               return false;
            }

            var2 = 1;
            var3 = var0.charAt(1);
         } else {
            var2 = 0;
         }

         int var4;
         int var5;
         if (var3 == ':') {
            if (var0.charAt(var2 + 1) != ':') {
               return false;
            }

            var4 = 2;
            var5 = var2;
            var2 += 2;
         } else {
            var4 = 0;
            var5 = -1;
         }

         int var6 = 0;
         int var7 = var2;

         while(true) {
            label168: {
               if (var7 < var1) {
                  var3 = var0.charAt(var7);
                  if (isValidHexChar(var3)) {
                     if (var6 >= 4) {
                        return false;
                     }

                     ++var6;
                     break label168;
                  }

                  switch(var3) {
                  case '%':
                     var1 = var7;
                     break;
                  case '.':
                     if ((var5 >= 0 || var4 == 6) && (var4 != 7 || var5 < var2) && var4 <= 7) {
                        int var8 = var7 - var6;
                        int var9 = var8 - 2;
                        if (isValidIPv4MappedChar(var0.charAt(var9))) {
                           if (!isValidIPv4MappedChar(var0.charAt(var9 - 1)) || !isValidIPv4MappedChar(var0.charAt(var9 - 2)) || !isValidIPv4MappedChar(var0.charAt(var9 - 3))) {
                              return false;
                           }

                           var9 -= 5;
                        }

                        while(var9 >= var2) {
                           char var10 = var0.charAt(var9);
                           if (var10 != '0' && var10 != ':') {
                              return false;
                           }

                           --var9;
                        }

                        int var11 = AsciiString.indexOf(var0, '%', var8 + 7);
                        if (var11 < 0) {
                           var11 = var1;
                        }

                        return isValidIpV4Address(var0, var8, var11);
                     }

                     return false;
                  case ':':
                     if (var4 > 7) {
                        return false;
                     }

                     if (var0.charAt(var7 - 1) == ':') {
                        if (var5 >= 0) {
                           return false;
                        }

                        var5 = var7 - 1;
                     } else {
                        var6 = 0;
                     }

                     ++var4;
                     break label168;
                  default:
                     return false;
                  }
               }

               if (var5 < 0) {
                  return var4 == 7 && var6 > 0;
               }

               return var5 + 2 == var1 || var6 > 0 && (var4 < 8 || var5 <= var2);
            }

            ++var7;
         }
      }
   }

   private static boolean isValidIpV4Word(CharSequence var0, int var1, int var2) {
      int var3 = var2 - var1;
      char var4;
      if (var3 >= 1 && var3 <= 3 && (var4 = var0.charAt(var1)) >= '0') {
         if (var3 == 3) {
            char var5;
            char var6;
            return (var5 = var0.charAt(var1 + 1)) >= '0' && (var6 = var0.charAt(var1 + 2)) >= '0' && (var4 <= '1' && var5 <= '9' && var6 <= '9' || var4 == '2' && var5 <= '5' && (var6 <= '5' || var5 < '5' && var6 <= '9'));
         } else {
            return var4 <= '9' && (var3 == 1 || isValidNumericChar(var0.charAt(var1 + 1)));
         }
      } else {
         return false;
      }
   }

   private static boolean isValidHexChar(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'A' && var0 <= 'F' || var0 >= 'a' && var0 <= 'f';
   }

   private static boolean isValidNumericChar(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   private static boolean isValidIPv4MappedChar(char var0) {
      return var0 == 'f' || var0 == 'F';
   }

   private static boolean isValidIPv4MappedSeparators(byte var0, byte var1, boolean var2) {
      return var0 == var1 && (var0 == 0 || !var2 && var1 == -1);
   }

   private static boolean isValidIPv4Mapped(byte[] var0, int var1, int var2, int var3) {
      boolean var4 = var2 + var3 >= 14;
      return var1 <= 12 && var1 >= 2 && (!var4 || var2 < 12) && isValidIPv4MappedSeparators(var0[var1 - 1], var0[var1 - 2], var4) && PlatformDependent.isZero(var0, 0, var1 - 3);
   }

   public static boolean isValidIpV4Address(CharSequence var0) {
      return isValidIpV4Address((CharSequence)var0, 0, var0.length());
   }

   public static boolean isValidIpV4Address(String var0) {
      return isValidIpV4Address((String)var0, 0, var0.length());
   }

   private static boolean isValidIpV4Address(CharSequence var0, int var1, int var2) {
      return var0 instanceof String ? isValidIpV4Address((String)var0, var1, var2) : (var0 instanceof AsciiString ? isValidIpV4Address((AsciiString)var0, var1, var2) : isValidIpV4Address0(var0, var1, var2));
   }

   private static boolean isValidIpV4Address(String var0, int var1, int var2) {
      int var3 = var2 - var1;
      int var4;
      return var3 <= 15 && var3 >= 7 && (var4 = var0.indexOf(46, var1 + 1)) > 0 && isValidIpV4Word(var0, var1, var4) && (var4 = var0.indexOf(46, var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && (var4 = var0.indexOf(46, var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && isValidIpV4Word(var0, var4 + 1, var2);
   }

   private static boolean isValidIpV4Address(AsciiString var0, int var1, int var2) {
      int var3 = var2 - var1;
      int var4;
      return var3 <= 15 && var3 >= 7 && (var4 = var0.indexOf('.', var1 + 1)) > 0 && isValidIpV4Word(var0, var1, var4) && (var4 = var0.indexOf('.', var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && (var4 = var0.indexOf('.', var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && isValidIpV4Word(var0, var4 + 1, var2);
   }

   private static boolean isValidIpV4Address0(CharSequence var0, int var1, int var2) {
      int var3 = var2 - var1;
      int var4;
      return var3 <= 15 && var3 >= 7 && (var4 = AsciiString.indexOf(var0, '.', var1 + 1)) > 0 && isValidIpV4Word(var0, var1, var4) && (var4 = AsciiString.indexOf(var0, '.', var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && (var4 = AsciiString.indexOf(var0, '.', var1 = var4 + 2)) > 0 && isValidIpV4Word(var0, var1 - 1, var4) && isValidIpV4Word(var0, var4 + 1, var2);
   }

   public static Inet6Address getByName(CharSequence var0) {
      return getByName(var0, true);
   }

   public static Inet6Address getByName(CharSequence var0, boolean var1) {
      byte[] var2 = getIPv6ByName(var0, var1);
      if (var2 == null) {
         return null;
      } else {
         try {
            return Inet6Address.getByAddress((String)null, var2, -1);
         } catch (UnknownHostException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   private static byte[] getIPv6ByName(CharSequence var0, boolean var1) {
      byte[] var2 = new byte[16];
      int var3 = var0.length();
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = -1;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;

      int var12;
      boolean var13;
      for(var13 = false; var9 < var3; ++var9) {
         char var14 = var0.charAt(var9);
         switch(var14) {
         case '.':
            ++var11;
            var12 = var9 - var8;
            if (var12 <= 3 && var8 >= 0 && var11 <= 3 && (var10 <= 0 || var6 + var5 >= 12) && var9 + 1 < var3 && var6 < var2.length && (var11 != 1 || var1 && (var6 == 0 || isValidIPv4Mapped(var2, var6, var4, var5)) && (var12 != 3 || isValidNumericChar(var0.charAt(var9 - 1)) && isValidNumericChar(var0.charAt(var9 - 2)) && isValidNumericChar(var0.charAt(var9 - 3))) && (var12 != 2 || isValidNumericChar(var0.charAt(var9 - 1)) && isValidNumericChar(var0.charAt(var9 - 2))) && (var12 != 1 || isValidNumericChar(var0.charAt(var9 - 1))))) {
               var7 <<= 3 - var12 << 2;
               var8 = (var7 & 15) * 100 + (var7 >> 4 & 15) * 10 + (var7 >> 8 & 15);
               if (var8 >= 0 && var8 <= 255) {
                  var2[var6++] = (byte)var8;
                  var7 = 0;
                  var8 = -1;
                  break;
               }

               return null;
            }

            return null;
         case ':':
            ++var10;
            if (var9 - var8 > 4 || var11 > 0 || var10 > 8 || var6 + 1 >= var2.length) {
               return null;
            }

            var7 <<= 4 - (var9 - var8) << 2;
            if (var5 > 0) {
               var5 -= 2;
            }

            var2[var6++] = (byte)((var7 & 15) << 4 | var7 >> 4 & 15);
            var2[var6++] = (byte)((var7 >> 8 & 15) << 4 | var7 >> 12 & 15);
            var12 = var9 + 1;
            if (var12 < var3 && var0.charAt(var12) == ':') {
               ++var12;
               if (var4 != 0 || var12 < var3 && var0.charAt(var12) == ':') {
                  return null;
               }

               ++var10;
               var13 = var10 == 2 && var7 == 0;
               var4 = var6;
               var5 = var2.length - var6 - 2;
               ++var9;
            }

            var7 = 0;
            var8 = -1;
            break;
         default:
            if (!isValidHexChar(var14) || var11 > 0 && !isValidNumericChar(var14)) {
               return null;
            }

            if (var8 < 0) {
               var8 = var9;
            } else if (var9 - var8 > 4) {
               return null;
            }

            var7 += StringUtil.decodeHexNibble(var14) << (var9 - var8 << 2);
         }
      }

      boolean var15 = var4 > 0;
      if (var11 > 0) {
         if (var8 > 0 && var9 - var8 > 3 || var11 != 3 || var6 >= var2.length) {
            return null;
         }

         if (var10 == 0) {
            var5 = 12;
         } else {
            if (var10 < 2 || (var15 || var10 != 6 || var0.charAt(0) == ':') && (!var15 || var10 >= 8 || var0.charAt(0) == ':' && var4 > 2)) {
               return null;
            }

            var5 -= 2;
         }

         var7 <<= 3 - (var9 - var8) << 2;
         var8 = (var7 & 15) * 100 + (var7 >> 4 & 15) * 10 + (var7 >> 8 & 15);
         if (var8 < 0 || var8 > 255) {
            return null;
         }

         var2[var6++] = (byte)var8;
      } else {
         var12 = var3 - 1;
         if (var8 > 0 && var9 - var8 > 4 || var10 < 2 || !var15 && (var10 + 1 != 8 || var0.charAt(0) == ':' || var0.charAt(var12) == ':') || var15 && (var10 > 8 || var10 == 8 && (var4 <= 2 && var0.charAt(0) != ':' || var4 >= 14 && var0.charAt(var12) != ':')) || var6 + 1 >= var2.length || var8 < 0 && var0.charAt(var12 - 1) != ':' || var4 > 2 && var0.charAt(0) == ':') {
            return null;
         }

         if (var8 >= 0 && var9 - var8 <= 4) {
            var7 <<= 4 - (var9 - var8) << 2;
         }

         var2[var6++] = (byte)((var7 & 15) << 4 | var7 >> 4 & 15);
         var2[var6++] = (byte)((var7 >> 8 & 15) << 4 | var7 >> 12 & 15);
      }

      var9 = var6 + var5;
      if (!var13 && var9 < var2.length) {
         for(var9 = 0; var9 < var5; ++var9) {
            var8 = var9 + var4;
            var6 = var8 + var5;
            if (var6 >= var2.length) {
               break;
            }

            var2[var6] = var2[var8];
            var2[var8] = 0;
         }
      } else {
         if (var9 >= var2.length) {
            ++var4;
         }

         for(var9 = var6; var9 < var2.length; ++var9) {
            for(var8 = var2.length - 1; var8 >= var4; --var8) {
               var2[var8] = var2[var8 - 1];
            }

            var2[var8] = 0;
            ++var4;
         }
      }

      if (var11 > 0) {
         var2[10] = var2[11] = -1;
      }

      return var2;
   }

   public static String toSocketAddressString(InetSocketAddress var0) {
      String var1 = String.valueOf(var0.getPort());
      StringBuilder var2;
      if (var0.isUnresolved()) {
         String var3 = getHostname(var0);
         var2 = newSocketAddressStringBuilder(var3, var1, !isValidIpV6Address(var3));
      } else {
         InetAddress var5 = var0.getAddress();
         String var4 = toAddressString(var5);
         var2 = newSocketAddressStringBuilder(var4, var1, var5 instanceof Inet4Address);
      }

      return var2.append(':').append(var1).toString();
   }

   public static String toSocketAddressString(String var0, int var1) {
      String var2 = String.valueOf(var1);
      return newSocketAddressStringBuilder(var0, var2, !isValidIpV6Address(var0)).append(':').append(var2).toString();
   }

   private static StringBuilder newSocketAddressStringBuilder(String var0, String var1, boolean var2) {
      int var3 = var0.length();
      if (var2) {
         return (new StringBuilder(var3 + 1 + var1.length())).append(var0);
      } else {
         StringBuilder var4 = new StringBuilder(var3 + 3 + var1.length());
         return var3 > 1 && var0.charAt(0) == '[' && var0.charAt(var3 - 1) == ']' ? var4.append(var0) : var4.append('[').append(var0).append(']');
      }
   }

   public static String toAddressString(InetAddress var0) {
      return toAddressString(var0, false);
   }

   public static String toAddressString(InetAddress var0, boolean var1) {
      if (var0 instanceof Inet4Address) {
         return var0.getHostAddress();
      } else if (!(var0 instanceof Inet6Address)) {
         throw new IllegalArgumentException("Unhandled type: " + var0);
      } else {
         return toAddressString(var0.getAddress(), 0, var1);
      }
   }

   private static String toAddressString(byte[] var0, int var1, boolean var2) {
      int[] var3 = new int[8];
      int var5 = var1 + var3.length;

      int var4;
      for(var4 = var1; var4 < var5; ++var4) {
         var3[var4] = (var0[var4 << 1] & 255) << 8 | var0[(var4 << 1) + 1] & 255;
      }

      int var6 = -1;
      int var8 = -1;
      int var9 = 0;

      int var7;
      for(var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == 0) {
            if (var6 < 0) {
               var6 = var4;
            }
         } else if (var6 >= 0) {
            var7 = var4 - var6;
            if (var7 > var9) {
               var8 = var6;
               var9 = var7;
            }

            var6 = -1;
         }
      }

      if (var6 >= 0) {
         var7 = var4 - var6;
         if (var7 > var9) {
            var8 = var6;
            var9 = var7;
         }
      }

      if (var9 == 1) {
         var9 = 0;
         var8 = -1;
      }

      int var10 = var8 + var9;
      StringBuilder var11 = new StringBuilder(39);
      if (var10 < 0) {
         var11.append(Integer.toHexString(var3[0]));

         for(var4 = 1; var4 < var3.length; ++var4) {
            var11.append(':');
            var11.append(Integer.toHexString(var3[var4]));
         }
      } else {
         boolean var12;
         if (!inRangeEndExclusive(0, var8, var10)) {
            var11.append(Integer.toHexString(var3[0]));
            var12 = false;
         } else {
            var11.append("::");
            var12 = var2 && var10 == 5 && var3[5] == 65535;
         }

         for(var4 = 1; var4 < var3.length; ++var4) {
            if (inRangeEndExclusive(var4, var8, var10)) {
               if (!inRangeEndExclusive(var4 - 1, var8, var10)) {
                  var11.append("::");
               }
            } else {
               if (!inRangeEndExclusive(var4 - 1, var8, var10)) {
                  if (var12 && var4 != 6) {
                     var11.append('.');
                  } else {
                     var11.append(':');
                  }
               }

               if (var12 && var4 > 5) {
                  var11.append(var3[var4] >> 8);
                  var11.append('.');
                  var11.append(var3[var4] & 255);
               } else {
                  var11.append(Integer.toHexString(var3[var4]));
               }
            }
         }
      }

      return var11.toString();
   }

   public static String getHostname(InetSocketAddress var0) {
      return PlatformDependent.javaVersion() >= 7 ? var0.getHostString() : var0.getHostName();
   }

   private static boolean inRangeEndExclusive(int var0, int var1, int var2) {
      return var0 >= var1 && var0 < var2;
   }

   private NetUtil() {
      super();
   }

   static {
      logger.debug("-Djava.net.preferIPv4Stack: {}", (Object)IPV4_PREFERRED);
      logger.debug("-Djava.net.preferIPv6Addresses: {}", (Object)IPV6_ADDRESSES_PREFERRED);
      byte[] var0 = new byte[]{127, 0, 0, 1};
      byte[] var1 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
      Inet4Address var2 = null;

      try {
         var2 = (Inet4Address)InetAddress.getByAddress("localhost", var0);
      } catch (Exception var20) {
         PlatformDependent.throwException(var20);
      }

      LOCALHOST4 = var2;
      Inet6Address var3 = null;

      try {
         var3 = (Inet6Address)InetAddress.getByAddress("localhost", var1);
      } catch (Exception var19) {
         PlatformDependent.throwException(var19);
      }

      LOCALHOST6 = var3;
      ArrayList var4 = new ArrayList();

      try {
         Enumeration var5 = NetworkInterface.getNetworkInterfaces();
         if (var5 != null) {
            while(var5.hasMoreElements()) {
               NetworkInterface var6 = (NetworkInterface)var5.nextElement();
               if (SocketUtils.addressesFromNetworkInterface(var6).hasMoreElements()) {
                  var4.add(var6);
               }
            }
         }
      } catch (SocketException var23) {
         logger.warn("Failed to retrieve the list of available network interfaces", (Throwable)var23);
      }

      NetworkInterface var24 = null;
      Object var25 = null;
      Iterator var7 = var4.iterator();

      NetworkInterface var8;
      Enumeration var9;
      label200:
      while(var7.hasNext()) {
         var8 = (NetworkInterface)var7.next();
         var9 = SocketUtils.addressesFromNetworkInterface(var8);

         while(var9.hasMoreElements()) {
            InetAddress var10 = (InetAddress)var9.nextElement();
            if (var10.isLoopbackAddress()) {
               var24 = var8;
               var25 = var10;
               break label200;
            }
         }
      }

      if (var24 == null) {
         try {
            var7 = var4.iterator();

            while(var7.hasNext()) {
               var8 = (NetworkInterface)var7.next();
               if (var8.isLoopback()) {
                  var9 = SocketUtils.addressesFromNetworkInterface(var8);
                  if (var9.hasMoreElements()) {
                     var24 = var8;
                     var25 = (InetAddress)var9.nextElement();
                     break;
                  }
               }
            }

            if (var24 == null) {
               logger.warn("Failed to find the loopback interface");
            }
         } catch (SocketException var22) {
            logger.warn("Failed to find the loopback interface", (Throwable)var22);
         }
      }

      if (var24 != null) {
         logger.debug("Loopback interface: {} ({}, {})", var24.getName(), var24.getDisplayName(), ((InetAddress)var25).getHostAddress());
      } else if (var25 == null) {
         try {
            if (NetworkInterface.getByInetAddress(LOCALHOST6) != null) {
               logger.debug("Using hard-coded IPv6 localhost address: {}", (Object)var3);
               var25 = var3;
            }
         } catch (Exception var18) {
         } finally {
            if (var25 == null) {
               logger.debug("Using hard-coded IPv4 localhost address: {}", (Object)var2);
               var25 = var2;
            }

         }
      }

      LOOPBACK_IF = var24;
      LOCALHOST = (InetAddress)var25;
      SOMAXCONN = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            int var1 = PlatformDependent.isWindows() ? 200 : 128;
            File var2 = new File("/proc/sys/net/core/somaxconn");
            BufferedReader var3 = null;

            try {
               if (var2.exists()) {
                  var3 = new BufferedReader(new FileReader(var2));
                  var1 = Integer.parseInt(var3.readLine());
                  if (NetUtil.logger.isDebugEnabled()) {
                     NetUtil.logger.debug("{}: {}", var2, var1);
                  }
               } else {
                  Integer var4 = null;
                  if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                     var4 = NetUtil.sysctlGetInt("kern.ipc.somaxconn");
                     if (var4 == null) {
                        var4 = NetUtil.sysctlGetInt("kern.ipc.soacceptqueue");
                        if (var4 != null) {
                           var1 = var4;
                        }
                     } else {
                        var1 = var4;
                     }
                  }

                  if (var4 == null) {
                     NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", var2, var1);
                  }
               }
            } catch (Exception var13) {
               NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", var2, var1, var13);
            } finally {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Exception var12) {
                  }
               }

            }

            return var1;
         }
      });
   }
}
