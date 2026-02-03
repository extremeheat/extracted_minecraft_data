package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundAcceptTeleportationPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundAcceptTeleportationPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundAcceptTeleportationPacket>codec(ServerboundAcceptTeleportationPacket::write, ServerboundAcceptTeleportationPacket::new);
   private final int id;

   public ServerboundAcceptTeleportationPacket(final int id) {
      super();
      this.id = id;
   }

   private ServerboundAcceptTeleportationPacket(final FriendlyByteBuf input) {
      super();
      this.id = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.id);
   }

   public PacketType<ServerboundAcceptTeleportationPacket> type() {
      return GamePacketTypes.SERVERBOUND_ACCEPT_TELEPORTATION;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
