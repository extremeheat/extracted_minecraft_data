package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundStatusResponsePacket(ServerStatus status) implements Packet<ClientStatusPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundStatusResponsePacket> STREAM_CODEC = Packet.codec(
      ClientboundStatusResponsePacket::write, ClientboundStatusResponsePacket::new
   );

   private ClientboundStatusResponsePacket(FriendlyByteBuf var1) {
      this(var1.readJsonWithCodec(ServerStatus.CODEC));
   }

   public ClientboundStatusResponsePacket(ServerStatus status) {
      super();
      this.status = status;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeJsonWithCodec(ServerStatus.CODEC, this.status);
   }

   @Override
   public PacketType<ClientboundStatusResponsePacket> type() {
      return StatusPacketTypes.CLIENTBOUND_STATUS_RESPONSE;
   }

   public void handle(ClientStatusPacketListener var1) {
      var1.handleStatusResponse(this);
   }
}