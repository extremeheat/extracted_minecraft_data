package net.minecraft.network;

public class NetworkStatistics$PacketStat {
   private final int field_152482_a;
   private final NetworkStatistics$PacketStatData field_152483_b;

   public NetworkStatistics$PacketStat(int var1, NetworkStatistics$PacketStatData var2) {
      super();
      this.field_152482_a = var1;
      this.field_152483_b = var2;
   }

   @Override
   public String toString() {
      return "PacketStat(" + this.field_152482_a + ")" + this.field_152483_b;
   }
}
