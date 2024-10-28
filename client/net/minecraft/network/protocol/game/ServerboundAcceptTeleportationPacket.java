package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundAcceptTeleportationPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundAcceptTeleportationPacket> STREAM_CODEC = Packet.codec(ServerboundAcceptTeleportationPacket::write, ServerboundAcceptTeleportationPacket::new);
   private final int id;

   public ServerboundAcceptTeleportationPacket(int var1) {
      super();
      this.id = var1;
   }

   private ServerboundAcceptTeleportationPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
   }

   public PacketType<ServerboundAcceptTeleportationPacket> type() {
      return GamePacketTypes.SERVERBOUND_ACCEPT_TELEPORTATION;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
