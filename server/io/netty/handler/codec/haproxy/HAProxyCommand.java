package io.netty.handler.codec.haproxy;

public enum HAProxyCommand {
   LOCAL((byte)0),
   PROXY((byte)1);

   private static final byte COMMAND_MASK = 15;
   private final byte byteValue;

   private HAProxyCommand(byte var3) {
      this.byteValue = var3;
   }

   public static HAProxyCommand valueOf(byte var0) {
      int var1 = var0 & 15;
      switch((byte)var1) {
      case 0:
         return LOCAL;
      case 1:
         return PROXY;
      default:
         throw new IllegalArgumentException("unknown command: " + var1);
      }
   }

   public byte byteValue() {
      return this.byteValue;
   }
}
