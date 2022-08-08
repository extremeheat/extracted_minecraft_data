package net.minecraft.util.profiling.jfr.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;

public final class NetworkPacketSummary {
   private final PacketCountAndSize totalPacketCountAndSize;
   private final List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors;
   private final Duration recordingDuration;

   public NetworkPacketSummary(Duration var1, List<Pair<PacketIdentification, PacketCountAndSize>> var2) {
      super();
      this.recordingDuration = var1;
      this.totalPacketCountAndSize = (PacketCountAndSize)var2.stream().map(Pair::getSecond).reduce(PacketCountAndSize::add).orElseGet(() -> {
         return new PacketCountAndSize(0L, 0L);
      });
      this.largestSizeContributors = var2.stream().sorted(Comparator.comparing(Pair::getSecond, NetworkPacketSummary.PacketCountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
   }

   public double getCountsPerSecond() {
      return (double)this.totalPacketCountAndSize.totalCount / (double)this.recordingDuration.getSeconds();
   }

   public double getSizePerSecond() {
      return (double)this.totalPacketCountAndSize.totalSize / (double)this.recordingDuration.getSeconds();
   }

   public long getTotalCount() {
      return this.totalPacketCountAndSize.totalCount;
   }

   public long getTotalSize() {
      return this.totalPacketCountAndSize.totalSize;
   }

   public List<Pair<PacketIdentification, PacketCountAndSize>> largestSizeContributors() {
      return this.largestSizeContributors;
   }

   public static record PacketCountAndSize(long a, long b) {
      final long totalCount;
      final long totalSize;
      static final Comparator<PacketCountAndSize> SIZE_THEN_COUNT = Comparator.comparing(PacketCountAndSize::totalSize).thenComparing(PacketCountAndSize::totalCount).reversed();

      public PacketCountAndSize(long var1, long var3) {
         super();
         this.totalCount = var1;
         this.totalSize = var3;
      }

      PacketCountAndSize add(PacketCountAndSize var1) {
         return new PacketCountAndSize(this.totalCount + var1.totalCount, this.totalSize + var1.totalSize);
      }

      public long totalCount() {
         return this.totalCount;
      }

      public long totalSize() {
         return this.totalSize;
      }
   }

   public static record PacketIdentification(PacketFlow a, int b, int c) {
      private final PacketFlow direction;
      private final int protocolId;
      private final int packetId;
      private static final Map<PacketIdentification, String> PACKET_NAME_BY_ID;

      public PacketIdentification(PacketFlow var1, int var2, int var3) {
         super();
         this.direction = var1;
         this.protocolId = var2;
         this.packetId = var3;
      }

      public String packetName() {
         return (String)PACKET_NAME_BY_ID.getOrDefault(this, "unknown");
      }

      public static PacketIdentification from(RecordedEvent var0) {
         return new PacketIdentification(var0.getEventType().getName().equals("minecraft.PacketSent") ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND, var0.getInt("protocolId"), var0.getInt("packetId"));
      }

      public PacketFlow direction() {
         return this.direction;
      }

      public int protocolId() {
         return this.protocolId;
      }

      public int packetId() {
         return this.packetId;
      }

      static {
         ImmutableMap.Builder var0 = ImmutableMap.builder();
         ConnectionProtocol[] var1 = ConnectionProtocol.values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ConnectionProtocol var4 = var1[var3];
            PacketFlow[] var5 = PacketFlow.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               PacketFlow var8 = var5[var7];
               Int2ObjectMap var9 = var4.getPacketsByIds(var8);
               var9.forEach((var3x, var4x) -> {
                  var0.put(new PacketIdentification(var8, var4.getId(), var3x), var4x.getSimpleName());
               });
            }
         }

         PACKET_NAME_BY_ID = var0.build();
      }
   }
}
