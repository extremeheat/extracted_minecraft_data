package net.minecraft.network.protocol.game;

import java.util.BitSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public record ClientboundLevelChunkWithLightPacket(int x, int z, ClientboundLevelChunkPacketData chunkData, ClientboundLightUpdatePacketData lightData) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkWithLightPacket> STREAM_CODEC;

   public ClientboundLevelChunkWithLightPacket(final LevelChunk levelChunk, final LevelLightEngine lightEngine, final @Nullable BitSet skyChangedLightSectionFilter, final @Nullable BitSet blockChangedLightSectionFilter) {
      ChunkPos chunkPos = levelChunk.getPos();
      this(chunkPos.x(), chunkPos.z(), new ClientboundLevelChunkPacketData(levelChunk), new ClientboundLightUpdatePacketData(chunkPos, lightEngine, skyChangedLightSectionFilter, blockChangedLightSectionFilter));
   }

   public ClientboundLevelChunkWithLightPacket {
      super();
   }

   public PacketType<ClientboundLevelChunkWithLightPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LEVEL_CHUNK_WITH_LIGHT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleLevelChunkWithLight(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ClientboundLevelChunkWithLightPacket::x, ByteBufCodecs.INT, ClientboundLevelChunkWithLightPacket::z, ClientboundLevelChunkPacketData.STREAM_CODEC, ClientboundLevelChunkWithLightPacket::chunkData, ClientboundLightUpdatePacketData.STREAM_CODEC, ClientboundLevelChunkWithLightPacket::lightData, ClientboundLevelChunkWithLightPacket::new);
   }
}
