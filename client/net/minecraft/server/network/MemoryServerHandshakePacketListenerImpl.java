package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public MemoryServerHandshakePacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
   }

   @Override
   public void handleIntention(ClientIntentionPacket var1) {
      if (var1.intention() != ClientIntent.LOGIN) {
         throw new UnsupportedOperationException("Invalid intention " + var1.intention());
      } else {
         this.connection.setClientboundProtocolAfterHandshake(ClientIntent.LOGIN);
         this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
      }
   }

   @Override
   public void onDisconnect(Component var1) {
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }
}
