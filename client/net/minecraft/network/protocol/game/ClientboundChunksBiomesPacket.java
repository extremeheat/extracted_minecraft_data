package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record ClientboundChunksBiomesPacket(List<ChunkBiomeData> chunkBiomeData) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundChunksBiomesPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundChunksBiomesPacket>codec(ClientboundChunksBiomesPacket::write, ClientboundChunksBiomesPacket::new);
   private static final int TWO_MEGABYTES = 2097152;

   private ClientboundChunksBiomesPacket(FriendlyByteBuf var1) {
      this(var1.readList(ChunkBiomeData::new));
   }

   public ClientboundChunksBiomesPacket(List<ChunkBiomeData> var1) {
      super();
      this.chunkBiomeData = var1;
   }

   public static ClientboundChunksBiomesPacket forChunks(List<LevelChunk> var0) {
      return new ClientboundChunksBiomesPacket(var0.stream().map(ChunkBiomeData::new).toList());
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.chunkBiomeData, (var0, var1x) -> var1x.write(var0));
   }

   public PacketType<ClientboundChunksBiomesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHUNKS_BIOMES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunksBiomes(this);
   }

   public static record ChunkBiomeData(ChunkPos pos, byte[] buffer) {
      public ChunkBiomeData(LevelChunk var1) {
         this(var1.getPos(), new byte[calculateChunkSize(var1)]);
         extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), var1);
      }

      public ChunkBiomeData(FriendlyByteBuf var1) {
         this(var1.readChunkPos(), var1.readByteArray(2097152));
      }

      public ChunkBiomeData(ChunkPos var1, byte[] var2) {
         super();
         this.pos = var1;
         this.buffer = var2;
      }

      private static int calculateChunkSize(LevelChunk var0) {
         int var1 = 0;

         for(LevelChunkSection var5 : var0.getSections()) {
            var1 += var5.getBiomes().getSerializedSize();
         }

         return var1;
      }

      public FriendlyByteBuf getReadBuffer() {
         return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
      }

      private ByteBuf getWriteBuffer() {
         ByteBuf var1 = Unpooled.wrappedBuffer(this.buffer);
         var1.writerIndex(0);
         return var1;
      }

      public static void extractChunkData(FriendlyByteBuf var0, LevelChunk var1) {
         for(LevelChunkSection var5 : var1.getSections()) {
            var5.getBiomes().write(var0);
         }

      }

      public void write(FriendlyByteBuf var1) {
         var1.writeChunkPos(this.pos);
         var1.writeByteArray(this.buffer);
      }
   }
}
