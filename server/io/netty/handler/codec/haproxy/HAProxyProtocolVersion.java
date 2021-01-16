package io.netty.handler.codec.haproxy;

public enum HAProxyProtocolVersion {
   V1((byte)16),
   V2((byte)32);

   private static final byte VERSION_MASK = -16;
   private final byte byteValue;

   private HAProxyProtocolVersion(byte var3) {
      this.byteValue = var3;
   }

   public static HAProxyProtocolVersion valueOf(byte var0) {
      int var1 = var0 & -16;
      switch((byte)var1) {
      case 16:
         return V1;
      case 32:
         return V2;
      default:
         throw new IllegalArgumentException("unknown version: " + var1);
      }
   }

   public byte byteValue() {
      return this.byteValue;
   }
}
