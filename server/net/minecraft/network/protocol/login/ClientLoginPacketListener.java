package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface ClientLoginPacketListener extends PacketListener {
   void handleHello(ClientboundHelloPacket var1);

   void handleGameProfile(ClientboundGameProfilePacket var1);

   void handleDisconnect(ClientboundLoginDisconnectPacket var1);

   void handleCompression(ClientboundLoginCompressionPacket var1);

   void handleCustomQuery(ClientboundCustomQueryPacket var1);
}
