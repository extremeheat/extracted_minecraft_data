package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HAProxyMessage {
   private static final HAProxyMessage V1_UNKNOWN_MSG;
   private static final HAProxyMessage V2_UNKNOWN_MSG;
   private static final HAProxyMessage V2_LOCAL_MSG;
   private final HAProxyProtocolVersion protocolVersion;
   private final HAProxyCommand command;
   private final HAProxyProxiedProtocol proxiedProtocol;
   private final String sourceAddress;
   private final String destinationAddress;
   private final int sourcePort;
   private final int destinationPort;
   private final List<HAProxyTLV> tlvs;

   private HAProxyMessage(HAProxyProtocolVersion var1, HAProxyCommand var2, HAProxyProxiedProtocol var3, String var4, String var5, String var6, String var7) {
      this(var1, var2, var3, var4, var5, portStringToInt(var6), portStringToInt(var7));
   }

   private HAProxyMessage(HAProxyProtocolVersion var1, HAProxyCommand var2, HAProxyProxiedProtocol var3, String var4, String var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, Collections.emptyList());
   }

   private HAProxyMessage(HAProxyProtocolVersion var1, HAProxyCommand var2, HAProxyProxiedProtocol var3, String var4, String var5, int var6, int var7, List<HAProxyTLV> var8) {
      super();
      if (var3 == null) {
         throw new NullPointerException("proxiedProtocol");
      } else {
         HAProxyProxiedProtocol.AddressFamily var9 = var3.addressFamily();
         checkAddress(var4, var9);
         checkAddress(var5, var9);
         checkPort(var6);
         checkPort(var7);
         this.protocolVersion = var1;
         this.command = var2;
         this.proxiedProtocol = var3;
         this.sourceAddress = var4;
         this.destinationAddress = var5;
         this.sourcePort = var6;
         this.destinationPort = var7;
         this.tlvs = Collections.unmodifiableList(var8);
      }
   }

   static HAProxyMessage decodeHeader(ByteBuf var0) {
      if (var0 == null) {
         throw new NullPointerException("header");
      } else if (var0.readableBytes() < 16) {
         throw new HAProxyProtocolException("incomplete header: " + var0.readableBytes() + " bytes (expected: 16+ bytes)");
      } else {
         var0.skipBytes(12);
         byte var1 = var0.readByte();

         HAProxyProtocolVersion var2;
         try {
            var2 = HAProxyProtocolVersion.valueOf(var1);
         } catch (IllegalArgumentException var16) {
            throw new HAProxyProtocolException(var16);
         }

         if (var2 != HAProxyProtocolVersion.V2) {
            throw new HAProxyProtocolException("version 1 unsupported: 0x" + Integer.toHexString(var1));
         } else {
            HAProxyCommand var3;
            try {
               var3 = HAProxyCommand.valueOf(var1);
            } catch (IllegalArgumentException var15) {
               throw new HAProxyProtocolException(var15);
            }

            if (var3 == HAProxyCommand.LOCAL) {
               return V2_LOCAL_MSG;
            } else {
               HAProxyProxiedProtocol var4;
               try {
                  var4 = HAProxyProxiedProtocol.valueOf(var0.readByte());
               } catch (IllegalArgumentException var14) {
                  throw new HAProxyProtocolException(var14);
               }

               if (var4 == HAProxyProxiedProtocol.UNKNOWN) {
                  return V2_UNKNOWN_MSG;
               } else {
                  int var5 = var0.readUnsignedShort();
                  int var9 = 0;
                  int var10 = 0;
                  HAProxyProxiedProtocol.AddressFamily var11 = var4.addressFamily();
                  String var6;
                  String var7;
                  if (var11 == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
                     if (var5 < 216 || var0.readableBytes() < 216) {
                        throw new HAProxyProtocolException("incomplete UNIX socket address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 216+ bytes)");
                     }

                     int var12 = var0.readerIndex();
                     int var13 = var0.forEachByte(var12, 108, ByteProcessor.FIND_NUL);
                     int var8;
                     if (var13 == -1) {
                        var8 = 108;
                     } else {
                        var8 = var13 - var12;
                     }

                     var6 = var0.toString(var12, var8, CharsetUtil.US_ASCII);
                     var12 += 108;
                     var13 = var0.forEachByte(var12, 108, ByteProcessor.FIND_NUL);
                     if (var13 == -1) {
                        var8 = 108;
                     } else {
                        var8 = var13 - var12;
                     }

                     var7 = var0.toString(var12, var8, CharsetUtil.US_ASCII);
                     var0.readerIndex(var12 + 108);
                  } else {
                     byte var17;
                     if (var11 == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
                        if (var5 < 12 || var0.readableBytes() < 12) {
                           throw new HAProxyProtocolException("incomplete IPv4 address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 12+ bytes)");
                        }

                        var17 = 4;
                     } else {
                        if (var11 != HAProxyProxiedProtocol.AddressFamily.AF_IPv6) {
                           throw new HAProxyProtocolException("unable to parse address information (unknown address family: " + var11 + ')');
                        }

                        if (var5 < 36 || var0.readableBytes() < 36) {
                           throw new HAProxyProtocolException("incomplete IPv6 address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 36+ bytes)");
                        }

                        var17 = 16;
                     }

                     var6 = ipBytesToString(var0, var17);
                     var7 = ipBytesToString(var0, var17);
                     var9 = var0.readUnsignedShort();
                     var10 = var0.readUnsignedShort();
                  }

                  List var18 = readTlvs(var0);
                  return new HAProxyMessage(var2, var3, var4, var6, var7, var9, var10, var18);
               }
            }
         }
      }
   }

   private static List<HAProxyTLV> readTlvs(ByteBuf var0) {
      HAProxyTLV var1 = readNextTLV(var0);
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(4);

         do {
            var2.add(var1);
            if (var1 instanceof HAProxySSLTLV) {
               var2.addAll(((HAProxySSLTLV)var1).encapsulatedTLVs());
            }
         } while((var1 = readNextTLV(var0)) != null);

         return var2;
      }
   }

   private static HAProxyTLV readNextTLV(ByteBuf var0) {
      if (var0.readableBytes() < 4) {
         return null;
      } else {
         byte var1 = var0.readByte();
         HAProxyTLV.Type var2 = HAProxyTLV.Type.typeForByteValue(var1);
         int var3 = var0.readUnsignedShort();
         switch(var2) {
         case PP2_TYPE_SSL:
            ByteBuf var4 = var0.retainedSlice(var0.readerIndex(), var3);
            ByteBuf var5 = var0.readSlice(var3);
            byte var6 = var5.readByte();
            int var7 = var5.readInt();
            if (var5.readableBytes() < 4) {
               return new HAProxySSLTLV(var7, var6, Collections.emptyList(), var4);
            }

            ArrayList var8 = new ArrayList(4);

            do {
               HAProxyTLV var9 = readNextTLV(var5);
               if (var9 == null) {
                  break;
               }

               var8.add(var9);
            } while(var5.readableBytes() >= 4);

            return new HAProxySSLTLV(var7, var6, var8, var4);
         case PP2_TYPE_ALPN:
         case PP2_TYPE_AUTHORITY:
         case PP2_TYPE_SSL_VERSION:
         case PP2_TYPE_SSL_CN:
         case PP2_TYPE_NETNS:
         case OTHER:
            return new HAProxyTLV(var2, var1, var0.readRetainedSlice(var3));
         default:
            return null;
         }
      }
   }

   static HAProxyMessage decodeHeader(String var0) {
      if (var0 == null) {
         throw new HAProxyProtocolException("header");
      } else {
         String[] var1 = var0.split(" ");
         int var2 = var1.length;
         if (var2 < 2) {
            throw new HAProxyProtocolException("invalid header: " + var0 + " (expected: 'PROXY' and proxied protocol values)");
         } else if (!"PROXY".equals(var1[0])) {
            throw new HAProxyProtocolException("unknown identifier: " + var1[0]);
         } else {
            HAProxyProxiedProtocol var3;
            try {
               var3 = HAProxyProxiedProtocol.valueOf(var1[1]);
            } catch (IllegalArgumentException var5) {
               throw new HAProxyProtocolException(var5);
            }

            if (var3 != HAProxyProxiedProtocol.TCP4 && var3 != HAProxyProxiedProtocol.TCP6 && var3 != HAProxyProxiedProtocol.UNKNOWN) {
               throw new HAProxyProtocolException("unsupported v1 proxied protocol: " + var1[1]);
            } else if (var3 == HAProxyProxiedProtocol.UNKNOWN) {
               return V1_UNKNOWN_MSG;
            } else if (var2 != 6) {
               throw new HAProxyProtocolException("invalid TCP4/6 header: " + var0 + " (expected: 6 parts)");
            } else {
               return new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, var3, var1[2], var1[3], var1[4], var1[5]);
            }
         }
      }
   }

   private static String ipBytesToString(ByteBuf var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1 == 4) {
         var2.append(var0.readByte() & 255);
         var2.append('.');
         var2.append(var0.readByte() & 255);
         var2.append('.');
         var2.append(var0.readByte() & 255);
         var2.append('.');
         var2.append(var0.readByte() & 255);
      } else {
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
         var2.append(':');
         var2.append(Integer.toHexString(var0.readUnsignedShort()));
      }

      return var2.toString();
   }

   private static int portStringToInt(String var0) {
      int var1;
      try {
         var1 = Integer.parseInt(var0);
      } catch (NumberFormatException var3) {
         throw new HAProxyProtocolException("invalid port: " + var0, var3);
      }

      if (var1 > 0 && var1 <= 65535) {
         return var1;
      } else {
         throw new HAProxyProtocolException("invalid port: " + var0 + " (expected: 1 ~ 65535)");
      }
   }

   private static void checkAddress(String var0, HAProxyProxiedProtocol.AddressFamily var1) {
      if (var1 == null) {
         throw new NullPointerException("addrFamily");
      } else {
         switch(var1) {
         case AF_UNSPEC:
            if (var0 != null) {
               throw new HAProxyProtocolException("unable to validate an AF_UNSPEC address: " + var0);
            }

            return;
         case AF_UNIX:
            return;
         default:
            if (var0 == null) {
               throw new NullPointerException("address");
            } else {
               switch(var1) {
               case AF_IPv4:
                  if (!NetUtil.isValidIpV4Address(var0)) {
                     throw new HAProxyProtocolException("invalid IPv4 address: " + var0);
                  }
                  break;
               case AF_IPv6:
                  if (!NetUtil.isValidIpV6Address(var0)) {
                     throw new HAProxyProtocolException("invalid IPv6 address: " + var0);
                  }
                  break;
               default:
                  throw new Error();
               }

            }
         }
      }
   }

   private static void checkPort(int var0) {
      if (var0 < 0 || var0 > 65535) {
         throw new HAProxyProtocolException("invalid port: " + var0 + " (expected: 1 ~ 65535)");
      }
   }

   public HAProxyProtocolVersion protocolVersion() {
      return this.protocolVersion;
   }

   public HAProxyCommand command() {
      return this.command;
   }

   public HAProxyProxiedProtocol proxiedProtocol() {
      return this.proxiedProtocol;
   }

   public String sourceAddress() {
      return this.sourceAddress;
   }

   public String destinationAddress() {
      return this.destinationAddress;
   }

   public int sourcePort() {
      return this.sourcePort;
   }

   public int destinationPort() {
      return this.destinationPort;
   }

   public List<HAProxyTLV> tlvs() {
      return this.tlvs;
   }

   static {
      V1_UNKNOWN_MSG = new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, HAProxyProxiedProtocol.UNKNOWN, (String)null, (String)null, 0, 0);
      V2_UNKNOWN_MSG = new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY, HAProxyProxiedProtocol.UNKNOWN, (String)null, (String)null, 0, 0);
      V2_LOCAL_MSG = new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.LOCAL, HAProxyProxiedProtocol.UNKNOWN, (String)null, (String)null, 0, 0);
   }
}
