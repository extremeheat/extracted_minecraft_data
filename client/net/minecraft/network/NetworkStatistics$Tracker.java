package net.minecraft.network;

import java.util.concurrent.atomic.AtomicReference;

class NetworkStatistics$Tracker {
   private AtomicReference[] field_152490_a = new AtomicReference[100];

   public NetworkStatistics$Tracker() {
      super();

      for(int var1 = 0; var1 < 100; ++var1) {
         this.field_152490_a[var1] = new AtomicReference<>(new NetworkStatistics$PacketStatData(0L, 0, 0.0, null));
      }
   }

   public void func_152488_a(int var1, long var2) {
      try {
         if (var1 < 0 || var1 >= 100) {
            return;
         }

         NetworkStatistics$PacketStatData var4;
         NetworkStatistics$PacketStatData var5;
         do {
            var4 = (NetworkStatistics$PacketStatData)this.field_152490_a[var1].get();
            var5 = var4.func_152494_a(var2);
         } while(!this.field_152490_a[var1].compareAndSet(var4, var5));
      } catch (Exception var6) {
         if (NetworkStatistics.access$100().isDebugEnabled()) {
            NetworkStatistics.access$100().debug(NetworkStatistics.access$200(), "NetStat failed with packetId: " + var1, var6);
         }
      }
   }

   public long func_152485_a() {
      long var1 = 0L;

      for(int var3 = 0; var3 < 100; ++var3) {
         var1 += ((NetworkStatistics$PacketStatData)this.field_152490_a[var3].get()).func_152493_a();
      }

      return var1;
   }

   public long func_152489_b() {
      long var1 = 0L;

      for(int var3 = 0; var3 < 100; ++var3) {
         var1 += (long)((NetworkStatistics$PacketStatData)this.field_152490_a[var3].get()).func_152495_b();
      }

      return var1;
   }

   public NetworkStatistics$PacketStat func_152484_c() {
      int var1 = -1;
      NetworkStatistics$PacketStatData var2 = new NetworkStatistics$PacketStatData(-1L, -1, 0.0, null);

      for(int var3 = 0; var3 < 100; ++var3) {
         NetworkStatistics$PacketStatData var4 = (NetworkStatistics$PacketStatData)this.field_152490_a[var3].get();
         if (NetworkStatistics$PacketStatData.access$300(var4) > NetworkStatistics$PacketStatData.access$300(var2)) {
            var1 = var3;
            var2 = var4;
         }
      }

      return new NetworkStatistics$PacketStat(var1, var2);
   }

   public NetworkStatistics$PacketStat func_152486_d() {
      int var1 = -1;
      NetworkStatistics$PacketStatData var2 = new NetworkStatistics$PacketStatData(-1L, -1, 0.0, null);

      for(int var3 = 0; var3 < 100; ++var3) {
         NetworkStatistics$PacketStatData var4 = (NetworkStatistics$PacketStatData)this.field_152490_a[var3].get();
         if (NetworkStatistics$PacketStatData.access$400(var4) > NetworkStatistics$PacketStatData.access$400(var2)) {
            var1 = var3;
            var2 = var4;
         }
      }

      return new NetworkStatistics$PacketStat(var1, var2);
   }

   public NetworkStatistics$PacketStat func_152487_a(int var1) {
      return var1 >= 0 && var1 < 100 ? new NetworkStatistics$PacketStat(var1, (NetworkStatistics$PacketStatData)this.field_152490_a[var1].get()) : null;
   }
}
