package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private static final Component IGNORE_STATUS_REASON = Component.literal("Ignoring status request");
   private final MinecraftServer server;
   private final Connection connection;

   public ServerHandshakePacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
   }

   public void handleIntention(ClientIntentionPacket var1) {
      switch (var1.getIntention()) {
         case LOGIN:
            this.connection.setProtocol(ConnectionProtocol.LOGIN);
            if (var1.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
               MutableComponent var2;
               if (var1.getProtocolVersion() < 754) {
                  var2 = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
               } else {
                  var2 = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
               }

               this.connection.send(new ClientboundLoginDisconnectPacket(var2));
               this.connection.disconnect(var2);
            } else {
               this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
            }
            break;
         case STATUS:
            if (this.server.repliesToStatus()) {
               this.connection.setProtocol(ConnectionProtocol.STATUS);
               this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
            } else {
               this.connection.disconnect(IGNORE_STATUS_REASON);
            }
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
