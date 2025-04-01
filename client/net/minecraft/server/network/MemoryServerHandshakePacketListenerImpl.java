package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TheGame;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public MemoryServerHandshakePacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
   }

   public void handleIntention(ClientIntentionPacket var1) {
      if (var1.intention() != ClientIntent.LOGIN) {
         throw new UnsupportedOperationException("Invalid intention " + String.valueOf(var1.intention()));
      } else {
         TheGame var2 = this.server.theGame();
         if (var2 == null) {
            throw new IllegalStateException("Not really running, what's up?");
         } else {
            this.connection.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginPacketListenerImpl(var2, this.connection, false));
            this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
         }
      }
   }

   public void onDisconnect(DisconnectionDetails var1) {
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }
}
