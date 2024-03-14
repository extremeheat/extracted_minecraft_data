package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacketData {
   private static final int TWO_MEGABYTES = 2097152;
   private final CompoundTag heightmaps;
   private final byte[] buffer;
   private final List<ClientboundLevelChunkPacketData.BlockEntityInfo> blockEntitiesData;

   public ClientboundLevelChunkPacketData(LevelChunk var1) {
      super();
      this.heightmaps = new CompoundTag();

      for(Entry var3 : var1.getHeightmaps()) {
         if (((Heightmap.Types)var3.getKey()).sendToClient()) {
            this.heightmaps.put(((Heightmap.Types)var3.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var3.getValue()).getRawData()));
         }
      }

      this.buffer = new byte[calculateChunkSize(var1)];
      extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), var1);
      this.blockEntitiesData = Lists.newArrayList();

      for(Entry var5 : var1.getBlockEntities().entrySet()) {
         this.blockEntitiesData.add(ClientboundLevelChunkPacketData.BlockEntityInfo.create((BlockEntity)var5.getValue()));
      }
   }

   public ClientboundLevelChunkPacketData(RegistryFriendlyByteBuf var1, int var2, int var3) {
      super();
      this.heightmaps = var1.readNbt();
      if (this.heightmaps == null) {
         throw new RuntimeException("Can't read heightmap in packet for [" + var2 + ", " + var3 + "]");
      } else {
         int var4 = var1.readVarInt();
         if (var4 > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
         } else {
            this.buffer = new byte[var4];
            var1.readBytes(this.buffer);
            this.blockEntitiesData = ClientboundLevelChunkPacketData.BlockEntityInfo.LIST_STREAM_CODEC.decode(var1);
         }
      }
   }

   public void write(RegistryFriendlyByteBuf var1) {
      var1.writeNbt(this.heightmaps);
      var1.writeVarInt(this.buffer.length);
      var1.writeBytes(this.buffer);
      ClientboundLevelChunkPacketData.BlockEntityInfo.LIST_STREAM_CODEC.encode(var1, this.blockEntitiesData);
   }

   private static int calculateChunkSize(LevelChunk var0) {
      int var1 = 0;

      for(LevelChunkSection var5 : var0.getSections()) {
         var1 += var5.getSerializedSize();
      }

      return var1;
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf var1 = Unpooled.wrappedBuffer(this.buffer);
      var1.writerIndex(0);
      return var1;
   }

   public static void extractChunkData(FriendlyByteBuf var0, LevelChunk var1) {
      for(LevelChunkSection var5 : var1.getSections()) {
         var5.write(var0);
      }
   }

   public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int var1, int var2) {
      return var3 -> this.getBlockEntitiesTags(var3, var1, var2);
   }

   private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput var1, int var2, int var3) {
      int var4 = 16 * var2;
      int var5 = 16 * var3;
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(ClientboundLevelChunkPacketData.BlockEntityInfo var8 : this.blockEntitiesData) {
         int var9 = var4 + SectionPos.sectionRelative(var8.packedXZ >> 4);
         int var10 = var5 + SectionPos.sectionRelative(var8.packedXZ);
         var6.set(var9, var8.y, var10);
         var1.accept(var6, var8.type, var8.tag);
      }
   }

   public FriendlyByteBuf getReadBuffer() {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
   }

   public CompoundTag getHeightmaps() {
      return this.heightmaps;
   }

   static class BlockEntityInfo {
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkPacketData.BlockEntityInfo> STREAM_CODEC = StreamCodec.ofMember(
         ClientboundLevelChunkPacketData.BlockEntityInfo::write, ClientboundLevelChunkPacketData.BlockEntityInfo::new
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, List<ClientboundLevelChunkPacketData.BlockEntityInfo>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
         ByteBufCodecs.list()
      );
      final int packedXZ;
      final int y;
      final BlockEntityType<?> type;
      @Nullable
      final CompoundTag tag;

      private BlockEntityInfo(int var1, int var2, BlockEntityType<?> var3, @Nullable CompoundTag var4) {
         super();
         this.packedXZ = var1;
         this.y = var2;
         this.type = var3;
         this.tag = var4;
      }

      private BlockEntityInfo(RegistryFriendlyByteBuf var1) {
         super();
         this.packedXZ = var1.readByte();
         this.y = var1.readShort();
         this.type = ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE).decode(var1);
         this.tag = var1.readNbt();
      }

      private void write(RegistryFriendlyByteBuf var1) {
         var1.writeByte(this.packedXZ);
         var1.writeShort(this.y);
         ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE).encode(var1, this.type);
         var1.writeNbt(this.tag);
      }

      static ClientboundLevelChunkPacketData.BlockEntityInfo create(BlockEntity var0) {
         CompoundTag var1 = var0.getUpdateTag(var0.getLevel().registryAccess());
         BlockPos var2 = var0.getBlockPos();
         int var3 = SectionPos.sectionRelative(var2.getX()) << 4 | SectionPos.sectionRelative(var2.getZ());
         return new ClientboundLevelChunkPacketData.BlockEntityInfo(var3, var2.getY(), var0.getType(), var1.isEmpty() ? null : var1);
      }
   }

   @FunctionalInterface
   public interface BlockEntityTagOutput {
      void accept(BlockPos var1, BlockEntityType<?> var2, @Nullable CompoundTag var3);
   }
}
