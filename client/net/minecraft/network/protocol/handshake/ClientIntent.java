package net.minecraft.network.protocol.handshake;

public enum ClientIntent {
   STATUS,
   LOGIN,
   TRANSFER;

   private static final int STATUS_ID = 1;
   private static final int LOGIN_ID = 2;
   private static final int TRANSFER_ID = 3;

   private ClientIntent() {
   }

   public static ClientIntent byId(int var0) {
      return switch(var0) {
         case 1 -> STATUS;
         case 2 -> LOGIN;
         case 3 -> TRANSFER;
         default -> throw new IllegalArgumentException("Unknown connection intent: " + var0);
      };
   }

   public int id() {
      return switch(this) {
         case STATUS -> 1;
         case LOGIN -> 2;
         case TRANSFER -> 3;
      };
   }
}
