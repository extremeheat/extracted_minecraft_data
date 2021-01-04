package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacket implements Packet<ClientGamePacketListener> {
   private int x;
   private int z;
   private int availableSections;
   private CompoundTag heightmaps;
   private byte[] buffer;
   private List<CompoundTag> blockEntitiesTags;
   private boolean fullChunk;

   public ClientboundLevelChunkPacket() {
      super();
   }

   public ClientboundLevelChunkPacket(LevelChunk var1, int var2) {
      super();
      ChunkPos var3 = var1.getPos();
      this.x = var3.x;
      this.z = var3.z;
      this.fullChunk = var2 == 65535;
      this.heightmaps = new CompoundTag();
      Iterator var4 = var1.getHeightmaps().iterator();

      Entry var5;
      while(var4.hasNext()) {
         var5 = (Entry)var4.next();
         if (((Heightmap.Types)var5.getKey()).sendToClient()) {
            this.heightmaps.put(((Heightmap.Types)var5.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var5.getValue()).getRawData()));
         }
      }

      this.buffer = new byte[this.calculateChunkSize(var1, var2)];
      this.availableSections = this.extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), var1, var2);
      this.blockEntitiesTags = Lists.newArrayList();
      var4 = var1.getBlockEntities().entrySet().iterator();

      while(true) {
         BlockEntity var7;
         int var8;
         do {
            if (!var4.hasNext()) {
               return;
            }

            var5 = (Entry)var4.next();
            BlockPos var6 = (BlockPos)var5.getKey();
            var7 = (BlockEntity)var5.getValue();
            var8 = var6.getY() >> 4;
         } while(!this.isFullChunk() && (var2 & 1 << var8) == 0);

         CompoundTag var9 = var7.getUpdateTag();
         this.blockEntitiesTags.add(var9);
      }
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = var1.readInt();
      this.z = var1.readInt();
      this.fullChunk = var1.readBoolean();
      this.availableSections = var1.readVarInt();
      this.heightmaps = var1.readNbt();
      int var2 = var1.readVarInt();
      if (var2 > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[var2];
         var1.readBytes(this.buffer);
         int var3 = var1.readVarInt();
         this.blockEntitiesTags = Lists.newArrayList();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.blockEntitiesTags.add(var1.readNbt());
         }

      }
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.x);
      var1.writeInt(this.z);
      var1.writeBoolean(this.fullChunk);
      var1.writeVarInt(this.availableSections);
      var1.writeNbt(this.heightmaps);
      var1.writeVarInt(this.buffer.length);
      var1.writeBytes(this.buffer);
      var1.writeVarInt(this.blockEntitiesTags.size());
      Iterator var2 = this.blockEntitiesTags.iterator();

      while(var2.hasNext()) {
         CompoundTag var3 = (CompoundTag)var2.next();
         var1.writeNbt(var3);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLevelChunk(this);
   }

   public FriendlyByteBuf getReadBuffer() {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf var1 = Unpooled.wrappedBuffer(this.buffer);
      var1.writerIndex(0);
      return var1;
   }

   public int extractChunkData(FriendlyByteBuf var1, LevelChunk var2, int var3) {
      int var4 = 0;
      LevelChunkSection[] var5 = var2.getSections();
      int var6 = 0;

      int var7;
      for(var7 = var5.length; var6 < var7; ++var6) {
         LevelChunkSection var8 = var5[var6];
         if (var8 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !var8.isEmpty()) && (var3 & 1 << var6) != 0) {
            var4 |= 1 << var6;
            var8.write(var1);
         }
      }

      if (this.isFullChunk()) {
         Biome[] var9 = var2.getBiomes();

         for(var7 = 0; var7 < var9.length; ++var7) {
            var1.writeInt(Registry.BIOME.getId(var9[var7]));
         }
      }

      return var4;
   }

   protected int calculateChunkSize(LevelChunk var1, int var2) {
      int var3 = 0;
      LevelChunkSection[] var4 = var1.getSections();
      int var5 = 0;

      for(int var6 = var4.length; var5 < var6; ++var5) {
         LevelChunkSection var7 = var4[var5];
         if (var7 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !var7.isEmpty()) && (var2 & 1 << var5) != 0) {
            var3 += var7.getSerializedSize();
         }
      }

      if (this.isFullChunk()) {
         var3 += var1.getBiomes().length * 4;
      }

      return var3;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int getAvailableSections() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   public CompoundTag getHeightmaps() {
      return this.heightmaps;
   }

   public List<CompoundTag> getBlockEntitiesTags() {
      return this.blockEntitiesTags;
   }
}
