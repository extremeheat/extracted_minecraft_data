package net.minecraft.network.play.client;

public enum C16PacketClientStatus$EnumState {
   PERFORM_RESPAWN(0),
   REQUEST_STATS(1),
   OPEN_INVENTORY_ACHIEVEMENT(2);

   private final int field_151403_d;
   private static final C16PacketClientStatus$EnumState[] field_151404_e = new C16PacketClientStatus$EnumState[values().length];

   private C16PacketClientStatus$EnumState(int var3) {
      this.field_151403_d = var3;
   }

   static {
      for(C16PacketClientStatus$EnumState var3 : values()) {
         field_151404_e[var3.field_151403_d] = var3;
      }
   }
}
