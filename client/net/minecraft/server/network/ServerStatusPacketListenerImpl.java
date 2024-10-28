package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

public class ServerStatusPacketListenerImpl implements ServerStatusPacketListener {
   private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
   private final ServerStatus status;
   private final Connection connection;
   private boolean hasRequestedStatus;

   public ServerStatusPacketListenerImpl(ServerStatus var1, Connection var2) {
      super();
      this.status = var1;
      this.connection = var2;
   }

   public void onDisconnect(DisconnectionDetails var1) {
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void handleStatusRequest(ServerboundStatusRequestPacket var1) {
      if (this.hasRequestedStatus) {
         this.connection.disconnect(DISCONNECT_REASON);
      } else {
         this.hasRequestedStatus = true;
         this.connection.send(new ClientboundStatusResponsePacket(this.status));
      }
   }

   public void handlePingRequest(ServerboundPingRequestPacket var1) {
      this.connection.send(new ClientboundPongResponsePacket(var1.getTime()));
      this.connection.disconnect(DISCONNECT_REASON);
   }
}
