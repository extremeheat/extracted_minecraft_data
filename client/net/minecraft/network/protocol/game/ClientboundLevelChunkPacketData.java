package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
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
      Iterator var2 = var1.getHeightmaps().iterator();

      Entry var3;
      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         if (((Heightmap.Types)var3.getKey()).sendToClient()) {
            this.heightmaps.put(((Heightmap.Types)var3.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var3.getValue()).getRawData()));
         }
      }

      this.buffer = new byte[calculateChunkSize(var1)];
      extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), var1);
      this.blockEntitiesData = Lists.newArrayList();
      var2 = var1.getBlockEntities().entrySet().iterator();

      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         this.blockEntitiesData.add(ClientboundLevelChunkPacketData.BlockEntityInfo.create((BlockEntity)var3.getValue()));
      }

   }

   public ClientboundLevelChunkPacketData(FriendlyByteBuf var1, int var2, int var3) {
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
            this.blockEntitiesData = var1.readList(ClientboundLevelChunkPacketData.BlockEntityInfo::new);
         }
      }
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeNbt(this.heightmaps);
      var1.writeVarInt(this.buffer.length);
      var1.writeBytes(this.buffer);
      var1.writeCollection(this.blockEntitiesData, (var0, var1x) -> {
         var1x.write(var0);
      });
   }

   private static int calculateChunkSize(LevelChunk var0) {
      int var1 = 0;
      LevelChunkSection[] var2 = var0.getSections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelChunkSection var5 = var2[var4];
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
      LevelChunkSection[] var2 = var1.getSections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelChunkSection var5 = var2[var4];
         var5.write(var0);
      }

   }

   public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int var1, int var2) {
      return (var3) -> {
         this.getBlockEntitiesTags(var3, var1, var2);
      };
   }

   private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput var1, int var2, int var3) {
      int var4 = 16 * var2;
      int var5 = 16 * var3;
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      Iterator var7 = this.blockEntitiesData.iterator();

      while(var7.hasNext()) {
         ClientboundLevelChunkPacketData.BlockEntityInfo var8 = (ClientboundLevelChunkPacketData.BlockEntityInfo)var7.next();
         int var9 = var4 + SectionPos.sectionRelative(var8.packedXZ >> 4);
         int var10 = var5 + SectionPos.sectionRelative(var8.packedXZ);
         var6.set(var9, var8.field_427, var10);
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
      final int packedXZ;
      // $FF: renamed from: y int
      final int field_427;
      final BlockEntityType<?> type;
      @Nullable
      final CompoundTag tag;

      private BlockEntityInfo(int var1, int var2, BlockEntityType<?> var3, @Nullable CompoundTag var4) {
         super();
         this.packedXZ = var1;
         this.field_427 = var2;
         this.type = var3;
         this.tag = var4;
      }

      private BlockEntityInfo(FriendlyByteBuf var1) {
         super();
         this.packedXZ = var1.readByte();
         this.field_427 = var1.readShort();
         int var2 = var1.readVarInt();
         this.type = (BlockEntityType)Registry.BLOCK_ENTITY_TYPE.byId(var2);
         this.tag = var1.readNbt();
      }

      void write(FriendlyByteBuf var1) {
         var1.writeByte(this.packedXZ);
         var1.writeShort(this.field_427);
         var1.writeVarInt(Registry.BLOCK_ENTITY_TYPE.getId(this.type));
         var1.writeNbt(this.tag);
      }

      static ClientboundLevelChunkPacketData.BlockEntityInfo create(BlockEntity var0) {
         CompoundTag var1 = var0.getUpdateTag();
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
