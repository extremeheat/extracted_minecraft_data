package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSectionBlocksUpdatePacket> STREAM_CODEC = Packet.codec(ClientboundSectionBlocksUpdatePacket::write, ClientboundSectionBlocksUpdatePacket::new);
   private static final int POS_IN_SECTION_BITS = 12;
   private final SectionPos sectionPos;
   private final short[] positions;
   private final BlockState[] states;

   public ClientboundSectionBlocksUpdatePacket(SectionPos var1, ShortSet var2, LevelChunkSection var3) {
      super();
      this.sectionPos = var1;
      int var4 = var2.size();
      this.positions = new short[var4];
      this.states = new BlockState[var4];
      int var5 = 0;

      for(ShortIterator var6 = var2.iterator(); var6.hasNext(); ++var5) {
         short var7 = (Short)var6.next();
         this.positions[var5] = var7;
         this.states[var5] = var3.getBlockState(SectionPos.sectionRelativeX(var7), SectionPos.sectionRelativeY(var7), SectionPos.sectionRelativeZ(var7));
      }

   }

   private ClientboundSectionBlocksUpdatePacket(FriendlyByteBuf var1) {
      super();
      this.sectionPos = SectionPos.of(var1.readLong());
      int var2 = var1.readVarInt();
      this.positions = new short[var2];
      this.states = new BlockState[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         long var4 = var1.readVarLong();
         this.positions[var3] = (short)((int)(var4 & 4095L));
         this.states[var3] = (BlockState)Block.BLOCK_STATE_REGISTRY.byId((int)(var4 >>> 12));
      }

   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLong(this.sectionPos.asLong());
      var1.writeVarInt(this.positions.length);

      for(int var2 = 0; var2 < this.positions.length; ++var2) {
         var1.writeVarLong((long)Block.getId(this.states[var2]) << 12 | (long)this.positions[var2]);
      }

   }

   public PacketType<ClientboundSectionBlocksUpdatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SECTION_BLOCKS_UPDATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBlocksUpdate(this);
   }

   public void runUpdates(BiConsumer<BlockPos, BlockState> var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

      for(int var3 = 0; var3 < this.positions.length; ++var3) {
         short var4 = this.positions[var3];
         var2.set(this.sectionPos.relativeToBlockX(var4), this.sectionPos.relativeToBlockY(var4), this.sectionPos.relativeToBlockZ(var4));
         var1.accept(var2, this.states[var3]);
      }

   }
}
