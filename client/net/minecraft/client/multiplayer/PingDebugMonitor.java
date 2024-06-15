package net.minecraft.client.multiplayer;

import net.minecraft.Util;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.debugchart.LocalSampleLogger;

public class PingDebugMonitor {
   private final ClientPacketListener connection;
   private final LocalSampleLogger delayTimer;

   public PingDebugMonitor(ClientPacketListener var1, LocalSampleLogger var2) {
      super();
      this.connection = var1;
      this.delayTimer = var2;
   }

   public void tick() {
      this.connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
   }

   public void onPongReceived(ClientboundPongResponsePacket var1) {
      this.delayTimer.logSample(Util.getMillis() - var1.time());
   }
}
