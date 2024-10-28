package net.minecraft.network.protocol.status;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class StatusPacketTypes {
   public static final PacketType<ClientboundStatusResponsePacket> CLIENTBOUND_STATUS_RESPONSE = createClientbound("status_response");
   public static final PacketType<ServerboundStatusRequestPacket> SERVERBOUND_STATUS_REQUEST = createServerbound("status_request");

   public StatusPacketTypes() {
      super();
   }

   private static <T extends Packet<ClientStatusPacketListener>> PacketType<T> createClientbound(String var0) {
      return new PacketType(PacketFlow.CLIENTBOUND, new ResourceLocation(var0));
   }

   private static <T extends Packet<ServerStatusPacketListener>> PacketType<T> createServerbound(String var0) {
      return new PacketType(PacketFlow.SERVERBOUND, new ResourceLocation(var0));
   }
}