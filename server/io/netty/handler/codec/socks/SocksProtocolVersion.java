package io.netty.handler.codec.socks;

public enum SocksProtocolVersion {
   SOCKS4a((byte)4),
   SOCKS5((byte)5),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksProtocolVersion(byte var3) {
      this.b = var3;
   }

   /** @deprecated */
   @Deprecated
   public static SocksProtocolVersion fromByte(byte var0) {
      return valueOf(var0);
   }

   public static SocksProtocolVersion valueOf(byte var0) {
      SocksProtocolVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksProtocolVersion var4 = var1[var3];
         if (var4.b == var0) {
            return var4;
         }
      }

      return UNKNOWN;
   }

   public byte byteValue() {
      return this.b;
   }
}
