package net.minecraft.network.protocol.common;

import net.minecraft.network.protocol.cookie.ServerCookiePacketListener;
import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerCommonPacketListener extends ServerCookiePacketListener, ServerPacketListener {
   void handleKeepAlive(ServerboundKeepAlivePacket var1);

   void handlePong(ServerboundPongPacket var1);

   void handleCustomPayload(ServerboundCustomPayloadPacket var1);

   void handleResourcePackResponse(ServerboundResourcePackPacket var1);

   void handleClientInformation(ServerboundClientInformationPacket var1);
}
