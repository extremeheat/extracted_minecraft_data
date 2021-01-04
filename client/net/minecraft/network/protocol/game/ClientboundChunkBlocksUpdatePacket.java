package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class ClientboundChunkBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
   private ChunkPos chunkPos;
   private ClientboundChunkBlocksUpdatePacket.BlockUpdate[] updates;

   public ClientboundChunkBlocksUpdatePacket() {
      super();
   }

   public ClientboundChunkBlocksUpdatePacket(int var1, short[] var2, LevelChunk var3) {
      super();
      this.chunkPos = var3.getPos();
      this.updates = new ClientboundChunkBlocksUpdatePacket.BlockUpdate[var1];

      for(int var4 = 0; var4 < this.updates.length; ++var4) {
         this.updates[var4] = new ClientboundChunkBlocksUpdatePacket.BlockUpdate(var2[var4], var3);
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.chunkPos = new ChunkPos(var1.readInt(), var1.readInt());
      this.updates = new ClientboundChunkBlocksUpdatePacket.BlockUpdate[var1.readVarInt()];

      for(int var2 = 0; var2 < this.updates.length; ++var2) {
         this.updates[var2] = new ClientboundChunkBlocksUpdatePacket.BlockUpdate(var1.readShort(), (BlockState)Block.BLOCK_STATE_REGISTRY.byId(var1.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.chunkPos.x);
      var1.writeInt(this.chunkPos.z);
      var1.writeVarInt(this.updates.length);
      ClientboundChunkBlocksUpdatePacket.BlockUpdate[] var2 = this.updates;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ClientboundChunkBlocksUpdatePacket.BlockUpdate var5 = var2[var4];
         var1.writeShort(var5.getOffset());
         var1.writeVarInt(Block.getId(var5.getBlock()));
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBlocksUpdate(this);
   }

   public ClientboundChunkBlocksUpdatePacket.BlockUpdate[] getUpdates() {
      return this.updates;
   }

   public class BlockUpdate {
      private final short offset;
      private final BlockState block;

      public BlockUpdate(short var2, BlockState var3) {
         super();
         this.offset = var2;
         this.block = var3;
      }

      public BlockUpdate(short var2, LevelChunk var3) {
         super();
         this.offset = var2;
         this.block = var3.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(ClientboundChunkBlocksUpdatePacket.this.chunkPos.getBlockAt(this.offset >> 12 & 15, this.offset & 255, this.offset >> 8 & 15));
      }

      public short getOffset() {
         return this.offset;
      }

      public BlockState getBlock() {
         return this.block;
      }
   }
}
