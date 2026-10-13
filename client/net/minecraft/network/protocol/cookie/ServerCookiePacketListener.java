package net.minecraft.network.protocol.cookie;

import net.minecraft.network.ServerboundPacketListener;

public interface ServerCookiePacketListener extends ServerboundPacketListener {
   void handleCookieResponse(ServerboundCookieResponsePacket packet);
}
