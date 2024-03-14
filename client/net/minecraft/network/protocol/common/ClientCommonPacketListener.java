package net.minecraft.network.protocol.common;

import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.protocol.cookie.ClientCookiePacketListener;

public interface ClientCommonPacketListener extends ClientCookiePacketListener, ClientboundPacketListener {
   void handleKeepAlive(ClientboundKeepAlivePacket var1);

   void handlePing(ClientboundPingPacket var1);

   void handleCustomPayload(ClientboundCustomPayloadPacket var1);

   void handleDisconnect(ClientboundDisconnectPacket var1);

   void handleResourcePackPush(ClientboundResourcePackPushPacket var1);

   void handleResourcePackPop(ClientboundResourcePackPopPacket var1);

   void handleUpdateTags(ClientboundUpdateTagsPacket var1);

   void handleStoreCookie(ClientboundStoreCookiePacket var1);

   void handleTransfer(ClientboundTransferPacket var1);
}
