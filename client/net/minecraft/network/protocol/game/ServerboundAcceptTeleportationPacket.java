package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundAcceptTeleportationPacket(int id, double x, double y, double z, float yRot, float xRot) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundAcceptTeleportationPacket> STREAM_CODEC;

   public ServerboundAcceptTeleportationPacket {
      super();
   }

   public PacketType<ServerboundAcceptTeleportationPacket> type() {
      return GamePacketTypes.SERVERBOUND_ACCEPT_TELEPORTATION;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleAcceptTeleportPacket(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundAcceptTeleportationPacket::id, ByteBufCodecs.DOUBLE, ServerboundAcceptTeleportationPacket::x, ByteBufCodecs.DOUBLE, ServerboundAcceptTeleportationPacket::y, ByteBufCodecs.DOUBLE, ServerboundAcceptTeleportationPacket::z, ByteBufCodecs.FLOAT, ServerboundAcceptTeleportationPacket::yRot, ByteBufCodecs.FLOAT, ServerboundAcceptTeleportationPacket::xRot, ServerboundAcceptTeleportationPacket::new);
   }
}
