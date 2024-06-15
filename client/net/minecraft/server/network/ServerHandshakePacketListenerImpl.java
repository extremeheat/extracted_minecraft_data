package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.MinecraftServer;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private static final Component IGNORE_STATUS_REASON = Component.translatable("disconnect.ignoring_status_request");
   private final MinecraftServer server;
   private final Connection connection;

   public ServerHandshakePacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
   }

   @Override
   public void handleIntention(ClientIntentionPacket var1) {
      switch (var1.intention()) {
         case LOGIN:
            this.beginLogin(var1, false);
            break;
         case STATUS:
            ServerStatus var3 = this.server.getStatus();
            this.connection.setupOutboundProtocol(StatusProtocols.CLIENTBOUND);
            if (this.server.repliesToStatus() && var3 != null) {
               this.connection.setupInboundProtocol(StatusProtocols.SERVERBOUND, new ServerStatusPacketListenerImpl(var3, this.connection));
            } else {
               this.connection.disconnect(IGNORE_STATUS_REASON);
            }
            break;
         case TRANSFER:
            if (!this.server.acceptsTransfers()) {
               this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
               MutableComponent var2 = Component.translatable("multiplayer.disconnect.transfers_disabled");
               this.connection.send(new ClientboundLoginDisconnectPacket(var2));
               this.connection.disconnect(var2);
            } else {
               this.beginLogin(var1, true);
            }
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + var1.intention());
      }
   }

   private void beginLogin(ClientIntentionPacket var1, boolean var2) {
      this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
      if (var1.protocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
         MutableComponent var3;
         if (var1.protocolVersion() < 754) {
            var3 = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
         } else {
            var3 = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
         }

         this.connection.send(new ClientboundLoginDisconnectPacket(var3));
         this.connection.disconnect(var3);
      } else {
         this.connection.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginPacketListenerImpl(this.server, this.connection, var2));
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
