package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

public class ClientboundLevelChunkPacketData {
   private static final StreamCodec<ByteBuf, Map<Heightmap.Types, long[]>> HEIGHTMAPS_STREAM_CODEC;
   private static final int TWO_MEGABYTES = 2097152;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkPacketData> STREAM_CODEC;
   private final Map<Heightmap.Types, long[]> heightmaps;
   private final byte[] buffer;
   private final List<BlockEntityInfo> blockEntitiesData;

   public ClientboundLevelChunkPacketData(final LevelChunk levelChunk) {
      super();
      this.heightmaps = (Map)levelChunk.getHeightmaps().stream().filter((entryx) -> ((Heightmap.Types)entryx.getKey()).sendToClient()).collect(Collectors.toMap(Map.Entry::getKey, (entryx) -> (long[])((Heightmap)entryx.getValue()).getRawData().clone()));
      this.buffer = new byte[calculateChunkSize(levelChunk)];
      extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), levelChunk);
      this.blockEntitiesData = Lists.newArrayList();

      for(Map.Entry<BlockPos, BlockEntity> entry : levelChunk.getBlockEntities().entrySet()) {
         this.blockEntitiesData.add(ClientboundLevelChunkPacketData.BlockEntityInfo.create((BlockEntity)entry.getValue()));
      }

   }

   private ClientboundLevelChunkPacketData(final Map<Heightmap.Types, long[]> heightmaps, final byte[] buffer, final List<BlockEntityInfo> blockEntitiesData) {
      super();
      this.heightmaps = heightmaps;
      this.buffer = buffer;
      this.blockEntitiesData = blockEntitiesData;
   }

   private static int calculateChunkSize(final LevelChunk chunk) {
      int total = 0;

      for(LevelChunkSection section : chunk.getSections()) {
         total += section.getSerializedSize();
      }

      return total;
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf buffer = Unpooled.wrappedBuffer(this.buffer);
      buffer.writerIndex(0);
      return buffer;
   }

   public static void extractChunkData(final FriendlyByteBuf buffer, final LevelChunk chunk) {
      for(LevelChunkSection section : chunk.getSections()) {
         section.write(buffer);
      }

      if (buffer.writerIndex() != buffer.capacity()) {
         int var10002 = buffer.capacity();
         throw new IllegalStateException("Didn't fill chunk buffer: expected " + var10002 + " bytes, got " + buffer.writerIndex());
      }
   }

   public void forEachBlockEntityTag(final int chunkX, final int chunkZ, final BlockEntityTagOutput output) {
      int baseX = 16 * chunkX;
      int baseZ = 16 * chunkZ;
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

      for(BlockEntityInfo data : this.blockEntitiesData) {
         int unpackedX = baseX + SectionPos.sectionRelative(data.packedXZ >> 4);
         int unpackedZ = baseZ + SectionPos.sectionRelative(data.packedXZ);
         pos.set(unpackedX, data.y, unpackedZ);
         output.accept(pos, data.type, (CompoundTag)data.tag.orElse((Object)null));
      }

   }

   public FriendlyByteBuf getReadBuffer() {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
   }

   public Map<Heightmap.Types, long[]> getHeightmaps() {
      return this.heightmaps;
   }

   static {
      HEIGHTMAPS_STREAM_CODEC = ByteBufCodecs.map((size) -> new EnumMap(Heightmap.Types.class), Heightmap.Types.STREAM_CODEC, ByteBufCodecs.LONG_ARRAY);
      STREAM_CODEC = StreamCodec.composite(HEIGHTMAPS_STREAM_CODEC, (p) -> p.heightmaps, ByteBufCodecs.byteArray(2097152), (p) -> p.buffer, ClientboundLevelChunkPacketData.BlockEntityInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), (p) -> p.blockEntitiesData, ClientboundLevelChunkPacketData::new);
   }

   private static record BlockEntityInfo(byte packedXZ, short y, BlockEntityType<?> type, Optional<CompoundTag> tag) {
      public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityInfo> STREAM_CODEC;

      private BlockEntityInfo {
         super();
      }

      private static BlockEntityInfo create(final BlockEntity blockEntity) {
         CompoundTag tag = blockEntity.getUpdateTag(blockEntity.getLevel().registryAccess());
         BlockPos pos = blockEntity.getBlockPos();
         int xz = SectionPos.sectionRelative(pos.getX()) << 4 | SectionPos.sectionRelative(pos.getZ());
         return new BlockEntityInfo((byte)xz, (short)pos.getY(), blockEntity.getType(), tag.isEmpty() ? Optional.empty() : Optional.of(tag));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BYTE, BlockEntityInfo::packedXZ, ByteBufCodecs.SHORT, BlockEntityInfo::y, ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE), BlockEntityInfo::type, ByteBufCodecs.OPTIONAL_COMPOUND_TAG, BlockEntityInfo::tag, BlockEntityInfo::new);
      }
   }

   @FunctionalInterface
   public interface BlockEntityTagOutput {
      void accept(BlockPos pos, BlockEntityType<?> type, @Nullable CompoundTag tag);
   }
}
