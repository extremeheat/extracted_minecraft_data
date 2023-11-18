package net.minecraft.client.multiplayer;

import net.minecraft.Util;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.util.SampleLogger;

public class PingDebugMonitor {
   private final ClientPacketListener connection;
   private final SampleLogger delayTimer;

   public PingDebugMonitor(ClientPacketListener var1, SampleLogger var2) {
      super();
      this.connection = var1;
      this.delayTimer = var2;
   }

   public void tick() {
      this.connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
   }

   public void onPongReceived(ClientboundPongResponsePacket var1) {
      this.delayTimer.logSample(Util.getMillis() - var1.getTime());
   }
}
