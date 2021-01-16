package io.netty.handler.codec.socks;

public enum SocksSubnegotiationVersion {
   AUTH_PASSWORD((byte)1),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksSubnegotiationVersion(byte var3) {
      this.b = var3;
   }

   /** @deprecated */
   @Deprecated
   public static SocksSubnegotiationVersion fromByte(byte var0) {
      return valueOf(var0);
   }

   public static SocksSubnegotiationVersion valueOf(byte var0) {
      SocksSubnegotiationVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksSubnegotiationVersion var4 = var1[var3];
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
