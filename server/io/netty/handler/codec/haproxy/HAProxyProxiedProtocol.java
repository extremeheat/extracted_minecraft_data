package io.netty.handler.codec.haproxy;

public enum HAProxyProxiedProtocol {
   UNKNOWN((byte)0, HAProxyProxiedProtocol.AddressFamily.AF_UNSPEC, HAProxyProxiedProtocol.TransportProtocol.UNSPEC),
   TCP4((byte)17, HAProxyProxiedProtocol.AddressFamily.AF_IPv4, HAProxyProxiedProtocol.TransportProtocol.STREAM),
   TCP6((byte)33, HAProxyProxiedProtocol.AddressFamily.AF_IPv6, HAProxyProxiedProtocol.TransportProtocol.STREAM),
   UDP4((byte)18, HAProxyProxiedProtocol.AddressFamily.AF_IPv4, HAProxyProxiedProtocol.TransportProtocol.DGRAM),
   UDP6((byte)34, HAProxyProxiedProtocol.AddressFamily.AF_IPv6, HAProxyProxiedProtocol.TransportProtocol.DGRAM),
   UNIX_STREAM((byte)49, HAProxyProxiedProtocol.AddressFamily.AF_UNIX, HAProxyProxiedProtocol.TransportProtocol.STREAM),
   UNIX_DGRAM((byte)50, HAProxyProxiedProtocol.AddressFamily.AF_UNIX, HAProxyProxiedProtocol.TransportProtocol.DGRAM);

   private final byte byteValue;
   private final HAProxyProxiedProtocol.AddressFamily addressFamily;
   private final HAProxyProxiedProtocol.TransportProtocol transportProtocol;

   private HAProxyProxiedProtocol(byte var3, HAProxyProxiedProtocol.AddressFamily var4, HAProxyProxiedProtocol.TransportProtocol var5) {
      this.byteValue = var3;
      this.addressFamily = var4;
      this.transportProtocol = var5;
   }

   public static HAProxyProxiedProtocol valueOf(byte var0) {
      switch(var0) {
      case 0:
         return UNKNOWN;
      case 17:
         return TCP4;
      case 18:
         return UDP4;
      case 33:
         return TCP6;
      case 34:
         return UDP6;
      case 49:
         return UNIX_STREAM;
      case 50:
         return UNIX_DGRAM;
      default:
         throw new IllegalArgumentException("unknown transport protocol + address family: " + (var0 & 255));
      }
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public HAProxyProxiedProtocol.AddressFamily addressFamily() {
      return this.addressFamily;
   }

   public HAProxyProxiedProtocol.TransportProtocol transportProtocol() {
      return this.transportProtocol;
   }

   public static enum TransportProtocol {
      UNSPEC((byte)0),
      STREAM((byte)1),
      DGRAM((byte)2);

      private static final byte TRANSPORT_MASK = 15;
      private final byte transportByte;

      private TransportProtocol(byte var3) {
         this.transportByte = var3;
      }

      public static HAProxyProxiedProtocol.TransportProtocol valueOf(byte var0) {
         int var1 = var0 & 15;
         switch((byte)var1) {
         case 0:
            return UNSPEC;
         case 1:
            return STREAM;
         case 2:
            return DGRAM;
         default:
            throw new IllegalArgumentException("unknown transport protocol: " + var1);
         }
      }

      public byte byteValue() {
         return this.transportByte;
      }
   }

   public static enum AddressFamily {
      AF_UNSPEC((byte)0),
      AF_IPv4((byte)16),
      AF_IPv6((byte)32),
      AF_UNIX((byte)48);

      private static final byte FAMILY_MASK = -16;
      private final byte byteValue;

      private AddressFamily(byte var3) {
         this.byteValue = var3;
      }

      public static HAProxyProxiedProtocol.AddressFamily valueOf(byte var0) {
         int var1 = var0 & -16;
         switch((byte)var1) {
         case 0:
            return AF_UNSPEC;
         case 16:
            return AF_IPv4;
         case 32:
            return AF_IPv6;
         case 48:
            return AF_UNIX;
         default:
            throw new IllegalArgumentException("unknown address family: " + var1);
         }
      }

      public byte byteValue() {
         return this.byteValue;
      }
   }
}
