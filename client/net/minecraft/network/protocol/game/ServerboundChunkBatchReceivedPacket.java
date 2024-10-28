package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChunkBatchReceivedPacket(float desiredChunksPerTick) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChunkBatchReceivedPacket> STREAM_CODEC = Packet.codec(ServerboundChunkBatchReceivedPacket::write, ServerboundChunkBatchReceivedPacket::new);

   private ServerboundChunkBatchReceivedPacket(FriendlyByteBuf var1) {
      this(var1.readFloat());
   }

   public ServerboundChunkBatchReceivedPacket(float var1) {
      super();
      this.desiredChunksPerTick = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.desiredChunksPerTick);
   }

   public PacketType<ServerboundChunkBatchReceivedPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHUNK_BATCH_RECEIVED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChunkBatchReceived(this);
   }

   public float desiredChunksPerTick() {
      return this.desiredChunksPerTick;
   }
}
