package net.minecraft.network.protocol.cookie;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class CookiePacketTypes {
   public static final PacketType<ClientboundCookieRequestPacket> CLIENTBOUND_COOKIE_REQUEST = createClientbound("cookie_request");
   public static final PacketType<ServerboundCookieResponsePacket> SERVERBOUND_COOKIE_RESPONSE = createServerbound("cookie_response");

   public CookiePacketTypes() {
      super();
   }

   private static <T extends Packet<ClientCookiePacketListener>> PacketType<T> createClientbound(String var0) {
      return new PacketType(PacketFlow.CLIENTBOUND, ResourceLocation.withDefaultNamespace(var0));
   }

   private static <T extends Packet<ServerCookiePacketListener>> PacketType<T> createServerbound(String var0) {
      return new PacketType(PacketFlow.SERVERBOUND, ResourceLocation.withDefaultNamespace(var0));
   }
}
