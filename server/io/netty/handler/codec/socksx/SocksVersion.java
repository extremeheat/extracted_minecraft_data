package io.netty.handler.codec.socksx;

public enum SocksVersion {
   SOCKS4a((byte)4),
   SOCKS5((byte)5),
   UNKNOWN((byte)-1);

   private final byte b;

   public static SocksVersion valueOf(byte var0) {
      if (var0 == SOCKS4a.byteValue()) {
         return SOCKS4a;
      } else {
         return var0 == SOCKS5.byteValue() ? SOCKS5 : UNKNOWN;
      }
   }

   private SocksVersion(byte var3) {
      this.b = var3;
   }

   public byte byteValue() {
      return this.b;
   }
}
