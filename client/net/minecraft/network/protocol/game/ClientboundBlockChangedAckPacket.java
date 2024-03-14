package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundBlockChangedAckPacket(int b) implements Packet<ClientGamePacketListener> {
   private final int sequence;
   public static final StreamCodec<FriendlyByteBuf, ClientboundBlockChangedAckPacket> STREAM_CODEC = Packet.codec(
      ClientboundBlockChangedAckPacket::write, ClientboundBlockChangedAckPacket::new
   );

   private ClientboundBlockChangedAckPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundBlockChangedAckPacket(int var1) {
      super();
      this.sequence = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.sequence);
   }

   @Override
   public PacketType<ClientboundBlockChangedAckPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_CHANGED_ACK;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockChangedAck(this);
   }
}
