package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

   public ClientboundLevelChunkPacketData(final RegistryFriendlyByteBuf input, final int x, final int z) {
      super();
      this.heightmaps = (Map)HEIGHTMAPS_STREAM_CODEC.decode(input);
      int size = input.readVarInt();
      if (size > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[size];
         input.readBytes(this.buffer);
         this.blockEntitiesData = (List)ClientboundLevelChunkPacketData.BlockEntityInfo.LIST_STREAM_CODEC.decode(input);
      }
   }

   public void write(final RegistryFriendlyByteBuf output) {
      HEIGHTMAPS_STREAM_CODEC.encode(output, this.heightmaps);
      output.writeVarInt(this.buffer.length);
      output.writeBytes(this.buffer);
      ClientboundLevelChunkPacketData.BlockEntityInfo.LIST_STREAM_CODEC.encode(output, this.blockEntitiesData);
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

   public Consumer<BlockEntityTagOutput> getBlockEntitiesTagsConsumer(final int x, final int z) {
      return (output) -> this.getBlockEntitiesTags(output, x, z);
   }

   private void getBlockEntitiesTags(final BlockEntityTagOutput output, final int x, final int z) {
      int baseX = 16 * x;
      int baseZ = 16 * z;
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

      for(BlockEntityInfo data : this.blockEntitiesData) {
         int unpackedX = baseX + SectionPos.sectionRelative(data.packedXZ >> 4);
         int unpackedZ = baseZ + SectionPos.sectionRelative(data.packedXZ);
         pos.set(unpackedX, data.y, unpackedZ);
         output.accept(pos, data.type, data.tag);
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
   }

   private static class BlockEntityInfo {
      public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityInfo> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, BlockEntityInfo>ofMember(BlockEntityInfo::write, BlockEntityInfo::new);
      public static final StreamCodec<RegistryFriendlyByteBuf, List<BlockEntityInfo>> LIST_STREAM_CODEC;
      private final int packedXZ;
      private final int y;
      private final BlockEntityType<?> type;
      private final @Nullable CompoundTag tag;

      private BlockEntityInfo(final int packedXZ, final int y, final BlockEntityType<?> type, final @Nullable CompoundTag tag) {
         super();
         this.packedXZ = packedXZ;
         this.y = y;
         this.type = type;
         this.tag = tag;
      }

      private BlockEntityInfo(final RegistryFriendlyByteBuf input) {
         super();
         this.packedXZ = input.readByte();
         this.y = input.readShort();
         this.type = (BlockEntityType)ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE).decode(input);
         this.tag = input.readNbt();
      }

      private void write(final RegistryFriendlyByteBuf output) {
         output.writeByte(this.packedXZ);
         output.writeShort(this.y);
         ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE).encode(output, this.type);
         output.writeNbt(this.tag);
      }

      private static BlockEntityInfo create(final BlockEntity blockEntity) {
         CompoundTag tag = blockEntity.getUpdateTag(blockEntity.getLevel().registryAccess());
         BlockPos pos = blockEntity.getBlockPos();
         int xz = SectionPos.sectionRelative(pos.getX()) << 4 | SectionPos.sectionRelative(pos.getZ());
         return new BlockEntityInfo(xz, pos.getY(), blockEntity.getType(), tag.isEmpty() ? null : tag);
      }

      static {
         LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
      }
   }

   @FunctionalInterface
   public interface BlockEntityTagOutput {
      void accept(BlockPos pos, BlockEntityType<?> type, @Nullable CompoundTag tag);
   }
}
