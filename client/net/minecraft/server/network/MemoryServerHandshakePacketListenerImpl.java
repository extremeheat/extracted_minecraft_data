package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.server.MinecraftServer;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public MemoryServerHandshakePacketListenerImpl(final MinecraftServer server, final Connection connection) {
      super();
      this.server = server;
      this.connection = connection;
   }

   public void handleIntention(final ClientIntentionPacket packet) {
      if (packet.intention() != ClientIntent.LOGIN) {
         throw new UnsupportedOperationException("Invalid intention " + String.valueOf(packet.intention()));
      } else {
         this.connection.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginPacketListenerImpl(this.server, this.connection, false));
         this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
      }
   }

   public void onDisconnect(final DisconnectionDetails details) {
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }
}
