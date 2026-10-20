package net.minecraft.network.protocol.login;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.cookie.ClientCookiePacketListener;

public interface ClientLoginPacketListener extends ClientCookiePacketListener {
   default ConnectionProtocol protocol() {
      return ConnectionProtocol.LOGIN;
   }

   void handleKey(ClientboundKeyPacket packet);

   void handleLoginFinished(ClientboundLoginFinishedPacket packet);

   void handleDisconnect(ClientboundLoginDisconnectPacket packet);

   void handleCompression(ClientboundSetupCompressionPacket packet);

   void handleEnableOptions(ClientboundApplyOptionsPacket packet);

   void handleCustomQuery(ClientboundCustomQueryPacket packet);
}
