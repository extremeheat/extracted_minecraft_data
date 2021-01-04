package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public ServerHandshakePacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
   }

   public void handleIntention(ClientIntentionPacket var1) {
      switch(var1.getIntention()) {
      case LOGIN:
         this.connection.setProtocol(ConnectionProtocol.LOGIN);
         TranslatableComponent var2;
         if (var1.getProtocolVersion() > SharedConstants.getCurrentVersion().getProtocolVersion()) {
            var2 = new TranslatableComponent("multiplayer.disconnect.outdated_server", new Object[]{SharedConstants.getCurrentVersion().getName()});
            this.connection.send(new ClientboundLoginDisconnectPacket(var2));
            this.connection.disconnect(var2);
         } else if (var1.getProtocolVersion() < SharedConstants.getCurrentVersion().getProtocolVersion()) {
            var2 = new TranslatableComponent("multiplayer.disconnect.outdated_client", new Object[]{SharedConstants.getCurrentVersion().getName()});
            this.connection.send(new ClientboundLoginDisconnectPacket(var2));
            this.connection.disconnect(var2);
         } else {
            this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
         }
         break;
      case STATUS:
         this.connection.setProtocol(ConnectionProtocol.STATUS);
         this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + var1.getIntention());
      }

   }

   public void onDisconnect(Component var1) {
   }

   public Connection getConnection() {
      return this.connection;
   }
}
