package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundChunkBatchFinishedPacket(int batchSize) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundChunkBatchFinishedPacket> STREAM_CODEC = Packet.codec(
      ClientboundChunkBatchFinishedPacket::write, ClientboundChunkBatchFinishedPacket::new
   );

   private ClientboundChunkBatchFinishedPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundChunkBatchFinishedPacket(int batchSize) {
      super();
      this.batchSize = batchSize;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.batchSize);
   }

   @Override
   public PacketType<ClientboundChunkBatchFinishedPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHUNK_BATCH_FINISHED;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBatchFinished(this);
   }
}