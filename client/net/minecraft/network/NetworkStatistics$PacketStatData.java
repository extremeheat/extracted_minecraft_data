package net.minecraft.network;

class NetworkStatistics$PacketStatData {
   private final long field_152496_a;
   private final int field_152497_b;
   private final double field_152498_c;

   private NetworkStatistics$PacketStatData(long var1, int var3, double var4) {
      super();
      this.field_152496_a = var1;
      this.field_152497_b = var3;
      this.field_152498_c = var4;
   }

   public NetworkStatistics$PacketStatData func_152494_a(long var1) {
      return new NetworkStatistics$PacketStatData(
         var1 + this.field_152496_a, this.field_152497_b + 1, (double)((var1 + this.field_152496_a) / (long)(this.field_152497_b + 1))
      );
   }

   public long func_152493_a() {
      return this.field_152496_a;
   }

   public int func_152495_b() {
      return this.field_152497_b;
   }

   @Override
   public String toString() {
      return "{totalBytes=" + this.field_152496_a + ", count=" + this.field_152497_b + ", averageBytes=" + this.field_152498_c + '}';
   }
}
