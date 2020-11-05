package net.minecraft.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RateKickingConnection extends Connection {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component EXCEED_REASON = new TranslatableComponent("disconnect.exceeded_packet_rate");
   private final int rateLimitPacketsPerSecond;

   public RateKickingConnection(int var1) {
      super(PacketFlow.SERVERBOUND);
      this.rateLimitPacketsPerSecond = var1;
   }

   protected void tickSecond() {
      super.tickSecond();
      float var1 = this.getAverageReceivedPackets();
      if (var1 > (float)this.rateLimitPacketsPerSecond) {
         LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", var1);
         this.send(new ClientboundDisconnectPacket(EXCEED_REASON), (var1x) -> {
            this.disconnect(EXCEED_REASON);
         });
         this.setReadOnly();
      }

   }
}
