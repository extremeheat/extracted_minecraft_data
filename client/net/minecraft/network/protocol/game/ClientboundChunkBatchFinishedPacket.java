package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundChunkBatchFinishedPacket(int batchSize) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundChunkBatchFinishedPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundChunkBatchFinishedPacket>codec(ClientboundChunkBatchFinishedPacket::write, ClientboundChunkBatchFinishedPacket::new);

   private ClientboundChunkBatchFinishedPacket(final FriendlyByteBuf input) {
      this(input.readVarInt());
   }

   public ClientboundChunkBatchFinishedPacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.batchSize);
   }

   public PacketType<ClientboundChunkBatchFinishedPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHUNK_BATCH_FINISHED;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChunkBatchFinished(this);
   }
}
