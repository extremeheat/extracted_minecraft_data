package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.level.ChunkPos;

public record ClientboundDebugChunkValuePacket(ChunkPos chunkPos, DebugSubscription.Update<?> update) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDebugChunkValuePacket> STREAM_CODEC;

   public ClientboundDebugChunkValuePacket(ChunkPos var1, DebugSubscription.Update<?> var2) {
      super();
      this.chunkPos = var1;
      this.update = var2;
   }

   public PacketType<ClientboundDebugChunkValuePacket> type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_CHUNK_VALUE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDebugChunkValue(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ChunkPos.STREAM_CODEC, ClientboundDebugChunkValuePacket::chunkPos, DebugSubscription.Update.STREAM_CODEC, ClientboundDebugChunkValuePacket::update, ClientboundDebugChunkValuePacket::new);
   }
}
