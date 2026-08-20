package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record ClientboundChunksBiomesPacket(List<ChunkBiomeData> chunkBiomeData) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundChunksBiomesPacket> STREAM_CODEC;
   private static final int TWO_MEGABYTES = 2097152;

   public ClientboundChunksBiomesPacket {
      super();
   }

   public static ClientboundChunksBiomesPacket forChunks(final List<LevelChunk> chunks) {
      return new ClientboundChunksBiomesPacket(chunks.stream().map(ChunkBiomeData::new).toList());
   }

   public PacketType<ClientboundChunksBiomesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHUNKS_BIOMES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChunksBiomes(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ClientboundChunksBiomesPacket.ChunkBiomeData.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundChunksBiomesPacket::chunkBiomeData, ClientboundChunksBiomesPacket::new);
   }

   public static record ChunkBiomeData(ChunkPos pos, byte[] buffer) {
      public static final StreamCodec<ByteBuf, ChunkBiomeData> STREAM_CODEC;

      public ChunkBiomeData(final LevelChunk chunk) {
         this(chunk.getPos(), new byte[calculateChunkSize(chunk)]);
         extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), chunk);
      }

      public ChunkBiomeData {
         super();
      }

      private static int calculateChunkSize(final LevelChunk chunk) {
         int total = 0;

         for(LevelChunkSection section : chunk.getSections()) {
            total += section.getBiomes().getSerializedSize();
         }

         return total;
      }

      public FriendlyByteBuf getReadBuffer() {
         return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
      }

      private ByteBuf getWriteBuffer() {
         ByteBuf buffer = Unpooled.wrappedBuffer(this.buffer);
         buffer.writerIndex(0);
         return buffer;
      }

      public static void extractChunkData(final FriendlyByteBuf buffer, final LevelChunk chunk) {
         for(LevelChunkSection section : chunk.getSections()) {
            section.getBiomes().write(buffer);
         }

         if (buffer.writerIndex() != buffer.capacity()) {
            int var10002 = buffer.capacity();
            throw new IllegalStateException("Didn't fill biome buffer: expected " + var10002 + " bytes, got " + buffer.writerIndex());
         }
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ChunkPos.STREAM_CODEC, ChunkBiomeData::pos, ByteBufCodecs.byteArray(2097152), ChunkBiomeData::buffer, ChunkBiomeData::new);
      }
   }
}
