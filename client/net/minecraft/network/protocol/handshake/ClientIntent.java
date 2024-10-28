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
      ClientIntent var10000;
      switch (var0) {
         case 1 -> var10000 = STATUS;
         case 2 -> var10000 = LOGIN;
         case 3 -> var10000 = TRANSFER;
         default -> throw new IllegalArgumentException("Unknown connection intent: " + var0);
      }

      return var10000;
   }

   public int id() {
      byte var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = 1;
         case 1 -> var10000 = 2;
         case 2 -> var10000 = 3;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ClientIntent[] $values() {
      return new ClientIntent[]{STATUS, LOGIN, TRANSFER};
   }
}
