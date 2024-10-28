package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record PacketIdentification(String direction, String protocolId, String packetId) {
   public PacketIdentification(String var1, String var2, String var3) {
      super();
      this.direction = var1;
      this.protocolId = var2;
      this.packetId = var3;
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
