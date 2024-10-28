package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record PacketIdentification(String direction, String protocolId, String packetId) {
   public PacketIdentification(String direction, String protocolId, String packetId) {
      super();
      this.direction = direction;
      this.protocolId = protocolId;
      this.packetId = packetId;
   }

   public static PacketIdentification from(RecordedEvent var0) {
      return new PacketIdentification(var0.getString("packetDirection"), var0.getString("protocolId"), var0.getString("packetId"));
   }

   public String direction() {
      return this.direction;
   }

   public String protocolId() {
      return this.protocolId;
   }

   public String packetId() {
      return this.packetId;
   }
}
